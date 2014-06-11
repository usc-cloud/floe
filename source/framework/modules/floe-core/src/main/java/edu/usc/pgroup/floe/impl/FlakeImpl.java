/*
 * Copyright 2011, University of Southern California. All Rights Reserved.
 * 
 * This software is experimental in nature and is provided on an AS-IS basis only. 
 * The University SPECIFICALLY DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT 
 * LIMITATION ANY WARRANTY AS TO MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * This software may be reproduced and used for non-commercial purposes only, 
 * so long as this copyright notice is reproduced with each such copy made.
 */

package edu.usc.pgroup.floe.impl;

/***
 * A Flake is the computation unit that can process a message/task. 
 * The task that it performs depends on the type of Pellet.
 * A Flake is part of a Container, and the resources available to a Flake are determined by the Container.
 *
 * @author Sreedhar Natarajan (sreedhan@usc.edu)
 * @author Yogesh Simmhan (simmhan@usc.edu)
 * @version v0.1, 2012-01-11
 *
 */

import edu.usc.pgroup.floe.api.communication.*;
import edu.usc.pgroup.floe.api.framework.Flake;
import edu.usc.pgroup.floe.api.framework.FlakeInfo;
import edu.usc.pgroup.floe.api.framework.PalletVersionInfo;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;
import edu.usc.pgroup.floe.api.framework.pelletmodels.*;
import edu.usc.pgroup.floe.api.framework.rest.FlakeCreationInfo;
import edu.usc.pgroup.floe.api.framework.rest.FlakeWiringInfo;
import edu.usc.pgroup.floe.api.state.FState;
import edu.usc.pgroup.floe.api.state.FStateUpdator;
import edu.usc.pgroup.floe.impl.communication.*;
import edu.usc.pgroup.floe.impl.deployers.PalletJarDeployer;
import edu.usc.pgroup.floe.impl.events.MessageEvent;
import edu.usc.pgroup.floe.impl.events.MessageEvent.MessageEventType;
import edu.usc.pgroup.floe.impl.events.MessageEventListener;
import edu.usc.pgroup.floe.impl.pelletHandlers.*;
import edu.usc.pgroup.floe.impl.pelletRunners.PelletTask;
import edu.usc.pgroup.floe.impl.pelletRunners.PelletTask.PelletTaskStatus;
import edu.usc.pgroup.floe.impl.queues.SinkQueue;
import edu.usc.pgroup.floe.impl.queues.SourceQueue;
import edu.usc.pgroup.floe.util.Constants;
import edu.usc.pgroup.floe.util.ContainerMonitor;
import edu.usc.pgroup.floe.util.NetworkUtil;
import edu.usc.pgroup.floe.util.PalletJarUtils;

import java.io.File;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Integer.*;

public class FlakeImpl<T> implements Flake, Runnable, MessageEventListener {
    private static Logger logger = Logger.getLogger(FlakeImpl.class.getName());

    public static enum FlakeStatus {
        CREATED, INITIALIZED, PAUSE_INITIALIZED, PAUSED, RUNNING, STOP_INITIALIZED, CORE_CHANGE_INITIALIZED, STOPPED;
    }

    // Each Flake could have a Single Input Buffer and Single Output Buffer
    // Each Flake to have two ports up and running. One for incoming requests
    // and one for outgoing requests.
    private ForkJoinPool forkJoinPool;
    FlakeInfo flakeInfo;
    // List of Pellet Tasks & The corresponding status
    private List<Port> inputPorts;
    private List<Port> outputPorts;

    //List of Transport Senders and Receivers
    private Map<String, Receiver> receivers;
    private Map<String, Sender> senders;


    private ControlChannelCommunicationManager communicationManager;

    HashMap<String, T> mappablePelletMap; // Hash Map for the Mappable Pellet
    ArrayList<String> portNameMap; // ArrayList to keep track of different
    // portName;
    Thread flakeThread;
    boolean startFlag;
    boolean resourceAltered;
    boolean pelletNumberAltered;
    int pelletCount;
    private ResourceInfo alterResourceRequest;
    private final Lock pelletCountLock;
    private final Condition availablePelletSlot;
    private int runningPelletCount;
    private PelletHandler pelletHandler;
    private SourceQueue sourceQueue;
    private SinkQueue sinkQueue;
    private Class pelletClass;
    private String pelletType;
    private FlakeStatus flakeStatus;
    private final List<PelletTask> runningTasks = new ArrayList<PelletTask>();
    private final FState state;
    private ContainerMonitor containerMonitor;

    private List<TransportInfoBase> successors;

//    // class laoder which loads pellet jars
//    private URLClassLoader classLoader;


    double averagePelletProcessingLatency;
    private FlakeCreationInfo flakeCreationInfo;
    private String nodeId;

    private List<ActivePelletVersions> activePelletVersions = new ArrayList<ActivePelletVersions>();
    private List<PalletVersionInfo> palletVersionInfos;


    private int lastUpdatedMode = 1;

    public static final int SINGLE_UPDATE_MODE = 1;

    public static final int MULTI_UPDATE_MODE = 2;

    public FlakeImpl() {
        this.flakeStatus = FlakeStatus.CREATED;
        alterResourceRequest = new ResourceInfo();
        pelletCountLock = new ReentrantLock();
        availablePelletSlot = pelletCountLock.newCondition();
        this.runningPelletCount = 0;
        resourceAltered = false;
        pelletNumberAltered = false;
        this.alterResourceRequest = new ResourceInfo();
        state = new FState();
        this.averagePelletProcessingLatency = -1;
        this.communicationManager = new ControlChannelCommunicationManager(this);

    }

    @Override
    public FlakeInfo getFlakeInfo() {
        // update buffer lengths..
        this.flakeInfo.setInBufferSize(sourceQueue.getSize());
        this.flakeInfo.setOutBufferSize(sinkQueue.getSize());
        // this.flakeInfo.setPelletCount(this.pelletCount);

        return this.flakeInfo;
    }


    public void setLastUpdatedMode(int lastUpdatedMode) {
        this.lastUpdatedMode = lastUpdatedMode;
    }

    public void createInputListenersAndRecievers(String inpFlakeID, List<Port> inpPorts, Map<String, Receiver> receivers) {
        // Start the Transport Receivers
        // Start only those channels in the inpList of type push

        // Identify whether there is a Map Based Communication or Single Message
        // Based Communication.
        // Port Based or Single Input Based on the Input Port Tags in the Node
        // Config File

        try {
            logger.info("Establishing source channel for Flake " + inpFlakeID);

            for (Port inpPort : inpPorts) {
                inpPort.setFlakeId(flakeInfo.getflakeId());
                // Port Based Communication
                if (inpPort.getDataTransferMode().contains("Push") || inpPort.getDataTransferMode().contains("push") || inpPort.getDataTransferMode().contains("PUSH")) {
                    // If port found use the Mappable Input Port else use
                    // the normal Single Input Buffer

                    // TODO change this to get params form coordinator and create channel in a generic way
                    Map<String, Object> params = new HashMap<String, Object>();

                    String portname = inpPort.getPortName() == null ? "NULL" : inpPort.getPortName();

                    params.put(ChannelFactory.KEY, portname);
                    params.put(ChannelFactory.QUEUE, sourceQueue);
                    params.put(ChannelFactory.SERVER_SIDE, "true");

                    Receiver receiver = new ReceiverImpl();
                    receiver.init();


                    //TODO: RENAME CHANNEL-- TO SOMETHING LIKE CHANNEL_BUNDLE (but shorter)
                    String cId = receiver.createChannel(params);
                    receivers.put(portname, receiver);
                    receiver.start();


                    //NOTE: DO NOT SWAP ANYTHING HERE>> 
                    //WILL DO THAT LATER>> 
                    //BELOW COMMENT IS RETAINED FROM SREEDHAR>> 
                    //BUT WE DONT FOLLOW THAT


                    // Swap the Edge Information and Return back the
                    // Connection Information
                    // So the Other Side can establish a connection
                    TransportInfoBase tempConnectionInfo = receiver.getChannel(cId).getTransportInfo();

                    inpPort.setTransportInfo(tempConnectionInfo);

                    //??
                    //this.flakeInfo.addSourceConnection(tempConnectionInfo);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // The Connections are established based on whether Pull Fiber can be
    // established
    // in the first phase or not
    public void createOutputConnectionsAndSenders(List<Port> outPorts, Map<String, Sender> tempSinkChannel) {
        // Start the TCP Sink Fiber
        // Start only those channels in the outputList of type Pull
        //WILL DO LATER
        try {
            System.out.print("\n");
            for (Port port : outPorts) {
                if (port.getPortName() == null) {
                    // if(tempEdge.getchannelBehaviourType().contains("Pull"))
                    // {
                    // ServerSocket inServer = new ServerSocket(0);
                    // SinkChannel<T> outTCPChannel = new
                    // TCPSinkPullChannel<T>(this.inpBuffer);
                    // tempSinkChannel.add(outTCPChannel);
                    // //System.out.println("A Push Fiber Established at " +
                    // inServer.getLocalPort());
                    // outTCPChannel.openConnection();
                    // this.flakeInfo.addSourceConnection(outTCPChannel.getConnectionInfo());
                    // inServer.close();
                    // }
                } else {
                    // if(tempEdge.getchannelBehaviourType().contains("Pull"))
                    // {
                    // BlockingQueue<Message<T>> tempQueue = null;
                    // if(this.mappableInputBuffer.containsKey(tempEdge.getedgePort().getport()))
                    // {
                    // tempQueue =
                    // this.mappableInputBuffer.get(tempEdge.getedgePort().getport());
                    // }
                    // else
                    // {
                    // tempQueue = this.inpBuffer;
                    // }
                    // ServerSocket inServer = new ServerSocket(0);
                    // SinkChannel<T> outTCPChannel = new
                    // TCPSinkPullChannel<T>(tempQueue);
                    // ConnectionInfo tempConnectionInfo =
                    // outTCPChanNnel.getConnectionInfo();
                    // tempConnectionInfo.setPortName(tempEdge.getedgePort().getport());
                    // tempSinkChannel.add(outTCPChannel);
                    // //System.out.println("A Push Fiber Established at " +
                    // inServer.getLocalPort());
                    // outTCPChannel.openConnection();
                    // this.flakeInfo.addSourceConnection(tempConnectionInfo);
                    // inServer.close();
                    // }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setInputPorts(List<Port> inpPorts) {
        this.inputPorts = inpPorts;

    }

    public void setOutputPorts(List<Port> outPorts) {
        this.outputPorts = outPorts;

    }

    @Override
    public List<TransportInfoBase> getSuccessors() {
        return successors;
    }

    public SourceQueue getSourceQueue() {
        return sourceQueue;
    }

    public SinkQueue getSinkQueue() {
        return sinkQueue;
    }

    public PelletHandler getPelletHandler() {
        return pelletHandler;
    }

    @Override
    public List<TransportInfoBase> getPredecessors() {

        //TODO implemennt this
        return null;
    }


    @Override
    public String create(FlakeCreationInfo flakeCreationInfo) {
        // TODO Auto-generated method stub
        this.flakeCreationInfo = flakeCreationInfo;

        return create(flakeCreationInfo.getNodeID(), flakeCreationInfo.getPellet(), flakeCreationInfo.getResource(),
                flakeCreationInfo.getInputPorts(),
                flakeCreationInfo.getOutputPorts());
    }

    @Override
    public String create(String inpNodeID, String pelletType, ResourceInfo resources, List<Port> inpPorts, List<Port> outPorts) {
        try {

            this.nodeId = inpNodeID;

            String flakeID = "Flake@" + NetworkUtil.getHostAddress() + "@" + inpNodeID + "@" + UUID.randomUUID().toString();

            // Flake Specific Info
            this.flakeInfo = new FlakeInfo(flakeID, pelletType, resources);

            ((ContainerImpl) FloeRuntimeEnvironment.getEnvironment().getContainer()).
                    getPalletTypeToFlakeIdMap().put(pelletType, flakeInfo);

            synchronized (this.flakeStatus) {
                this.flakeInfo.setstatus("Starting");
                this.flakeStatus = FlakeStatus.INITIALIZED;
            }

            receivers = new ConcurrentHashMap<String, Receiver>();
            senders = new ConcurrentHashMap<String, Sender>();

            // Set Up Ports if the InputPorts list is not empty
            // Becomes a Port Based Message Exchange when Map Entry exists
            this.setInputPorts(inpPorts);

            // Set Up Output Ports If the OutputPorts list is not empty
            // Create Input Source Connections
            this.setOutputPorts(outPorts);

            flakeInfo.setInputPorts(inpPorts);
            flakeInfo.setOutputPorts(outPorts);

            this.pelletType = pelletType;
            this.pelletClass = loadClass(pelletType);
            this.pelletHandler = createPelletHandler(this.pelletClass);
            this.sourceQueue = this.pelletHandler.createSourceQueue();
            this.sinkQueue = this.pelletHandler.createSinkQueue();

            sinkQueue.addMessageEventListener(this);
            sourceQueue.addMessageEventListener(this);

            this.createInputListenersAndRecievers(inpNodeID, inpPorts, receivers);


            // Create Output Sink Connections
            //?
            this.createOutputConnectionsAndSenders(outPorts, senders);

            ActivePelletVersions apvs = new ActivePelletVersions();
            apvs.setPalletClass(pelletClass);
            apvs.setPelletType(pelletType);
            apvs.setVersion("0");
            apvs.setPalletId(nodeId);
            activePelletVersions.add(apvs);
            int pelletsPerCore = 1;
            try {
                pelletsPerCore = parseInt(FloeRuntimeEnvironment.getEnvironment().
                        getSystemConfigParam(Constants.PELLETS_PER_CORE));
            } catch (Exception e) {

            }
            this.pelletCount = getResources().getNumberOfCores() * pelletsPerCore;


            if (FloeRuntimeEnvironment.getEnvironment().getSystemConfigParam(Constants.STATIC_PELLET_COUNT) != null) {
                this.pelletCount = Integer.parseInt(FloeRuntimeEnvironment.getEnvironment().
                        getSystemConfigParam(Constants.STATIC_PELLET_COUNT));
                System.out.println("PELLET Count : " + pelletCount);
            }
            this.flakeInfo.setPelletCount(pelletCount);
            // Create Flake Task. To Make Sure the Thread is not Running
            this.startFlag = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("*********************************** Flake id : " + getFlakeId() + " ********************************");
        return this.flakeInfo.getflakeId();
    }

    private PelletHandler createPelletHandler(Class pelletClass) {
        Class pelletInterface = null;
        for (Class pi : pelletClass.getInterfaces()) {
            if (pi.getName().endsWith("Pellet")) {
                pelletInterface = pi;
                break;
            }
        }
        if (pelletInterface == Pellet.class) {
            SingleInSingleOutPelletHandler ph = new SingleInSingleOutPelletHandler();
            ph.setCurrentFlake(this);
            return ph;
        } else if (pelletInterface == StreamInStreamOutPellet.class) {
            return new StreamInStreamOutPelletHandler();
        } else if (pelletInterface == TupleInTupleOutPellet.class) {
            return new TupleInTupleOutPelletHandler(getInputPortNames(), getOutputPortNames());
        } else if (pelletInterface == BSPPellet.class) {
            return new BSPPelletHandler();
        } else if (pelletInterface == MapperPellet.class) {
            return new MapperPelletHandler();
        } else if (pelletInterface == ReducerPellet.class) {
            return new ReducerPelletHandler();
        } else if (pelletInterface == SingleInStreamTupleOutPellet.class) {
            return new SingleInStreamTupleOutPelletHandler(getOutputPortNames());
        } else if (pelletInterface == StreamTupleInStreamTupleOutPellet.class) {
            return new StreamTupleInStreamTupleOutPelletHandler(getInputPortNames(), getOutputPortNames());
        } else {
            throw new UnsupportedOperationException("Unsupported pellet type : " + pelletClass.getName() + " which implements interface  "
                    + pelletInterface.getName());
        }
    }

    @Override
    public void wire(List<FlakeWiringInfo> inpConnectionList) {
        // Wiring To be established now for the Assosciated Channels
        System.out.println("Sink Fiber Establishment for " + this.flakeInfo.getflakeId());
        System.out.println("\tChannels at Port ");
        Map<String, Sender> tempSinkList = senders;
        Map<String, Receiver> tempSourceList = receivers;
        //this.successors = inpConnectionList;
        //?
        /*for (ConnectionInfoBase tempConnectionInfo : inpConnectionList) {
            tempConnectionInfo.setDestAddress(tempConnectionInfo.getSourceAddress());
            tempConnectionInfo.setOutPort(tempConnectionInfo.getInPortNo());

            if (tempConnectionInfo.getEdge().getchannelBehaviourType().contains("Push")) {

                Map<String, Object> params = new HashMap<String, Object>();
                params.put(ChannelFactory.KEY, tempConnectionInfo.getPortName());
                params.put(ChannelFactory.QUEUE, sinkQueue);
                params.put(ChannelFactory.SERVER_SIDE, "false");


                SenderChannel sender = new SenderChannelImpl(communicationManager);
                sender.init();
                sender.createChannel(tempConnectionInfo, params);
                sender.start();
                tempSinkList.put(tempConnectionInfo.getPortName() == null ?
                        "NULL" : tempConnectionInfo.getPortName(), sender);
                //TODO make this configurable

                sender.setSenderFiberStrategy(new SimpleRoundRobinSenderFiberStrategy());
                sender.setKey(tempConnectionInfo.getPortName());

            }
        }*/
        this.receivers = tempSourceList;
        this.senders = tempSinkList;
    }


    @Override
    public void wire(FlakeWiringInfo wiringInfo) {
        // TODO Auto-generated method stub
        System.out.println("Sink Fiber Establishment for " + this.flakeInfo.getflakeId());
        System.out.println("\tChannels at Port ");

        Port selfConnectionPort = wiringInfo.getSelfConnectionPort();
        Port otherEndConnectionPort = wiringInfo.getOtherEndConnectionPort();

        //IF we are on the Sink end of a PUSH channel.. do nothing.. 
        //IF we are on the Source end of a PULL channel.. do nothing.. 

        if ("PUSH".equalsIgnoreCase(selfConnectionPort.getDataTransferMode()) && otherEndConnectionPort.getTransportInfo() == null)
            return;

        if ("PULL".equalsIgnoreCase(selfConnectionPort.getDataTransferMode()) && selfConnectionPort.getTransportInfo() == null)
            return;


        if ("PUSH".equalsIgnoreCase(selfConnectionPort.getDataTransferMode()))
        //if PUSH then we are on the source end.. otherwise we would have returned
        {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(ChannelFactory.KEY, selfConnectionPort.getPortName());
            params.put(ChannelFactory.QUEUE, sinkQueue);
            params.put(ChannelFactory.SERVER_SIDE, "false");
            params.put(ChannelFactory.FIBER_STRATERGY, selfConnectionPort.getFiberStratergy());

            Sender sender = senders.get(selfConnectionPort.getPortName());
            if (sender == null) {
                sender = new SenderImpl();
                sender.init(params);
                SenderChannelStrategy channelStrategy = null;

                if (selfConnectionPort.getSendChannelStratergy() == null) {
                    channelStrategy = new DuplicateSenderChannelStrategy();
                } else {
                    try {
                        Class clzz = Class.forName(selfConnectionPort.getSendChannelStratergy());
                        channelStrategy = (SenderChannelStrategy) clzz.newInstance();
                    } catch (Exception e) {
                        channelStrategy = new DuplicateSenderChannelStrategy();
                    }
                }


                sender.setSenderStrategy(channelStrategy);
            }

            sender.setPort(selfConnectionPort);
            sender.connect(otherEndConnectionPort, otherEndConnectionPort.getFlakeId());
            //sender.startSenderChannel(cid);

            sender.setPort(selfConnectionPort);
            senders.put(selfConnectionPort.getPortName(), sender);
        }
        if ("PULL".equalsIgnoreCase(selfConnectionPort.getDataTransferMode())) {
            //TODO
        }
    }

    @Override
    public void updatePalletLogic(String palletType) {
        if (palletType.equals(this.pelletType)) {
            stopRunningPellets();

            this.pelletClass = loadClass(this.pelletType);

        } else {

            Class palletClass = loadClass(palletType);
            PelletHandler hander = createPelletHandler(palletClass);

            if (hander.getClass().getName().equals(this.pelletHandler.getClass().getName())) {
                stopRunningPellets();
                this.pelletClass = palletClass;
            } else {

                throw new UnsupportedOperationException("Updating to a Different pellet Type is not supported");
            }
        }

        lastUpdatedMode = SINGLE_UPDATE_MODE;

    }

    @Override
    public boolean markForUpdate(String palletType, String version) {

        ActivePelletVersions newVersion = new ActivePelletVersions();
        newVersion.setPelletType(palletType);
        newVersion.setVersion(version);
        activePelletVersions.add(newVersion);
        // remove old ones
        if (activePelletVersions.size() > 2) {
            activePelletVersions.remove(0);
        }
        lastUpdatedMode = MULTI_UPDATE_MODE;
        return false;
    }

    @Override
    public boolean isSingleton() {
        return this.flakeCreationInfo.isSingleton();
    }

    @Override
    public void startFlake() {
        if (this.startFlag == false) {
            // Initializing the Thread
            this.flakeThread = new Thread(this);
            // processMessage has to be executed in a seperate thread
            this.startFlag = true;
            this.flakeStatus = FlakeStatus.RUNNING;
            this.flakeInfo.setstatus("Processing");
            this.flakeThread.start();
            if (containerMonitor == null && "true".equalsIgnoreCase(FloeRuntimeEnvironment.
                    getEnvironment().getSystemConfigParam(Constants.ENABLE_MONITORING))) {
                containerMonitor = new ContainerMonitor(FloeRuntimeEnvironment.getEnvironment().getContainer(), this);
                containerMonitor.start();
            }
        }
    }

    public int getLastUpdatedMode() {
        return lastUpdatedMode;
    }


    public List<String> getOutputPortNames() {
        List<String> outputPts = new ArrayList<String>();
        if (this.outputPorts == null) {
            return outputPts;
        }
        for (Port port : this.outputPorts) {
            outputPts.add(port.getPortName());
        }
        return outputPts;
    }

    public List<String> getInputPortNames() {
        List<String> inputPts = new ArrayList<String>();
        if (this.inputPorts == null) {
            return inputPts;
        }
        for (Port port : this.inputPorts) {
            inputPts.add(port.getPortName());
        }
        return inputPts;
    }

    private void clearRunningTasks() {
        logger.info("Clearing running tasks..");
        for (PelletTask task : runningTasks) {
            task.join();
        }
        runningTasks.clear();
    }

    @Override
    public void run() {
//        ResourceInfo tempResourceInfo = new ResourceInfo(1);
//        this.flakeInfo.setresources(tempResourceInfo);
        logger.info("Setting number of cores to " + this.flakeInfo.getResourceInfo().getNumberOfCores());
        this.forkJoinPool = new ForkJoinPool(this.flakeInfo.getResourceInfo().getNumberOfCores());
//        this.flakeInfo.setPelletCount(1);
//        this.pelletCount = 1;
        exitThread:
        while (flakeStatus != FlakeStatus.STOP_INITIALIZED && flakeStatus != FlakeStatus.STOPPED) {
            // block and receive message
            pelletCountLock.lock();
            // sometimes waiting for multiple times is needed
            // as the notifications can be because of alterPelletCount
            // in which case the pelletCount can go up or down depending on the
            // caller.
            synchronized (availablePelletSlot) {
                // flakeStatus is paused or pause initliazed.. keep on waiting
                while (runningPelletCount >= pelletCount || flakeStatus == FlakeStatus.PAUSE_INITIALIZED || flakeStatus == FlakeStatus.PAUSED
                        || flakeStatus == FlakeStatus.STOP_INITIALIZED || flakeStatus == FlakeStatus.CORE_CHANGE_INITIALIZED) {
                    // update status if pause complete.. and continue to wait
                    if (flakeStatus == FlakeStatus.PAUSE_INITIALIZED && runningPelletCount == 0) {
                        clearRunningTasks();
                        flakeStatus = FlakeStatus.PAUSED;
                    }
                    // update status if stop complete.. and exit
                    if (flakeStatus == FlakeStatus.STOP_INITIALIZED && runningPelletCount == 0) {
                        clearRunningTasks();
                        flakeStatus = FlakeStatus.STOPPED;
                        break exitThread;
                    }
                    // core change initialized and running pellets stopped
                    // running...
                    if (flakeStatus == FlakeStatus.CORE_CHANGE_INITIALIZED && runningPelletCount == 0) {
                        logger.info("Core change complete.. Resetting fork join pool..");
                        clearRunningTasks();
                        resetForkJoinPool();
                        flakeStatus = FlakeStatus.RUNNING;
                        this.flakeInfo.setstatus("Processing");
                        // re-do the checks before going on to wait..
                        logger.info("Back after core change completion..");
                        continue;
                    }
                    try {
                        logger.info("Running pellet count " + runningPelletCount + " pelletCount: " + pelletCount);
                        availablePelletSlot.await();
                    } catch (InterruptedException e) {
                        logger.log(Level.INFO, "Interrupted exception while waiting for finding available free slot.. Exiting", e);
                        break exitThread;
                    }
                }
            }
            if (flakeThread.isInterrupted()) {
                logger.log(Level.INFO, "Interrupted exception while waiting for finding available free slot.. Exiting");
                break;
            }
            PelletTask freeSlotTask = null;
            for (PelletTask runningTask : runningTasks) {
                if (runningTask.getPelletStatus() == PelletTaskStatus.COMPLETED) {
                    freeSlotTask = runningTask;
                }
            }
            // Make sure the completed task finished completely.
            System.out.println("Joing till tasks are complete");
            if (freeSlotTask != null) {
                freeSlotTask.join();
            }

            System.out.println("Join Complete......");
            System.out.println("Creating a new Pallet Task......");
            PelletTask newTask = new PelletTask(pelletHandler, sourceQueue, sinkQueue, state.getCopy(), this, this.pelletClass);
            runningPelletCount++;
            runningTasks.add(newTask);
            this.forkJoinPool.submit(newTask);
            pelletCountLock.unlock();
        }
    }

    public void notifyPelletCompletion(Object pelletInstance) {
        pelletCountLock.lock();
        runningPelletCount--;
        if (pelletInstance instanceof FStateUpdator) {
            FStateUpdator updator = (FStateUpdator) pelletInstance;
            state.updateState(updator);
        }
        availablePelletSlot.signal();
        pelletCountLock.unlock();

    }

    private void resetForkJoinPool() {
        try {
            logger.info("Resetting fork join pool...");
            // make sure the forkJoinPoll shuts down
            logger.info("Shutting down fork join pool..");
            forkJoinPool.shutdown();

            if (forkJoinPool.isTerminated() == false) {
                while (forkJoinPool.awaitTermination(500, TimeUnit.MICROSECONDS) == false)
                    ;
            }
            logger.info("Old forkJoin pool terminated..");
            logger.info("Creating new forkJoin pool with number of cores " + alterResourceRequest.getNumberOfCores());
            forkJoinPool = new ForkJoinPool(this.alterResourceRequest.getNumberOfCores());
            this.flakeInfo.setresources(alterResourceRequest);
            alterResourceRequest = null;
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Exception while resetting forkJoinPool", e);
        }
    }

    @Override
    public void modifyResource(ResourceInfo inpResource) {
        logger.info("Input resouce getting changed by " + inpResource.getNumberOfCores());
        if (inpResource.getNumberOfCores() == 0) {
            // Nothing to do if no change.
            return;
        }
        synchronized (flakeStatus) {
            if (flakeStatus == FlakeStatus.PAUSE_INITIALIZED || flakeStatus == FlakeStatus.STOP_INITIALIZED
                    || flakeStatus == FlakeStatus.CORE_CHANGE_INITIALIZED) {
                throw new UnsupportedOperationException();
            }
        }
        logger.info("Setting alter request");
        if (alterResourceRequest == null) {
            logger.info("Alter Request " + this.alterResourceRequest + " INp Resource " + inpResource);
            this.alterResourceRequest = new ResourceInfo(inpResource.getNumberOfCores() +
                    flakeInfo.getResourceInfo().getNumberOfCores());
            FloeRuntimeEnvironment.getEnvironment().getContainer().getContainerInfo().incrementResources(inpResource);
            this.flakeInfo.getResourceInfo().setNumberOfCores(inpResource.getNumberOfCores() +
                    flakeInfo.getResourceInfo().getNumberOfCores());
        } else {
            synchronized (alterResourceRequest) {
                logger.info("Alter Request " + this.alterResourceRequest + " INp Resource " + inpResource);
                this.alterResourceRequest = new ResourceInfo(inpResource.getNumberOfCores() +
                        flakeInfo.getResourceInfo().getNumberOfCores());
                FloeRuntimeEnvironment.getEnvironment().getContainer().getContainerInfo().incrementResources(inpResource);
                this.flakeInfo.getResourceInfo().setNumberOfCores(alterResourceRequest.getNumberOfCores());
            }
        }
        synchronized (flakeStatus) {
            if (flakeStatus == FlakeStatus.PAUSED || flakeStatus == FlakeStatus.CREATED ||

                    flakeStatus == FlakeStatus.INITIALIZED
                    || flakeStatus == FlakeStatus.STOPPED) {
                resetForkJoinPool();
                return;
            }
            logger.info("Initializing core change");
            flakeStatus = FlakeStatus.CORE_CHANGE_INITIALIZED;
            flakeInfo.setstatus("Resource being modified");
        }
        this.stopRunningPellets();
    }

    @Override
    public synchronized void modifyPelletCount(int inpCount) {
        pelletCountLock.lock();
        System.out.println("*********************Update count" + inpCount + " **************************");

        int pelletsPerCore = 1;
        try {
            pelletsPerCore = parseInt(FloeRuntimeEnvironment.getEnvironment().
                    getSystemConfigParam(Constants.PELLETS_PER_CORE));
        } catch (Exception e) {

        }
        if (inpCount > this.getResources().getNumberOfCores() * pelletsPerCore) {
            inpCount = this.getResources().getNumberOfCores() * pelletsPerCore;
        }
        pelletCount = inpCount;
        flakeInfo.setPelletCount(pelletCount);
        availablePelletSlot.signal();
        pelletCountLock.unlock();
    }

    @Override
    public void stopFlake() {
        synchronized (flakeStatus) {
            if (flakeStatus == FlakeStatus.STOPPED) {
                // nothing to do
                return;
            }
            if (containerMonitor != null) {
                containerMonitor.stop();
            }
            this.stopReceivers();
            int count = 0;
            while (sinkQueue.getSize() != 0 && count < 10) {
                try {
                    Thread.sleep(5000);
                    count++;
                } catch (InterruptedException e) {

                }
            }
            this.stopSenders();

            if (flakeStatus == FlakeStatus.PAUSE_INITIALIZED) {
                flakeStatus = FlakeStatus.STOP_INITIALIZED;
                this.flakeInfo.setstatus("StopInitialized");
                return;
            }

            if (flakeStatus == FlakeStatus.PAUSED) {

                flakeThread.interrupt();
            }
            if (flakeStatus == FlakeStatus.CREATED || flakeStatus == FlakeStatus.INITIALIZED) {
                // nothing to do..
                this.flakeInfo.setstatus("Stop");
                flakeStatus = FlakeStatus.STOPPED;
                return;
            }
            flakeStatus = FlakeStatus.STOP_INITIALIZED;
            this.flakeInfo.setstatus("StopInitialized");
        }

    }

    public void stopReceivers() {
        communicationManager.disconnectPredecessors(this.flakeInfo);
        for (Receiver receiver : receivers.values()) {
            receiver.stop();
        }
        // **** To decide whether to clear the Buffer or Not *****
    }

    public void stopSenders() {
        for (Sender sender : senders.values()) {
            sender.stop();
        }
    }


    private void stopRunningPellets() {
        logger.info("Stopping runnig pellets by setting landmarks");
        synchronized (runningTasks) {
            for (PelletTask pelletTask : runningTasks) {
                pelletTask.sendPauseLandmark();
            }
        }
    }

    @Override
    public void pauseFlake() {
        System.out.println("Pause flake for " + flakeInfo.getflakeId() + " called");
        synchronized (flakeStatus) {
            if (flakeStatus == FlakeStatus.STOPPED) {
                throw new UnsupportedOperationException("Cannot pause a stopped flake");
            }
            if (flakeStatus == FlakeStatus.CORE_CHANGE_INITIALIZED) {
                throw new UnsupportedOperationException("Cannot pause a while resource change initialized");
            }
            if (flakeStatus == FlakeStatus.PAUSE_INITIALIZED || flakeStatus == FlakeStatus.PAUSED) {
                // nothing to do..
                return;
            }
            if (flakeStatus == FlakeStatus.CREATED || flakeStatus == FlakeStatus.INITIALIZED) {
                // just updated the status
                flakeStatus = FlakeStatus.PAUSED;
                this.flakeInfo.setstatus("Paused");
                return;
            }

            flakeStatus = FlakeStatus.PAUSE_INITIALIZED;
            this.flakeInfo.setstatus("PauseInitilaized");
        }
        stopRunningPellets();
    }

    @Override
    public void resumeFlake() {
        if (flakeStatus != FlakeStatus.PAUSED) {
            throw new UnsupportedOperationException("Can resume only if paused...");
        }
        // If the current thread is not alive make it running and
        // set the Appropriate Status.
        if (!this.flakeThread.isAlive()) {
            throw new UnsupportedOperationException("Cannot resume a stopped thread.. Use start");
        }
        // reload class
        pelletClass = loadClass(this.pelletType);
        synchronized (flakeStatus) {
            flakeStatus = FlakeStatus.RUNNING;
            flakeInfo.setstatus("Processing");
        }
        pelletCountLock.lock();
        availablePelletSlot.signal();
        pelletCountLock.unlock();
    }

    @Override
    public String getPelletType() {
        return this.flakeInfo.getpelletType();
    }

    @Override
    public String getFlakeId() {
        return this.flakeInfo.getflakeId();
    }

    @Override
    public ResourceInfo getResources() {
        return this.flakeInfo.getResourceInfo();
    }

    @Override
    public Map<String, Receiver> getReceivers() {
        return receivers;
    }

    @Override
    public Map<String, Sender> getSenders() {
        return senders;
    }

    private void updateAverageProcessingLatency(String tag, Long inpTimeStamp, Long outTimeStamp) {
        Long latency = (long) -1;
        if (inpTimeStamp != null) {
            latency = outTimeStamp - inpTimeStamp;
        }

        if (averagePelletProcessingLatency == -1) {
            averagePelletProcessingLatency = latency;
        } else {

            averagePelletProcessingLatency = (averagePelletProcessingLatency + latency) / 2.0;

        }

    }

    public static Class loadClass(String className) {
        try {


            logger.info("Loading Pallet Class " + className);
            File jarDir = new File(PalletJarDeployer.FILE_DIR_PATH);
            Class clazz = null;
            if (jarDir.isDirectory()) {

                File[] jars = jarDir.listFiles();

                for (File jar : jars) {
                    List<String> classes = PalletJarUtils.getListOfClasses(jar);
                    if (classes.contains(className)) {
                        URL jarFileUrl = new URL("file:" + jar.getAbsolutePath());
                        ClassLoader classLoader = new URLClassLoader(new URL[]{jarFileUrl});
                        clazz = classLoader.loadClass(className);
                        return clazz;
                    }
                }
            } else {
                throw new RuntimeException("Error while Reading from Pallet Jar location :" +
                        PalletJarDeployer.FILE_DIR_PATH);
            }

            if (clazz == null) {
                clazz = Class.forName(className);
            }
            return clazz;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception at class loading", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void handleMessageEvent(MessageEvent e) {

        if (e.getMessageEventType() == MessageEventType.Incoming) {
            flakeInfo.handleIncomingMessage();
        } else {
            flakeInfo.handleOutgoingMessage();
        }
    }

    @Override
    public FlakeCreationInfo getFlakeCreationInfo() {
        return this.flakeCreationInfo;
    }

    public ControlChannelCommunicationManager getCommunicationManager() {
        return communicationManager;
    }

    @Override
    public String getNodeId() {
        return this.nodeId;
    }

    public List<ActivePelletVersions> getActivePelletVersions() {
        return activePelletVersions;
    }

    public List<PalletVersionInfo> getPalletVersionInfos() {
        return palletVersionInfos;
    }

    public void setPalletVersionInfos(List<PalletVersionInfo> palletVersionInfos) {
        this.palletVersionInfos = palletVersionInfos;
    }

    public Class getPelletClass() {
        return pelletClass;
    }

    public ContainerMonitor getContainerMonitor() {
        return containerMonitor;
    }
}
