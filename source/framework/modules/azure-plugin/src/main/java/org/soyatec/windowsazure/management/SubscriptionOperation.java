package org.soyatec.windowsazure.management;

import java.sql.Timestamp;
import java.util.Map;

/**
 * A single operation that has been performed on the subscription during the
 * specified timeframe.
 * 
 */
public class SubscriptionOperation {

	/**
	 * The globally unique identifier (GUID) of the operation.
	 */
	private String operationId;

	/**
	 * The target object for the operation. This value is equal to the URL for
	 * performing an HTTP GET on the object, and corresponds to the same values
	 * for the ObjectIdFilter in the request.
	 */
	private String operationObjectId;

	/**
	 * The name of the performed operation.
	 */
	private String operationName;

	/**
	 * The collection of parameters for the performed operation. The value of
	 * the parameter can be represented as a string or an XML document,
	 * depending on the subscription operation that was performed.
	 */
	private Map<String, String> operationParameters;

	/**
	 * The current status of the operation. Possible values are: either
	 * Succeeded, Failed, or InProgress.
	 */
	private OperationState operationStatus;

	/**
	 * Indicates whether the operation was initiated by using the Service
	 * Management API. False if it was initiated by another source, such as the
	 * Management Portal.
	 */
	private boolean usedServiceManagementApi;

	/**
	 * The email associated with the Windows Live ID of the user who initiated
	 * the operation from the Management Portal. This element is returned only
	 * if UsedServiceManagementApi is false.
	 */
	private String userEmailAddress;

	/**
	 * The IP address of the client computer that initiated the operation. This
	 * element is returned only if UsedServiceManagementApi is true.
	 */
	private String clientIP;

	/**
	 * The thumbprint of the subscription certificate used to initiate the
	 * operation.
	 */
	private String subscriptionCertificateThumbprint;

	/**
	 * The http status code of the operation.
	 */
	private String httpStatusCode;

	/**
	 * The time that the operation started to execute.
	 */
	private Timestamp operationStartedTime;

	/**
	 * The time that the operation finished executing. This element is not
	 * present if the operation is still in progress.
	 */
	private Timestamp operationCompletedTime;

	public Timestamp getOperationStartedTime() {
		return operationStartedTime;
	}

	public void setOperationStartedTime(Timestamp operationStartedTime) {
		this.operationStartedTime = operationStartedTime;
	}

	public Timestamp getOperationCompletedTime() {
		return operationCompletedTime;
	}

	public void setOperationCompletedTime(Timestamp operationCompletedTime) {
		this.operationCompletedTime = operationCompletedTime;
	}

	public String getOperationId() {
		return operationId;
	}

	public String getOperationObjectId() {
		return operationObjectId;
	}

	public String getOperationName() {
		return operationName;
	}

	public Map<String, String> getOperationParameters() {
		return operationParameters;
	}

	public OperationState getOperationStatus() {
		return operationStatus;
	}

	public boolean isUsedServiceManagementApi() {
		return usedServiceManagementApi;
	}

	public String getUserEmailAddress() {
		return userEmailAddress;
	}

	public String getClientIP() {
		return clientIP;
	}

	public String getSubscriptionCertificateThumbprint() {
		return subscriptionCertificateThumbprint;
	}

	public String getHttpStatusCode() {
		return httpStatusCode;
	}

	void setHttpStatusCode(String httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	void setOperationId(String operationId) {
		this.operationId = operationId;
	}

	void setOperationObjectId(String operationObjectId) {
		this.operationObjectId = operationObjectId;
	}

	void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	void setOperationParameters(Map<String, String> operationParameters) {
		this.operationParameters = operationParameters;
	}

	void setOperationStatus(OperationState operationStatus) {
		this.operationStatus = operationStatus;
	}

	void setUsedServiceManagementApi(boolean usedServiceManagementApi) {
		this.usedServiceManagementApi = usedServiceManagementApi;
	}

	void setUserEmailAddress(String userEmailAddress) {
		this.userEmailAddress = userEmailAddress;
	}

	void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	void setSubscriptionCertificateThumbprint(
			String subscriptionCertificateThumbprint) {
		this.subscriptionCertificateThumbprint = subscriptionCertificateThumbprint;
	}

}
