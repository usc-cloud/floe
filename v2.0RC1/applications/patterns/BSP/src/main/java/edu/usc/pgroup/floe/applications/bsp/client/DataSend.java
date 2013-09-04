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
package edu.usc.pgroup.floe.applications.bsp.client;

import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.communication.Sender;
import edu.usc.pgroup.floe.api.framework.floegraph.Node;
import edu.usc.pgroup.floe.impl.communication.*;
import edu.usc.pgroup.floe.util.BitConverter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class DataSend {

    ExecutorService executor = Executors.newCachedThreadPool();

    public DataSend() {

    }

    public static void main(String[] args) throws InterruptedException {
        Toolkit.getDefaultToolkit().beep();
        String location = "floe.cloudapp.net";
        // String location = "localhost";
        DataSend me = new DataSend();
        int port = Integer.parseInt("51192");
        int controlPort = Integer.parseInt("53106");


        TCPTransportInfo otherEndTransportInfo = new TCPTransportInfo();
        otherEndTransportInfo.setHostAddress(location);
        otherEndTransportInfo.setTcpListenerPort(port);


        TCPTransportInfo otherEndControlInfo = new TCPTransportInfo();
        otherEndControlInfo.setHostAddress(location);
        otherEndControlInfo.setTcpListenerPort(controlPort);

        otherEndTransportInfo.setControlChannelInfo(otherEndControlInfo);

        Node.Port otherEndport = new Node.Port();
        otherEndport.setPortName("in");
        otherEndport.setTransportType("TCP");
        otherEndport.setDataTransferMode("Push");

        otherEndport.setTransportInfo(otherEndTransportInfo);

        BlockingQueue<byte[]> tempQueue = new LinkedBlockingQueue<byte[]>();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(ChannelFactory.KEY, null);
        params.put(ChannelFactory.QUEUE, null);
        params.put(ChannelFactory.SERVER_SIDE, "false");

        Sender sender = new SenderImpl();
        sender.init(params);
        sender.connect(otherEndport, null);
        Message<byte[]> tempMessage = new MessageImpl<byte[]>();

//        BSPMessage msg = new BSPMessage();
//        msg.setType(BSPMessage.CTRL);

        String msg = "|***|";
        byte[] payLoad = BitConverter.getBytes(msg);

        tempMessage.putPayload(payLoad);

        // Send Equipment List Data First
        sender.setSenderStrategy(new DuplicateSenderChannelStrategy());
        long sTime = System.currentTimeMillis();

       // sender.send(tempMessage);
        if (false) {
            sender.stop();
            return;

        }

      //  me.simpleSend(sender, tempMessage,2.0);
      //  me.burstSend(sender,params,tempMessage,12,140,60*1000);
       me.randomWalkSend(sender,tempMessage,80,10,1);
    }


    private void simpleSend(Sender sender, Message msg, double rate) {
        long startTime = System.currentTimeMillis();
        int i = 0;
        for (i = 0; ; i++) {

            sender.send(msg);
            try {
                long time = (long) (1000 / rate);
                System.out.println(time);
                Thread.sleep(time);
                if ((System.currentTimeMillis() - startTime) > 15 * 60 * 1000) {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("i : " + i);
        }

        System.out.println("Throughput 1 : " + i * 1000 / (System.currentTimeMillis() - startTime));
        Toolkit.getDefaultToolkit().beep();
        //sender.stop();
    }


    private void burstSend(Sender sender,Map<String,Object> params,Message msg, int iterations, int msgsPerIteration, long delay) {
        Toolkit.getDefaultToolkit().beep();



        for (int i = 0; i < iterations; i++) {

          //  sender.connect(port, null);
            for (int j = 0; j < msgsPerIteration; j++) {
                sender.send(msg);
                System.out.println("i :" + i + " j : " + j);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {

                }
            }

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {

            }
        }

        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {

        }
      //  sender.stop();
        System.out.println("Done -Burst");
        Toolkit.getDefaultToolkit().beep();
    }


    private void randomWalkSend(Sender sender, Message msg, int iterations, int msgsPerIteration, double rate) {

        Toolkit.getDefaultToolkit().beep();
        double r = rate;
        for (int i = 0; i < iterations; i++) {

            int j = 0;
            for (j = 0; j < msgsPerIteration; j++) {
                System.out.println("Rate : " + r);
                sender.send(msg);
                try {
                    long time = (long) (1000 / r);
                    System.out.println(time);
                    Thread.sleep(time);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                     //To change body of catch statement use File | Settings | File Templates.
                }
                System.out.println("i : " + i + " j : " + j);
            }

            double random = Math.random();
            if (random < 0.5) {
                r +=1;
            } else {
                r -= 1;
                if (r <= 0) {
                    r = 0.5;
                }
            }

        }
        Toolkit.getDefaultToolkit().beep();
    }

    private class MessageSender implements Runnable {

        Sender sender;
        Message message;

        public MessageSender(Sender sender, Message message) {
            synchronized (sender) {
                this.sender = sender;
                this.message = message;
            }
        }

        @Override
        public void run() {
            System.out.println("Sent ");
            sender.send(message);
        }
    }

}
