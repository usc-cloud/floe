package edu.usc.pgroup.floe.impl.queues;

import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.communication.Fiber;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StreamSourceQueue extends SourceQueue {
    private static final Logger logger = Logger.getLogger(StreamSourceQueue.class.getName());
    private final BlockingQueue<Message> queue;

    public StreamSourceQueue() {
        queue = new LinkedBlockingQueue<Message>();
    }

    @Override
    public void queueMessage(Fiber sourceFiber, Message message) {

        raiseMessageRecievedEvent(message);

        if (message.getLandMark() == true) {
            System.out.println("Stream queue getting an landmark");
            handleLandmarksWaitForAll(sourceFiber, message);
            return;
        }
        try {
            logger.info("Adding message with object: " +
                    message +
                    "  to queue isLandmark: "
                    + message.getLandMark());
            queue.put(message);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Exception", e);
            throw new RuntimeException(e);
        }
    }

    public Message getMessage(int timeout, TimeUnit timeunit) {
        try {
            Message ret = queue.poll(timeout, timeunit);
            return ret;
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Exception", e);
            throw new RuntimeException(e);
        }
    }

    public Message peekMessage() {
        return queue.peek();
    }

    @Override
    public int getSize() {
        return queue.size();
    }

    @Override
    protected void addLandmarkToAllQueues(Message landmarkMessage) {
        System.out.println("At stream source queeu  adding landmark message");
        try {
            queue.put(landmarkMessage);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Interrupted exception", e);
            throw new RuntimeException(e);
        }
    }
}
