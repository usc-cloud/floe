package org.soyatec.windowsazure.management;

/**
 * This class contains a filter that will be used in the List Subscription
 * Operations operation. With this filter, the operation will only returns
 * subscription operations for the specified object type and object ID. The
 * filter will be set equal to the URL value for performing an HTTP GET on the
 * object. Applicable object types are included below as class method.
 * 
 */
public class ObjectIdFilter {
	private String filter;

	/**
	 * The constructor is only used internally. User should not use it to
	 * construct an object.
	 * 
	 * @param filter
	 */
	private ObjectIdFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * A string that represents the filter. It is used internally.
	 * 
	 * @return
	 */
	String getFilter() {
		return filter;
	}

	/**
	 * Filter for Subscription ID.
	 * 
	 * @return
	 */
	public static ObjectIdFilter subscription() {
		return new ObjectIdFilter("");
	}

	/**
	 * Filter for hosted service.
	 * 
	 * @param hostServiceName
	 *            The name of your hosted service.
	 * @return
	 */
	public static ObjectIdFilter hostedService(String hostServiceName) {
		return new ObjectIdFilter("/services/hostedservices/" + hostServiceName);
	}

	/**
	 * Filter for storage service.
	 * 
	 * @param storageServiceName
	 *            The name of your storage service.
	 * 
	 * @return
	 */
	public static ObjectIdFilter storageService(String storageServiceName) {
		return new ObjectIdFilter("/services/storageservices/"
				+ storageServiceName);
	}

	/**
	 * Filter for affinity group.
	 * 
	 * @param affinityGroupName
	 *            The name of affinity group.
	 * @return
	 */
	public static ObjectIdFilter affinityGroup(String affinityGroupName) {
		return new ObjectIdFilter("/affinitygroups/" + affinityGroupName);
	}

	/**
	 * Filter for deployment.
	 * 
	 * @param hostServiceName
	 *            The name of your hosted service.
	 * @param deploymentName
	 *            The name of your deployment.
	 * @return
	 */
	public static ObjectIdFilter deployment(String hostServiceName,
			String deploymentName) {
		return new ObjectIdFilter("/services/hostedservices/" + hostServiceName
				+ "/deployments/" + deploymentName);
	}

	/**
	 * Filter for role instance.
	 * 
	 * @param hostServiceName
	 *            The name of your hosted service.
	 * @param deploymentName
	 *            The name of your deployment.
	 * @param roleInstanceName
	 *            The name of role instance.
	 * @return
	 */
	public static ObjectIdFilter roleInstance(String hostServiceName,
			String deploymentName, String roleInstanceName) {
		return new ObjectIdFilter("/services/hostedservices/" + hostServiceName
				+ "/deployments/" + deploymentName + "/roleinstances/"
				+ roleInstanceName);
	}

	/**
	 * Filter for machine image.
	 * 
	 * @param machineImageName
	 *            The name of machine image.
	 * @return
	 */
	public static ObjectIdFilter machineImage(String machineImageName) {
		return new ObjectIdFilter("/machineimages/" + machineImageName);
	}

}
