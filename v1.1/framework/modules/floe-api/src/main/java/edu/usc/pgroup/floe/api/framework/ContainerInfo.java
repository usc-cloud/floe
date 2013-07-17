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


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.ResourceIdentifier;


/**
 * <class>ContainerInfo</class> described the resources for a Flake within a container.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ContainerInfo")
@XmlType(propOrder = {"containerId", "resourceDescription", "activeResources", "availableResources", "containerStatus","resourceIdentifier"})
public class ContainerInfo {
    @XmlElement(name = "containerId")
    String containerId;
    @XmlElement(name = "resourceDescription")
    String resourceDescription;
    @XmlElement(name = "activeResources")
    ResourceInfo activeResources;
    @XmlElement(name = "availableResources")
    ResourceInfo availableResources;
    @XmlElement(name = "containerStatus")
    String containerStatus;
    @XmlElement(name = "resourceIdentifier")
    ResourceIdentifier resourceIdentifier;

    public ContainerInfo() {

    }

    public String getContainerHost()
    {
    	return containerId.split("@")[1];
    }
    
    
    
    public ContainerInfo(String containerId, ResourceInfo availableResource, ResourceInfo activeResource) {
        assert containerId != null : "Container ID must be specified";
        this.containerId = containerId;
        // One core is allocated for the Container and the rest of the Cores
        // for the flakes.
        this.availableResources = availableResource;
        this.activeResources = activeResource;
    }

    public void setResourceIdentifier(ResourceIdentifier resourceIdentifier) {
		this.resourceIdentifier = resourceIdentifier;
	}
    
    public ResourceIdentifier getResourceIdentifier() {
		return resourceIdentifier;
	}
    
    public String getContainerId() {
        return this.containerId;
    }

    public void setContainerId(String containerID) {
        this.containerId = containerID;
    }

    public String getresourceDescription() {
        return this.resourceDescription;
    }

    public void setresourceDescription(String resourceDesc) {
        this.resourceDescription = resourceDesc;
    }

    public ResourceInfo getactiveResources() {
        return this.activeResources;
    }

    public void setactiveResources(ResourceInfo activeRes) {
        this.activeResources = activeRes;
    }

    public ResourceInfo getavailableResources() {
        return this.availableResources;
    }

    public void setavailableResources(ResourceInfo availRes) {
        this.availableResources = availRes;
    }

    public synchronized boolean incrementResources(ResourceInfo resources) {
        // Increment the assigned Resources &
        // Decrement the available Resources
        if (resources.numberOfCores <= this.availableResources.numberOfCores) {
            this.activeResources.numberOfCores = this.activeResources.numberOfCores + resources.numberOfCores;
            this.availableResources.numberOfCores = this.availableResources.numberOfCores - resources.numberOfCores;
            return true;
        }
        return false;

    }

    public synchronized boolean decerementResouces(ResourceInfo resources) {
        // Increment the available Resources &
        // Decrement the Assigned Resources
        if (resources.numberOfCores <= this.activeResources.numberOfCores) {
            this.activeResources.numberOfCores = this.activeResources.numberOfCores - resources.numberOfCores;
            this.availableResources.numberOfCores = this.availableResources.numberOfCores + resources.numberOfCores;
            return true;
        }
        return false;
    }

    public synchronized boolean incrementAvailableResource(ResourceInfo resources) {
        if (resources.numberOfCores <= this.activeResources.getNumberOfCores()) {
            this.availableResources.numberOfCores = this.availableResources.numberOfCores + resources.numberOfCores;
            return true;
        }
        return false;
    }

    public synchronized boolean decrementAvailableResource(ResourceInfo resources) {
        if (resources.numberOfCores <= this.availableResources.getNumberOfCores()) {
            this.availableResources.numberOfCores = this.availableResources.numberOfCores - resources.numberOfCores;
            return true;
        }
        return false;
    }

    public String getContainerStatus() {
        return this.containerStatus;
    }

    public void setContainerStatus(String inpStatus) {
        this.containerStatus = inpStatus;
    }
}
