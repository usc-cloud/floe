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
 * This class defines all properties of a Storage account.
 * 
 * @author yyang
 * 
 */
public class StorageAccountProperties {
	/**
	 * The name of storage account.
	 */
	private String name;

	/**
	 * The URL to the storage account.
	 */
	private String url;

	/**
	 * A description of the storage account.
	 */
	private String description;

	/**
	 * The affinity group with which this storage account is associated.
	 */
	private String affinityGroup;

	/**
	 * The geo-location of the storage account in Windows Azure, if your storage
	 * account is not associated with an affinity group.
	 */
	private String location;

	/**
	 * The label for the storage account.
	 */
	private String label;

	/**
	 * Construct a new StorageAccountProperties object.
	 */
	public StorageAccountProperties() {

	}
	
	/**
	 * Construct a new StorageAccountProperties object.
	 */
	public StorageAccountProperties(String name, String url,
			String description, String affinityGroup, String location,
			String label) {
		super();
		this.name = name;
		this.url = url;
		this.description = description;
		this.affinityGroup = affinityGroup;
		this.location = location;
		this.label = label;
	}

	/**
	 * @return the description of StorageAccountProperties.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of StorageAccountProperties.
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the affinity group of StorageAccountProperties.
	 */
	public String getAffinityGroup() {
		return affinityGroup;
	}

	/**
	 * Set the affinity group of StorageAccountProperties.
	 * 
	 * @param affinityGroup
	 */
	public void setAffinityGroup(String affinityGroup) {
		this.affinityGroup = affinityGroup;
	}

	/**
	 * @return the location of StorageAccountProperties.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Set the location of StorageAccountProperties.
	 * 
	 * @param location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the label of StorageAccountProperties.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set the label of StorageAccountProperties.
	 * 
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}
