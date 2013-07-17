package edu.usc.pgroup.floe.impl.manager.infraManager.infraHandler;

import java.util.HashMap;
import java.util.Map;

import edu.usc.pgroup.floe.api.framework.manager.infraManager.infraHandler.InfraHandler;
public class InfraHandlerFactory {
	static Map<String, InfraHandler> handlerMap = new HashMap<>();

	public static synchronized InfraHandler getInstance(String resourceType) {

		InfraHandler newHandlerInstanse = null;
		if(resourceType == null) resourceType = "Local";
		
		if(handlerMap.containsKey(resourceType))
		{
			return handlerMap.get(resourceType);
		}
		
		switch(resourceType.toLowerCase())
		{
		case "azure":
			newHandlerInstanse = new AzureInfraHandler();
			break;
		case "eucalyptus":
			newHandlerInstanse = new EucalyptusInfraHandler();
			break;
		case "local":
		default:
			newHandlerInstanse = new LocalClusterInfraHandler();
			resourceType = "Local";
		}
		
		handlerMap.put(resourceType, newHandlerInstanse);
		
		return newHandlerInstanse;		
	}
	
	
}
