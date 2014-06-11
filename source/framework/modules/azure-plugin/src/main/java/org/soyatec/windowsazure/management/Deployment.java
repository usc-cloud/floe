/**
 * Copyright  2006-2010 Soyatec
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * $Id$
 */
package org.soyatec.windowsazure.management;

import java.util.ArrayList;
import java.util.List;

/**
 * This class collects and defines the information of an application deployment.
 * 
 * @author yyang
 * 
 */
public class Deployment {

	/**
	 * The unique deployment name generated internally by Windows Azure.
	 */
	private String name;

	/**
	 * The unique identifier for this deployment.
	 */
	private String privateId;

	/**
	 * The environment to which the hosted service is deployed, either staging
	 * or production.
	 */
	private DeploymentSlotType deploymentSlot;

	/**
	 * The status of the deployment.
	 */
	private DeploymentStatus status;

	/**
	 * The label for the deployment.
	 */
	private String label;

	/**
	 * The URL for the deployment.
	 */
	private String url;

	/**
	 * The configuration file
	 */
	private String configuration;

	/**
	 * The version of the Windows Azure SDK that was used to generate the .cspkg
	 * that created this deployment.
	 */
	private String sdkVersion;

	/**
	 * A list of role instance in the deployment.
	 */
	private List<RoleInstance> roleInstances;

	/**
	 * A list of role in the deployment.
	 */
	private List<Role> roles;

	/**
	 * A list of input endpoints exposed by the deployment.
	 */
	private List<InputEndpoint> inputEndpoints;

	/**
	 * Parent node for elements describing an upgrade that is currently
	 * underway. If there is no upgrade in progress, this element and its
	 * children will not be present in the response body for the Get Deployment
	 * operation.
	 */
	private UpgradeStatus upgradeStatus;

	/**
	 * An integer value that indicates the number of upgrade domains in the
	 * deployment.
	 */
	private int upgradeDomainCount;

	/**
	 * @return the sdkVersion
	 */
	public String getSdkVersion() {
		return sdkVersion;
	}

	/**
	 * @param sdkVersion
	 *            the sdkVersion to set
	 */
	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	/**
	 * @return the deploymentSlot of Deployment
	 */
	public DeploymentSlotType getDeploymentSlot() {
		return deploymentSlot;
	}

	/**
	 * Set the deploymentSlot of Deployment
	 * 
	 * @param deploymentSlot
	 */
	public void setDeploymentSlot(DeploymentSlotType deploymentSlot) {
		this.deploymentSlot = deploymentSlot;
	}

	/**
	 * @return the upgradeStatus of Deployment
	 */
	public UpgradeStatus getUpgradeStatus() {
		return upgradeStatus;
	}

	/**
	 * Set the upgradeStatus of Deployment
	 * 
	 * @param upgradeStatus
	 */
	public void setUpgradeStatus(UpgradeStatus upgradeStatus) {
		this.upgradeStatus = upgradeStatus;
	}

	/**
	 * Add a roleInstance for the deployment
	 * 
	 * @param role
	 */
	public void addRoleInstance(RoleInstance role) {
		if (roleInstances == null)
			roleInstances = new ArrayList<RoleInstance>();
		roleInstances.add(role);
	}
	
	public void addInputEndpoint(InputEndpoint point){
		if(inputEndpoints == null)
			inputEndpoints = new ArrayList<InputEndpoint>();
		inputEndpoints.add(point);
	}

	
	
	/**
	 * @return the inputEndpoints
	 */
	public List<InputEndpoint> getInputEndpoints() {
		return inputEndpoints;
	}

	/**
	 * @param inputEndpoints the inputEndpoints to set
	 */
	public void setInputEndpoints(List<InputEndpoint> inputEndpoints) {
		this.inputEndpoints = inputEndpoints;
	}

	/**
	 * @return roleInstances of the Deployment
	 */
	public List<RoleInstance> getRoleInstances() {
		return roleInstances;
	}

	/**
	 * Set the roleInstances of Deployment
	 * 
	 * @param roleInstances
	 */
	public void setRoleInstances(List<RoleInstance> roleInstances) {
		this.roleInstances = roleInstances;
	}

	/**
	 * @return roles of the Deployment
	 */
	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * Set the roles of Deployment
	 * 
	 * @param roles
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	/**
	 * Add a role for the deployment
	 * 
	 * @param role
	 */
	public void addRole(Role role) {
		if (roles == null)
			roles = new ArrayList<Role>();
		roles.add(role);
	}

	/**
	 * @return the name of Deployment
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of Deployment.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return privateId of Deployment.
	 */
	public String getPrivateId() {
		return privateId;
	}

	/**
	 * Set the privateId of Deployment.
	 * 
	 * @param privateId
	 */
	public void setPrivateId(String privateId) {
		this.privateId = privateId;
	}

	/**
	 * @return the status of Deployment.
	 */
	public DeploymentStatus getStatus() {
		return status;
	}

	/**
	 * Set the status of Deployment.
	 * 
	 * @param status
	 */
	public void setStatus(DeploymentStatus status) {
		this.status = status;
	}

	/**
	 * @return the label of Deployment.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set the label of Deployment.
	 * 
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return url of Deployment.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Set the url of Deployment.
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return configuration of Deployment
	 */
	public String getConfiguration() {
		return configuration;
	}

	/**
	 * Set the configuration of Deployment
	 * 
	 * @param configuration
	 */
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	/**
	 * @return the upgradeDomainCount of Deployment
	 */
	public int getUpgradeDomainCount() {
		return upgradeDomainCount;
	}

	/**
	 * Set the upgradeDomainCount of Deployment
	 * 
	 * @param upgradeDomainCount
	 */
	public void setUpgradeDomainCount(int upgradeDomainCount) {
		this.upgradeDomainCount = upgradeDomainCount;
	}

}
