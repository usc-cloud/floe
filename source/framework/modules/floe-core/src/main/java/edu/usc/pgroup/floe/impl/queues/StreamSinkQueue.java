package edu.usc.pgroup.floe.impl.queues;

import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.communication.Sender;
import edu.usc.pgroup.floe.api.communication.SenderChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class StreamSinkQueue extends SinkQueue {
    private static final Logger logger = Logger
            .getLogger(TupleSourceQueue.class.getName());
    private final List<Sender> outputSinks;
    private int turn = 0;
    private Object writeLock = new Object();

    private LinkedBlockingQueue<Object> msgQueue = new LinkedBlockingQueue<Object>();
    private MesssageSender messsageSender;

    public StreamSinkQueue() {
        outputSinks = new ArrayList<Sender>();
        if(messsageSender == null) {
            messsageSender = new MesssageSender();
            new Thread(messsageSender).start();
        }
    }

    @Override
    public synchronized void addSender(String key, Sender sender) {
        outputSinks.add(sender);
    }

    public synchronized void writeBroadcastMessage(Object message) {
        synchronized (writeLock) {

            raiseMessageSentEvent((Message) message);
            for (Sender sender : outputSinks) {
                sender.send((Message) message);
            }


        }
    }

    public synchronized void writeMessage(Object message) {
        synchronized (writeLock) {

            if (outputSinks.size() == 0) {
                return;
            }

            msgQueue.add(message);
        }
    }

    @Override
    public int getSize() {
        synchronized (writeLock) {
            return msgQueue.size();
        }
    }

    private class MesssageSender implements Runnable {

        @Override
        public void run() {

            while (true) {
                Object message = null;
                try {
                    message = msgQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(message == null)
                    continue;
                turn++;
                int index = turn % outputSinks.size();
                Sender sender = outputSinks.get(index);

                Message msg = (Message) message;
                raiseMessageSentEvent((Message) message);
                sender.send(msg);

            }
        }
    }

}
