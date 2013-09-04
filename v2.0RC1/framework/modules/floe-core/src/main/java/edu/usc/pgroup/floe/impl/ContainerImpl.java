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


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import edu.usc.pgroup.floe.api.communication.Fiber;
import edu.usc.pgroup.floe.api.communication.Sender;
import edu.usc.pgroup.floe.api.communication.SenderChannel;
import edu.usc.pgroup.floe.api.communication.TransportInfoBase;
import edu.usc.pgroup.floe.api.framework.*;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;
import edu.usc.pgroup.floe.api.framework.manager.resourceManager.AcquireContainerRequest;
import edu.usc.pgroup.floe.api.framework.manager.resourceManager.AcquireContainerResponse;
import edu.usc.pgroup.floe.api.framework.rest.FlakeCreationInfo;
import edu.usc.pgroup.floe.api.framework.rest.FlakeWiringInfo;
import edu.usc.pgroup.floe.api.framework.rest.RestNeighborList;
import edu.usc.pgroup.floe.api.framework.healthmanager.HealthEventManager;
import edu.usc.pgroup.floe.healthmanager.LocalHealthEventListener;
import edu.usc.pgroup.floe.healthmanager.LocalHealthEventManager;
import edu.usc.pgroup.floe.impl.rest.RestService;
import edu.usc.pgroup.floe.util.Constants;
import edu.usc.pgroup.floe.util.NetworkUtil;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

public class ContainerImpl implements Container {

    private static Logger logger = Logger.getLogger(ContainerImpl.class.getName());

    public static int CONTAINER_REST_SERVICE_PORT = 45002;
    public static String CONTAINER_REST_SERVICE_PKG = "edu.usc.pgroup.floe.impl.rest.container";


    ContainerInfo containerInfo;
    List<Flake> flakeList;


    public static URI BASE_URI = NetworkUtil.getBaseURI(CONTAINER_REST_SERVICE_PORT);
    public RestService restService;

    private String managerHost;

    private int managerPort;

    private String coordinatorHost;

    private int coordinatorPort;

    private List<NeighborInfo> predecessors;

    private List<NeighborInfo> successors;

    private Map<String,FlakeInfo> palletTypeToFlakeIdMap = new HashMap<>();



    private HealthEventManager healthEventManager = new LocalHealthEventManager();

    ContainerImpl() {

        try {
            this.flakeList = new ArrayList<Flake>();
            String containerID = "Container@" + NetworkUtil.getHostAddress();

            ResourceInfo available = new ResourceInfo();
            ResourceInfo allocated = new ResourceInfo();
            //TODO check this
            available.setNumberOfCores(Runtime.getRuntime().availableProcessors()*3);
            //available.setNumCores(2);
            allocated.setNumberOfCores(0);
            this.containerInfo = new ContainerInfo(containerID, available, allocated);
            if (restService == null) {
                restService = new RestService(CONTAINER_REST_SERVICE_PKG, NetworkUtil.getBaseURI(CONTAINER_REST_SERVICE_PORT));
            }

            healthEventManager.registerListener(new LocalHealthEventListener());

            restService.start();
        } catch (IOException e) {
            String msg = "Unexpected Error while starting container";
            logger.severe("Unexpected Error while starting container");
            throw new RuntimeException(msg, e);
        }
    }

    /*@Override
    public FlakeInfo createFlake(String inpFlakeID, String pelletType, ResourceInfo resources, List<Edge> inpChannel, List<Edge> outChannel, List<Port> inpPorts, List<Port> outPorts) {
        synchronized (this) {
            Flake flake = new FlakeImpl();
            if(this.containerInfo.incrementResources(resources)) {
                flake.create(inpFlakeID,pelletType,resources,inpChannel,outChannel,inpPorts,outPorts);
                this.flakeList.add(flake);
                return flake.getFlakeInfo();
            }
        }
        return null;
    }*/

    @Override
    public FlakeInfo createFlake(FlakeCreationInfo flakeCreationInfo) {
        synchronized (this) {
            Flake flake = new FlakeImpl();
            if (this.containerInfo.incrementResources(flakeCreationInfo.getResource())) {
                flake.create(flakeCreationInfo);
                if(flakeCreationInfo.isScalingOut()) {
                    flake.getFlakeInfo().setScaledOut(true);
                }
                this.flakeList.add(flake);
                return flake.getFlakeInfo();
            }
        }
        return null;
    }

    @Override
    public void startFlake(String flakeID) {
        for (Flake flake : flakeList) {
            if (flake.getFlakeId().matches(flakeID)) {
                flake.startFlake();
            }
        }
    }

    @Override
    public HealthEventManager getHealthEventManager() {
        return healthEventManager;
    }

    @Override
    public String updateFlake(String flakeId, String palletType) {
        for (Flake flake : flakeList) {
            String fId = flake.getFlakeId().split("@")[2];
            if (flakeId.matches(fId)) {
                flake.updatePalletLogic(palletType);
            }
        }

        return flakeId;
    }



    @Override
    public void stopFlake(String flakeID) {
        for (Flake flake : flakeList) {
            if (flake.getFlakeId().matches(flakeID)) {
                flake.stopFlake();
                this.containerInfo.decerementResouces(flake.getResources());
            }
        }
    }

    @Override
    public void pauseFlake(String flakeID) {
        for (Flake flake : flakeList) {
            if (flake.getFlakeId().matches(flakeID)) {
                flake.pauseFlake();
            }
        }
    }

    @Override
    public void resumeFlake(String flakeID) {
        for (Flake flake : flakeList) {
            if (flake.getFlakeId().matches(flakeID)) {
                flake.resumeFlake();
            }
        }
    }

    @Override
    public void wireFlake(String flakeId, List<FlakeWiringInfo> wiringInfoList) {
        synchronized (this) {
            for (Flake flake : flakeList) {
                if (flake.getFlakeId().matches(flakeId)) {
                    flake.wire(wiringInfoList);
                }
            }
        }
    }


    @Override
    public void wireFlake(String flakeID, FlakeWiringInfo wiringInfo) {
        synchronized (this) {
            for (Flake flake : flakeList) {
                if (flake.getFlakeId().matches(flakeID)) {
                    flake.wire(wiringInfo);
                }
            }
        }
    }

    @Override
    public List<FlakeInfo> listFlakes() {
        List<FlakeInfo> flakeInfoList = new ArrayList<FlakeInfo>();
        for (Flake flake : flakeList) {
            flakeInfoList.add(flake.getFlakeInfo());
        }
        return flakeInfoList;
    }

    @Override
    public ContainerInfo getContainerInfo() {
        return containerInfo;
    }

    @Override
    public boolean updateFlakeResources(String flakeID, ResourceInfo resourceInfo) {
        try {
            Iterator<Flake> flakeIter = this.flakeList.iterator();
            Flake tempFlake = null;
            while (flakeIter.hasNext()) {
                tempFlake = flakeIter.next();
                if (tempFlake.getFlakeId().matches(flakeID)) {
                    break;
                } else
                    tempFlake = null;
            }
            if (tempFlake != null) {
                int aggregateCount = tempFlake.getResources().getNumberOfCores() + resourceInfo.getNumberOfCores();
                int totalCoresAllocated = 0;
                synchronized (containerInfo) {
                    int coreAvailable = this.containerInfo.getavailableResources().getNumberOfCores() +
                            this.containerInfo.getactiveResources().getNumberOfCores();
                    flakeIter = this.flakeList.iterator();
                    Flake tempFlakeNew = null;
                    while (flakeIter.hasNext()) {
                        tempFlakeNew = flakeIter.next();
                        totalCoresAllocated += tempFlakeNew.getResources().getNumberOfCores();
                    }
                    if (aggregateCount > 0 && aggregateCount <= coreAvailable) {
                        tempFlake.modifyResource(resourceInfo);
                    } else {

                        if (aggregateCount > coreAvailable) {

                            int c = coreAvailable - totalCoresAllocated;
                            if (c > 0 && tempFlake.getResources().getNumberOfCores() != c) {
                                resourceInfo.setNumberOfCores(c);
                                tempFlake.modifyResource(resourceInfo);
                            }
                            return false;
                        }
                        System.out.println("Request Violates the Resource Allocation Specifications");
                        edu.usc.pgroup.floe.util.Logger.getInstance().LogInfo("Core Allocation Error aggregate count : " +
                                aggregateCount + " core avail" + coreAvailable);
                        return false;
                    }
                }
            }
        }catch (UnsupportedOperationException e) {
                        //ignore
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
        return true;
    }

    @Override
    public boolean updatePalletCount(String flakeId, int palletCount) {
        try {
            int tempCount = 0;
            Iterator<Flake> flakeIter = this.flakeList.iterator();
            Flake tempFlake = null;
            while (flakeIter.hasNext()) {
                tempFlake = flakeIter.next();
                if (tempFlake.getFlakeId().matches(flakeId)) {
                    break;
                } else
                    tempFlake = null;
                tempCount++;
            }
            if (tempFlake != null) {
                int aggregateCount = tempFlake.getFlakeInfo().getPelletCount() + palletCount;
                if ((aggregateCount > 0)) {
                    tempFlake.modifyPelletCount(palletCount);
                    this.flakeList.set(tempCount, tempFlake);
                } else {
                	//YS: Is this case ever reached? Looks like only a lower bounds check (0) is done, not an upper bound check (coreCount*6)
                    System.out.println("Pellet Count Not Modified. Violates Resource Allocation Conditions.");
                    edu.usc.pgroup.floe.util.Logger.getInstance().LogInfo("Update False  aggrigate count :" +
                            aggregateCount + " Res : " + tempFlake.getResources().getNumberOfCores() * 6);
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            edu.usc.pgroup.floe.util.Logger.getInstance().LogInfo("Exception close " + ex.getMessage());

            return false;
        }
        return true;
    }


    @Override
    public boolean scaleIn(String flakeId) {

        int tempCount = 0;
        Iterator<Flake> flakeIter = this.flakeList.iterator();
        Flake tempFlake = null;
        while (flakeIter.hasNext()) {
            tempFlake = flakeIter.next();
            if (tempFlake.getFlakeId().matches(flakeId)) {
                break;
            } else
                tempFlake = null;
            tempCount++;
        }



        if(tempFlake != null) {
            tempFlake.stopFlake();
            ResourceInfo resourceInfo = tempFlake.getResources();
            containerInfo.decerementResouces(resourceInfo);

            DefaultClientConfig config = new DefaultClientConfig();
            config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                    true);
            config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

            Client client = Client.create(config);


            //step 1 get resource
            //todo: move to constants class.
            WebResource reqResourceWebRes = client.resource("http://" + FloeRuntimeEnvironment.getEnvironment().
                    getSystemConfigParam(Constants.COORDINATOR_HOST) + ":" +
                    FloeRuntimeEnvironment.getEnvironment().getSystemConfigParam(Constants.COORDINATOR_PORT) +
                    "/Coordinator/updateContainerInfo");
            client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
            return true;
        }   else {
            return false;
        }


    }

    @Override
    public boolean scaleOut(String flakeId, ResourceInfo resource) {
        try {
            int tempCount = 0;
            Iterator<Flake> flakeIter = this.flakeList.iterator();
            Flake tempFlake = null;
            while (flakeIter.hasNext()) {
                tempFlake = flakeIter.next();
                if (tempFlake.getFlakeId().matches(flakeId)) {
                    break;
                } else
                    tempFlake = null;
                tempCount++;
            }
            if (tempFlake != null) {

                if(tempFlake.isSingleton()) {
                    return false;
                }
                //send request to the coordinator..

                List<String> nodeStrList = new ArrayList<String>();
                List<ResourceInfo> resourceList = new ArrayList<ResourceInfo>();


                nodeStrList.add(tempFlake.getNodeId()); //note: nodeId NOT equals flakeId
                resourceList.add(resource);

                AcquireContainerRequest containerRequest = AcquireContainerRequest.createRequest();
                containerRequest.setRequestedResource(resource);

                //ask the coordinator for the resource.
                DefaultClientConfig config = new DefaultClientConfig();
                config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                        true);
                config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

                Client client = Client.create(config);


                //step 1 get resource
                //todo: move to constants class.
                WebResource reqResourceWebRes = client.resource("http://" + FloeRuntimeEnvironment.getEnvironment().getSystemConfigParam(Constants.COORDINATOR_HOST) + ":" + FloeRuntimeEnvironment.getEnvironment().getSystemConfigParam(Constants.COORDINATOR_PORT) + "/Coordinator/requestResources");
                client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
                ClientResponse reqResourceResponse = reqResourceWebRes.post(ClientResponse.class, containerRequest);

                AcquireContainerResponse tempContainerResourceInfo = reqResourceResponse.getEntity(AcquireContainerResponse.class);
                ContainerInfo cInfo = tempContainerResourceInfo.getContainerInfo();

                //todo: check null..

                String containerID = cInfo.getContainerId();

                //step 2.. create flake..
                //create a flake in the new container..

                String containerIP = cInfo.getContainerHost();
                WebResource flakeCreationWebRes = client.resource("http://" + containerIP + ":45002/Container/createFlake");
                client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
                tempFlake.getFlakeCreationInfo().setScalingOut(false);
                tempFlake.getFlakeCreationInfo().setResource(resource);
                ClientResponse flakeCreationResponse = flakeCreationWebRes.post(ClientResponse.class, tempFlake.getFlakeCreationInfo());
                tempFlake.getFlakeInfo().setScaledOut(true);


                FlakeInfo newFlakeInfo = flakeCreationResponse.getEntity(FlakeInfo.class);


                //step 3.. leg one of communication (use wire flake function on appropriate end based on push or pull mode)
                //step 4.. leg two of communication (use control channel on the other end)

                //FOr senders on the new flake... but iterate though the parents to get the relvant information.
                List<TransportInfoBase> senderConnections = new ArrayList<TransportInfoBase>();
                for (Sender sender : tempFlake.getSenders().values()) {
                    //NOTE: senderChannel is associated with port. We have same channel behavirour and tranport type for all channels for a particular senderChannel.
                    Port selfConnectionPort = sender.getPort();
                    if ("push".equalsIgnoreCase(selfConnectionPort.getDataTransferMode())) {
                        for (SenderChannel senderChannel : sender.getSenderChannels().values())
                            for (Fiber fiber : senderChannel.getFibers().values()) {
                                Port otherEndConnectionPort = fiber.getOtherEndConnectionPort();

                                FlakeWiringInfo flakeWiringInfo = new FlakeWiringInfo();
                                flakeWiringInfo.setFlakeID(newFlakeInfo.getflakeId());

                                flakeWiringInfo.setSelfConnectionPort(selfConnectionPort);
                                flakeWiringInfo.setOtherEndConnectionPort(otherEndConnectionPort);

                                WebResource wireFlakeResource = client.resource("http://" + cInfo.getContainerHost() + ":45002/Container/wireFlake");
                                client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);

                                /*restFlakeWiringInfo.setConnectionList(senderConnections);*/ //TODO: fix this
                                ClientResponse wireFlakeResponse = wireFlakeResource.put(ClientResponse.class, flakeWiringInfo);
                            }
                    }
                }


                /*List<ConnectionInfo> receiverConnections = new ArrayList<ConnectionInfo>();
                for(Receiver reciever: tempFlake.getReceivers().values())
                {
                    for(Fiber channel: reciever.getFibers().values())
                    {
                        if("Push".equals(channel.getConnectionInfo().getEdge().getchannelBehaviourType()))
                        {
                            receiverConnections.add(channel.getConnectionInfo());
                        }
                        else
                        {
                            //todo: use wire flake here.. later..
                        }
                    }
                }*/

                //TODO: Assumes its push channel only.. update it later to account for pull channels as well. 
                ((FlakeImpl) tempFlake).getCommunicationManager().updatePredecessors(newFlakeInfo);



                //Step 5
                //start the NEW flake..                
                String tempFlakeID = newFlakeInfo.getflakeId();
                WebResource startFlakeResource = client.resource("http://" + tempFlakeID.split("@")[1] + ":45002/Container/startFlake/FlakeID=" + tempFlakeID);
                client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
                ClientResponse startFlake = startFlakeResource.put(ClientResponse.class);

                //TODO: DO some error checking here.. to see if the error has been resolved..

            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void setManager(String managerHost, int managerPort) {
        // TODO Auto-generated method stub
        this.managerHost = managerHost;
        this.managerPort = managerPort;
    }

    public void setCoordinator(String coordinatorHost, int coordinatorPort) {
        // TODO Auto-generated method stub
        this.coordinatorHost = coordinatorHost;
        this.coordinatorPort = coordinatorPort;
    }

    @Override
    public boolean setListOfPredececessors(String flakeID,
                                           RestNeighborList neighbours) {
        predecessors = neighbours.getNodeList();
        return false;
    }

    @Override
    public boolean setListOfSuccessors(String flakeID,
                                       RestNeighborList neighbours) {
        successors = neighbours.getNodeList();
        return false;
    }

    public boolean markForUpdate(String flakeId, String palletType, String version) {
        String id = flakeId.split("@")[2];
        for (Flake flake : flakeList) {
            String fId = flake.getNodeId();
            if (id.equals(fId)) {
                return flake.markForUpdate(palletType, version);
            }
        }

        return false;
    }

    public boolean updateSourcePellet(String flakeId, List<PalletVersionInfo> versionInfos) {

        for (Flake flake : flakeList) {
            String fId = flake.getFlakeId().split("@")[2];
            if (flakeId.matches(fId)) {
                ((FlakeImpl) flake).setPalletVersionInfos(versionInfos);
                return true;
            }
        }

        return false;
    }

    public List<Flake> getFlakeList() {
        return flakeList;
    }

    public Map<String, FlakeInfo> getPalletTypeToFlakeIdMap() {
        return palletTypeToFlakeIdMap;
    }


}
