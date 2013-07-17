package edu.usc.pgroup.floe.impl.manager.resourceManager;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;

import com.sun.jersey.api.client.config.DefaultClientConfig;
import edu.usc.pgroup.floe.api.framework.ContainerInfo;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.AcquireResourceRequest;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.AcquireResourceResponse;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.ResourceIdentifier;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.infraHandler.InfraHandler;
import edu.usc.pgroup.floe.api.framework.manager.resourceManager.AcquireContainerRequest;
import edu.usc.pgroup.floe.api.framework.manager.resourceManager.AcquireContainerResponse;
import edu.usc.pgroup.floe.api.framework.manager.resourceManager.ContainerIdentifier;
import edu.usc.pgroup.floe.api.framework.manager.resourceManager.ResourceManager;
import edu.usc.pgroup.floe.impl.manager.infraManager.infraHandler.InfraHandlerFactory;
import edu.usc.pgroup.floe.impl.manager.infraManager.infraHandler.LocalClusterInfraHandler;
import edu.usc.pgroup.floe.impl.rest.RestService;
import edu.usc.pgroup.floe.util.EucalyptusInstance;
import edu.usc.pgroup.floe.util.NetworkUtil;

public class ResourceManagerImpl implements ResourceManager {

	private static ResourceManagerImpl instance;

	private RestService restService;

	public final int MANAGER_REST_SERVICE_PORT = 45001;
    public final String MANAGER_REST_SERVICE_PKG = "edu.usc.pgroup.floe.impl.rest.manager";
    public final URI BASE_URI = NetworkUtil.getBaseURI(MANAGER_REST_SERVICE_PORT);

    Map<String, ResourceIdentifier> containerToResourceMap = new TreeMap<String, ResourceIdentifier>();


	private ResourceManagerImpl()
	{
		if (restService == null) {
			restService = new RestService(MANAGER_REST_SERVICE_PKG, BASE_URI);
		}
		restService.start();
	}

	public synchronized static ResourceManagerImpl getManager() {
		if(instance == null)
			instance = new ResourceManagerImpl();
		return instance;
	}

	@Override
	public AcquireContainerResponse acquireContainers(
			AcquireContainerRequest containerRequest) {


		AcquireContainerResponse response = new AcquireContainerResponse();

		ResourceInfo requestedResource = containerRequest.getRequestedResource();
		ContainerInfo availableContainer = null;
		int maxCore = 0;
        for(ResourceIdentifier rid: containerToResourceMap.values())
		{


			ContainerInfo cinfo = getContainerInfo(rid);
             if(maxCore < cinfo.getavailableResources().getNumberOfCores()) {
                 if (requestedResource.getNumberOfCores() <= cinfo.getavailableResources().getNumberOfCores()) {
                     maxCore = cinfo.getavailableResources().getNumberOfCores();
                     availableContainer = cinfo;
                 }
             }

		}

		if(availableContainer == null)
		{
			String resourceType = containerRequest.getRequestedResource().getOptionalConfiguration().getParams().get("resourceType");

			InfraHandler handler = InfraHandlerFactory.getInstance(resourceType);

			if(handler == null) return null;


			//TODO: Change Object to keyvalue pairs.. and set appropriate values

			AcquireResourceRequest resourceRequest = AcquireResourceRequest.getNew();
			resourceRequest.setResourceInfo(containerRequest.getRequestedResource());

			AcquireResourceResponse resourceAcquireResponse  = handler.acquireResource(resourceRequest);
			if(resourceAcquireResponse == null) return null;


			//Deploy the container on the resource
			////Assume. For now assume that the container is auto deployed in the resource.


			//Request the containerInfo from the container..
			availableContainer = getContainerInfo(resourceAcquireResponse.getResourceId());
			availableContainer.setResourceIdentifier(resourceAcquireResponse.getResourceId());
			containerToResourceMap.put(availableContainer.getContainerId(), resourceAcquireResponse.getResourceId());
		}

		//set the appropriate response here
		response.setContainerInfo(availableContainer);
		response.setRequestId(containerRequest.getRequestId());
		response.setContainerId(availableContainer.getContainerId());

		return response;
	}

	@Override
	public void releaseContainers(List<ContainerIdentifier> containers) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ContainerInfo> listContainers() {
		// TODO Auto-generated method stub
		return null;
	}


	public String registerContainer(String containerID, String host)
	{
		// Function Should be deleted in the Final Version.
		// Just for the Initial Version. Since we assume we know all the Containers in Place.

		//Also get the containerIP/host..

		ContainerInfo cont = getContainerInfo(host);
		LocalClusterInfraHandler localHandler = (LocalClusterInfraHandler) InfraHandlerFactory.getInstance("Local");

		//Resource
		ResourceIdentifier rid = localHandler.registerResource(cont.getavailableResources(), host);
		cont.setResourceIdentifier(rid);

		containerToResourceMap.put(cont.getContainerId(), rid);
		return "Container Info";
	}

	private ContainerInfo getContainerInfo(String host) {
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                true);
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

        Client tempClient = Client.create(config);
		String containerIP = host;



		WebResource r = tempClient.resource("http://"+ containerIP + ":45002/Container/getContainerInfo");
        tempClient.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);


        ClientResponse response = r.get(ClientResponse.class);
		ContainerInfo tempContainerRes = response.getEntity(ContainerInfo.class);
		return tempContainerRes;
	}

	private ContainerInfo getContainerInfo(ResourceIdentifier resourceId) {
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                true);
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

        Client tempClient = Client.create(config);
		String containerIP = resourceId.getHost();

		WebResource r = tempClient.resource("http://"+ containerIP + ":45002/Container/getContainerInfo");
		tempClient.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		ClientResponse response = r.get(ClientResponse.class);
		ContainerInfo tempContainerRes = response.getEntity(ContainerInfo.class);
        System.out.println("Got Container info : ");
        System.out.println("C ID : " + tempContainerRes.getContainerId());
        System.out.println("Avaiable Cores : " + tempContainerRes.getavailableResources().getNumberOfCores());
		return tempContainerRes;
	}
}