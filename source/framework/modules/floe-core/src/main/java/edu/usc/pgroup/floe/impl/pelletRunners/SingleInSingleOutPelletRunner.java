package edu.usc.pgroup.floe.impl.pelletRunners;

import edu.usc.pgroup.floe.api.framework.FlakeInfo;
import edu.usc.pgroup.floe.impl.ActivePelletVersions;
import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.exception.LandmarkPauseException;
import edu.usc.pgroup.floe.api.framework.PalletVersionInfo;
import edu.usc.pgroup.floe.api.framework.pelletmodels.Pellet;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.api.stream.FEmitter;
import edu.usc.pgroup.floe.api.stream.FIterator;
import edu.usc.pgroup.floe.impl.FloeRuntimeEnvironment;
import edu.usc.pgroup.floe.impl.ContainerImpl;
import edu.usc.pgroup.floe.impl.FlakeImpl;
import edu.usc.pgroup.floe.impl.communication.MessageImpl;
import edu.usc.pgroup.floe.impl.queues.*;
import edu.usc.pgroup.floe.impl.stream.FEmitterImpl;
import edu.usc.pgroup.floe.impl.stream.FIteratorImpl;
import edu.usc.pgroup.floe.util.Constants;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingleInSingleOutPelletRunner extends PelletRunner {
    private static Logger logger = Logger.getLogger(SingleInSingleOutPelletRunner.class.getName());
    private final FIterator iterator;
    private final FEmitter emitter;
    private Object pelletInstance;
    private FlakeImpl flake;

    public SingleInSingleOutPelletRunner(SourceQueue sourceQueue, SinkQueue sinkQueue, Class pellet, StateObject stateObject, FlakeImpl flake) {
        super(sourceQueue, sinkQueue, pellet, stateObject);
        this.iterator = new FIteratorImpl((StreamSourceQueue) sourceQueue);
        this.emitter = new FEmitterImpl((StreamSinkQueue) sinkQueue);
        this.flake = flake;
    }

    @Override
    public void runPellet() {
        try {

            Object input = iterator.next();
            try {
                pelletClass = getCorrectPelletVersion();

            } catch (RuntimeException e) {

                logger.warning("Discarding message ");
                throw e;

            }
            pelletInstance = pelletClass.newInstance();
//            if(input == null) {
//                return;
//            } else {
//                input = iterator.next();
//            }

            Class partypes[] = new Class[]{Object.class, StateObject.class};
            Method invokeMethod = null;

            invokeMethod = pelletClass.getMethod("invoke", partypes);

            Object[] argList = {input, stateObject};

            long startTime = System.currentTimeMillis();
            Object output = invokeMethod.invoke(pelletInstance, argList);
            long stopTime = System.currentTimeMillis();
            FlakeInfo info  = ((ContainerImpl) FloeRuntimeEnvironment.getEnvironment().getContainer()).
                    getPalletTypeToFlakeIdMap().get(pelletClass.getName());

            if(info != null) {
                info.setLatency(stopTime - startTime);
            } else {
                System.out.println("Error Flake Info not found");
            }

            emitter.emit(output);
        } catch (NoSuchMethodException e) {
            logger.log(Level.SEVERE, "Method not found in class " + pelletClass.getCanonicalName(), e);
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            logger.log(Level.SEVERE, "Error while invoking pallet logic " + pelletClass.getName(), e);
            throw new RuntimeException(e);
        } catch (LandmarkPauseException e) {
            logger.warning("Iterator Paused. Update in progress. Pallet Runner No longer in Use!!!");
        } catch (Exception e) {

            logger.log(Level.SEVERE, "Error while invoking pallet logic " + pelletClass.getName(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class getPelletModel() {
        return Pellet.class;
    }

    @Override
    public void sendPauseLandmark() {
        ((FIteratorImpl) iterator).setPauseLandmark();
    }

    @Override
    public Object getPelletInstance() {
        return pelletInstance;
    }

    private Class getCorrectPelletVersion() throws LandmarkPauseException, IllegalAccessException, InstantiationException {

        Class palletClass = null;
        if (flake.getLastUpdatedMode() == FlakeImpl.SINGLE_UPDATE_MODE) {
            return flake.getPelletClass();
        } else if (flake.getLastUpdatedMode() == FlakeImpl.MULTI_UPDATE_MODE) {

            //charith
            Object palletInstance = null;
            Message message = MessageImpl.getCurrentContextMessage();

            List<PalletVersionInfo> palletVersionInfos = flake.getPalletVersionInfos();
            if (message.getProperty(Constants.MESSAGE_ROUTE_VERSIONS) == null) {
                // This the new message to the system
                //Update the message context information about all the version Infos
                if (palletVersionInfos != null) {
                    message.setProperty(Constants.MESSAGE_ROUTE_VERSIONS, palletVersionInfos);
                } else {
                    return flake.getPelletClass();
                }

            }

            List<PalletVersionInfo> vInfos = (List<PalletVersionInfo>) message.getProperty(Constants.MESSAGE_ROUTE_VERSIONS);

            for (PalletVersionInfo versionInfo : vInfos) {
                if (flake.getNodeId().equals(versionInfo.getFlakeId().split("@")[2])) {
                    List<ActivePelletVersions> pelletVersions = flake.getActivePelletVersions();
                    for (ActivePelletVersions v : pelletVersions) {
                        if (versionInfo.getPelletType().equals(v.getPelletType()) && versionInfo.getVersion().equals(v.getVersion())) {

                            if (v.getPalletClass() == null) {
                                palletClass = FlakeImpl.loadClass(v.getPelletType());
                                if (palletClass == null) {
                                    throw new RuntimeException("Updated Class not found");
                                }
                                v.setPalletClass(palletClass);

                                return palletClass;
                            }

                            return v.getPalletClass();

                        }
                    }

                    //Version info in the message does not match any impl.
                    throw new RuntimeException("No Match found");
                }
            }

        }
        return null;
    }


}
