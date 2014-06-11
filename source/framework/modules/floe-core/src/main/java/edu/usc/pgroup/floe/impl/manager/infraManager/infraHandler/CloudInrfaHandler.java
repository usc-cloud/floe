package edu.usc.pgroup.floe.impl.manager.infraManager.infraHandler;

import java.util.List;
import java.util.Map;

import edu.usc.pgroup.floe.api.framework.ContainerInfo;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.AcquireResourceRequest;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.AcquireResourceResponse;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.ResourceIdentifier;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.infraHandler.InfraHandler;

public abstract class CloudInrfaHandler implements InfraHandler {

	@Override
	public AcquireResourceResponse acquireResource(
			AcquireResourceRequest resourceRequests
			) {

		//"emi-D37D148D"
		
		Map<String, String> optionalInfo = resourceRequests.getResourceInfo().getOptionalConfiguration().getParams();
		
		deployInstance(optionalInfo);
		
		//Create the response here.. 
		return null;
	}

	@Override
	public void releaseContainers(List<ResourceIdentifier> containers) {		
		for(ResourceIdentifier container : containers)
		{
			String instanceID = (String) container.getResourceId();
			releaseInstance(instanceID);
		}
	}

	@Override
	public List<ContainerInfo> listResources() {
		// TODO Auto-generated method stub
		return null;
	}

	protected abstract void deployInstance(Map<String,String> params);
	protected abstract void releaseInstance(String instanceID);
}
