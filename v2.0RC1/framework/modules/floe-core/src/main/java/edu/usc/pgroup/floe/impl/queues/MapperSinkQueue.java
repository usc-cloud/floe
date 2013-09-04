package edu.usc.pgroup.floe.impl.queues;

import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.communication.Sender;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

public class MapperSinkQueue extends SinkQueue {
    private static final Logger logger = Logger.getLogger(TupleSourceQueue.class.getName());
    protected final List<Sender> outputSinks;

    private LinkedBlockingDeque<TupleMessage> messageQueue = new LinkedBlockingDeque<>();
    private MessageSender sender ;
    public MapperSinkQueue() {
        outputSinks = new ArrayList<Sender>();
        if(sender == null) {
            sender = new MessageSender();
            new Thread(sender).start();
        }
    }

    @Override
    public synchronized void addSender(String key, Sender sender) {
        outputSinks.add(sender);
    }

    public synchronized void writeMessage(String key, Message message) {


        TupleMessage msg = new TupleMessage();
        msg.setKey(key);
        msg.setMessage(message);
        messageQueue.add(msg);

    }

    public synchronized void writeBroadcastMessage(Message message) {
        raiseMessageSentEvent(message);
        for (Sender sender : outputSinks) {
            sender.send(message);
        }
    }

    protected int getEdgeIndex(String key) {
        int sum = 0;
        for (int i = 0; i < key.length(); i++) {
            sum += key.charAt(i);
        }
        return sum % outputSinks.size();
    }

    @Override
    public int getSize() {
        return messageQueue.size();
    }

    private class TupleMessage {
        private String key;
        private Message message;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
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

                String key = msg.getKey();
                Message message = msg.getMessage();

                int index = getEdgeIndex(key);

                logger.info("Mapper sink queue sending key: " + key + " to edge index " + index);
                Sender sender = outputSinks.get(index);


                message.setKey(key);
                logger.info("Sending the message : " + message + "  from Mapper ");
                raiseMessageSentEvent(message);
                sender.send(message);
            }
        }
    }

}
