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

/**
 * Represents an instance of a role.
 */
public class RoleInstance {
	/**
	 * The name of the role.
	 */
	private String roleName;

	/**
	 * The name of the specific role instance (if any).
	 */
	private String instanceName;

	/**
	 * The current status of this instance. Possible values are: Ready, Busy,
	 * Initializing, Stopping, Stopped, Unresponsive.
	 */
	private InstanceStatus instanceStatus;

	/**
	 * The upgrade domain that this role instance belongs to. During an upgrade
	 * deployment, all roles in the same upgrade domain are upgraded at the same
	 * time.
	 */
	private int instanceUpgradeDomain;

	/**
	 * The fault domain that this role instance belongs to. Role instances in
	 * the same fault domain may be vulnerable to the failure of a single piece
	 * of hardware.
	 */
	private int instanceFaultDomain;

	/**
	 * The size of the role instance. Possible values are: ExtraSmall, Small,
	 * Medium, Large, ExtraLarge.
	 */
	private InstanceSize instanceSize;

	/**
	 * Construct a new RoleInstance object.
	 */
	public RoleInstance() {

	}

	/**
	 * Construct a new RoleInstance object with a byte roleName, instanceName
	 * and instanceStatus, instanceUpgradeDomain, instanceFaultDomain, instanceSize.
	 */
	public RoleInstance(String roleName, String instanceName,
			InstanceStatus instanceStatus, int instanceUpgradeDomain,
			int instanceFaultDomain, InstanceSize instanceSize) {
		super();
		this.roleName = roleName;
		this.instanceName = instanceName;
		this.instanceStatus = instanceStatus;
		this.instanceUpgradeDomain = instanceUpgradeDomain;
		this.instanceFaultDomain = instanceFaultDomain;
		this.instanceSize = instanceSize;
	}

	/**
	 * @return the instanceUpgradeDomain
	 */
	public int getInstanceUpgradeDomain() {
		return instanceUpgradeDomain;
	}

	/**
	 * @param instanceUpgradeDomain
	 *            the instanceUpgradeDomain to set
	 */
	public void setInstanceUpgradeDomain(int instanceUpgradeDomain) {
		this.instanceUpgradeDomain = instanceUpgradeDomain;
	}

	/**
	 * @return the instanceFaultDomain
	 */
	public int getInstanceFaultDomain() {
		return instanceFaultDomain;
	}

	/**
	 * @param instanceFaultDomain
	 *            the instanceFaultDomain to set
	 */
	public void setInstanceFaultDomain(int instanceFaultDomain) {
		this.instanceFaultDomain = instanceFaultDomain;
	}

	/**
	 * @return the instanceSize
	 */
	public InstanceSize getInstanceSize() {
		return instanceSize;
	}

	/**
	 * @param instanceSize
	 *            the instanceSize to set
	 */
	public void setInstanceSize(InstanceSize instanceSize) {
		this.instanceSize = instanceSize;
	}

	/**
	 * @return the role name of RoleInstance.
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * Set the role name of RoleInstance.
	 * 
	 * @param roleName
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the instanceName of RoleInstance.
	 */
	public String getInstanceName() {
		return instanceName;
	}

	/**
	 * Set the instanceName of RoleInstance.
	 * 
	 * @param instanceName
	 */
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	/**
	 * @return the instanceState of RoleInstance.
	 */
	public InstanceStatus getInstanceStatus() {
		return instanceStatus;
	}

	/**
	 * Set the instanceState of RoleInstance.
	 * 
	 * @param instanceState
	 */
	public void setInstanceStatus(InstanceStatus instanceStatus) {
		this.instanceStatus = instanceStatus;
	}

}
