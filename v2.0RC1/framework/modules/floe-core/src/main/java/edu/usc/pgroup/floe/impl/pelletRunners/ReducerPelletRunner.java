package edu.usc.pgroup.floe.impl.pelletRunners;

import edu.usc.pgroup.floe.api.framework.pelletmodels.ReducerPellet;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.api.stream.FIterator;
import edu.usc.pgroup.floe.api.stream.FMapEmitter;
import edu.usc.pgroup.floe.impl.queues.*;
import edu.usc.pgroup.floe.impl.stream.FMapEmitterImpl;
import edu.usc.pgroup.floe.impl.stream.FReducerIteratorImpl;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReducerPelletRunner extends PelletRunner {
	private static final Logger logger = Logger.getLogger(ReducerPelletRunner.class.getName());
	private FIterator iterator;
	private FMapEmitter emitter;
	private String key = null;
	private boolean pauseLandmark;
	private Object pelletInstance;

	public ReducerPelletRunner(SourceQueue sourceQueue, SinkQueue sinkQueue, Class pellet, StateObject stateObject) {
		super(sourceQueue, sinkQueue, pellet, stateObject);
	}

	@Override
	public void runPellet() {
		while (this.key == null) {
			if (pauseLandmark) {
				// do nothing if a pause landmark is received.
				return;
			}
			ReducerSourceQueue sQ = (ReducerSourceQueue) sourceQueue;
			this.key = sQ.readNewKey();
		}
		this.iterator = new FReducerIteratorImpl((ReducerSourceQueue) this.sourceQueue, key);
		this.emitter = new FMapEmitterImpl((MapperSinkQueue) sinkQueue);
		try {
			pelletInstance = pelletClass.newInstance();
			Class partypes[] = new Class[] { String.class, FIterator.class, FMapEmitter.class };
			Method invokeMethod = pelletClass.getMethod("invoke", partypes);
			Object[] argList = { this.key, iterator, emitter };
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
	public Object getPelletInstance() {
		return pelletInstance;
	}

	@Override
	public Class getPelletModel() {
		return ReducerPellet.class;
	}

	@Override
	public void sendPauseLandmark() {
		this.pauseLandmark = true;
		((FReducerIteratorImpl) iterator).setPauseLandmark();
	}

}
