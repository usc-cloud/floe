package edu.usc.pgroup.floe.api.framework.manager.infraManager;

import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;


public class AcquireResourceResponse {

	ResourceIdentifier resourceId;
	String requesetId;
	ResourceInfo resourceInfo;
	
	public void setRequesetId(String requesetId) {
		this.requesetId = requesetId;
	}
	
	public String getRequesetId() {
		return requesetId;
	}
	
	public ResourceIdentifier getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(ResourceIdentifier resourceId) {
		this.resourceId = resourceId;
	}

	public void setResourceInfo(ResourceInfo resourceInfo) {
		this.resourceInfo = resourceInfo;
	}
	
	public ResourceInfo getResourceInfo() {
		return resourceInfo;
	}
}
