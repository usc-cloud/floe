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
 * This is the Coordinator Implementation  
 *
 * @author Sreedhar Natarajan (sreedhan@usc.edu)
 * @author Yogesh Simmhan (simmhan@usc.edu)
 * @version v0.1, 2012-01-27
 *
 */

import java.net.URI;
import java.util.*;
import java.util.Map.Entry;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;

import com.sun.jersey.api.client.config.DefaultClientConfig;
import edu.usc.pgroup.floe.api.communication.Fiber;
import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.communication.TransportInfoBase;
import edu.usc.pgroup.floe.api.communication.Receiver;
import edu.usc.pgroup.floe.api.framework.*;
import edu.usc.pgroup.floe.api.framework.floegraph.Edge;
import edu.usc.pgroup.floe.api.framework.floegraph.Edge.Sink;
import edu.usc.pgroup.floe.api.framework.floegraph.Edge.Source;
import edu.usc.pgroup.floe.api.framework.floegraph.FloeGraph;
import edu.usc.pgroup.floe.api.framework.floegraph.Node;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;
import edu.usc.pgroup.floe.api.framework.manager.resourceManager.AcquireContainerRequest;
import edu.usc.pgroup.floe.api.framework.manager.resourceManager.AcquireContainerResponse;
import edu.usc.pgroup.floe.api.framework.rest.FlakeCreationInfo;
import edu.usc.pgroup.floe.api.framework.rest.FlakeWiringInfo;
import edu.usc.pgroup.floe.impl.communication.ChannelFactory;
import edu.usc.pgroup.floe.impl.communication.ReceiverImpl;
import edu.usc.pgroup.floe.impl.queues.SourceQueue;
import edu.usc.pgroup.floe.impl.rest.RestService;
import edu.usc.pgroup.floe.util.NetworkUtil;

public class CoordinatorImpl implements Coordinator {
    static int floeNo = 0;
    List<FloeInfo> floeList;
    List<FlakeInfo> flakes = new ArrayList<FlakeInfo>();
    List<FlakeInfo> sourceFlakes = new ArrayList<FlakeInfo>();
    private FloeGraph floeGraph;
    Map<String, FlakeInfo> nodeToFlakeMap = new HashMap<String, FlakeInfo>();

    static CoordinatorImpl coordinator;

    public RestService restService;
    public static int COORDINATOR_REST_SERVICE_PORT = 45000;
    public static String COORDINATOR_REST_SERVICE_PKG = "edu.usc.pgroup.floe.impl.rest.coordinator";
    public static URI BASE_URI = NetworkUtil.getBaseURI(COORDINATOR_REST_SERVICE_PORT);

    private Receiver receiver = null;
    private Port coordinatorPort = null;

    private TreeMap<String, AcquireContainerResponse> allContainers = new TreeMap<String, AcquireContainerResponse>();

    CoordinatorImpl() {
        if (restService == null) {
            restService = new RestService(COORDINATOR_REST_SERVICE_PKG, BASE_URI);
            this.floeList = new ArrayList<FloeInfo>();
        }

        restService.start();
    }

    public static synchronized CoordinatorImpl getCoordinator() {
        if (coordinator != null) {
            return coordinator;

        } else {
            coordinator = new CoordinatorImpl();
            return coordinator;
        }
        // Send an Acknowledgment to the Manager
    }

    public ContainerInfo requestContainer(ResourceInfo resourceInfo) {

        // Issue Commands to the Manager to Create/ Retrieve Containers for the
        // Flakes
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                true);
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

        Client client = Client.create(config);
        WebResource webRes;
        ClientResponse response;


        AcquireContainerRequest resourceRequest = AcquireContainerRequest.createRequest();
        resourceRequest.setRequestedResource(resourceInfo);

        //TODO: Enable this: 
        //resourceRequest.setOptionalInfo(resourceInfo.getOptionalConfiguration());


        webRes = client.resource("http://localhost:45001/Manager/allocateContainer");
        client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        response = webRes.post(ClientResponse.class, resourceRequest);
        AcquireContainerResponse acquireContainerResponse = response.getEntity(AcquireContainerResponse.class);

        allContainers.put(acquireContainerResponse.getContainerId(), acquireContainerResponse);

        ContainerInfo allocatedContainer = acquireContainerResponse.getContainerInfo();

        return allocatedContainer;
    }

    public void updateContainerInfo(ContainerInfo containerInfo) {

        //TODO Alok

        // After scale down happened container will send its updated container information to the container.
        // Based on this information Coordinator must talk with Manager and decide what to do with the machine.

        System.out.println("***********UPDATED Container info**************");
        System.out.println("Container id : " + containerInfo.getContainerId());
        System.out.println("Avaiable cores : " + containerInfo.getactiveResources().getNumberOfCores());

    }


    private HashMap<String, List<TransportInfoBase>> findSourceNodeTransportList(FloeGraph floeGraph, Map<String, FlakeInfo> nodeToFlakeMap) {
        HashMap<String, List<TransportInfoBase>> sourceNodeTransportMap = new HashMap<String, List<TransportInfoBase>>();
//         if(floeGraph.getEdges() == null || floeGraph.getEdges().getEdges() == null) {
//             return sourceNodeTransportMap;
//         }
//
//        if(floeGraph.getNodes() == null || floeGraph.getNodes().getNodes() == null) {
//            return sourceNodeTransportMap;
//        }
        List<Edge> edges;
        if(floeGraph.getEdges()== null || floeGraph.getEdges().getEdges() == null)
            edges =  new ArrayList<Edge>();
        else
            edges =floeGraph.getEdges().getEdges();


        List<Node> nodes = floeGraph.getNodes().getNodes();
        if(nodes == null) {
            return sourceNodeTransportMap;
        }

        List<String> nodeIds = new ArrayList<String>();

        for (Node n : nodes) {
            nodeIds.add(n.getNodeId());
        }

        for (Edge e : edges) {
            String nodeId = e.getSink().getNodeId();
            nodeIds.remove(nodeId);
        }

        for (String nId : nodeIds) {

            for (Node node : nodes) {
                if (node.getNodeId().equals(nId)) {

                    FlakeInfo info = nodeToFlakeMap.get(node.getNodeId());
                    sourceFlakes.add(info);

                    List<TransportInfoBase> sourcePortTransport = new ArrayList<TransportInfoBase>();

                    for (Port p : info.getInputPorts()) {
                        System.out.println("Source Port " + p.getPortName() + "Connection Info");
                        p.getTransportInfo().printConnectionInfoDetails();

                        sourcePortTransport.add(p.getTransportInfo());
                    }
                    if (sourcePortTransport.size() > 0) {
                        sourceNodeTransportMap.put(node.getNodeId(), sourcePortTransport);
                    }
                }
            }

        }

        return sourceNodeTransportMap;
    }

    private List<Node> findSourceNodeList(FloeGraph floeGraph) {

        List<Node> sourceNodeList = new ArrayList<Node>();

        List<Edge> edges = floeGraph.getEdges().getEdges();
        List<Node> nodes = floeGraph.getNodes().getNodes();
        if(nodes == null) {
            nodes = new ArrayList<Node>();
        }

        if(edges == null) {
            edges = new ArrayList<Edge>();
        }

        List<String> nodeIds = new ArrayList<String>();

        for (Node n : nodes) {
            nodeIds.add(n.getNodeId());
        }

        for (Edge e : edges) {
            String nodeId = e.getSink().getNodeId();
            nodeIds.remove(nodeId);
        }

        for (String nId : nodeIds) {

            for (Node node : nodes) {
                if (node.getNodeId().equals(nId)) {
                    sourceNodeList.add(node);
                }
            }

        }

        return sourceNodeList;
    }


    private List<Node> findSinkNodeList(FloeGraph floeGraph) {
        List<Node> sinkNodeList = new ArrayList<Node>();

        List<Edge> edges = floeGraph.getEdges().getEdges();
        List<Node> nodes = floeGraph.getNodes().getNodes();
        if(nodes == null) {
            nodes = new ArrayList<Node>();
        }

        if(edges == null) {
            edges = new ArrayList<Edge>();
        }

        List<String> nodeIds = new ArrayList<String>();

        for (Node n : nodes) {
            nodeIds.add(n.getNodeId());
        }

        for (Edge e : edges) {
            String nodeId = e.getSource().getNodeId();
            nodeIds.remove(nodeId);
        }

        for (String nId : nodeIds) {

            for (Node node : nodes) {
                if (node.getNodeId().equals(nId)) {
                    sinkNodeList.add(node);
                }
            }

        }

        return sinkNodeList;
    }

    @Override
    public StartFloeInfo createFloe(FloeGraph graph) {


        //Create Flakes
        //Naive Logic.. TODO: Make this plugable... So that we can implement an intelligent pellet collocation logic.
        //For each pellet node in the graph, request container from the resource manager and "create flake"..

        StartFloeInfo startFloeInfo = new StartFloeInfo();
        this.floeGraph = graph;
        startFloeInfo.setFloeID(UUID.randomUUID().toString());

        List<Node> nodeList = graph.getNodes().getNodes();

        for (Node pelletNode : nodeList) {
            ResourceInfo requiredResources = pelletNode.getResource();

            ContainerInfo allocatedContainer = requestContainer(requiredResources);

            if (allocatedContainer == null) {
                //TODO: Clean up already allocated resources for this application
                return null; //or throw exception?
            }


            //For each input and output port..
            //set the TransportInfo which includes "TransportType"="TCP/HTTP" etc and dataTransferMethod="PUSH/PULL"
            //Update the PelletNode itself..
            boolean success = setPortTransportInfo(pelletNode, graph);

            if (!success) {
                //TODO: Clean up already allocated resources for this application
                return null; //or throw exception?
            }

            FlakeCreationInfo flakeCreationInfo = new FlakeCreationInfo();
            flakeCreationInfo.setNodeID(pelletNode.getNodeId());
            flakeCreationInfo.setPellet(pelletNode.getPelletType());
            flakeCreationInfo.setInputPorts(pelletNode.getInPorts().getPorts());
            flakeCreationInfo.setOutputPorts(pelletNode.getOutPorts().getPorts());
            flakeCreationInfo.setResource(requiredResources);
            flakeCreationInfo.setSingleton(pelletNode.isSingleton());

            FlakeInfo flakeInfo = createFlake(flakeCreationInfo, allocatedContainer);

            if (flakeInfo == null) {
                //TODO: Clean up already allocated resources for this application
                return null; //or throw exception?
            }
            nodeToFlakeMap.put(pelletNode.getNodeId(), flakeInfo);

            flakeInfo.printInfo(System.out);

            flakes.add(flakeInfo);
        }

        createCoordinatorPort();
        HashMap<String, List<TransportInfoBase>> sourceNodeTransport = findSourceNodeTransportList(graph, nodeToFlakeMap);

        startFloeInfo.setSourceNodeTransport(sourceNodeTransport);


        //Wire the flakes..
        //Once all the flakes are created.. for each edge, send wireFlake request to the containers appropriately..
        //Send the request for wireFlake to the source if it is a PUSH. Send it to the sink, if it is a PULL

        List<Edge> edgeList;
        if(graph.getEdges() == null || graph.getEdges().getEdges() == null)
            edgeList = new ArrayList<Edge>();
        else
            edgeList = graph.getEdges().getEdges();

        //For now wiring each edge.. we might change this later for performance.. group all "out" edges for a node and connect
        for (Edge edge : edgeList) {
            Source source = edge.getSource();
            Sink sink = edge.getSink();

            FlakeInfo sourceFlake = nodeToFlakeMap.get(source.getNodeId());
            FlakeInfo sinkFlake = nodeToFlakeMap.get(sink.getNodeId());

            Port sourcePort = sourceFlake.getOutputPort(source.getPort());
            Port sinkPort = sinkFlake.getInputPort(sink.getPort());

            //Send Source's connection info to Sink.
            //Send Sink's connection info to Source.
            FlakeWiringInfo sourceWiringInfo = new FlakeWiringInfo();
            sourceWiringInfo.setFlakeID(sourceFlake.getflakeId());
            sourceWiringInfo.setSelfConnectionPort(sourcePort);
            sourceWiringInfo.setOtherEndConnectionPort(sinkPort);
            WireFlake(sourceWiringInfo);


            FlakeWiringInfo sinkWiringInfo = new FlakeWiringInfo();
            sinkWiringInfo.setFlakeID(sinkFlake.getflakeId());
            sinkWiringInfo.setSelfConnectionPort(sinkPort);
            sinkWiringInfo.setOtherEndConnectionPort(sourcePort);
            WireFlake(sinkWiringInfo);
        }


        /**
         * Wire Sinks to the coordinator   Only for Push
         */
        List<Node> sinks = findSinkNodeList(floeGraph);
        if(sinks != null && sinks.size() > 0) {
            for(Node node : sinks) {
                FlakeInfo sourceFlake = nodeToFlakeMap.get(node.getNodeId());
                for(Port p : sourceFlake.getOutputPorts() ) {
                     FlakeWiringInfo sourceWiringInfo = new FlakeWiringInfo();
                     sourceWiringInfo.setFlakeID(sourceFlake.getflakeId());
                     sourceWiringInfo.setSelfConnectionPort(p);
                     sourceWiringInfo.setOtherEndConnectionPort(coordinatorPort);
                     WireFlake(sourceWiringInfo);
                }
            }
        }

        //updateSourceFlakeVersionInfo(nodeToFlakeMap.values());
        FloeInfo floeInfo = new FloeInfo();
        floeInfo.setFloeID(startFloeInfo.getFloeID());
        floeInfo.setNodeFlakeMap(nodeToFlakeMap);
        floeList.add(floeInfo);

        updateSourceFlakeVersionInfo();
        return startFloeInfo;


    }


    public List<FlakeInfo> getFlakes() {
        return flakes;
    }

    private void WireFlake(FlakeWiringInfo wiringInfo) {
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                true);
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

        Client client = Client.create(config);

        try {
            String tempFlakeID = wiringInfo.getFlakeID();
            WebResource webRes = client.resource("http://" + tempFlakeID.split("@")[1] + ":45002/Container/wireFlake");
            client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);

            ClientResponse response = webRes.put(ClientResponse.class, wiringInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean setPortTransportInfo(Node pelletNode, FloeGraph graph) {
        // TODO Traverse graph and set values.. setting defaults to PUSH/TCP
        for (Port p : pelletNode.getInPorts().getPorts()) {
            p.setNodeId(pelletNode.getNodeId());
            p.setDataTransferMode("PUSH");
            p.setTransportType("TCP");
        }

        for (Port p : pelletNode.getOutPorts().getPorts()) {
            p.setNodeId(pelletNode.getNodeId());
            p.setDataTransferMode("PUSH");
            p.setTransportType("TCP");
        }
        return true;
    }

    //Todo:Charith:
    //Deploys a flake in the given container and returns the FlakeInfo object returned from that flake.
    //Use the deployFlake function below as a sample.
    private FlakeInfo createFlake(FlakeCreationInfo flakeCreationInfo, ContainerInfo allocatedContainer) {
        String containerIP = allocatedContainer.getContainerHost();
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                true);
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

        Client client = Client.create(config);
        WebResource webRes = null;
        ClientResponse response = null;

        try {
            webRes = client.resource("http://" + containerIP + ":45002/Container/createFlake");
            client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
            response = webRes.post(ClientResponse.class, flakeCreationInfo);
            FlakeInfo tempFlakeInfo = response.getEntity(FlakeInfo.class);
            return tempFlakeInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void startFloe(String floeId) {
        // Start the Floe with mentioned Floe ID
        FloeInfo currFloeInfo = null;
        for (FloeInfo tempFloeInfo : this.floeList) {
            if (tempFloeInfo.getFloeID().matches(floeId)) {
                currFloeInfo = tempFloeInfo;
                break;
            }
        }
        if (currFloeInfo != null) {

            Map<String, FlakeInfo> nodeFlakeMap = currFloeInfo.getNodeFlakeMap();


            WebResource webRes;
            DefaultClientConfig config = new DefaultClientConfig();
            config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                    true);
            config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

            Client client = Client.create(config);
            ClientResponse response;
            for (Entry<String, FlakeInfo> node : nodeFlakeMap.entrySet()) {
                String nodeId = node.getKey();
                FlakeInfo finfo = node.getValue();


                String tempFlakeID = finfo.getflakeId();
                webRes = client.resource("http://" + tempFlakeID.split("@")[1] + ":45002/Container/startFlake/FlakeID=" + tempFlakeID);
                client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
                response = webRes.put(ClientResponse.class);
            }
        }
    }


    @Override

    public void stopFloe(String floeId) {

    }

    public void stopFloeGracefully(String floeId) {
        if (floeGraph != null) {
            //turnoff source flakes
            DefaultClientConfig config = new DefaultClientConfig();
            config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                    true);
            config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

            Client client = Client.create(config);
            ClientResponse response = null;
            WebResource webRes;
            for (FlakeInfo flakeInfo : sourceFlakes) {
                webRes = client.resource("http://" + flakeInfo.getflakeId().split("@")[1] +
                        ":45002/Container/stopFlake/FlakeID=" + flakeInfo.getflakeId());
                client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
                response = webRes.put(ClientResponse.class);
            }

            // turn off other flakes in BFS manner
            List<Node> front = findSourceNodeList(floeGraph);
            int i = 0;
            while (true) {

                if((front.size() -1) < i) {
                    break;
                }

                Node node = front.get(i++);

                for (Edge e : floeGraph.getEdges().getEdges()) {

                    if (e.getSource().getNodeId().equalsIgnoreCase(node.getNodeId())) {
                        String sinkNode = e.getSink().getNodeId();
                        stopFlake(nodeToFlakeMap.get(sinkNode));


                        for(Node n : floeGraph.getNodes().getNodes()) {
                            if(n.getNodeId().equalsIgnoreCase(sinkNode)) {
                                front.add(n);
                            }
                        }
                    }
                }

            }

        }
    }


    private void stopFlake(FlakeInfo flakeInfo) {
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                true);
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

        Client client = Client.create(config);
        ClientResponse response = null;
        WebResource webRes;

        webRes = client.resource("http://" + flakeInfo.getflakeId().split("@")[1] +
                ":45002/Container/stopFlake/FlakeID=" + flakeInfo.getflakeId());
        client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        response = webRes.put(ClientResponse.class);
    }

    private void updateSourceFlakeVersionInfo() {
        List<PalletVersionInfo> palletVersionInfos = new ArrayList<PalletVersionInfo>();
        for (FlakeInfo flakeInfo : flakes) {

            PalletVersionInfo versionInfo = new PalletVersionInfo();
            versionInfo.setFlakeId(flakeInfo.getflakeId());
            versionInfo.setVersion(flakeInfo.getVersion());
            versionInfo.setPelletType(flakeInfo.getpelletType());
            palletVersionInfos.add(versionInfo);

        }


        updateSourceFlakeState(sourceFlakes, palletVersionInfos);


    }

    public boolean updateFlakes(String currentPallet, String newPallet) {

        List<PalletVersionInfo> palletVersionInfos = new ArrayList<PalletVersionInfo>();

        for (FlakeInfo flakeInfo : flakes) {
            if (flakeInfo.getpelletType().equals(currentPallet.trim())) {
                int version = Integer.parseInt(flakeInfo.getVersion());
                version += 1;
                flakeInfo.setVersion(version + "");
                flakeInfo.setPelletType(newPallet);

                markFlakeForUpdate(flakeInfo.getflakeId(), newPallet, version + "",
                        flakeInfo.getflakeId().split("@")[1]);
            }
            PalletVersionInfo versionInfo = new PalletVersionInfo();
            versionInfo.setFlakeId(flakeInfo.getflakeId());
            versionInfo.setVersion(flakeInfo.getVersion());
            versionInfo.setPelletType(flakeInfo.getpelletType());
            palletVersionInfos.add(versionInfo);

        }

        updateSourceFlakeState(sourceFlakes, palletVersionInfos);


        return false;
    }

    private void updateSourceFlakeState(List<FlakeInfo> flaks, List<PalletVersionInfo> palletVersionInfos) {
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                true);
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

        Client client = Client.create(config);
        WebResource webRes = client.resource("http://" + flaks.get(0).getflakeId().split("@")[1] +
                ":45002/Container/updateSourceFlakeState/FlakeID=" + flaks.get(0).getflakeId());
        client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        PalletVersionInfoSet versionInfoSet = new PalletVersionInfoSet();
        versionInfoSet.setPalletVersionInfos(palletVersionInfos);
        webRes.put(ClientResponse.class, versionInfoSet);
    }

    private void markFlakeForUpdate(String flakeId, String palletType, String version, String host) {
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                true);
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

        Client c = Client.create(config);
        WebResource r = c.resource("http://" + host + ":45002/Container/markForUpdate/FlakeID=" + flakeId +
                "/palletType=" + palletType + "/version=" + version);
        c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        r.put();
    }


    private void createCoordinatorPort() {
        Port inPort = new Port();
        inPort.setDataTransferMode("Push");
        inPort.setPortName("CORD");

        Map<String, Object> params = new HashMap<String, Object>();

        String portname = inPort.getPortName() == null ? "NULL" : inPort.getPortName();

        params.put(ChannelFactory.KEY, portname);
        params.put(ChannelFactory.QUEUE, new SourceQueue() {
            @Override
            protected void addLandmarkToAllQueues(Message landmarkMessage) {

            }

            @Override
            public void queueMessage(Fiber sourceFiber, Message message) {
                System.out.println(" Message Received " + message);
            }

            @Override
            public int getSize() {
                return 0;
            }
        });
        params.put(ChannelFactory.SERVER_SIDE, "true");

        Receiver r = new ReceiverImpl();
        r.init();


        //TODO: RENAME CHANNEL-- TO SOMETHING LIKE CHANNEL_BUNDLE (but shorter)
        String cId = r.createChannel(params);
        this.receiver = r;
        r.start();



        TransportInfoBase tempConnectionInfo = r.getChannel(cId).getTransportInfo();
        inPort.setTransportInfo(tempConnectionInfo);
        this.coordinatorPort = inPort;
    }

    @Override
    public void pauseFloe(String floeId) {

    }

    @Override
    public void resumeFloe(String floeId) {

    }

    @Override
    public void updateFloe(String floeId, FloeGraph graph) {

    }

    public List<FloeInfo> getFloeList() {
        return floeList;
    }
}
