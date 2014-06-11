package edu.usc.pgroup.floe.impl.stream;

import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.stream.FEmitter;
import edu.usc.pgroup.floe.impl.communication.MessageImpl;
import edu.usc.pgroup.floe.impl.queues.*;
import edu.usc.pgroup.floe.api.util.BitConverter;

public class FEmitterImpl implements FEmitter {
	private final StreamSinkQueue sinkRouter;

	public FEmitterImpl(StreamSinkQueue sinkRouter) {
		this.sinkRouter = sinkRouter;
	}

	@Override
	public void emit(Object object) {
		Message message = MessageImpl.getCurrentContextMessage();
		byte[] bytes = BitConverter.getBytes(object);
		message.putPayload(bytes);
		sinkRouter.writeMessage(message);
	}

	@Override
	public void emitLandmark() {
		Message message = MessageImpl.getCurrentContextMessage();
		message.putPayload(null);
		message.setLandMark(true);
		sinkRouter.writeMessage(message);
	}

	@Override
	public void emitLandmarkBroadcast() {
		Message message = MessageImpl.getCurrentContextMessage();;
		message.putPayload(null);
		message.setLandMark(true);
		sinkRouter.writeBroadcastMessage(message);
	}

	@Override
	public void emitMessageBroadcast(Object output) {
		throw new UnsupportedOperationException("emitMessageBroadcast not supported");
	}
}
