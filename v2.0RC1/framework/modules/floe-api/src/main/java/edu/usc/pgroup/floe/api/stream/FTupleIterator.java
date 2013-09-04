package edu.usc.pgroup.floe.api.stream;

import edu.usc.pgroup.floe.api.exception.LandmarkException;
import edu.usc.pgroup.floe.api.exception.LandmarkPauseException;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface FTupleIterator {
	public Map<String, Object> next() throws LandmarkException, LandmarkPauseException;

	public Map<String, Object> next(int timeout, TimeUnit timeunit) throws LandmarkException, LandmarkPauseException;
}
