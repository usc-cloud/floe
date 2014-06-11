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
import edu.usc.pgroup.floe.api.util.BitConverter;
import edu.usc.pgroup.floe.util.NetworkUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class TCPControlFiber<T> implements Fiber<T>, Runnable {

    private static Logger logger = Logger.getLogger(TCPPushFiber.class.getName());

    private BlockingQueue<Message<T>> messageQueue = new LinkedBlockingDeque<Message<T>>();

    private boolean serverSide = true;

    private TCPTransportInfo controlChannelTransportInfo;
    private Port otherEndPort;

    ExecutorService execSvc;


    private List<Socket> clientList = new ArrayList<Socket>();
    /**
     * Server side
     */
    private ServerSocket serverSocket;

    private String overrideKey;

    private boolean running;

    private Socket clientSocket;

    private String id;
    public TCPControlFiber(boolean serverSide) {
        this.serverSide = serverSide;
        controlChannelTransportInfo = new TCPTransportInfo();
        execSvc = Executors.newCachedThreadPool();
    }
    
    
    @Override
    public void init() {
        if (serverSide) {

            try {
                ServerSocket tempServer = new ServerSocket(0);
                this.controlChannelTransportInfo.setHostAddress(NetworkUtil.getHostAddress());
                this.controlChannelTransportInfo.setTcpListenerPort(tempServer.getLocalPort());
                tempServer.close();

                this.running = true;
                execSvc.execute(this);
            } catch (IOException e) {
                logger.severe("Unexcepted Error while initializing Control Fiber " + e);
            }

        } else {

            try {
            	
            	TCPTransportInfo otherEndControlChannelInfo = new TCPTransportInfo(otherEndPort.getTransportInfo().getControlChannelInfo());            	
                clientSocket = new Socket(otherEndControlChannelInfo.getHostAddress(), otherEndControlChannelInfo.getTcpListenerInputPort());
                this.running = true;
                execSvc.execute(this);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public Message read(long timeOutMills) {
        try {
            return messageQueue.poll(timeOutMills, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.info("Error reading message " + e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(Message message) {
        OutputStream clientOut = null;
        if (!serverSide) {
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

                ex.printStackTrace();
            } finally {


            }
        } else {
            logger.info("Sending Control information to clients ");
            byte[] msgByteBuffer = BitConverter.getBytes(message);
            for(Socket client : clientList) {
                if (msgByteBuffer != null) {
                    try {
                        clientOut = client.getOutputStream();
                        byte[] sizeByteBuffer = BitConverter.intToByteArray(msgByteBuffer.length);
                        byte[] finalBuffer = new byte[msgByteBuffer.length + sizeByteBuffer.length];
                        System.arraycopy(sizeByteBuffer, 0, finalBuffer, 0, sizeByteBuffer.length);
                        System.arraycopy(msgByteBuffer, 0, finalBuffer, sizeByteBuffer.length, msgByteBuffer.length);
                        clientOut.write(finalBuffer);
                        clientOut.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

        }
    }

    @Override
    public TransportInfoBase getTransportInfo() {
        return controlChannelTransportInfo;
    }

    @Override
    public void setOtherEndConnectionPort(Port otherEndPort) {
        this.otherEndPort = otherEndPort;
    }

    @Override
    public void close() {
        if (serverSide) {
            try {
               // this.serverSocket.close();
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
        try {
            if (serverSide) {
                this.serverSocket = new ServerSocket(controlChannelTransportInfo.getTcpListenerInputPort());

                while (running) {

                    Socket newSocket = this.serverSocket.accept();
                    clientList.add(newSocket);
                    ServerHandler handler = new ServerHandler(messageQueue, newSocket);
                    this.execSvc.execute(handler);


                }
            } else {

                ClientHandler handler = new ClientHandler(messageQueue, clientSocket);
                execSvc.execute(handler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class ServerHandler implements Runnable {

        Socket client;
        BlockingQueue queue;
        InputStream ois;

        public ServerHandler(BlockingQueue queue, Socket socket) {
            this.queue = queue;
            this.client = socket;
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

            }
            return null;
        }

        @Override
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
                        messageQueue.put(inpMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            } finally {

            }
        }



    }


    private class ClientHandler implements Runnable {


        Socket client;
        BlockingQueue queue;
        InputStream ois;

        public ClientHandler(BlockingQueue queue, Socket client) {

            this.client = client;
            this.queue = queue;
        }

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

            }
            return null;
        }

        @Override
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
                        messageQueue.put(inpMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            } finally {

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
