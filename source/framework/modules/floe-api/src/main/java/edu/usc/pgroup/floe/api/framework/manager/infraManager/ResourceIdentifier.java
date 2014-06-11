package edu.usc.pgroup.floe.api.framework.manager.infraManager;

import java.util.UUID;

public class ResourceIdentifier {

	private String resourceId;
	private String host;
	private boolean isAllocated;


	public void setHost(String host) {
		this.host = host;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	public String getResourceId() {
		return resourceId;
	}

    public boolean isAllocated() {
        return isAllocated;
    }

    public void setAllocated(boolean allocated) {
        isAllocated = allocated;
    }

    public static ResourceIdentifier getNewId() {
		// TODO Auto-generated method stub
		ResourceIdentifier rid = new ResourceIdentifier();
		rid.setResourceId(UUID.randomUUID().toString());
		
		return rid;
	}

}
