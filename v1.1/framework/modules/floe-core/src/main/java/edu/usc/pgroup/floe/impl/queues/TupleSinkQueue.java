package edu.usc.pgroup.floe.impl.queues;

import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.communication.Sender;
import edu.usc.pgroup.floe.api.communication.SenderChannel;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

public class TupleSinkQueue extends SinkQueue {
    private static final Logger logger = Logger
            .getLogger(TupleSourceQueue.class.getName());
    private final Map<String,Sender> outputSinks;
    private final List<String> tupleKeys;

    private LinkedBlockingDeque<TupleMessage> messageQueue = new LinkedBlockingDeque<>();
    private MessageSender sender ;

    public TupleSinkQueue(List<String> tupleKeys) {
        this.tupleKeys = tupleKeys;
        outputSinks = new HashMap<String,Sender>();
        if(sender == null) {
            sender = new MessageSender();
            new Thread(sender).start();
        }
    }

    @Override
    public void addSender(String key, Sender sender) {
        if (tupleKeys.contains(key) == false) {
            throw new IllegalArgumentException("Invalid tuple key: " + key
                    + " valid keys: " + tupleKeys.toString());
        }
        synchronized (outputSinks) {
            outputSinks.put(key, sender);
        }
    }

    public void writeMessage(Map<String, Message> messages) {
        for (String key : messages.keySet()) {
            if (tupleKeys.contains(key) == false) {
                throw new IllegalArgumentException("Tuple key doesn't exist "
                        + key);
            }
            Message message = messages.get(key);
            TupleMessage msg = new TupleMessage(key,message);
            messageQueue.add(msg);

        }
    }

    public void writeMessageBroadcast(Message message) {
        raiseMessageSentEvent(message);
        synchronized (outputSinks) {
            for (Sender sender : outputSinks.values()) {
                sender.send(message);
            }
        }
    }

    @Override
    public int getSize() {
        return messageQueue.size();
    }


    private class TupleMessage {
        private String key;
        private Message message;

        public TupleMessage(String key,Message message) {
            this.key = key;
            this.message = message;
        }

        public String getKey() {
            return key;
        }



        public Message getMessage() {
            return message;
        }


    }

    private class MessageSender implements Runnable {

        @Override
        public void run() {
            while (true) {
                TupleMessage msg = null;
                try {
                    msg = messageQueue.take();
                } catch (InterruptedException e) {

                }

                Sender sender = null;
                synchronized (outputSinks) {
                    sender = outputSinks.get(msg.getKey());
                }
                if (sender == null) {
                    throw new IllegalArgumentException("Unexpected key " + msg.getKey());
                }

                raiseMessageSentEvent(msg.getMessage());
                sender.send(msg.getMessage());
            }
        }
    }
}
