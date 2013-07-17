package edu.usc.pgroup.floe.impl.manager.infraManager.infraHandler;

import java.util.ArrayList;
import java.util.List;

import edu.usc.pgroup.floe.api.framework.ContainerInfo;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.AcquireResourceRequest;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.AcquireResourceResponse;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.ResourceIdentifier;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.infraHandler.InfraHandler;

public class LocalClusterInfraHandler implements InfraHandler {

	//TODO: Change this.. 
	List<LocalResource> freeResourceList = new ArrayList<>();
	List<LocalResource> allocatedResourceList = new ArrayList<>(); 
	
	@Override
	public AcquireResourceResponse acquireResource(
			AcquireResourceRequest resourceRequest) {

		
		for(LocalResource resource: freeResourceList)
		{
			ResourceInfo cinfo = resourceRequest.getResourceInfo();
			//TODO: CHange this logic later.. 
			if(resource.getNumberOfCores() >= cinfo.getNumberOfCores())
			{
				AcquireResourceResponse response = new AcquireResourceResponse();

				
				freeResourceList.remove(resource);
				allocatedResourceList.add(resource);
				
				response.setResourceId(resource.getResourceId());
				response.setRequesetId(resourceRequest.getRequestId());
				response.setResourceInfo((ResourceInfo)resource);
				return response;
			}
		}
		return null;
	}

	@Override
	public void releaseContainers(List<ResourceIdentifier> containers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ContainerInfo> listResources() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public ResourceIdentifier registerResource(ResourceInfo resource, String host)
	{
		LocalResource localResource = new LocalResource(resource);
		
		ResourceIdentifier rid = ResourceIdentifier.getNewId();
		rid.setHost(host);
		
		localResource.setResourceId(rid);
		freeResourceList.add(localResource);
		
		return rid;
	}
}
