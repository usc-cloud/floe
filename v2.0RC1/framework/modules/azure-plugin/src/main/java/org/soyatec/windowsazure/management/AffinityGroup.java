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
 * Within a subscription, it is possible to define affinity groups. An affinity
 * group groups the services together on Windows Azure servers. If services in a
 * subscription need to work together. For example, if the hosted service stores
 * data in the Blob or Table service or relies on the Queue service for
 * workflow. Then the developer can organize its hosted service and storage
 * account within an affinity group for optimal performance. </br> Developer can
 * create an affinity group in the Developer Portal when he/she creates a
 * storage account or a hosted service. And then other services can be
 * associated with the affinity group.
 * 
 * 
 */
public class AffinityGroup {

	/**
	 * The name for the affinity group that is unique to the subscription.
	 */
	private String name;

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

	public AffinityGroup() {
	}

	/**
	 * Constructor for AffinityGroup.
	 * 
	 * @param name
	 * @param label
	 * @param description
	 * @param location
	 */
	public AffinityGroup(String name, String label, String description,
			String location) {
		this.name = name;
		this.label = label;
		this.description = description;
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
	 * @return the description
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
	 * @return the location
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AffinityGroup [description=" + description + ", location="
				+ location + ", name=" + name + "]";
	}

}
