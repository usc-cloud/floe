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
 * This class is used to manage the Storage account.
 * 
 * @author xiaowei.ye@soyatec.com
 * 
 */
public class StorageAccount {
	
	/**
	 * The name of storage account. 
	 */
	private String name;
	
	/**
	 * The URL to the storage account.
	 */
	private String url;

	/**
	 * The primary access key for the storage account.
	 */
	private String primaryKey;

	/**
	 * The secondary access key for the storage account.
	 */
	private String secondaryKey;


	/**
	 * Construct a new StorageAccount object.
	 */
	public StorageAccount() {

	}

	/**
	 * Construct a new StorageAccount object with a name.
	 */
	public StorageAccount(String name) {
		this.name = name;
	}

	/**
	 * @return the primaryKey
	 */
	public String getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @param primaryKey
	 *            the primaryKey to set
	 */
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * @return the secondaryKey
	 */
	public String getSecondaryKey() {
		return secondaryKey;
	}

	/**
	 * @param secondaryKey
	 *            the secondaryKey to set
	 */
	public void setSecondaryKey(String secondaryKey) {
		this.secondaryKey = secondaryKey;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StorageService [name=" + name + ", url=" + url + "]";
	}

}
