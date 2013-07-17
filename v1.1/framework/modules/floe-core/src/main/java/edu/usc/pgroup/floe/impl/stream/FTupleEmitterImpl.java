package edu.usc.pgroup.floe.impl.stream;

import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.stream.FTupleEmitter;
import edu.usc.pgroup.floe.impl.communication.MessageImpl;
import edu.usc.pgroup.floe.impl.queues.*;
import edu.usc.pgroup.floe.api.util.BitConverter;

import java.util.HashMap;
import java.util.Map;

public class FTupleEmitterImpl implements FTupleEmitter {
	private final TupleSinkQueue sinkRouter;

	public FTupleEmitterImpl(TupleSinkQueue sinkRouter) {
		this.sinkRouter = sinkRouter;
	}

	@Override
	public void emit(Map<String, Object> tuple) {
		Map<String, Message> messageTuple = new HashMap<String, Message>();
		for (String key : tuple.keySet()) {
			Message message = new MessageImpl();
			message.putPayload(BitConverter.getBytes(tuple.get(key)));
			messageTuple.put(key, message);
		}
		sinkRouter.writeMessage(messageTuple);
	}

	@Override
	public void emitLandmark(String tupleKey) {
		MessageImpl message = new MessageImpl();
		message.putPayload(null);
		message.setLandMark(true);
		Map<String, Message> messageTuple = new HashMap<String, Message>();
		messageTuple.put(tupleKey, message);
		sinkRouter.writeMessage(messageTuple);
	}

	@Override
	public void emitLandmarkBroadcast() {
		Message message = new MessageImpl();
		message.putPayload(null);
		message.setLandMark(true);
		sinkRouter.writeMessageBroadcast(message);

	}

	@Override
	public void emitMessageBroadcast(Object output) {
		throw new UnsupportedOperationException("emitMessageBroadcast not supported");
	}

}
