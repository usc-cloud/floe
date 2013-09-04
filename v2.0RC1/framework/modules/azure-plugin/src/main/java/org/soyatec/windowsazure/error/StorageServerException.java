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
package org.soyatec.windowsazure.error;

/**
 * Server exceptions indicates server side problems. 
 * 
 */
public class StorageServerException extends StorageException {
	private static final long serialVersionUID = 618213257073011943L;
	private StorageExtendedErrorInformation details;

	/**
     * Construct a new StorageServerException object.
     */
	public StorageServerException(StorageErrorCode serviceTimeout,
			String statusDescription, int statusCode, Exception inner) {
		super(serviceTimeout, statusDescription, statusCode, inner);
	}

	/**
     * Construct a new StorageServerException object.
     */
	public StorageServerException(StorageErrorCode serviceTimeout,
			String statusDescription, int statusCode,
			StorageExtendedErrorInformation details, Exception inner) {
		this(serviceTimeout, statusDescription, statusCode, inner);
		this.details = details;
	}

	/**
	 * Get the storage extended error information of StorageServerException.
	 * @return StorageExtendedErrorInformation
	 */
	public StorageExtendedErrorInformation getDetails() {
		return details;
	}

}
