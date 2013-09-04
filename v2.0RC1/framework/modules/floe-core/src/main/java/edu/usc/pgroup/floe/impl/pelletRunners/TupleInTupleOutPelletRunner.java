package edu.usc.pgroup.floe.impl.pelletRunners;

import edu.usc.pgroup.floe.api.exception.LandmarkException;
import edu.usc.pgroup.floe.api.exception.LandmarkPauseException;
import edu.usc.pgroup.floe.api.framework.pelletmodels.TupleInTupleOutPellet;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.api.stream.FTupleEmitter;
import edu.usc.pgroup.floe.api.stream.FTupleIterator;
import edu.usc.pgroup.floe.impl.queues.*;

import edu.usc.pgroup.floe.impl.stream.FTupleEmitterImpl;
import edu.usc.pgroup.floe.impl.stream.FTupleIteratorImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TupleInTupleOutPelletRunner extends PelletRunner {
	private final FTupleIterator iterator;
	private final FTupleEmitter emitter;
	private Object pelletInstance;

    private static Logger logger = Logger.getLogger(TupleInTupleOutPelletRunner.class.getName());

	public TupleInTupleOutPelletRunner(SourceQueue sourceQueue, SinkQueue sinkQueue, Class pellet, StateObject stateObject,
			List<String> inputTupleKeys) {
		super(sourceQueue, sinkQueue, pellet, stateObject);
		this.iterator = new FTupleIteratorImpl((TupleSourceQueue) sourceQueue, inputTupleKeys);
		this.emitter = new FTupleEmitterImpl((TupleSinkQueue) sinkQueue);
	}

	@Override
	public void runPellet() {
		try {
			pelletInstance = pelletClass.newInstance();
			Class partypes[] = new Class[] { Map.class, StateObject.class };
			Method invokeMethod = pelletClass.getMethod("invoke", partypes);
			Map<String, Object> input = iterator.next();
			Object[] argList = { input, stateObject };
			Map<String, Object> ret = (Map<String, Object>) invokeMethod.invoke(pelletInstance, argList);
			emitter.emit(ret);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (LandmarkException e) {
			throw new RuntimeException(e);
		} catch (LandmarkPauseException e) {
            logger.warning("Pallet Runner " + this.getPelletInstance().toString() + " No longer in use !!!");
        }
    }

	@Override
	public Object getPelletInstance() {
		return pelletInstance;
	}

	@Override
	public Class getPelletModel() {
		return TupleInTupleOutPellet.class;
	}

	@Override
	public void sendPauseLandmark() {
		((FTupleIteratorImpl) iterator).setPauseLandmark();

	}
}
