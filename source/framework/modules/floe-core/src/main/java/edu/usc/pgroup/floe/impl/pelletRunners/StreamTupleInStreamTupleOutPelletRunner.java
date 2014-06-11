package edu.usc.pgroup.floe.impl.pelletRunners;

import edu.usc.pgroup.floe.api.framework.pelletmodels.StreamTupleInStreamTupleOutPellet;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.api.stream.FTupleEmitter;
import edu.usc.pgroup.floe.api.stream.FTupleIterator;
import edu.usc.pgroup.floe.impl.queues.*;
import edu.usc.pgroup.floe.impl.stream.FTupleEmitterImpl;
import edu.usc.pgroup.floe.impl.stream.FTupleIteratorImpl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StreamTupleInStreamTupleOutPelletRunner extends PelletRunner {
	private static final Logger logger = Logger.getLogger(StreamTupleInStreamTupleOutPelletRunner.class.getName());
	private final FTupleIterator iterator;
	private final FTupleEmitter emitter;
	private final List<String> inTupleKeys;
	private final List<String> outTupleKeys;
	private Object pelletInstance;

	public StreamTupleInStreamTupleOutPelletRunner(SourceQueue sourceQueue, SinkQueue sinkQueue, Class pelletClass, StateObject stateObject,
			List<String> inTupleKeys, List<String> outTupleKeys) {
		super(sourceQueue, sinkQueue, pelletClass, stateObject);
		iterator = new FTupleIteratorImpl((TupleSourceQueue) sourceQueue, inTupleKeys);
		emitter = new FTupleEmitterImpl((TupleSinkQueue) sinkQueue);
		this.inTupleKeys = inTupleKeys;
		this.outTupleKeys = outTupleKeys;
	}

	@Override
	public void sendPauseLandmark() {
		((FTupleIteratorImpl) iterator).setPauseLandmark();
	}

	@Override
	public void runPellet() {
		try {
			this.pelletInstance = pelletClass.newInstance();
			Class partypes[] = new Class[] { FTupleIterator.class, FTupleEmitter.class, StateObject.class };
			Method invokeMethod = pelletClass.getMethod("invoke", partypes);
			Object[] argList = { iterator, emitter, stateObject };
			invokeMethod.invoke(pelletInstance, argList);
        } catch (NoSuchMethodException e) {
            logger.log(Level.SEVERE, "Method not found in class " + pelletClass.getCanonicalName(), e);
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            logger.log(Level.SEVERE,"Error while invoking pallet logic " + pelletClass.getName(),e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Unexpected Error while running pallet " + pelletClass.getName(),e);
            throw new RuntimeException(e);
        }
	}

	@Override
	public Class getPelletModel() {
		return StreamTupleInStreamTupleOutPellet.class;
	}

	@Override
	public Object getPelletInstance() {
		return pelletInstance;
	}

}
