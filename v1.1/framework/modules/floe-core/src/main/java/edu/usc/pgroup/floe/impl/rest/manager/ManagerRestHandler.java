package edu.usc.pgroup.floe.impl.rest.manager;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import edu.usc.pgroup.floe.api.framework.ContainerInfo;
import edu.usc.pgroup.floe.api.framework.manager.resourceManager.AcquireContainerRequest;
import edu.usc.pgroup.floe.api.framework.manager.resourceManager.AcquireContainerResponse;
import edu.usc.pgroup.floe.impl.manager.resourceManager.ResourceManagerImpl;

@Resource
@Provider
@Path("/Manager")
public class ManagerRestHandler
{
	@GET
	@Produces("text/plain")
    public String welcomeMessage() 
	{
         // Return some cliched textual content    	
        return "Manager is Up and Running";
    }
	@Path("/allocateContainer")
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public AcquireContainerResponse allocateContainer(AcquireContainerRequest acquireContainerRequest) 
	{						
		System.out.println("Allocate Resources Invoked");
		ResourceManagerImpl manager = ResourceManagerImpl.getManager();
		
		//HashMap<String,ContainerInfo>
		AcquireContainerResponse containerResponse = manager.acquireContainers(acquireContainerRequest);
		System.out.println("Manager Finished Allocation ");			
		return containerResponse;
    }
	
	@Path("/deallocateResources")
	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces("text/plain")
    public String deallocateResources() 
	{		
		// Return some cliched textual content    	
        return "Resource DeAllocated";
    }
	
	
	@Path("/addContainerInfo/Container={containerID}/Host={host}")
	@GET	
	@Produces("text/plain")
    public String addContainerInfo(@PathParam("containerID") String containerID, @PathParam("host") String host) 
	{		
		// Return some cliched textual content		
		ResourceManagerImpl refManager = ResourceManagerImpl.getManager();	
		refManager.registerContainer(containerID,host);
        return "Container Info Added - " + containerID;
    }
	
	@Path("/listContainers")
	@GET			
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<ContainerInfo> listContainers()
	{
		try
		{
			ResourceManagerImpl refManager = ResourceManagerImpl.getManager();
			List<ContainerInfo> containerList = refManager.listContainers();			
			return containerList;			
		}
		catch(Exception e)	
		{
			
		}
		return null;
	}
	
}
