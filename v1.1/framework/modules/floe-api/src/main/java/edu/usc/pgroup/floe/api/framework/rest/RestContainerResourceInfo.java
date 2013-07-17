package edu.usc.pgroup.floe.api.framework.rest;


import edu.usc.pgroup.floe.api.framework.ContainerInfo;

import javax.xml.bind.annotation.*;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RestContainerResourceInfo")
@XmlType(propOrder = { "containerList","nodeList"})
public class RestContainerResourceInfo 
{
	@XmlElement(name = "nodeList")
	List<String> nodeList;
	@XmlElement(name = "containeList")
	List<ContainerInfo> containerList;
	
	
	public RestContainerResourceInfo()
	{		
	}
	public void setcontainerList(List<ContainerInfo> inpContainerList)
	{		
		this.containerList = inpContainerList;		
	}
	public List<ContainerInfo> getcontainerList()
	{		
		return this.containerList;
	}	
	public void setnodeList(List<String> inpNodeList)
	{
		this.nodeList = inpNodeList;
	}
	public List<String> getnodeList()
	{
		return this.nodeList;
	}
}
