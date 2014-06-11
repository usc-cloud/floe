package edu.usc.pgroup.floe.impl.manager.infraManager.infraHandler;


import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.ResourceIdentifier;



public class LocalResource extends ResourceInfo {
	boolean resourceAllocated;
	ResourceIdentifier resourceId;
	
	
	public LocalResource() {
		// TODO Auto-generated constructor stub
	}
	
	public LocalResource(ResourceInfo resource) {
		//super.setMemory(resource.getMemory());
		super.setNumberOfCores(resource.getNumberOfCores());
		//super.setType(resource.getType());
		
		resourceAllocated = false;
		
	}

	public void setResourceAllocated(boolean resourceAllocated) {
		this.resourceAllocated = resourceAllocated;
	}
	
	public boolean getResourceAllocated(){
		return this.resourceAllocated;
	}
	
	public ResourceIdentifier getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(ResourceIdentifier resourceId) {
		this.resourceId = resourceId;
	}	
}
