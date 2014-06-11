package edu.usc.pgroup.floe.impl.queues;

import edu.usc.pgroup.floe.api.communication.Fiber;
import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.framework.Flake;
import edu.usc.pgroup.floe.impl.FloeRuntimeEnvironment;
import edu.usc.pgroup.floe.impl.ContainerImpl;
import edu.usc.pgroup.floe.impl.FlakeImpl;
import edu.usc.pgroup.floe.impl.events.MessageEvent;
import edu.usc.pgroup.floe.impl.events.MessageEvent.MessageEventType;
import edu.usc.pgroup.floe.impl.events.MessageEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class SourceQueue implements Queue
{
    private final List<Fiber> sourceFibers = new LinkedList<Fiber>();
    private final HashMap<Fiber, List<Message>> landmarkMap = new HashMap<Fiber, List<Message>>();

    protected List<MessageEventListener> messageEventListeners = new ArrayList<MessageEventListener>();

    protected abstract void addLandmarkToAllQueues(Message landmarkMessage);

    public void addSourceChannel(Fiber sourceFiber) {
        sourceFibers.add(sourceFiber);
    }

    protected void handleLandmarksWaitForAll(Fiber sourceFiber, Message message) {
        if (sourceFibers.contains(sourceFiber) == false) {
            throw new IllegalArgumentException("Message from a not added source channel");
        }
        int landmarkMapSize;
        synchronized (landmarkMap) {
            List<Message> existingList = landmarkMap.get(sourceFiber);
            if (existingList == null) {
                existingList = new LinkedList<Message>();
            }
            existingList.add(message);
            landmarkMap.put(sourceFiber, existingList);
            landmarkMapSize = landmarkMap.size();
        }
        // Landmarks from all input channels
        if (landmarkMapSize == sourceFibers.size()) {
            addLandmarkToAllQueues(message);
            synchronized (landmarkMap) {
                // remove one message from all the source channel
                List<Fiber> keysToRemove = new LinkedList<Fiber>();
                for (Fiber fiber : landmarkMap.keySet()) {
                    LinkedList<Message> list = (LinkedList) landmarkMap.get(fiber);
                    list.removeFirst();
                    if (list.size() == 0) {
                        keysToRemove.add(fiber);
                    }
                }
                for (Fiber fiber : keysToRemove) {
                    landmarkMap.remove(fiber);
                }
            }
        }
    }

    public abstract void queueMessage(Fiber sourceFiber, Message message);

    public abstract int getSize();

    public void addMessageEventListener(MessageEventListener listener)
    {
        messageEventListeners.add(listener);
    }

    protected void raiseMessageRecievedEvent(Message message)
    {
        MessageEvent event = new MessageEvent(MessageEventType.Incoming,-1);
        for(Flake f :((ContainerImpl) FloeRuntimeEnvironment.getEnvironment().
                getContainer()).getFlakeList()) {
            FlakeImpl flake = (FlakeImpl)f;

            if(flake.getContainerMonitor() != null)
                flake.getContainerMonitor().setMessageStarted(true);
        }
        for(MessageEventListener listener : messageEventListeners)
        {
            listener.handleMessageEvent(event);
        }


    }
}
