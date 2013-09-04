package edu.usc.pgroup.floe.impl.stream;

import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.exception.LandmarkException;
import edu.usc.pgroup.floe.api.exception.LandmarkPauseException;
import edu.usc.pgroup.floe.api.stream.FIterator;
import edu.usc.pgroup.floe.impl.queues.*;
import edu.usc.pgroup.floe.api.util.BitConverter;

import java.util.concurrent.TimeUnit;

public class FReducerIteratorImpl implements FIterator {
	private final ReducerSourceQueue sourceRouter;
	private final String key;
	private boolean pauseLandmark = false;

	public FReducerIteratorImpl(ReducerSourceQueue sourceRouter, String key) {
		this.sourceRouter = sourceRouter;
		this.key = key;
	}

	@Override
	public Object next(int timeout, TimeUnit timeunit) throws LandmarkException, LandmarkPauseException {
		Message message = null;
		if (pauseLandmark) {
			System.out.println("Throwing pause landmar.....");
			throw new LandmarkPauseException("Pause exception at landmark");
		}
		message = sourceRouter.readMessage(key, timeout, timeunit);
		if (message == null) {
			return null;
		}
		if (message.getLandMark() == true) {
			System.out.println("----------Goning to emit landmark---------");
			throw new LandmarkException("landmark found");
		}
		byte[] bytes = (byte[]) message.getPayload();
		return BitConverter.getObject(bytes);

	}

    @Override
    public Object peek() {
        throw new UnsupportedOperationException();
    }

    @Override
	public Object next() throws LandmarkException, LandmarkPauseException {
		Object ret = null;
		while (ret == null)
			ret = this.next(5000, TimeUnit.MICROSECONDS);
		return ret;
	}

	public void setPauseLandmark() {
		pauseLandmark = true;
	}

}
