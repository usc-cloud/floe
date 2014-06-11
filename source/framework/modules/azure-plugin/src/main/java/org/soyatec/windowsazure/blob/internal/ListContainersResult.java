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
package org.soyatec.windowsazure.blob.internal;

import java.util.List;


class ListContainersResult {
	
	private List<ContainerProperties> contains;
	
	private String nextMarker= null;

	/**
	 * 
	 * @return a list of container
	 */
	public List<ContainerProperties> getContains() {
		return contains;
	}

	/**
	 * 
	 * @param contains
	 *        a list of container to set
	 */
	public void setContains(List<ContainerProperties> contains) {
		this.contains = contains;
	}

	/**
	 * 
	 * @return the nextMarker
	 */
	public String getNextMarker() {
		return nextMarker;
	}

	/**
	 * 
	 * @param nextMarker
	 *          the nextMarker to set
	 */
	public void setNextMarker(String nextMarker) {
		this.nextMarker = nextMarker;
	}
	
	
	
}
