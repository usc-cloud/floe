package edu.usc.pgroup.floe.api.stream;

import edu.usc.pgroup.floe.api.exception.LandmarkException;
import edu.usc.pgroup.floe.api.exception.LandmarkPauseException;

import java.util.concurrent.TimeUnit;

public interface FIterator<Input> {
	public Input next() throws LandmarkException, LandmarkPauseException;

	public Input next(int timeout, TimeUnit timeUnit) throws LandmarkException, LandmarkPauseException;

    public Input peek();
}
