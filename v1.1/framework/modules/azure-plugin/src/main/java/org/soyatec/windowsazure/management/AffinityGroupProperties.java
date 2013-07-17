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
 * This class defines the properties of AffinityGroup
 * 
 * @author yyang
 */
public class AffinityGroupProperties {

	/**
	 * A label for the affinity group which may be up to 100 characters in
	 * length.
	 */
	private String label;

	/**
	 * A description for the affinity group. The description may be up to 1024
	 * characters in length.
	 */
	private String description;

	/**
	 * The location where the affinity group will be created. To list available
	 * locations, use the List Locations operation.
	 */
	private String location;
	/**
	 * A list of hosted service associated with the specified affinity group.
	 */
	private List<HostedService> hostedServices;

	/**
	 * A list of storage service associated with the specified affinity group.
	 */
	private List<StorageService> storageServices;

	/**
	 * @return return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the hostedServices
	 */
	public List<HostedService> getHostedServices() {
		return hostedServices;
	}

	/**
	 * @param hostedServices
	 *            the hostedServices to set
	 */
	public void setHostedServices(List<HostedService> hostedServices) {
		this.hostedServices = hostedServices;
	}

	/**
	 * @return the storageServices
	 */
	public List<StorageService> getStorageServices() {
		return storageServices;
	}

	/**
	 * @param storageServices
	 *            the storageServices to set
	 */
	public void setStorageServices(List<StorageService> storageServices) {
		this.storageServices = storageServices;
	}

	/**
	 * Add the service to hostedServices.
	 * 
	 * @param service
	 */
	public void addHostedService(HostedService service) {
		if (hostedServices == null)
			hostedServices = new ArrayList<HostedService>();
		hostedServices.add(service);
	}

	/**
	 * Add service to storageServices
	 * 
	 * @param service
	 */
	public void addStorageService(StorageService service) {
		if (storageServices == null)
			storageServices = new ArrayList<StorageService>();
		storageServices.add(service);
	}

}
