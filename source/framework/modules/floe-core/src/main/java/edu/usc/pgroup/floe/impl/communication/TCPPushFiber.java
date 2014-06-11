/*
 * Copyright 2011, University of Southern California. All Rights Reserved.
 * 
 * This software is experimental in nature and is provided on an AS-IS basis only. 
 * The University SPECIFICALLY DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT 
 * LIMITATION ANY WARRANTY AS TO MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * This software may be reproduced and used for non-commercial purposes only, 
 * so long as this copyright notice is reproduced with each such copy made.
 */
package edu.usc.pgroup.floe.impl.communication;

import edu.usc.pgroup.floe.api.communication.Fiber;
import edu.usc.pgroup.floe.api.communication.TransportInfoBase;
import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;
import edu.usc.pgroup.floe.impl.queues.Queue;
import edu.usc.pgroup.floe.impl.queues.SinkQueue;
import edu.usc.pgroup.floe.impl.queues.SourceQueue;
import edu.usc.pgroup.floe.api.util.BitConverter;
import edu.usc.pgroup.floe.util.NetworkUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPPushFiber<T> implements Fiber, Runnable {

    private static Logger logger = Logger.getLogger(TCPPushFiber.class.getName());

    private boolean serverSide = true;

    private SourceQueue sourceQueue;

    private SinkQueue sinkQueue;

    private TCPTransportInfo connectionInfo;
    private Port otherEndPort;
    

    ExecutorService execSvc;

    /**
     * Server side
     */
    private ServerSocket serverSocket;

    private String overrideKey;

    private boolean running;

    private Socket clientSocket;

    private String id;


    private TCPTransportInfo otherEndConnectionInfo;
    public TCPPushFiber(Queue queue, boolean serverSide) {
        this.serverSide = serverSide;
        if (serverSide) {
            sourceQueue = (SourceQueue) queue;
        } else {
            sinkQueue = (SinkQueue) queue;
        }

        connectionInfo = new TCPTransportInfo();

    }

    public TCPPushFiber(Queue queue, String key, boolean serverSide) {
        this.serverSide = serverSide;
        if (serverSide) {
            sourceQueue = (SourceQueue) queue;
        } else {
            sinkQueue = (SinkQueue) queue;
        }

        connectionInfo = new TCPTransportInfo();
        this.overrideKey = key;
    }

    @Override
    public void init() {
        if (serverSide) {
            try {
                ServerSocket tempServer = new ServerSocket(0);
                this.connectionInfo.setHostAddress(NetworkUtil.getHostAddress());
                this.connectionInfo.setTcpListenerPort(tempServer.getLocalPort());
                tempServer.close();

                this.execSvc = Executors.newCachedThreadPool();
                this.running = true;
                execSvc.execute(this);
            } catch (IOException e) {
                logger.severe("Error while Operning port " + e);
            }


        } else {	//PULL.. TODO: verify this later
            try {
            	otherEndConnectionInfo = new TCPTransportInfo(otherEndPort.getTransportInfo());
                clientSocket = new Socket(otherEndConnectionInfo.getHostAddress(), otherEndConnectionInfo.getTcpListenerInputPort());
            } catch (IOException e) {
                logger.severe("Error while connecting : " + e);
            }

        }
    }

    @Override
    public Message read(long timeOutMills) {
        throw new UnsupportedOperationException("Get Message not supported in Push mechanish");
    }

    @Override
    public void write(Message message) {
        if (!serverSide) {
            OutputStream clientOut = null;
            try {
                byte[] msgByteBuffer = BitConverter.getBytes(message);
                if (msgByteBuffer != null) {
                    clientOut = clientSocket.getOutputStream();
                    byte[] sizeByteBuffer = BitConverter.intToByteArray(msgByteBuffer.length);
                    byte[] finalBuffer = new byte[msgByteBuffer.length + sizeByteBuffer.length];
                    System.arraycopy(sizeByteBuffer, 0, finalBuffer, 0, sizeByteBuffer.length);
                    System.arraycopy(msgByteBuffer, 0, finalBuffer, sizeByteBuffer.length, msgByteBuffer.length);
                    clientOut.write(finalBuffer);
                    clientOut.flush();
                    return;
                }

            } catch (Exception ex) {
                logger.severe("Error while writing the message to channel " + ex);
                ex.printStackTrace();
                try {
                    clientSocket = new Socket(otherEndConnectionInfo.getHostAddress(), otherEndConnectionInfo.getTcpListenerInputPort());
                } catch (IOException e) {
                    logger.severe("Error while connecting : " + e);
                }

            } finally {
//                try {
//                    clientOut.close();
//                } catch (IOException e) {
//                    //Ignore
//                }
            }
        } else {
            throw new UnsupportedOperationException("Can't write message to a server TCP Push channel");
        }
    }

    @Override
    public TransportInfoBase getTransportInfo() {
        return connectionInfo;
    }

    @Override
    public void setOtherEndConnectionPort(Port otherEndPort) {
        this.otherEndPort = otherEndPort;
    }

    @Override
    public void close() {
        if (serverSide) {
            try {
                //this.serverSocket.close();
                this.running = false;
                this.execSvc.shutdown();
            } catch (Exception e) {
                logger.warning("Error while shutting down the channel " + e);
            }

        } else {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    //Ignore
                }
            }
        }
    }


    @Override
    public void run() {

        if (serverSide) {
            try {
                this.serverSocket = new ServerSocket(this.connectionInfo.getTcpListenerInputPort());
                while (running) {
                    Socket newSocket = this.serverSocket.accept();
                    ServerSideChannelConnector newConnection = new ServerSideChannelConnector(this, newSocket);
                    this.execSvc.execute(newConnection);
                }
            } catch (SocketException e) {
                logger.severe("Error while Reading from TCP Push channel : " + e);
            } catch (Exception ex) {
                logger.severe("Unexpected Error while accessing TCP Push channel " + ex);
            }
        } else {
            //DO Nothing
        }
    }


    class ServerSideChannelConnector implements Runnable {
        Socket client = null;
        InputStream ois = null;
        TCPPushFiber channel;


        public ServerSideChannelConnector(TCPPushFiber channel, Socket clientSocket) {
            this.channel = channel;
            client = clientSocket;
        }

        // Reads the mentioned amount of bytes from the Socket Connection
        public byte[] readBytes(int size) {
            try {
                byte[] retByte = new byte[size];
                int read = 0, offset = 0, toRead = size;
                int i = 1;
                while (toRead > 0 && (read = ois.read(retByte, offset, toRead)) > 0) {
                    toRead -= read;
                    offset += read;
                    i++;
                }
                return retByte;
            } catch (Exception e) {
            	e.printStackTrace();
            }
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                ois = client.getInputStream();

                byte[] headObj = new byte[4];
                //Fetch the size of the input data from the first 4 bytes
                while (true) {
                    headObj = readBytes(4);
                    int tempSize = 0;
                    ByteBuffer byteBuffer = ByteBuffer.wrap(headObj);
                    IntBuffer intBuffer = byteBuffer.asIntBuffer();
                    tempSize = intBuffer.get();
                    intBuffer.clear();
                    if (tempSize > 0) {
                        byte[] recObj = readBytes(tempSize);
                        Message inpMsg = (Message) BitConverter.getObject(recObj);
                        if (overrideKey != null) {
                            inpMsg.setKey(overrideKey);
                        }
                        sourceQueue.queueMessage(this.channel, inpMsg);
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Interrupted Exception, Network Source Fiber Queue", e);

            } finally {
                try {
                    client.close();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

	@Override
	public Port getOtherEndConnectionPort() {
		return otherEndPort;
	}

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
