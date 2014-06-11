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

import java.util.List;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.soyatec.windowsazure.internal.util.ssl.SslUtil;
import org.soyatec.windowsazure.proxy.AbstractProxyDelegate;

/**
 * The class is to manage storage accounts and hosted services, service
 * deployments, and affinity groups.
 */
public abstract class ServiceManagement extends AbstractProxyDelegate {

	private String endPointHost = null;
	private final String subscriptionId;
	private final SSLSocketFactory sslSocketFactory;

	private boolean blocking = false;

	/**
	 * Construct a new ServiceManagement object with a subscription id, store
	 * file key, store password key, trust store file, trust store password,
	 * certificate alias and endPointHost.
	 */
	public ServiceManagement(String subscriptionId, String keyStoreFile,
			String keyStorePassword, String trustStoreFile,
			String trustStorePassword, String certificateAlias,
			String endPointHost) throws Exception {
		this.subscriptionId = subscriptionId;
		this.endPointHost = endPointHost;
		this.sslSocketFactory = SslUtil.createSSLSocketFactory(keyStoreFile,
				keyStorePassword, trustStoreFile, trustStorePassword,
				certificateAlias);
	}

	/**
	 * Indicate whether the thread will be blocked for some long running
	 * operations, such as deployment.
	 * 
	 * @return true: is blocking / false: not blocking
	 */
	public boolean isBlocking() {
		return blocking;
	}

	/**
	 * If blocking is true, the thread will be blocked for some long running
	 * operations, such as deployment.
	 * 
	 * @param blocking
	 */
	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}

	/**
	 * Windows aure subscription ID.
	 * 
	 * @return the subscriptionId
	 */
	public String getSubscriptionId() {
		return subscriptionId;
	}

	/**
	 * The factory which is used to create ssl socket connection.
	 * 
	 * @return the sslSocketFactory
	 */
	SSLSocketFactory getSslSocketFactory() {
		return sslSocketFactory;
	}

	/**
	 * The url of service management api endpoint address.
	 * 
	 * @return the base url.
	 */
	String getBaseUrl() {
		if (this.endPointHost == null)
			return "https://management.core.windows.net:443/" + subscriptionId;
		else
			return "https://" + this.endPointHost + ":443/" + subscriptionId;

	}

	/**
	 * The List Hosted Services operation lists the hosted services available
	 * under the current subscription.
	 * 
	 * @return hostedServices A list of hosted services available under the
	 *         current subscription.
	 */
	public abstract List<HostedService> listHostedServices();

	/**
	 * The Create Deployment operation uploads a new service package and creates
	 * a new deployment on staging or production.
	 * 
	 * </p>
	 * 
	 * Note that it is possible to call Create Deployment only for a hosted
	 * service that has previously been created.
	 * 
	 * </p>
	 * 
	 * The Create Deployment operation is an asynchronous operation. To
	 * determine whether the management service has finished processing the
	 * request, call Get Operation Status.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param slotType
	 *            Deployment slot type, either staging or production
	 * @param configuration
	 *            It contains information for deployment, such as service
	 *            package url, service configuration file, etc.
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String createDeployment(String serviceName,
			DeploymentSlotType slotType, DeploymentConfiguration configuration,
			AsyncResultCallback callback);

	public abstract String createLinuxVirtualMachineDeployment(String serviceName, String deploymentName, 
			String label, String roleName, String hostName, String username, String newPassword, 
			String OSImageLink, String imageName, String roleSize, AsyncResultCallback callback);

    public abstract String startVMRole(String serviceName,String deploymentName,String roleName,
                                       AsyncResultCallback callback);

	/**
	 * The Get Deployment operation get the deployment with the service name and
	 * deploymentSlotType.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param slotType
	 *            Deployment slot type, either staging or production
	 * @return
	 */
	public abstract Deployment getDeployment(String serviceName,
			DeploymentSlotType slotType);

	/**
	 * The Get Deployment operation get the deployment with the service name and
	 * deployment name.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param deploymentName
	 *            The name of deployment
	 * @return
	 */
	public abstract Deployment getDeployment(String serviceName,
			String deploymentName);

	/**
	 * The Delete Deployment operation Delete the deployment with the service
	 * name, deploymentSlotType and asyncResultCallback.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param slotType
	 *            Deployment slot type, either staging or production
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String deleteDeployment(String serviceName,
			DeploymentSlotType slotType, AsyncResultCallback callback);

	/**
	 * The Delete Deployment operation Delete the deployment with the service
	 * name, deployment name and asyncResultCallback.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param deploymentName
	 *            The name of deployment
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String deleteDeployment(String serviceName,
			String deploymentName, AsyncResultCallback callback);

	/**
	 * The Get Service Properties operation Get the hosted service properties
	 * with the service name and embedDetail(true/false).
	 * 
	 * @param serviceName
	 *            The name of your hosted service.
	 * @param embedDetail
	 *            When the embedDetail parameter is specified, the management
	 *            service returns properties for all deployments of the service,
	 *            as well as for the service itself. The default value is false.
	 * @return
	 */
	public abstract HostedServiceProperties getHostedServiceProperties(
			String serviceName, boolean embedDetail);

	/**
	 * The Get Operation Status Get the operation status with request id.
	 * 
	 * @param requestId
	 *            the request id of operation
	 * @return
	 */
	public abstract OperationStatus getOperationStatus(String requestId);

	/**
	 * The List Storage Accounts operation lists the storage accounts available
	 * under the current subscription.
	 * 
	 * @return
	 */
	public abstract List<StorageAccount> listStorageAccounts();

	/**
	 * The List Affinity Accounts operation lists the affinity groups available
	 * under the current subscription.
	 * 
	 * @return
	 */
	public abstract List<AffinityGroup> listAffinityGroups();

	/**
	 * The Get Storage Account Keys operation lists the storage account keys for
	 * the specified storage account.
	 * 
	 * @param serviceName
	 *            The name of the desired storage account.
	 * @return
	 */
	public abstract StorageAccountKey getStorageAccountKeys(String serviceName);

	/**
	 * The Regenerate Keys operation regenerates the primary or secondary access
	 * key for the specified storage account.
	 * 
	 * @param serviceName
	 *            The name of the desired storage account.
	 * @param type
	 *            Indicate what key should be regenerated.
	 * @return
	 */
	public abstract StorageAccountKey regenerateKeys(String serviceName,
			KeyType type);

	/**
	 * The Get Storage Account Properties operation get the storage account
	 * properties.
	 * 
	 * @param serviceName
	 *            The name of the desired storage account.
	 * @return
	 */
	public abstract StorageAccountProperties getStorageAccountProperties(
			String serviceName);

	/**
	 * The Get Affinity Group Properties operation get the Affinity Group
	 * Properties with group name.
	 * 
	 * @param groupName
	 *            The name of the desired affinity group
	 * @return
	 */
	public abstract AffinityGroupProperties getAffinityGroupProperties(
			String groupName);

	/**
	 * The Create Affinity Group operation creates a new affinity group for the
	 * specified subscription.
	 * 
	 * @param groupName
	 *            The name of the affinity group that is unique to the
	 *            subscription.
	 * @param label
	 *            Required. A label for the affinity group. The label may be up
	 *            to 100 characters in length.
	 * @param description
	 *            Optional. A description for the affinity group. The
	 *            description may be up to 1024 characters in length.
	 * @param location
	 *            The location where the affinity group will be created. To list
	 *            available locations, use the List Locations operation.
	 */
	public abstract void createAffinityGroup(String groupName, String label,
			String description, String location);

	/**
	 * The Delete Affinity Group operation deletes an affinity group in the
	 * specified subscription.
	 * 
	 * @param groupName
	 *            The name of the desired affinity group
	 */
	public abstract void deleteAffinityGroup(String groupName);

	/**
	 * The Update Affinity Group operation updates the label and/or the
	 * description for an affinity group for the specified subscription.
	 * 
	 * 
	 * @param groupName
	 *            The name of the desired affinity group
	 * @param label
	 *            Required. A label for the affinity group. The label may be up
	 *            to 100 characters in length.
	 * @param description
	 *            Optional. A description for the affinity group. The
	 *            description may be up to 1024 characters in length.
	 */
	public abstract void updateAffinityGroup(String groupName, String label,
			String description);

	/**
	 * The Update Deployment Status operation is update the status of the
	 * Deployment.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param slotType
	 *            Deployment slot type, either staging or production
	 * @param status
	 *            The deployment status, either running or suspended
	 * @param callback
	 * @return
	 */
	public abstract String updateDeplymentStatus(String serviceName,
			DeploymentSlotType slotType, UpdateStatus status,
			AsyncResultCallback callback);

	/**
	 * The Update Deployment Status operation is update the status of the
	 * Deployment.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param deploymentName
	 *            The unique name of your deployment
	 * @param status
	 *            The deployment status, either running or suspended
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String updateDeplymentStatus(String serviceName,
			String deploymentName, UpdateStatus status,
			AsyncResultCallback callback);

	/**
	 * The Upgrade Deployment operation initiates an upgrade. The Upgrade
	 * Deployment operation is an asynchronous operation. To determine whether
	 * the Management service has finished processing the request, call Get
	 * Operation Status.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param slotType
	 *            Deployment slot type, either staging or production
	 * @param configuration
	 *            It contains information for upgrade, including the service
	 *            package url, the role name etc.
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String upgradeDeployment(String serviceName,
			DeploymentSlotType slotType, UpgradeConfiguration configuration,
			AsyncResultCallback callback);

	/**
	 * The Upgrade Deployment operation initiates an upgrade. The Upgrade
	 * Deployment operation is an asynchronous operation. To determine whether
	 * the Management service has finished processing the request, call Get
	 * Operation Status.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param deploymentName
	 *            The name of deployment
	 * @param configuration
	 *            It contains information for upgrade, including the service
	 *            package url, the role name etc.
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String upgradeDeployment(String serviceName,
			String deploymentName, UpgradeConfiguration configuration,
			AsyncResultCallback callback);

	/**
	 * The Walk Upgrade Domain operation is walk upgrade the deployment.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param deploymentName
	 *            The name of deployment
	 * @param domainId
	 *            An integer value that identifies the upgrade domain to walk.
	 *            Upgrade domains are identified with a zero-based index: the
	 *            first upgrade domain has an ID of 0, the second has an ID of
	 *            1, and so on.
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String walkUpgradeDomain(String serviceName,
			String deploymentName, int domainId, AsyncResultCallback callback);

	/**
	 * The Walk Upgrade Domain operation is walk upgrade the deployment.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param slotType
	 *            Deployment slot type, either staging or production
	 * @param domainId
	 *            An integer value that identifies the upgrade domain to walk.
	 *            Upgrade domains are identified with a zero-based index: the
	 *            first upgrade domain has an ID of 0, the second has an ID of
	 *            1, and so on.
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String walkUpgradeDomain(String serviceName,
			DeploymentSlotType slotType, int domainId,
			AsyncResultCallback callback);

	/**
	 * The Change Deployment Configuration operation is to change the
	 * configuration of the Deployment.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param slotType
	 *            Deployment slot type, either staging or production
	 * @param configurationFileUrl
	 *            The file path of service configuration file.
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String changeDeploymentConfiguration(String serviceName,
			DeploymentSlotType slotType, String configurationFileUrl,
			AsyncResultCallback callback);

	/**
	 * The Change Deployment Configuration operation is to change the
	 * configuration of the Deployment.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param deploymentName
	 *            The name of deployment
	 * @param configurationFileUrl
	 *            The file path of service configuration file.
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String changeDeploymentConfiguration(String serviceName,
			String deploymentName, String configurationFileUrl,
			AsyncResultCallback callback);

	/**
	 * The Swap Deployment operation is to swap deployment.
	 * 
	 * @param serviceName
	 *            The name of hosted service.
	 * @param productName
	 *            The name of the production deployment.
	 * @param sourceName
	 *            The name of the source deployment, which will be swapped
	 *            withproduction.
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String swapDeployment(String serviceName,
			String productName, String sourceName, AsyncResultCallback callback);

	/**
	 * The List Certificates operation lists all certificates associated with
	 * the specified hosted service.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @return
	 */
	public abstract List<Certificate> listCertificates(String serviceName);

	/**
	 * The Get Certificate operation returns the public data for the specified
	 * certificate.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param thumbprintAlgorithm
	 *            The algorithm for the certificate's thumbprint
	 * @param thumbprint
	 *            The hexadecimal representation of the thumbprint
	 * @return
	 */
	public abstract Certificate getCertificate(String serviceName,
			String thumbprintAlgorithm, String thumbprint);

	/**
	 * The Delete Certificate operation deletes a certificate from the
	 * subscription's certificate store. see
	 * http://msdn.microsoft.com/en-us/library/ee460803.aspx
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param thumbprintAlgorithm
	 *            The algorithm for the certificate's thumbprint
	 * @param thumbprint
	 *            The hexadecimal representation of the thumbprint
	 * @return
	 */
	public abstract void deleteCertificate(String serviceName,
			String thumbprintAlgorithm, String thumbprint);

	/**
	 * The Add Certificate operation adds a certificate to the subscription. See
	 * http://msdn.microsoft.com/en-us/library/ee460817.aspx for more detail.
	 * 
	 * @param serviceName
	 *            The name of hosted service
	 * @param data
	 *            The content of certificate file in byte array.
	 * @param format
	 *            The format of certificate file
	 * @param password
	 *            The password of certificate file
	 */
	public abstract void addCertificate(String serviceName, byte[] data,
			CertificateFormat format, String password);

	/**
	 * The List Operating Systems operation lists the versions of the guest
	 * operating system that are currently available in Windows Azure. Currently
	 * Windows Azure supports two operating system families: the Windows Azure
	 * guest operating system that is substantially compatible with Windows
	 * Server 2008 SP2, and the Windows Azure guest operating system that is
	 * substantially compatible with Windows Server 2008 R2.
	 * http://msdn.microsoft.com/en-us/library/ff684168.aspx
	 */
	public abstract List<OperatingSystem> listOperatingSystems();

	/**
	 * Set the interval in milliseconds for polling operation status in
	 * asynchronous operations.
	 * 
	 * @param interval
	 *            the interval in milliseconds for polling operation status
	 */
	public abstract void setPollStatusInterval(int interval);

	/**
	 * Get the maximum thead count for polling operation status.
	 * 
	 * @return
	 */
	public abstract int getMaximumThreadCount();

	/**
	 * Set the maximum thead count for polling operation status. Note that all
	 * running tasks in pool will be stopped.
	 * 
	 * @param maximumThreadCount
	 */
	public abstract void setMaximumThreadCount(int maximumThreadCount);

	/**
	 * Get the interval in milliseconds for polling operation status in
	 * asynchronous operations.
	 * 
	 * @return the interval in milliseconds for polling operation status
	 */
	public abstract int getPollStatusInterval();

	/**
	 * The List OS Families operation lists the guest operating system families
	 * available in Windows Azure, and also lists the operating system versions
	 * available for each family. Currently Windows Azure supports two operating
	 * system families: the Windows Azure guest operating system that is
	 * substantially compatible with Windows Server 2008 SP2, and the Windows
	 * Azure guest operating system that is substantially compatible with
	 * Windows Server 2008 R2.
	 * 
	 * @return a list of OperatingSystemFamily
	 */
	public abstract List<OperatingSystemFamily> listOSFamilies();

	/**
	 * The List Locations operation lists all of the data center locations that
	 * are valid for your subscription.
	 * 
	 * @return a list of locations
	 */
	public abstract List<Location> listLocations();

	/**
	 * The Create Hosted Service operation creates a new hosted service in
	 * Windows Azure.
	 * 
	 * @param serviceName
	 *            Required. A name for the hosted service that is unique to the
	 *            subscription. It is also used as the prefix of service URL.
	 * @param label
	 *            Required. A label for the hosted service. The label may be up to 100
	 *            characters in length.
	 * @param description
	 *            Optional. A description for the hosted service. The description may be
	 *            up to 1024 characters in length.
	 * @param location
	 *            The location where the hosted service will be created. To list
	 *            available locations, use the listLocations operation.
	 * @param affinityGroup
	 *            The name of an existing affinity group associated with this
	 *            subscription. To list available affinity groups, use the
	 *            listAffinityGroups operation.
	 */
	public abstract void createHostedService(String serviceName, String label,
			String description, String location, String affinityGroup);

	/**
	 * The Delete Hosted Service operation deletes the specified hosted service
	 * from Windows Azure.
	 * 
	 * @param serviceName
	 *            The name of your hosted service.
	 */
	public abstract void deleteHostedService(String serviceName);

	/**
	 * The Update Hosted Service operation updates the label and/or the
	 * description for a hosted service in Windows Azure.
	 * 
	 * @param serviceName
	 *            The name of your hosted service.
	 * @param label
	 *            A label for the hosted service. The label may be up to 100
	 *            characters in length. You must specify a value for either
	 *            Label or Description, or for both.
	 * @param description
	 *            A description for the hosted service. The description may be
	 *            up to 1024 characters in length. You must specify a value for
	 *            either Label or Description, or for both.
	 * 
	 */
	public abstract void updateHostedService(String serviceName, String label,
			String description);

	/**
	 * The Reboot Role Instance operation requests a reboot of a role instance
	 * that is running in a deployment. The Reboot Role Instance operation is an
	 * asynchronous operation.
	 * 
	 * @param serviceName
	 *            The name of your hosted service.
	 * @param slotType
	 *            Deployment slot type, either staging or production
	 * @param roleInstanceName
	 *            The name of role instance
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String rebootRoleInstance(String serviceName,
			DeploymentSlotType slotType, String roleInstanceName,
			AsyncResultCallback callback);

	/**
	 * The Reboot Role Instance operation requests a reboot of a role instance
	 * that is running in a deployment. The Reboot Role Instance operation is an
	 * asynchronous operation.
	 * 
	 * @param serviceName
	 *            The name of your hosted service.
	 * @param deploymentName
	 *            The name of deployment
	 * @param roleInstanceName
	 *            The name of role instance
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String rebootRoleInstance(String serviceName,
			String deploymentName, String roleInstanceName,
			AsyncResultCallback callback);

	/**
	 * The Reimage Role Instance operation requests a reimage of a role instance
	 * that is running in a deployment. The Reimage Role Instance operation is
	 * an asynchronous operation.
	 * 
	 * @param serviceName
	 *            The name of your hosted service.
	 * @param slotType
	 *            Deployment slot type, either staging or production
	 * @param roleInstanceName
	 *            The name of role instance
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String reimageRoleInstance(String serviceName,
			DeploymentSlotType slotType, String roleInstanceName,
			AsyncResultCallback callback);

	/**
	 * The Reimage Role Instance operation requests a reimage of a role instance
	 * that is running in a deployment. The Reimage Role Instance operation is
	 * an asynchronous operation.
	 * 
	 * @param serviceName
	 *            The name of your hosted service.
	 * @param deploymentName
	 *            The name of deployment
	 * @param roleInstanceName
	 *            The name of role instance
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.
	 * @return
	 */
	public abstract String reimageRoleInstance(String serviceName,
			String deploymentName, String roleInstanceName,
			AsyncResultCallback callback);

	/**
	 * The List Subscription Operations operation returns a list of create,
	 * update, and delete operations that were performed on a subscription
	 * during the specified timeframe. See
	 * http://msdn.microsoft.com/en-us/library/gg715318.aspx for more detail.
	 * 
	 * @param startTimeFrame
	 *            The start of the timeframe to begin listing subscription
	 *            operations in UTC format. This parameter and the
	 *            <end-of-timeframe> parameter indicate the timeframe to
	 *            retrieve subscription operations. This parameter cannot
	 *            indicate a start date of more than 90 days in the past.
	 * @param endTimeFrame
	 *            The end of the timeframe to begin listing subscription
	 *            operations in UTC format. This parameter and the
	 *            <start-of-timeframe> parameter indicate the timeframe to
	 *            retrieve subscription operations.
	 * @return
	 */
	public abstract List<SubscriptionOperation> listSubscriptionOperations(
			String startTimeFrame, String endTimeFrame);

	/**
	 * The List Subscription Operations operation returns a list of create,
	 * update, and delete operations that were performed on a subscription
	 * during the specified timeframe. See
	 * http://msdn.microsoft.com/en-us/library/gg715318.aspx for more detail.
	 * 
	 * @param startTimeFrame
	 *            The start of the timeframe to begin listing subscription
	 *            operations in UTC format. This parameter and the
	 *            <end-of-timeframe> parameter indicate the timeframe to
	 *            retrieve subscription operations. This parameter cannot
	 *            indicate a start date of more than 90 days in the past.
	 * @param endTimeFrame
	 *            The end of the timeframe to begin listing subscription
	 *            operations in UTC format. This parameter and the
	 *            <start-of-timeframe> parameter indicate the timeframe to
	 *            retrieve subscription operations.
	 * @param filter
	 *            Optional. Returns subscription operations only for the
	 *            specified object type and object ID. This parameter must be
	 *            set equal to the URL value for performing an HTTP GET on the
	 *            object. If no object is specified, a filter is not applied.
	 * @param operationStatus
	 *            Optional. Returns subscription operations only for the
	 *            specified result status, either Succeeded, Failed, or
	 *            InProgress. This filter can be combined with the
	 *            ObjectIdFilter to select subscription operations for an object
	 *            with a specific result status. If no result status is
	 *            specified, a filter is not applied.
	 * @param limit
	 *            The number of Subscription Operation that should be returned.
	 *            If you specify -1, it will return all Subscription Operations
	 *            during the specified timeframe.
	 * @return
	 */
	public abstract List<SubscriptionOperation> listSubscriptionOperations(
			String startTimeFrame, String endTimeFrame, ObjectIdFilter filter,
			OperationState operationStatus, int limit);

	/**
	 * The Create Storage Account operation creates a new storage account in
	 * Windows Azure.
	 * 
	 * @param accountName
	 *            Required. A name for the storage account that is unique to the
	 *            subscription.
	 * @param label
	 *            Required. A label for the storage account. The label may be up
	 *            to 100 characters in length.
	 * @param description
	 *            Optional. A description for the storage account. The
	 *            description may be up to 1024 characters in length.
	 * @param location
	 *            Required if AffinityGroup is not specified. The location where
	 *            the storage account is created. Specify either Location or
	 *            AffinityGroup, but not both.
	 * @param affinityGroup
	 *            Required if Location is not specified. The name of an existing
	 *            affinity group in the specified subscription. Specify either
	 *            Location or AffinityGroup, but not both.
	 * @param callback
	 *            The callback instance will be notified when the asynchronous
	 *            operation is completed.            
	 */
	public abstract void createStorageAccount(String accountName, String label,
			String description, String location, String affinityGroup, AsyncResultCallback callback);

	/**
	 * The Delete Storage Account operation deletes the specified storage
	 * account from Windows Azure.
	 * 
	 * @param accountName
	 *            the name of the storage account
	 */
	public abstract void deleteStorageAccount(String accountName);

	/**
	 * The Update Storage Account operation updates the label and/or the
	 * description for a storage account in Windows Azure.
	 * 
	 * @param accountName
	 *            the name of the storage account
	 * 
	 * @param label
	 *            Optional. A label for the storage account. The label may be up
	 *            to 100 characters in length. You must specify a value for
	 *            either Label or Description, or for both.
	 * @param description
	 *            Optional. A description for the storage account. The
	 *            description may be up to 1024 characters in length. You must
	 *            specify a value for either Label or Description, or for both.
	 */
	public abstract void updateStorageAccount(String accountName, String label,
			String description);

	

}
