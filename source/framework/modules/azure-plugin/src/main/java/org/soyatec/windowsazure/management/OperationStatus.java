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
 * This class defines the status of an operation.
 * 
 * @author yyang
 * 
 */
public class OperationStatus {
	/**
	 * The request ID of the asynchronous request. This value is returned in the
	 * x-ms-request-id response header of the asynchronous request.
	 */
	private String requestId;

	/**
	 * The status of the asynchronous request. Possible values include
	 * InProgress, Succeeded, or Failed.
	 */
	private OperationState status;

	/**
	 * The HTTP status code for the asynchronous request.
	 */
	private String httpCode;

	/**
	 * The management service error code returned if the asynchronous request
	 * failed. See http://msdn.microsoft.com/en-us/library/ee460801.aspx for
	 * information about possible error codes returned by the service.
	 */
	private String errorCode;

	/**
	 * The management service error message returned if the asynchronous request
	 * failed.
	 */
	private String errorMessage;

	/**
	 * @return the requestId of OperationStatus
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * Set the requestId of OperationStatus
	 * 
	 * @param requestId
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * @return the status of OperationStatus
	 */
	public OperationState getStatus() {
		return status;
	}

	/**
	 * Set the status of OperationStatus
	 * 
	 * @param status
	 */
	public void setStatus(OperationState status) {
		this.status = status;
	}

	/**
	 * @return the httpCode of OperationStatus
	 */
	public String getHttpCode() {
		return httpCode;
	}

	/**
	 * Set the httpCode of OperationStatus
	 * 
	 * @param httpCode
	 */
	public void setHttpCode(String httpCode) {
		this.httpCode = httpCode;
	}

	/**
	 * @return the errorCode of OperationStatus
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Set the errorCode of OperationStatus
	 * 
	 * @param errorCode
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return errorMessage of OperationStatus
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Set errorMessage of OperationStatus
	 * 
	 * @param errorMessage
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "OperationStatus [errorCode=" + errorCode + ", errorMessage="
				+ errorMessage + ", httpCode=" + httpCode + ", requestId="
				+ requestId + ", status=" + status + "]";
	}

}
