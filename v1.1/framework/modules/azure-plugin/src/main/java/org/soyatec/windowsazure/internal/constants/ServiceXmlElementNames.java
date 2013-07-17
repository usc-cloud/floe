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
package org.soyatec.windowsazure.internal.constants;

public final class ServiceXmlElementNames {

	public static final String HostedServices = "HostedServices";

	public static final String HostedService = "HostedService";

	public static final String HostedService_Url = "Url";

	public static final String HostedService_Name = "ServiceName";

	public static final String StorageServices = "StorageServices";

	public static final String StorageService = "StorageService";

	public static final String StorageService_Url = "Url";

	public static final String StorageService_Name = "ServiceName";

	public static final String StorageServiceKeys = "StorageServiceKeys";

	public static final String HostedServiceProperties = "HostedServiceProperties";

	public static final String Key_Primary = "Primary";

	public static final String Key_Secondary = "Secondary";

	public static final String StorageServiceProperties = "StorageServiceProperties";

	public static final String Label = "Label";

	public static final String Description = "Description";
	/**
	 * Constants for affinity group
	 */
	public static final String AffinityGroups = "AffinityGroups";
	public static final String AffinityGroup = "AffinityGroup";
	public static final String AffinityGroupName = "Name";
	public static final String AffinityGroupDescription = "Description";
	public static final String AffinityGroupLocation = "Location";
	public static final String AffinityGroupLabel = "Label";
	

	/**
	 * Constants for Certificate
	 */
	public static final String Certificate = "Certificate";
	public static final String CertificateUrl = "CertificateUrl";
	public static final String ThumbprintAlgorithm = "ThumbprintAlgorithm";
	public static final String Thumbprint = "Thumbprint";
	public static final String CertificateData = "Data";

	/**
	 * Constants for OperatingSystem
	 */
	public static final String OperatingSystem = "OperatingSystem";
	public static final String OperatingSystemVersion = "Version";
	public static final String OperatingSystemLabel = "Label";
	public static final String OperatingSystemDefault = "IsDefault";
	public static final String OperatingSystemActive = "IsActive";
	public static final String OperatingSystemFamily = "Family";
	public static final String OperatingSystemFamilyLabel = "FamilyLabel";
	
	/**
	 * Constants for OperatingSystemFamilies
	 */
	public static final String OperatingSystemFamilies = "OperatingSystemFamily";
	public static final String OperatingSystemFamiliesName = "Name";
	public static final String OperatingSystemFamiliesLabel = "Label";
	public static final String OperatingSystems = "OperatingSystems";

	/**
	 * 
	 */
	public static final String SubscriptionOperationCollection = "SubscriptionOperationCollection";
	public static final String SubscriptionOperations = "SubscriptionOperations";
	public static final String SubscriptionOperation = "SubscriptionOperation";
	public static final String ContinuationToken = "ContinuationToken";
	public static final String OperationId = "OperationId";
	public static final String OperationObjectId = "OperationObjectId";
	public static final String OperationName = "OperationName";
	public static final String OperationParameters = "OperationParameters";
	public static final String OperationParameter = "OperationParameter";
	public static final String OperationParameterName = "Name";
	public static final String OperationParameterValue = "Value";
	public static final String UsedServiceManagementApi = "UsedServiceManagementApi";
	public static final String ClientIP = "ClientIP";
	public static final String UserEmailAddress = "UserEmailAddress";
	public static final String SubscriptionCertificateThumbprint = "SubscriptionCertificateThumbprint";
	public static final String OperationCaller = "OperationCaller";
	public static final String Status = "Status";
	public static final String HttpStatusCode = "HttpStatusCode";
	
	/**
	 * Constants for Location
	 */
	public static final String Location = "Location";
	public static final String LocationName = "Name";

	/**
	 * Constants for deployment
	 */
	public static final String Deployment = "Deployment";
	public static final String DeploymentName = "Name";
	public static final String DeploymentSlot = "DeploymentSlot";
	public static final String DeploymentPrivateID = "PrivateID";
	public static final String DeploymentLabel = "Label";
	public static final String DeploymentUrl = "Url";
	public static final String DeploymentConfiguration = "Configuration";
	public static final String DeploymentStatus = "Status";
	public static final String DeploymentUpgradeStatus = "UpgradeStatus";
	public static final String DeploymentUpgradeDomainCount = "UpgradeDomainCount";
	public static final String DeploymentUpgradeStatusUpgradeType = "UpgradeType";
	public static final String DeploymentUpgradeStatusCurrentUpgradeDomainState = "CurrentUpgradeDomainState";
	public static final String DeploymentUpgradeStatusCurrentUpgradeDomain = "CurrentUpgradeDomain";
	public static final String DeploymentRoleInstanceList = "RoleInstanceList";
	public static final String DeploymentRoleInstance = "RoleInstance";
	public static final String DeploymentRoleInstanceRoleName = "RoleName";
	public static final String DeploymentRoleInstanceName = "InstanceName";
	public static final String DeploymentRoleInstanceState = "InstanceStatus";
	public static final String DeploymentRoleInstanceUpgradeDomain = "InstanceUpgradeDomain";
	public static final String DeploymentRoleInstanceFaultDomain = "InstanceFaultDomain";
	public static final String DeploymentRoleInstanceSize = "InstanceSize";
	public static final String DeploymentRole = "Role";
	public static final String DeploymentRoleName = "RoleName";
	public static final String DeploymentOSVersion = "OsVersion";
	public static final String DeploymentSdkVersion = "SdkVersion";

	/*
	 * Operation status constants
	 */
	public static final String OperationStatus = "OperationStatus";
	public static final String OperationStatusName = "Operation";
	public static final String OperationStatusId = "ID";
	public static final String OperationStatusStatus = "Status";
	public static final String OperationStatusHTTPCode = "HTTPCode";
	public static final String OperationStatusHTTPError = "Error";
	public static final String OperationStatusHTTPErrorCode = "Code";
	public static final String OperationStatusHTTPErrorMessage = "Message";
	public static final String OperationStartedTime = "OperationStartedTime";
	public static final String OperationCompletedTime = "OperationCompletedTime";
	
	public static final String InputEndpoint = "InputEndpoint";
	public static final String InputEndpointRoleName = "RoleName";
	public static final String InputEndpointVip = "Vip";
	public static final String InputEndpointPort = "Port";

}
