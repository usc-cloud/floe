package edu.usc.pgroup.floe.impl.rest.coordinator;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import edu.usc.pgroup.floe.api.framework.ContainerInfo;
import edu.usc.pgroup.floe.api.framework.FlakeInfo;
import edu.usc.pgroup.floe.api.framework.StartFloeInfo;
import edu.usc.pgroup.floe.api.framework.floegraph.FloeGraph;
import edu.usc.pgroup.floe.api.framework.manager.resourceManager.AcquireContainerRequest;
import edu.usc.pgroup.floe.api.framework.manager.resourceManager.AcquireContainerResponse;
import edu.usc.pgroup.floe.impl.CoordinatorImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.util.List;

@Provider
@Path("/Coordinator")
public class CoordinatorRestHandler
{	
	@GET
	@Produces("text/plain")
    public String welcomeMessage() 
	{
         // Return some cliched textual content    	
        return "Container is Up and Running";
    }	
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/createFloe")	
    public StartFloeInfo createFloe(FloeGraph currGraph) 
	{
		CoordinatorImpl tempCoordinator = CoordinatorImpl.getCoordinator();
		StartFloeInfo tempFloeOutInfo =  tempCoordinator.createFloe(currGraph);
		System.out.println("\n --- Floe Graph Created Successfully ---");
		System.out.println("\n --- Starting Flakes Now--- ");
		tempCoordinator.startFloe(tempFloeOutInfo.getFloeID());
		
		if(tempFloeOutInfo != null)
		{
			return tempFloeOutInfo;
		}			
		else
			return new StartFloeInfo();
    }
	@POST	
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/startFloe")	
    public StartFloeInfo startFloe(FloeGraph currGraph) 
	{
		CoordinatorImpl tempCoordinator = CoordinatorImpl.getCoordinator();
		StartFloeInfo tempFloeOutInfo =  tempCoordinator.createFloe(currGraph);
		System.out.println("\n --- Floe Graph Created Successfully ---");
		System.out.println("\n --- Starting Flakes Now--- ");
		
		tempCoordinator.startFloe(tempFloeOutInfo.getFloeID());
		if(tempFloeOutInfo != null)
		{
			return tempFloeOutInfo;			
		}			
		else
			return new StartFloeInfo();
    }
	
	@Path("/requestResources")
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public AcquireContainerResponse requestResources(AcquireContainerRequest containerRequest) 
	{						
		CoordinatorImpl tempCoordinator = CoordinatorImpl.getCoordinator();
		ContainerInfo cinfo = tempCoordinator.requestContainer(containerRequest.getRequestedResource());
				
		if(cinfo == null) return null;
				
		AcquireContainerResponse response = new AcquireContainerResponse();
		response.setRequestId(containerRequest.getRequestId());
		response.setContainerId(cinfo.getContainerId());
		response.setContainerInfo(cinfo);
				
        return response;
        
        //TODO: CREATE appropriate flake in the new contianer..
    }
	
	@GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("/getFlakeInfo")
    public List<FlakeInfo> getFlakeInfo() {
        CoordinatorImpl tempCoordinator = CoordinatorImpl.getCoordinator();
        //return tempCoordinator.getFlaks();
        return null;
    }

    @PUT
    @Path("/updateSingleFlake/FlakeID={flakeID}/palletType={palletType}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces("text/plain")
    public String updateSingleFlake(@PathParam("flakeID")String flakeId,@PathParam("palletType") String palletType) {
        if(flakeId == null || palletType == null) {
            throw new RuntimeException("Invalid input " + "Flake id:" + flakeId + " palletType:" + palletType);
        }

        for(FlakeInfo info : CoordinatorImpl.getCoordinator().getFlakes()) {

            String []parts = info.getflakeId().split("@");
            String containerHost = parts[1];
            String id = parts[2];
            if(id.equals(flakeId)) {
                DefaultClientConfig config = new DefaultClientConfig();
                config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                        true);
                config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

                Client c = Client.create(config);
                WebResource r = c.resource("http://"+containerHost+":45002/Container/updateSingleFlake/FlakeID="+flakeId +
                        "/palletType="+palletType );
                c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
                ClientResponse response ;
                c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
                r.put();
                return flakeId;
            }
        }

        return null;
    }

    @PUT
    @Path("/updateFlakes/currentType={currentType}/newType={newType}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void updateFlakes( @PathParam("currentType") String currentPalletType,
                               @PathParam("newType") String targetType){

           CoordinatorImpl.getCoordinator().updateFlakes(currentPalletType,targetType);

    }

    @PUT
    @Path("/stopFloe/floeId={floeId}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void updateFlakes(@PathParam("floeId") String floeId){
        CoordinatorImpl.getCoordinator().stopFloeGracefully(floeId);
    }

    @Path("/updateContainerInfo")
    @PUT
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void updateContainerInfo(ContainerInfo containerInfo) {
        CoordinatorImpl.getCoordinator().updateContainerInfo(containerInfo);
    }


}
