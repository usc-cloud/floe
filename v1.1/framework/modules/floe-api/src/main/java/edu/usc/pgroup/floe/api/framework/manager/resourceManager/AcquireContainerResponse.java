package edu.usc.pgroup.floe.api.framework.manager.resourceManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import edu.usc.pgroup.floe.api.framework.ContainerInfo;

@XmlRootElement(name = "AcquireContainerResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
		"requestId",
        "containerId",
        "containerInfo"
})
public class AcquireContainerResponse {

	String requestId;
	String containerId;
	ContainerInfo containerInfo;
	
	public void setContainerInfo(ContainerInfo containerInfo) {
		this.containerInfo = containerInfo;
	}
	
	public ContainerInfo getContainerInfo() {
		return containerInfo;
	}
	
	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}
	
	public String getContainerId() {
		return containerId;
	}
	
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public String getRequestId() {
		return requestId;
	}
}
