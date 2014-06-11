package edu.usc.pgroup.floe.impl.queues;

import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.communication.Sender;
import edu.usc.pgroup.floe.api.communication.SenderChannel;
import edu.usc.pgroup.floe.impl.events.MessageEvent;
import edu.usc.pgroup.floe.impl.events.MessageEvent.MessageEventType;
import edu.usc.pgroup.floe.impl.events.MessageEventListener;

import java.util.ArrayList;
import java.util.List;

public abstract class SinkQueue implements Queue{
	public abstract void addSender(String key, Sender sender);
	public abstract int getSize();
	
	protected List<MessageEventListener> messageEventListeners = new ArrayList<MessageEventListener>();
	
	public void addMessageEventListener(MessageEventListener listener)
	{
		messageEventListeners.add(listener);
	}
	
	protected void raiseMessageSentEvent(Message message)
	{
		MessageEvent event = new MessageEvent(MessageEventType.OutGoing,-1);
		for(MessageEventListener listener : messageEventListeners)
		{
			listener.handleMessageEvent(event);
		}
	}
}
