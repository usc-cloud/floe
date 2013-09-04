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
package edu.usc.pgroup.floe.impl.rest.container;


import edu.usc.pgroup.floe.api.framework.Container;
import edu.usc.pgroup.floe.api.framework.ContainerInfo;
import edu.usc.pgroup.floe.api.framework.Flake;
import edu.usc.pgroup.floe.api.framework.FlakeInfo;
import edu.usc.pgroup.floe.api.framework.PalletVersionInfo;
import edu.usc.pgroup.floe.api.framework.PalletVersionInfoSet;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;
import edu.usc.pgroup.floe.api.framework.rest.FlakeCreationInfo;
import edu.usc.pgroup.floe.api.framework.rest.FlakeWiringInfo;
import edu.usc.pgroup.floe.api.framework.rest.RestNeighborList;
import edu.usc.pgroup.floe.impl.FloeRuntimeEnvironment;
import edu.usc.pgroup.floe.impl.ContainerImpl;
import edu.usc.pgroup.floe.impl.FlakeImpl;
import edu.usc.pgroup.floe.util.Logger;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.util.List;

@Resource
@Provider
@Path("/Container")
public class ContainerRestHandler {
    @GET
    @Produces("text/plain")
    public String welcomeContainer() {
        // Return some cliched textual content
        return "Container is Up and Running";
    }

    @GET
    @Path("/getContainerInfo")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ContainerInfo getContainerInfo() {
        Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
        return refContainer.getContainerInfo();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/createFlake")
    public FlakeInfo createFlake(FlakeCreationInfo flakeCreationInfo) {
        Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
        FlakeInfo newFlake = refContainer.createFlake(flakeCreationInfo);
        if (newFlake != null) {
            return newFlake;
        } else {
            return new FlakeInfo();
        }
    }

    //Wire ONE EDGE AT A TIME.. 
    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/wireFlake")
    @Produces("text/plain")
    public void wireFlake(FlakeWiringInfo flakeWiringInfo) {
        Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
        refContainer.wireFlake(flakeWiringInfo.getFlakeID(), flakeWiringInfo);
    }

    @GET
    @Path("/listFlakes")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<FlakeInfo> listFlakes() {
        try {
            Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
            return refContainer.listFlakes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PUT
    @Path("/increaseCore/FlakeID={flakeID}/Size={coreSize}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces("text/plain")
    public String increaseCore(@PathParam("flakeID") String flakeID, @PathParam("coreSize") String coreSize) {
        try {
            System.out.println("Request for Increase in Core Size " + flakeID);

            Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
            ResourceInfo tempResourceInfo = new ResourceInfo();
            tempResourceInfo.setNumberOfCores(Integer.parseInt(coreSize));
            boolean accpeted = refContainer.updateFlakeResources(flakeID, tempResourceInfo);

            return String.valueOf(accpeted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

    @PUT
    @Path("/updateSingleFlake/FlakeID={flakeID}/palletType={palletType}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces("text/plain")
    public String updateSingleFlake(@PathParam("flakeID") String flakeId, @PathParam("palletType") String palletType) {
        if (flakeId == null || palletType == null) {
            throw new RuntimeException("Invalid input " + "Flake id:" + flakeId + " palletType:" + palletType);
        }

        Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
        return refContainer.updateFlake(flakeId, palletType);
    }


    @PUT
    @Path("/markForUpdate/FlakeID={flakeId}/palletType={palletType}/version={version}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void markForUpdate(@PathParam("flakeId")String flakeId,@PathParam("palletType")String palletType,
                                 @PathParam("version")String version) {
        if (flakeId == null || palletType == null || version == null) {
            throw new RuntimeException("Invalid input " + "Flake id:" + flakeId + " palletType:" + palletType +
                    " Version:" + version);
        }
        Logger.getInstance().LogInfo("MarkForUpdate," + System.currentTimeMillis());
        FloeRuntimeEnvironment.getEnvironment().getContainer().markForUpdate(flakeId,palletType,version);

    }


    @PUT
    @Path("/updateSourceFlakeState/FlakeID={flakeId}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void  updateSourceFlakeState(@PathParam("flakeId")String flakeId, PalletVersionInfoSet infoset) {
        List<PalletVersionInfo> versionInfos  = infoset.getPalletVersionInfos();
        ContainerImpl refContainer = (ContainerImpl) FloeRuntimeEnvironment.getEnvironment().getContainer();
        List<Flake> flakeList = refContainer.getFlakeList();

        String fid = flakeId.split("@")[2];
        for(Flake flake : flakeList) {
            if(flake.getNodeId().equals(fid)) {
                FlakeImpl fimpl = (FlakeImpl) flake;
                fimpl.setPalletVersionInfos(versionInfos);
                fimpl.setLastUpdatedMode(FlakeImpl.MULTI_UPDATE_MODE);
            }
        }

    }
    @PUT
    @Path("/decreaseCore/FlakeID={flakeID}/Size={coreSize}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces("text/plain")
    public String decreaseCore(@PathParam("flakeID") String flakeID, @PathParam("coreSize") String coreSize) {
        try {
            System.out.println("Request for Decrease in Core Size" + flakeID);
            Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
            ResourceInfo tempResourceInfo = new ResourceInfo();
            tempResourceInfo.setNumberOfCores(Integer.parseInt(coreSize) * -1);
            boolean accpeted = refContainer.updateFlakeResources(flakeID, tempResourceInfo);
            return String.valueOf(accpeted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

    @PUT
    @Path("/increasePelletCount/FlakeID={flakeID}/Size={pelletCount}")
    @Produces("text/plain")
    public String increasePelletCount(@PathParam("flakeID") String flakeID, @PathParam("pelletCount") String pelletCount) {
        boolean requestedAccepted = false;
        try {
            System.out.println("Request for Increase in Pellet Count " + flakeID);
            Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
            requestedAccepted = refContainer.updatePalletCount(flakeID, Integer.parseInt(pelletCount));
            return String.valueOf(requestedAccepted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

    @PUT
    @Path("/decreasePelletCount/FlakeID={flakeID}/Size={pelletCount}")
    @Produces("text/plain")
    public String decreasePelletCount(@PathParam("flakeID") String flakeID, @PathParam("pelletCount") String pelletCount) {
        boolean requestedAccepted = false;
        try {
            System.out.println("Request for decrease in Pellet Count " + flakeID);
            Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
            requestedAccepted = refContainer.updatePalletCount(flakeID, Integer.parseInt(pelletCount) * -1);
            return String.valueOf(requestedAccepted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

    @PUT
    @Path("/stopFlake/FlakeID={flakeID}")
    @Produces("text/plain")
    public String stopFlake(@PathParam("flakeID") String flakeID) {
        System.out.println("Trying to Stop Flake " + flakeID);
        Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
        refContainer.stopFlake(flakeID);
        return flakeID + " Stopped";
    }

    @PUT
    @Path("/startFlake/FlakeID={flakeID}")
    @Produces("text/plain")
    public String startFlake(@PathParam("flakeID") String flakeID) {
        Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
        refContainer.startFlake(flakeID);

        return flakeID + " Started";
    }

    @PUT
    @Path("/pauseFlake/FlakeID={flakeID}")
    @Produces("text/plain")
    public String pauseFlake(@PathParam("flakeID") String flakeID) {
        Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
        refContainer.pauseFlake(flakeID);
        return flakeID + " Paused";
    }

    @PUT
    @Path("/resumeFlake/FlakeID={flakeID}")
    @Produces("text/plain")
    public String resumeFlake(@PathParam("flakeID") String flakeID) {
        Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
        refContainer.resumeFlake(flakeID);
        return flakeID + " Resumed";
    }

    @PUT
    @Path("/stopContainer")
    @Produces("text/plain")
    public String stopContainer() {
        //ContainerImpl refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
        // Should write a method to remove all the Flakes and destroy the container
        // itself
        return "Container Stopped";
    }


    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/addToContainersTopology")//?ContainerID={containerID}&IP={ip}
    public void AddToContainersTopology(@QueryParam("containerID") String containerID, @QueryParam("ip") String ip) {
        //PeriodicHealthCheck.AddToContainersTopology(containerID,ip);
    }


    @Path("/scaleOut/FlakeID={flakeID}")
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public String scaleOut(@PathParam("flakeID") String flakeID, ResourceInfo resource) {
        boolean requestedAccepted = false;
        try {
            System.out.println("Request for Increase in Pellet Count " + flakeID);
            Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
            requestedAccepted = refContainer.scaleOut(flakeID, resource);
            return String.valueOf(requestedAccepted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }


    @PUT
    @Path("/scaleIn/FlakeID={flakeID}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public String scaleIn(@PathParam("flakeID") String flakeID) {
        boolean requestedAccepted = false;
        try {
            System.out.println("Request for shout down the meta flake --scale down " + flakeID);
            Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
            requestedAccepted = refContainer.scaleIn(flakeID);
            return String.valueOf(requestedAccepted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

    @Path("/notifyListOfPredecessors/FlakeID={flakeID}")
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public String notifyListOfPredecessors(@PathParam("flakeID") String flakeID, RestNeighborList neighbours) {
        boolean requestedAccepted = false;
        try {
            System.out.println("Request for Increase in Pellet Count " + flakeID);
            Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
            requestedAccepted = refContainer.setListOfPredececessors(flakeID, neighbours);
            return String.valueOf(requestedAccepted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

    @Path("/notifyListOfSuccessors/FlakeID={flakeID}")
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public String notifyListOfSuccessors(@PathParam("flakeID") String flakeID, RestNeighborList neighbours) {
        boolean requestedAccepted = false;
        try {
            System.out.println("Request for Increase in Pellet Count " + flakeID);
            Container refContainer = FloeRuntimeEnvironment.getEnvironment().getContainer();
            requestedAccepted = refContainer.setListOfSuccessors(flakeID, neighbours);
            return String.valueOf(requestedAccepted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }
}

