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
package edu.usc.pgroup.floe.api.framework;

import edu.usc.pgroup.floe.api.communication.TransportInfoBase;
import edu.usc.pgroup.floe.api.framework.floegraph.Edge;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;
import edu.usc.pgroup.floe.api.framework.healthmanager.HealthEventManager;
import edu.usc.pgroup.floe.api.framework.rest.FlakeCreationInfo;
import edu.usc.pgroup.floe.api.framework.rest.FlakeWiringInfo;
import edu.usc.pgroup.floe.api.framework.rest.RestNeighborList;

import java.util.List;

/***
 * Place Holder for Flakes
 * An Container maps to a single host (or) VM.
 * It is responsible for local resource allocation to Flakes that may be running within this VM.
 * A container can contain zero or more Flakes running within it.
 * A container is a single java process.
 */
 public interface Container {

    /**
     *  Create a Flake of specific type allocating resources for it
     * @param inpFlakeID  flake id
     * @param pelletType  Pallet Type for this flake
     * @param resources   Resource requirement
     * @param inpChannel  Input channel Info
     * @param outChannel  Out Fiber Info
     * @param inpPorts    In Ports
     * @param outPorts    Out ports
     * @return FlakeInfo of created Flake instance
     */
    /*public FlakeInfo createFlake(String inpFlakeID,
    		String pelletType, ResourceInfo resources,List<FloeGraph.Edge> inpChannel,
            List<FloeGraph.Edge> outChannel,List<Port> inpPorts,List<Port> outPorts);*/

    /*FlakeInfo createFlake(String inpFlakeID, String pelletType,
			ResourceInfo resources, List<Edge> inpChannel,
			List<Edge> outChannel, List<Port> inpPorts, List<Port> outPorts);*/
    
    
    public FlakeInfo createFlake(FlakeCreationInfo flakeCreationInfo);
    
    /**
     * Start the Flake with the specified channels
     * @param flakeID
     */
    public void startFlake(String flakeID);

    /**
     * Stop the Flake, Ignore the Buffer Entries
     * @param flakeID
     */
    public void stopFlake(String flakeID);

    /**
     * Pause the specified Flake. Channels remain active
     * @param flakeID
     */
    public void pauseFlake(String flakeID);

    /**
     * Resume the Flake
     * @param flakeID
     */
    public void resumeFlake(String flakeID);

    /**
     * Do the Flake wiring based on the connection info
     * @param flakeId flake id to wire
     * @param connectionInfos List of connections
     */
    public void wireFlake(String flakeId,List<FlakeWiringInfo> wiringInfoList);

    void wireFlake(String flakeID, FlakeWiringInfo wiringInfo);
    
    /**
     * Display the list of flakes that are running within this container
     */
    public List<FlakeInfo> listFlakes();

    /**
     * Get Container Information
     * @return  ContainerInfo {@Link ContainerInfo}
     */
    public ContainerInfo getContainerInfo();


    /**
     * Update a Given Flakes resource allocations
     * @param flakeId Id of the Flake to be updated
     * @param resourceInfo New Resource allocations
     * @return   true if resource allocation is successful false otherwise
     */
    public boolean updateFlakeResources(String flakeId,ResourceInfo resourceInfo);

    /**
     * Update a Given Flakes resource allocations
     * @param flakeId Id of the Flake to be updated
     * @param palletCount Number of pallets in the flake
     * @return   true if successful false otherwise
     */
    public boolean updatePalletCount(String flakeId,int palletCount);

	public boolean scaleOut(String flakeID, ResourceInfo resource);

    public boolean scaleIn(String flakeID);

	//Todo: Should remove this and create a central "Config" class that can be used to get these values.
	public void setManager(String managerHost, int managerPort);

	public boolean setListOfPredececessors(String flakeID,
			RestNeighborList neighbours);

	public boolean setListOfSuccessors(String flakeID,
			RestNeighborList neighbours);


    public String  updateFlake(String flakeId,String  palletType);

    /**
     * Mark Flake for update with a new version. Flake will keep this information and use it to do a lazy update when
     * a Message with new version comes.
     * @param flakeId  if of the flake to be updates (This is a numeric value 1,2 do not use internal flake id representation)
     * @param palletType pelletType to be updated
     * @param version  new version to be updated
     * @return  return whether update was successful or not.
     */
    public boolean markForUpdate(String flakeId,String palletType,String version);


	public HealthEventManager getHealthEventManager();
	
}
