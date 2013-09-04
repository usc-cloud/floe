package edu.usc.pgroup.floe.api.framework.manager.resourceManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;

@XmlRootElement(name = "ContainerRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "requestedResource",
        "requestId"
})
public class AcquireContainerRequest {

	ResourceInfo requestedResource;	
	String requestId;

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public String getRequestId() {
		return requestId;
	}
	
	public void setRequestedResource(ResourceInfo requestedResource) {
		this.requestedResource = requestedResource;
	}
	
	public ResourceInfo getRequestedResource() {
		return requestedResource;
	}
	
	
	public static AcquireContainerRequest createRequest() {
		AcquireContainerRequest request = new AcquireContainerRequest();
		request.setRequestId(UUID.randomUUID().toString());
		return request;
	}
}
