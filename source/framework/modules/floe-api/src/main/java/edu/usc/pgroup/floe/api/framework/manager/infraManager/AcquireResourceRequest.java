package edu.usc.pgroup.floe.api.framework.manager.infraManager;

import java.util.Map;
import java.util.UUID;

import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;


public class AcquireResourceRequest {
	ResourceInfo resourceInfo;
	String requestId;
	
	public ResourceInfo getResourceInfo() {
		return resourceInfo;
	}
		
	public void setResourceInfo(ResourceInfo resourceInfo) {
		this.resourceInfo = resourceInfo;
	}		
	
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public String getRequestId() {
		return requestId;
	}
	
	public static AcquireResourceRequest getNew()
	{
		AcquireResourceRequest req = new AcquireResourceRequest();
		req.setRequestId(UUID.randomUUID().toString());
		return req;
	}
}
