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
/***
 * A Flake is the computation unit that can process a message/task. 
 * The task that it performs depends on the type of Pellet.
 * A Flake is part of a Container, and the resources available to a Flake are determined by the Container.
 */

import edu.usc.pgroup.floe.api.communication.Receiver;
import edu.usc.pgroup.floe.api.communication.Sender;
import edu.usc.pgroup.floe.api.communication.TransportInfoBase;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;
import edu.usc.pgroup.floe.api.framework.rest.FlakeCreationInfo;
import edu.usc.pgroup.floe.api.framework.rest.FlakeWiringInfo;

import java.util.List;
import java.util.Map;

public interface Flake
{
    /**
     * Get the FlakeInfo information
     */
    public FlakeInfo getFlakeInfo();
    
    
    /**
     * Get the FlakeInfo information
     */
    public FlakeCreationInfo getFlakeCreationInfo();
    
    
    /**
     * Initialize a Flake of the given type. This allocates resources. No communication with other Flakes has been established.
     */

    String create(String inpNodeID, String pelletType, ResourceInfo resources,
			List<Port> inpPorts, List<Port> outPorts);
    
    public String create(FlakeCreationInfo flakeCreationInfo);
   
    /**
     * This sets up the input and output communication channels between source and sink Flakes.
     * Upon completion, the Flake is ready to accept and process messages.
     */
    public void wire(List<FlakeWiringInfo> wiringInfoList);
    public void wire(FlakeWiringInfo wiringInfo);
   
   

    public void startFlake();

    /**
     * This stops the current Flake after completing the current message.
     * The Flake cannot continue after this point.
     * All resources allocated to the Flake can be recovered.
     */
    public void stopFlake();

    /**
     * This pauses the Flake after completing the current message.
     * The Flake can be resumed later.
     * Implementations may allow state information of the Flake to be saved for resuming within a different container.
     */
    public void pauseFlake();

    /**
     * This resumes a paused the Flake and allows it to accept and process messages.
     * Implementations may the Flake to be resumed within a different container,
     * using different source and sinks, or different resource allocations than before.
     */

    public void modifyResource(ResourceInfo inpResource);
    public void modifyPelletCount(int count);
    public void notifyPelletCompletion(Object pelletInstance);
    public void resumeFlake();

    public String getPelletType();

    public String getFlakeId();
    
    public String getNodeId();

    public ResourceInfo getResources();

    public Map<String,Receiver> getReceivers();

    public Map<String,Sender> getSenders();

    public List<TransportInfoBase> getSuccessors();

    public List<TransportInfoBase> getPredecessors();

    public void updatePalletLogic(String palletType);

    public boolean markForUpdate(String palletType,String version);


    public boolean isSingleton();
	

	
}
