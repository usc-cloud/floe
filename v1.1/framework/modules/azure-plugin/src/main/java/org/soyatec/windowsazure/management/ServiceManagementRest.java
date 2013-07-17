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

import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.dom4j.Document;
import org.dom4j.Element;
import org.soyatec.windowsazure.authenticate.Base64;
import org.soyatec.windowsazure.blob.io.BlobStream;
import org.soyatec.windowsazure.constants.ConstChars;
import org.soyatec.windowsazure.error.StorageException;
import org.soyatec.windowsazure.internal.constants.HeaderNames;
import org.soyatec.windowsazure.internal.constants.HttpMethod;
import org.soyatec.windowsazure.internal.constants.HttpWebResponse;
import org.soyatec.windowsazure.internal.constants.ServiceXmlElementNames;
import org.soyatec.windowsazure.internal.constants.XmsVersion;
import org.soyatec.windowsazure.internal.util.HttpUtilities;
import org.soyatec.windowsazure.internal.util.Utilities;
import org.soyatec.windowsazure.internal.util.xml.XPathQueryHelper;
import org.soyatec.windowsazure.internal.util.xml.XmlUtil;
import org.soyatec.windowsazure.internal.util.xml.XmlValidationUtil;
import org.xml.sax.SAXException;

/**
 * See http://msdn.microsoft.com/en-us/library/ee460799.aspx for description.
 * 
 */
public class ServiceManagementRest extends ServiceManagement {

	private static final String SERVICE_CONFIGURATION_SCHEMA_XSD = "ServiceConfigurationSchema.xsd";

	private static final String APPLICATION_XML = "application/xml";

	private static final String MEIDA_TYPE_TEXT_XML = "text/xml";

	private static final String KEYS_ACTION_REGENERATE = "/keys?action=regenerate";
	private static final String SERVICE_KEYS = "/keys";
	private static final String SERVICES_AFFINITYGROUPS = "/affinitygroups";
	private static final String SERVICES_HOSTEDSERVICES = "/services/hostedservices";
	private static final String SERVICES_STORAGESERVICE = "/services/storageservices";
	private static final String SERVICES_VIRTUALMACHINE = SERVICES_HOSTEDSERVICES;
	
	private static final String DEPLOYMENT_SLOTS = "/deploymentslots";
	private static final String ROLE_INSTANCES = "/roleinstances";
	private static final String DEPLOYMENTS = "/deployments";

	private static final String CERTIFICATES = "/certificates";
	private static final String OPERATIONS = "/operations";

	private static final String OPERATING_SYSTEMS = "/operatingsystems";
	private static final String OPERATING_SYSTEM_FAMILIES = "/operatingsystemfamilies";

	private static final String LOCATIONS = "/locations";

	private static final String CREATE_DEPLOYMENT_BODY = "<?xml version=\"1.0\"?><CreateDeployment xmlns=\"http://schemas.microsoft.com/windowsazure\"><Name>{0}</Name><PackageUrl>{1}</PackageUrl><Label>{3}</Label><Configuration>{2}</Configuration><StartDeployment>{4}</StartDeployment><TreatWarningsAsError>{5}</TreatWarningsAsError></CreateDeployment>";
	// private static final String UPDATE_HOSTED_SERVICE =
	// "<?xml version=\"1.0\"?><UpdateHostedService xmlns=\"http://schemas.microsoft.com/windowsazure\"><Label>{0}</Label><Description>{1}</Description></UpdateHostedService>";
	private static final String UPDATE_STORAGE_SERVICE = "<?xml version=\"1.0\"?><UpdateStorageServiceInput xmlns=\"http://schemas.microsoft.com/windowsazure\">{0}{1}</UpdateStorageServiceInput>";
	private static final String CREATE_STORAGE_SERVICE = "<?xml version=\"1.0\"?><CreateStorageServiceInput xmlns=\"http://schemas.microsoft.com/windowsazure\"><ServiceName>{0}</ServiceName><Label>{1}</Label><Description>{2}</Description>{3}</CreateStorageServiceInput>";
	private static final String UPDATE_HOSTED_SERVICE = "<?xml version=\"1.0\"?><UpdateHostedService xmlns=\"http://schemas.microsoft.com/windowsazure\">{0}{1}</UpdateHostedService>";
	private static final String CREATE_HOSTED_SERVICE = "<?xml version=\"1.0\"?><CreateHostedService xmlns=\"http://schemas.microsoft.com/windowsazure\"><ServiceName>{0}</ServiceName><Label>{1}</Label><Description>{2}</Description>{3}</CreateHostedService>";
	private static final String CREATE_AFFINITY_GROUP = "<?xml version=\"1.0\"?><CreateAffinityGroup xmlns=\"http://schemas.microsoft.com/windowsazure\"><Name>{0}</Name><Label>{1}</Label><Description>{2}</Description><Location>{3}</Location></CreateAffinityGroup>";
	private static final String UPDATE_AFFINITY_GROUP = "<?xml version=\"1.0\"?><UpdateAffinityGroup xmlns=\"http://schemas.microsoft.com/windowsazure\"><Label>{0}</Label>{1}</UpdateAffinityGroup>";
	private static final String HOSTED_SERVICE_LOCATION = "<Location>{0}</Location>";
	private static final String HOSTED_SERVICE_AFFINITYGROUP = "<AffinityGroup>{0}</AffinityGroup>";
    private static final String START_ROLE_OPERATIONS ="<?xml version=\"1.0\"?><StartRoleOperation xmlns=\"http://schemas.microsoft.com/windowsazure\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "      <OperationType>StartRoleOperation</OperationType>\n" +
            "</StartRoleOperation>";

	private static final String CREATE_VIRTUALMACHINE = "<?xml version=\"1.0\"?>"+
"<Deployment xmlns=\"http://schemas.microsoft.com/windowsazure\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">"+
"   <Name>{0}</Name>"+
"   <Label>{1}</Label>      "+
"   <RoleList>"+
"      <Role>"+
"         <RoleName>{2}</RoleName>"+
"         <RoleType>PersistentVMRole</RoleType>"+      
"         <ConfigurationSets>"+
"            <ConfigurationSet> "+
"                <ConfigurationSetType>LinuxProvisioningConfiguration</ConfigurationSetType>"+
"                <HostName>{3}</HostName>"+
"                <UserName>{4}</UserName> "+
"                <UserPassword>{5}</UserPassword>"+ 
"                <DisableSshPasswordAuthentication>false</DisableSshPasswordAuthentication>"+
"            </ConfigurationSet>        "+
"            <ConfigurationSet> "+
"                <ConfigurationSetType>NetworkConfiguration</ConfigurationSetType>"+
"                <InputEndpoints>"+
"                   <InputEndpoint>"+
"                        <EnableDirectServerReturn>true</EnableDirectServerReturn>"+                
"                        <LoadBalancedEndpointSetName></LoadBalancedEndpointSetName>"+
"                        <LocalPort>22</LocalPort>"+
"                        <Name>ssh</Name>"+
"                        <Port>22</Port>   "+                     
"                        <Protocol>TCP</Protocol>"+                    
"                    </InputEndpoint>"+
"                </InputEndpoints>"+                     
"            </ConfigurationSet>"+
"         </ConfigurationSets>"+
"         <!--<DataVirtualHardDisks>"+
"            <DataVirtualHardDisk>"+
"               <HostCaching>ReadOnly</HostCaching>"+ 
"               <DiskLabel>{1}_disk</DiskLabel>      "+      
"               <DiskName>{1}_diskname</DiskName>"+
"               <Lun>0</Lun>"+
"               <LogicalDiskSizeInGB>10</LogicalDiskSizeInGB>"+                           
"            </DataVirtualHardDisk>"+
"         </DataVirtualHardDisks>-->"+
"         <OSVirtualHardDisk>"+
"            <HostCaching>ReadOnly</HostCaching>"+    
"            <DiskLabel>{1}_os_disk</DiskLabel>"+
"            <DiskName>{1}_os_diskname</DiskName>"+                    
"            <!--<MediaLink>{6}</MediaLink>-->"+
"            <SourceImageName>{7}</SourceImageName>"+
"         </OSVirtualHardDisk>      "+
"         <RoleSize>{8}</RoleSize>"+      
"      </Role>"+
"    </RoleList>"+    
"</Deployment>";

	

	private int pollStatusInterval = 1000; // milliseconds

	/**
	 * 
	 * Construct a new ServiceManagementRest object with a subscription id,
	 * store file key, store password key, trust store file, trust store
	 * password and certificate alias.
	 * 
	 */
	public ServiceManagementRest(String subscriptionId, String keyStoreFile,
			String keyStorePassword, String trustStoreFile,
			String trustStorePassword, String certificateAlias)
			throws Exception {
		super(subscriptionId, keyStoreFile, keyStorePassword, trustStoreFile,
				trustStorePassword, certificateAlias, null);
	}

	/**
	 * 
	 * Construct a new ServiceManagementRest object with a subscription id,
	 * store file key, store password key, trust store file, trust store
	 * password, certificate alias and endPointHost.
	 * 
	 */
	public ServiceManagementRest(String subscriptionId, String keyStoreFile,
			String keyStorePassword, String trustStoreFile,
			String trustStorePassword, String certificateAlias,
			String endPointHost) throws Exception {
		super(subscriptionId, keyStoreFile, keyStorePassword, trustStoreFile,
				trustStorePassword, certificateAlias, endPointHost);
	}

	/**
	 * The List Hosted Services operation lists the hosted services available
	 * under the current subscription.
	 */
	@Override
	public List<HostedService> listHostedServices() {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES),
				HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseHostServiceResponse(response
						.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The Get Deployment operation returns configuration information, status,
	 * and system properties for the specified deployment.
	 * 
	 * @param serviceName
	 * @param type
	 * @return111
	 */
	public Deployment getDeployment(String serviceName, DeploymentSlotType type) {
		HttpUriRequest request = createHttpRequest(HttpMethod.Get, serviceName,
				type, "");
		return getDeployment(request);
	}

	/**
	 * The Get Deployment operation get the deployment with the service name and
	 * deployment name.
	 * 
	 * @param serviceName
	 * @param deploymentName
	 * @return
	 */
	public Deployment getDeployment(String serviceName, String deploymentName) {
		HttpUriRequest request = createHttpRequest(HttpMethod.Get, serviceName,
				deploymentName, "");
		return getDeployment(request);
	}

	/**
	 * The Get Deployment operation get the deployment with the request.
	 * 
	 * @param request
	 * @return
	 */
	private Deployment getDeployment(HttpUriRequest request) {
		try {
			request.setHeader(HeaderNames.ApiVersion,
					XmsVersion.VERSION_2011_06_01);
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());

			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseDeploymentResponse(response
						.getStream());
			} else if (response.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
				return null;
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The Update Deployment Status operation initiates a change in deployment
	 * status. The Update Deployment Status operation is an asynchronous
	 * operation.
	 */
	public String updateDeplymentStatus(String serviceName,
			DeploymentSlotType type, UpdateStatus status,
			AsyncResultCallback callback) {
		HttpUriRequest request = createHttpRequest(HttpMethod.Post,
				serviceName, type, "/?comp=status");
		return updateDeplymentStatus(request, status, callback);
	}

	private HttpUriRequest createHttpRequest(String method, String serviceName,
			DeploymentSlotType type, String params) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + DEPLOYMENT_SLOTS
						+ ConstChars.Slash + type.getLiteral().toLowerCase()
						+ params), method);
		return request;
	}

	private HttpUriRequest createHttpRequest(String method, String serviceName,
			String deploymentName, String params) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + DEPLOYMENTS
						+ ConstChars.Slash + deploymentName + params), method);
		return request;
	}

	/**
	 * The Update Deployment Status operation is update the status of the
	 * Deployment.
	 * 
	 * @param serviceName
	 * @param deploymentName
	 * @param status
	 * @param callback
	 * @return
	 */
	public String updateDeplymentStatus(String serviceName,
			String deploymentName, UpdateStatus status,
			AsyncResultCallback callback) {
		HttpUriRequest request = createHttpRequest(HttpMethod.Post,
				serviceName, deploymentName, "/?comp=status");
		return updateDeplymentStatus(request, status, callback);
	}

	private String changeDeploymentConfiguration(HttpUriRequest request,
			String configurationBase64, AsyncResultCallback callback) {
		String template = "<ChangeConfiguration xmlns=\"http://schemas.microsoft.com/windowsazure\"><Configuration>{0}</Configuration></ChangeConfiguration>";
		// String configurationFile = readBase64(configurationFileUrl);

		request.setHeader(HeaderNames.ContentType, APPLICATION_XML);// MEIDA_TYPE_TEXT_XML);
		return sendAsyncPostRequest(request, callback, template,
				configurationBase64);
		// "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTE2Ij8+DQo8U2VydmljZUNvbmZpZ3VyYXRpb24geG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIgeG1sbnM6eHNkPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSIgc2VydmljZU5hbWU9IiIgeG1sbnM9Imh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vU2VydmljZUhvc3RpbmcvMjAwOC8xMC9TZXJ2aWNlQ29uZmlndXJhdGlvbiI+DQogIDxSb2xlIG5hbWU9IldlYlJvbGUiPg0KICAgIDxDb25maWd1cmF0aW9uU2V0dGluZ3MgLz4NCiAgICA8SW5zdGFuY2VzIGNvdW50PSIxIiAvPg0KICAgIDxDZXJ0aWZpY2F0ZXMgLz4NCiAgPC9Sb2xlPg0KPC9TZXJ2aWNlQ29uZmlndXJhdGlvbj4=");
	}

	private String updateDeplymentStatus(HttpUriRequest request,
			UpdateStatus status, AsyncResultCallback callback) {
		String template = "<UpdateDeploymentStatus xmlns=\"http://schemas.microsoft.com/windowsazure\"><Status>{0}</Status></UpdateDeploymentStatus>";
		request.setHeader(HeaderNames.ContentType, APPLICATION_XML);
		return sendAsyncPostRequest(request, callback, template,
				status.getLiteral());
	}

	/**
	 * The Swap Deployment operation initiates a virtual IP swap between the
	 * staging and production deployment slots for a service. If the service is
	 * currently running in the staging environment, it will be swapped to the
	 * production environment. If it is running in the production environment,
	 * it will be swapped to staging.
	 * 
	 * @param serviceName
	 * @param productName
	 * @param sourceName
	 * @param callback
	 * @return
	 */
	public String swapDeployment(String serviceName, String productName,
			String sourceName, AsyncResultCallback callback) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName), HttpMethod.Post);
		String template = "<Swap xmlns=\"http://schemas.microsoft.com/windowsazure\"><Production>{0}</Production><SourceDeployment>{1}</SourceDeployment></Swap>";
		request.setHeader(HeaderNames.ContentType, APPLICATION_XML);
		return sendAsyncPostRequest(request, callback, template, productName,
				sourceName);
	}

	/**
	 * @param request
	 * @param status
	 * @param callback
	 * @param template
	 * @return
	 */
	private String sendAsyncPostRequest(HttpUriRequest request,
			AsyncResultCallback callback, String template, Object... arugments) {
		String body = MessageFormat.format(template, arugments);
		((HttpEntityEnclosingRequest) request).setEntity(new ByteArrayEntity(
				body.getBytes()));
		return sendAsynchronousRequest(request, callback);
	}

	/**
	 * 
	 * The Walk Upgrade Domain operation specifies the next upgrade domain to be
	 * walked during an in-place upgrade. For more information on in-place
	 * upgrades.
	 * 
	 * @param serviceName
	 * @param type
	 * @param domainId
	 * @param callback
	 * @return
	 */
	public String walkUpgradeDomain(String serviceName,
			DeploymentSlotType type, int domainId, AsyncResultCallback callback) {
		HttpUriRequest request = createHttpRequest(HttpMethod.Post,
				serviceName, type, "/?comp=walkupgradedomain");
		return walkUpgradeDomain(request, domainId, callback);
	}

	/**
	 * The Walk Upgrade Domain operation is walk upgrade the deployment.
	 * 
	 * @param serviceName
	 * @param deploymentName
	 * @param domainId
	 * @param callback
	 * @return
	 */
	public String walkUpgradeDomain(String serviceName, String deploymentName,
			int domainId, AsyncResultCallback callback) {
		HttpUriRequest request = createHttpRequest(HttpMethod.Post,
				serviceName, deploymentName, "/?comp=walkupgradedomain");
		return walkUpgradeDomain(request, domainId, callback);
	}

	private String walkUpgradeDomain(HttpUriRequest request, int domainId,
			AsyncResultCallback callback) {
		// <WalkUpgradeDomain xmlns="http://schemas.microsoft.com/windowsazure">
		// <UpgradeDomain>upgrade-domain-id</UpgradeDomain>
		// </WalkUpgradeDomain>
		String template = "<WalkUpgradeDomain xmlns=\"http://schemas.microsoft.com/windowsazure\"><UpgradeDomain>{0}</UpgradeDomain></WalkUpgradeDomain>";
		request.setHeader(HeaderNames.ContentType, APPLICATION_XML);
		return sendAsyncPostRequest(request, callback, template, domainId);
	}

	/**
	 * The Change Deployment Configuration operation initiates a change to the
	 * deployment configuration.
	 * 
	 * @param serviceName
	 * @param type
	 * @param configurationFileUrl
	 * @param callback
	 * @return
	 */
	public String changeDeploymentConfiguration(String serviceName,
			DeploymentSlotType type, String configurationFileUrl,
			AsyncResultCallback callback) {
		HttpUriRequest request = createHttpRequest(HttpMethod.Post,
				serviceName, type, "/?comp=config");
		return changeDeploymentConfiguration(request,
				readBase64(configurationFileUrl), callback);
	}

	/**
	 * The Change Deployment Configuration operation initiates a change to the
	 * deployment configuration.
	 * 
	 * @param serviceName
	 * @param type
	 * @param configurationFileStream
	 * @param callback
	 * @return
	 */
	public String changeDeploymentConfiguration(String serviceName,
			DeploymentSlotType type, BlobStream configurationFileStream,
			AsyncResultCallback callback) {
		HttpUriRequest request = createHttpRequest(HttpMethod.Post,
				serviceName, type, "/?comp=config");
		return changeDeploymentConfiguration(request, configurationFileStream,
				callback);
	}

	/**
	 * The Change Deployment Configuration operation is to change the
	 * configuration of the Deployment.
	 * 
	 * @param serviceName
	 * @param deploymentName
	 * @param configurationFileStream
	 * @param callback
	 * @return
	 */
	public String changeDeploymentConfiguration(String serviceName,
			String deploymentName, BlobStream configurationFileStream,
			AsyncResultCallback callback) {
		HttpUriRequest request = createHttpRequest(HttpMethod.Post,
				serviceName, deploymentName, "/?comp=config");
		return changeDeploymentConfiguration(request, configurationFileStream,
				callback);
	}

	private String changeDeploymentConfiguration(HttpUriRequest request,
			BlobStream configurationFileStream, AsyncResultCallback callback) {
		try {
			String content = readBase64(configurationFileStream.getBytes());
			return changeDeploymentConfiguration(request, content, callback);
		} catch (IOException e) {
			throw new StorageException(e);
		}
	}

	/**
	 * The Change Deployment Configuration operation is to change the
	 * configuration of the Deployment.
	 * 
	 * @param serviceName
	 * @param deploymentName
	 * @param configurationFileUrl
	 * @param callback
	 * @return
	 */
	public String changeDeploymentConfiguration(String serviceName,
			String deploymentName, String configurationFileUrl,
			AsyncResultCallback callback) {
		HttpUriRequest request = createHttpRequest(HttpMethod.Post,
				serviceName, deploymentName, "/?comp=config");
		return changeDeploymentConfiguration(request,
				readBase64(configurationFileUrl), callback);
	}

	// private String readBase64(String configurationFileUrl) {
	// try {
	// String encoding = Utilities.detectEncoding(Utilities.getUrl(
	// configurationFileUrl).openStream());
	// byte[] bytes = Utilities.getBytesFromUrl(configurationFileUrl,
	// encoding);
	// return readBase64(bytes, encoding);
	// } catch (IOException e) {
	// throw new IllegalStateException("Configuration file is not valid, "
	// + e.getMessage());
	// }
	// }
	//
	// private String readBase64(byte[] bytes) {
	// return readBase64(bytes,"utf-8");
	// }
	//
	// private String readBase64(byte[] bytes, String encoding) {
	// try {
	// XmlValidationUtil.validate(bytes, this.getClass().getClassLoader()
	// .getResource(SERVICE_CONFIGURATION_SCHEMA_XSD));
	// String content = new String(bytes,encoding);
	// content = content.replaceAll("\r\n", "");
	// return Base64.encode(content.getBytes(encoding));
	// } catch (IOException e) {
	// throw new IllegalStateException("Configuration file is not valid, "
	// + e.getMessage());
	// } catch (SAXException ex) {
	// throw new IllegalStateException("Configuration file is not valid, "
	// + ex.getMessage());
	// }
	// }

	private String readBase64(String configurationFileUrl) {
		try {
			String content = Utilities.getContentFromUrl(configurationFileUrl);
			return encodeBase64(content);
		} catch (IOException e) {
			throw new IllegalStateException("Configuration file is not valid, "
					+ e.getMessage());
		}
	}

	private String readBase64(byte[] bytes) {
		return encodeBase64(new String(bytes));
	}

	private String encodeBase64(String content) {
		try {
			content = content.replaceAll("\r\n", "");
			byte[] bytes = content.getBytes("utf-8");
			XmlValidationUtil.validate(bytes, this.getClass().getClassLoader()
					.getResource(SERVICE_CONFIGURATION_SCHEMA_XSD));

			return Base64.encode(bytes);
		} catch (IOException e) {
			throw new IllegalStateException("Configuration file is not valid, "
					+ e.getMessage());
		} catch (SAXException ex) {
			throw new IllegalStateException("Configuration file is not valid, "
					+ ex.getMessage());
		}
	}

	/**
	 * The Upgrade Deployment operation initiates an upgrade. The Upgrade
	 * Deployment operation is an asynchronous operation. To determine whether
	 * the Management service has finished processing the request, call Get
	 * Operation Status.
	 * 
	 * @param serviceName
	 * @param type
	 * @param configuration
	 * @param callback
	 * @return
	 */
	public String upgradeDeployment(String serviceName,
			DeploymentSlotType type, UpgradeConfiguration configuration,
			AsyncResultCallback callback) {
		HttpUriRequest request = createHttpRequest(HttpMethod.Post,
				serviceName, type, "/?comp=upgrade");
		return upgradeDeployment(request, configuration, callback);
	}

	/**
	 * The Upgrade Deployment operation initiates an upgrade. The Upgrade
	 * Deployment operation is an asynchronous operation. To determine whether
	 * the Management service has finished processing the request, call Get
	 * Operation Status.
	 * 
	 * @param serviceName
	 * @param deploymentName
	 * @param configuration
	 * @param callback
	 * @return
	 */
	public String upgradeDeployment(String serviceName, String deploymentName,
			UpgradeConfiguration configuration, AsyncResultCallback callback) {
		HttpUriRequest request = createHttpRequest(HttpMethod.Post,
				serviceName, deploymentName, "/?comp=upgrade");
		return upgradeDeployment(request, configuration, callback);
	}

	private String upgradeDeployment(HttpUriRequest request,
			UpgradeConfiguration configuration, AsyncResultCallback callback) {
		configuration.validate();
		StringBuilder buf = new StringBuilder();
		buf.append("<UpgradeDeployment xmlns=\"http://schemas.microsoft.com/windowsazure\">");
		buf.append("<Mode>" + configuration.getMode().getLiteral() + "</Mode>");
		buf.append("<PackageUrl>" + configuration.getPackageBlobUrl()
				+ "</PackageUrl>");

		String content = "";
		if (configuration.getConfigurationFileUrl() != null)
			content = readBase64(configuration.getConfigurationFileUrl());
		else if (configuration.getConfigurationFileStream() != null) {
			try {
				content = readBase64(configuration.getConfigurationFileStream()
						.getBytes());
			} catch (IOException e) {
				throw new StorageException(e);
			}
		}

		buf.append("<Configuration>" + content // readBase64(configuration.getConfigurationFileUrl())
				+ "</Configuration>");
		buf.append("<Label>" + configuration.getBase64Label() + "</Label>");
		if (configuration.getUpgradeRole() != null) {
			buf.append("<RoleToUpgrade>" + configuration.getUpgradeRole()
					+ "</RoleToUpgrade>");
		}
		buf.append("</UpgradeDeployment>");
		request.setHeader(HeaderNames.ContentType, APPLICATION_XML);
		((HttpEntityEnclosingRequest) request).setEntity(new ByteArrayEntity(
				buf.toString().getBytes()));
		return sendAsynchronousRequest(request, callback);
	}

	/**
	 * The Delete Deployment operation Delete the deployment with the service
	 * name, deploymentSlotType and asyncResultCallback.
	 * 
	 * @param serviceName
	 * @param type
	 * @param callback
	 * @return
	 */
	public String deleteDeployment(String serviceName, DeploymentSlotType type,
			AsyncResultCallback callback) {
		HttpUriRequest request = createHttpRequest(HttpMethod.Delete,
				serviceName, type, "");
		return sendAsynchronousRequest(request, callback);
	}

	/**
	 * The Delete Deployment operation Delete the deployment with the service
	 * name, deployment name and asyncResultCallback.
	 * 
	 * @param serviceName
	 * @param deploymentName
	 * @param callback
	 * @return
	 */
	public String deleteDeployment(String serviceName, String deploymentName,
			AsyncResultCallback callback) {
		HttpUriRequest request = createHttpRequest(HttpMethod.Delete,
				serviceName, deploymentName, "");
		return sendAsynchronousRequest(request, callback);
	}

	/**
	 * Request-id is returned
	 * 
	 * @param request
	 * @param callback
	 * @return
	 */
	private String sendAsynchronousRequest(HttpUriRequest request,
			AsyncResultCallback callback) {
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				String requestId = getHeaderValueFromResponse(response,
						HeaderNames.ManagementRequestId);
				if (callback != null)
					observeOperationStatus(requestId, callback,
							this.isBlocking());
				return requestId;
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	private String getHeaderValueFromResponse(HttpWebResponse response,
			String headerName) {
		return response.getHeader(headerName);
	}

	/**
	 * The Get Hosted Service Properties operation retrieves system properties
	 * for the specified hosted service. These properties include the service
	 * name and service type; the name of the affinity group to which the
	 * service belongs, or its location if it is not part of an affinity group;
	 * and optionally, information on the service's deployments. When the
	 * request sets the embed-detail parameter to true, the response body
	 * includes additional details on the service's deployments:
	 */
	public HostedServiceProperties getHostedServiceProperties(
			String serviceName, boolean embedDetail) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + "?embed-detail="
						+ embedDetail), HttpMethod.Get);
		request.setHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2011_06_01);
		
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseHostedPropertiesResponse(response
						.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * For more detail, see: <a
	 * href="http://msdn.microsoft.com/en-us/library/ee460791.aspx">Service
	 * status</a>
	 * 
	 * </br>
	 * 
	 * If the request was successful and the asynchronous operation is being
	 * processed, the service returns status code 202 (Accept). Note that this
	 * status code does not indicate whether the operation itself has been
	 * processed successfully, but only that the request has been received by
	 * the service. If the return status code is not 202 (Accept), then the
	 * request must be retried.
	 * 
	 * @param response
	 * @return
	 */
	protected boolean isRequestAccepted(HttpWebResponse response) {
		return response.getStatusCode() == HttpStatus.SC_OK
				|| response.getStatusCode() == HttpStatus.SC_ACCEPTED;
	}

	/**
	 * The List Affinity Groups operation lists the affinity groups associated
	 * with the specified subscription.
	 */
	@Override
	public List<AffinityGroup> listAffinityGroups() {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_AFFINITYGROUPS),
				HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseAffinityGroupResponse(response
						.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The List Certificates operation lists all certificates associated with
	 * the specified hosted service.
	 * 
	 * @param serviceName
	 * @return
	 */
	public List<Certificate> listCertificates(String serviceName) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + CERTIFICATES),
				HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseCertificateResponse(response
						.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The Get Certificate operation returns the public data for the specified
	 * certificate.
	 * 
	 * @param serviceName
	 * @param thumbprintAlgorithm
	 *            the algorithm for the certificate's thumbprint
	 * @param thumbprint
	 *            the hexadecimal representation of the thumbprint
	 * @return
	 */
	public Certificate getCertificate(String serviceName,
			String thumbprintAlgorithm, String thumbprint) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + CERTIFICATES
						+ ConstChars.Slash + thumbprintAlgorithm + "-"
						+ thumbprint), HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				Document load = XmlUtil.load(response.getStream());
				Element element = load.getRootElement();
				return XPathQueryHelper.parseCertificate(element);
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The Delete Certificate operation deletes a certificate from the
	 * subscription's certificate store. see
	 * http://msdn.microsoft.com/en-us/library/ee460803.aspx
	 * 
	 * @param serviceName
	 * @param thumbprintAlgorithm
	 *            the algorithm for the certificate's thumbprint
	 * @param thumbprint
	 *            the hexadecimal representation of the thumbprint
	 * @return
	 */
	public void deleteCertificate(String serviceName,
			String thumbprintAlgorithm, String thumbprint) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + CERTIFICATES
						+ ConstChars.Slash + thumbprintAlgorithm + "-"
						+ thumbprint), HttpMethod.Delete);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return;
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * The Add Certificate operation adds a certificate to the subscription.
	 * http://msdn.microsoft.com/en-us/library/ee460817.aspx
	 * 
	 * @param serviceName
	 * @param data
	 * @param format
	 * @param password
	 */
	@Override
	public void addCertificate(String serviceName, byte[] data,
			CertificateFormat format, String password) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + CERTIFICATES),
				HttpMethod.Post);
		String template = "<CertificateFile xmlns=\"http://schemas.microsoft.com/windowsazure\"><Data>{0}</Data><CertificateFormat>{1}</CertificateFormat><Password>{2}</Password></CertificateFile>";
		request.setHeader(HeaderNames.ContentType, APPLICATION_XML);
		String body = MessageFormat.format(template, Base64.encode(data),
				format.getLiteral(), password == null ? "" : password);
		((HttpEntityEnclosingRequest) request).setEntity(new ByteArrayEntity(
				body.getBytes()));
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return;
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

	}

	/**
	 * list the OperatingSystem' Versions
	 * 
	 * @return a list of OperatingSystem type data
	 */
	public List<OperatingSystem> listOperatingSystems() {
		HttpUriRequest request = HttpUtilities.createHttpRequest(
				URI.create(getBaseUrl() + OPERATING_SYSTEMS), HttpMethod.Get);
		request.addHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2010_10_28);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseOperatingSystemResponse(response
						.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The Get Storage Keys operation returns the primary and secondary access
	 * keys for the specified storage account.
	 */
	public StorageAccountKey getStorageAccountKeys(String serviceName) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_STORAGESERVICE
						+ ConstChars.Slash + serviceName + SERVICE_KEYS),
				HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return convertServiceToKeys(response);
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Help method
	 * 
	 * @param response
	 * @return
	 */
	private StorageAccountKey convertServiceToKeys(HttpWebResponse response) {
		StorageAccount service = XPathQueryHelper
				.parseStorageServiceKeysResponse(response.getStream());
		if (service != null) {
			StorageAccountKey keys = new StorageAccountKey();
			keys.setPrimaryKey(service.getPrimaryKey());
			keys.setSecondaryKey(service.getSecondaryKey());
			return keys;
		}
		return null;
	}

	/**
	 * The Regenerate Keys operation regenerates the primary or secondary access
	 * key for the specified storage account.
	 */
	public StorageAccountKey regenerateKeys(String serviceName, KeyType type) {
		if (type == null) {
			throw new IllegalArgumentException("Key type null");
		}
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_STORAGESERVICE
						+ ConstChars.Slash + serviceName
						+ KEYS_ACTION_REGENERATE), HttpMethod.Post);
		request.addHeader(HeaderNames.ContentType, MEIDA_TYPE_TEXT_XML);
		try {
			String template = "<RegenerateKeys xmlns=\"http://schemas.microsoft.com/windowsazure\"><KeyType>{0}</KeyType></RegenerateKeys>";
			String body = MessageFormat.format(template, type.getLiteral());
			((HttpEntityEnclosingRequest) request)
					.setEntity(new ByteArrayEntity(body.getBytes()));
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return convertServiceToKeys(response);
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The Get Storage Account Properties operation returns the system
	 * properties for the specified storage account. These properties include:
	 * the address, description, and label of the storage account; and the name
	 * of the affinity group to which the service belongs, or its geo-location
	 * if it is not part of an affinity group.
	 */
	public StorageAccountProperties getStorageAccountProperties(
			String serviceName) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_STORAGESERVICE
						+ ConstChars.Slash + serviceName), HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper
						.parseStorageServicePropertiesResponse(response
								.getStream());
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The List Storage Accounts operation lists the storage accounts available
	 * under the current subscription.
	 */
	@Override
	public List<StorageAccount> listStorageAccounts() {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_STORAGESERVICE),
				HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseStorageServiceResponse(response
						.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;

	}

	/**
	 * The Get Affinity Group Properties operation returns the system properties
	 * associated with the specified affinity group.
	 */
	public AffinityGroupProperties getAffinityGroupProperties(String groupName) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_AFFINITYGROUPS
						+ ConstChars.Slash + groupName), HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper
						.parseAffinityGroupPropertiesResponse(response
								.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Set the interval in milliseconds for polling operation status in
	 * asynchronous operations.
	 * 
	 * @param interval
	 *            the interval in milliseconds for polling operation status
	 */
	public void setPollStatusInterval(int interval) {
		if (interval < 0)
			throw new IllegalArgumentException(
					"Interval cannot be negative numbers!");
		this.pollStatusInterval = interval;
	}

	/**
	 * Get the interval in milliseconds for polling operation status in
	 * asynchronous operations.
	 * 
	 * @return the interval in milliseconds for polling operation status
	 */
	public int getPollStatusInterval() {
		return pollStatusInterval;
	}

	class BlockingExecutor {
		ExecutorService thread = Executors.newSingleThreadExecutor(); // newSingleThreadScheduledExecutor();

		public void observe(final String requestId,
				final AsyncResultCallback callback) {
			Callable<Boolean> runnable = new Callable<Boolean>() {

				public Boolean call() throws Exception {
					while (true) {
						OperationStatus status = getOperationStatus(requestId);

						if (status.getStatus() == OperationState.Failed) {
							if (callback != null)
								callback.onError(status);
							return false;
						} else if (status.getStatus() == OperationState.Succeeded) {
							if (callback != null)
								callback.onSuccess(status);
							return true;
						}
						try {
							Thread.sleep(pollStatusInterval);
						} catch (InterruptedException e) {
							break;
						}
					}
					return false;
				}
			};

			Future<Boolean> result = thread.submit(runnable);
			try {
				result.get(); // blocking
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class NonBlockingExecutor {
		ScheduledExecutorService pool = Executors
				.newScheduledThreadPool(maximumThreadCount);

		public void setPoolSize(int maximumThreadCount) {
			if (!pool.isShutdown() || !pool.isTerminated()) {
				pool.shutdownNow();
			}
			pool = Executors.newScheduledThreadPool(maximumThreadCount);
		}

		public void observe(final String requestId,
				final AsyncResultCallback callback) {
			Callable<Boolean> runnable = new Callable<Boolean>() {

				public Boolean call() throws Exception {
					OperationStatus status = getOperationStatus(requestId);
					 System.out.println(System.currentTimeMillis() + "  " +
					 requestId + " " + status.toString() );

					if (status.getStatus() == OperationState.Failed) {
						if(callback != null)
							callback.onError(status);
						return false;
					} else if (status.getStatus() == OperationState.Succeeded) {
						if(callback != null)
							callback.onSuccess(status);
						return true;
					} else {
						// no result yet, run it again
						delayRun(this, pollStatusInterval);
					}
					return false;
				}
			};

			delayRun(runnable, pollStatusInterval);
		}

		private void delayRun(Callable<Boolean> callable, int pollStatusInterval) {
			pool.schedule(callable, pollStatusInterval, TimeUnit.MILLISECONDS);
		}

	}

	private int maximumThreadCount = 5;
	private BlockingExecutor blockingExectuor = new BlockingExecutor();
	private NonBlockingExecutor nonBlockingExecutor = new NonBlockingExecutor();

	public int getMaximumThreadCount() {
		return maximumThreadCount;
	}

	/**
	 * Note that all running tasks in pool will be stopped.
	 * 
	 * @param maximumThreadCount
	 */
	public void setMaximumThreadCount(int maximumThreadCount) {
		this.maximumThreadCount = maximumThreadCount;
		nonBlockingExecutor.setPoolSize(maximumThreadCount);
	}

	protected void observeOperationStatus(final String requestId,
			final AsyncResultCallback callback, boolean blocked) {
		if (blocked) {
			blockingExectuor.observe(requestId, callback);
		} else {
			nonBlockingExecutor.observe(requestId, callback);
		}
	}

	// protected void observeOperationStatus(final String requestId,
	// final AsyncResultCallback callback, boolean blocked) {
	// Runnable runnable = new Runnable() {
	//
	// public void run() {
	// while (true) {
	// OperationStatus status = getOperationStatus(requestId);
	// if (status.getStatus() == OperationState.Failed) {
	// callback.onError(status);
	// return;
	// } else if (status.getStatus() == OperationState.Succeeded) {
	// callback.onSuccess(status);
	// return;
	// }
	// try {
	// Thread.sleep(pollStatusInterval);
	// } catch (InterruptedException e) {
	// break;
	// }
	// }
	// }
	// };
	//
	// Thread thread = new Thread(runnable);
	// thread.start();
	// if (blocked){
	// try {
	// thread.join();
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	/**
	 * The Get Operation Status operation returns the status of the specified
	 * operation. After calling an asynchronous operation, you can call Get
	 * Operation Status to determine whether the operation has succeed, failed,
	 * or is still in progress.
	 */
	public OperationStatus getOperationStatus(String requestId) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + OPERATIONS + ConstChars.Slash
						+ requestId), HttpMethod.Get);
		try {
			attachHeaderForRequestId(requestId, request);
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseOperationStatusResponse(response
						.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * A value that uniquely identifies a request made against the management
	 * service.
	 * 
	 * @param requestId
	 * @param request
	 */
	private void attachHeaderForRequestId(String requestId,
			HttpUriRequest request) {
		request.addHeader(HeaderNames.ManagementRequestId, requestId);
	}

	/**
	 * The Create Deployment operation uploads a new service package and creates
	 * a new deployment on staging or production.
	 * 
	 * </p>
	 * 
	 * Note that it is possible to call Create Deployment only for a hosted
	 * service that has previously been created via the Windows Azure Developer
	 * Portal. You cannot upload a new hosted service via the Service Management
	 * API.
	 * 
	 * </p>
	 * 
	 * The Create Deployment operation is an asynchronous operation. To
	 * determine whether the management service has finished processing the
	 * request, call Get Operation Status.
	 * 
	 * @param serviceName
	 * @param deploySlotName
	 * @param define
	 * @return
	 */
	public String createDeployment(String serviceName,
			DeploymentSlotType deploySlotName,
			DeploymentConfiguration configuration, AsyncResultCallback callback) {
		if (serviceName == null)
			throw new IllegalArgumentException("Service name is required!");

		if (deploySlotName == null)
			throw new IllegalArgumentException(
					"Deployment slot type is required!");

		if (configuration == null)
			throw new IllegalArgumentException(
					"Deployment configuration is required!");

		configuration.validate();

		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + DEPLOYMENT_SLOTS
						+ ConstChars.Slash
						+ deploySlotName.getLiteral().toLowerCase()),
				HttpMethod.Post);
		request.addHeader(HeaderNames.ContentType, APPLICATION_XML);
		request.setHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2010_10_28);

		String content = "";
		if (configuration.getConfigurationFileUrl() != null)
			content = readBase64(configuration.getConfigurationFileUrl());
		else if (configuration.getConfigurationFileStream() != null) {
			try {
				content = readBase64(configuration.getConfigurationFileStream()
						.getBytes());
			} catch (IOException e) {
				throw new StorageException(e);
			}
		}

		String label = configuration.getBase64Label();
		String body = MessageFormat.format(CREATE_DEPLOYMENT_BODY,
				configuration.getName(), configuration.getPackageBlobUrl(),
				content, label,
				String.valueOf(configuration.isStartDeployment()),
				String.valueOf(configuration.isTreatWarningsAsError())); // configuration.getBase64ConfigurationFile()
		// body =
		// "<?xml version=\"1.0\"?><CreateDeployment xmlns=\"http://schemas.microsoft.com/windowsazure\"><Name>testdep</Name><PackageUrl>http://soyatecdemo.blob.core.windows.net/manageusage/simpletest</PackageUrl><Label>c2ltcGxldGVzdA==</Label><Configuration>PD94bWwgdmVyc2lvbj0iMS4wIj8+PFNlcnZpY2VDb25maWd1cmF0aW9uIHNlcnZpY2VOYW1lPSJzaW1wbGV0ZXN0IiB4bWxucz0iaHR0cDovL3NjaGVtYXMubWljcm9zb2Z0LmNvbS9TZXJ2aWNlSG9zdGluZy8yMDA4LzEwL1NlcnZpY2VDb25maWd1cmF0aW9uIj4gIDxSb2xlIG5hbWU9IldlYlJvbGUiPiAgICA8SW5zdGFuY2VzIGNvdW50PSIxIi8+ICAgIDxDb25maWd1cmF0aW9uU2V0dGluZ3M+ICAgIDwvQ29uZmlndXJhdGlvblNldHRpbmdzPiAgPC9Sb2xlPjwvU2VydmljZUNvbmZpZ3VyYXRpb24+</Configuration></CreateDeployment>";

		((HttpEntityEnclosingRequest) request).setEntity(new ByteArrayEntity(
				body.getBytes()));
		return sendAsynchronousRequest(request, callback);
	}


    @Override
    public String startVMRole(String serviceName, String deploymentName, String roleName, AsyncResultCallback callback) {

        if (deploymentName == null)
            throw new IllegalArgumentException("Deployment name is required!");
        if (serviceName == null)
            throw new IllegalArgumentException("Service Name is required!");
        if(roleName == null) {
            throw new IllegalArgumentException("RoleName is requred!");
        }

        HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
                URI.create(getBaseUrl() + SERVICES_VIRTUALMACHINE
                        + ConstChars.Slash + serviceName + DEPLOYMENTS + ConstChars.Slash +
                        deploymentName + ConstChars.Slash + ROLE_INSTANCES + ConstChars.Slash + roleName +
                        ConstChars.Slash + OPERATIONS),
                HttpMethod.Post);

        request.setHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2012_03_01);
        request.addHeader(HeaderNames.ContentType, APPLICATION_XML);



        String body = START_ROLE_OPERATIONS;

        System.out.println(body);

        ((HttpEntityEnclosingRequest) request).setEntity(new ByteArrayEntity(
                body.getBytes()));
        return sendAsynchronousRequest(request, callback);



    }

    @Override
	public String createLinuxVirtualMachineDeployment(String serviceName, String deploymentName,
			String label, String roleName, String hostName, String username,
			String newPassword, String OSImageLink, String imageName,
			String roleSize, AsyncResultCallback callback) {
		if (deploymentName == null)
			throw new IllegalArgumentException("Service name is required!");
		if (label == null)
			throw new IllegalArgumentException("Service label is required!");

		//if (isServiceExist(deploymentName))
			//throw new IllegalArgumentException("Service already exist!");
/*
		getBaseUrl() + SERVICES_HOSTEDSERVICES
		+ ConstChars.Slash + serviceName + DEPLOYMENTS
		+ ConstChars.Slash + deploymentName + ConstChars.Slash
		+ ROLE_INSTANCES + ConstChars.Slash + roleInstanceName
		+ "?comp=reimage")*/
		
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_VIRTUALMACHINE
						+ ConstChars.Slash + serviceName + DEPLOYMENTS),
				HttpMethod.Post);

		request.setHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2012_03_01);
		request.addHeader(HeaderNames.ContentType, APPLICATION_XML);

		String base64Label = Base64.encode(label.getBytes());
		String body = MessageFormat.format(CREATE_VIRTUALMACHINE, deploymentName, label, roleName, hostName, 
				username, newPassword, OSImageLink, imageName, roleSize);
		
		System.out.println(body);
		
		((HttpEntityEnclosingRequest) request).setEntity(new ByteArrayEntity(
				body.getBytes()));
		return sendAsynchronousRequest(request, callback);
		
				// body =
		// "<?xml version=\"1.0\"?><CreateDeployment xmlns=\"http://schemas.microsoft.com/windowsazure\"><Name>testdep</Name><PackageUrl>http://soyatecdemo.blob.core.windows.net/manageusage/simpletest</PackageUrl><Label>c2ltcGxldGVzdA==</Label><Configuration>PD94bWwgdmVyc2lvbj0iMS4wIj8+PFNlcnZpY2VDb25maWd1cmF0aW9uIHNlcnZpY2VOYW1lPSJzaW1wbGV0ZXN0IiB4bWxucz0iaHR0cDovL3NjaGVtYXMubWljcm9zb2Z0LmNvbS9TZXJ2aWNlSG9zdGluZy8yMDA4LzEwL1NlcnZpY2VDb25maWd1cmF0aW9uIj4gIDxSb2xlIG5hbWU9IldlYlJvbGUiPiAgICA8SW5zdGFuY2VzIGNvdW50PSIxIi8+ICAgIDxDb25maWd1cmF0aW9uU2V0dGluZ3M+ICAgIDwvQ29uZmlndXJhdGlvblNldHRpbmdzPiAgPC9Sb2xlPjwvU2VydmljZUNvbmZpZ3VyYXRpb24+</Configuration></CreateDeployment>";

		
	}
	
	/**
	 * List Operating System Families
	 * 
	 * @return a list of OperatingSystemFamily
	 */
	public List<OperatingSystemFamily> listOSFamilies() {
		HttpUriRequest request = HttpUtilities.createHttpRequest(
				URI.create(getBaseUrl() + OPERATING_SYSTEM_FAMILIES),
				HttpMethod.Get);
		request.addHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2010_10_28);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper
						.parseOperatingSystemFamiliesResponse(response
								.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The Reimage Role Instance operation requests a reimage of a role instance
	 * that is running in a deployment. The Reimage Role Instance operation is
	 * an asynchronous operation.
	 * 
	 * @return
	 */
	public String reimageRoleInstance(String serviceName,
			DeploymentSlotType deploySlotName, String roleInstanceName,
			AsyncResultCallback callback) {
		if (serviceName == null)
			throw new IllegalArgumentException("Service name is required!");

		if (deploySlotName == null)
			throw new IllegalArgumentException(
					"Deployment slot type is required!");

		if (roleInstanceName == null)
			throw new IllegalArgumentException(
					"Role instance name is required!");

		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + DEPLOYMENT_SLOTS
						+ ConstChars.Slash
						+ deploySlotName.getLiteral().toLowerCase()
						+ ConstChars.Slash + ROLE_INSTANCES + ConstChars.Slash
						+ roleInstanceName + "?comp=reimage"), HttpMethod.Post);
		request.setHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2010_10_28);
		return sendAsynchronousRequest(request, callback);

	}

	public String reimageRoleInstance(String serviceName,
			String deploymentName, String roleInstanceName,
			AsyncResultCallback callback) {
		if (serviceName == null)
			throw new IllegalArgumentException("Service name is required!");

		if (deploymentName == null)
			throw new IllegalArgumentException("Deployment name is required!");

		if (roleInstanceName == null)
			throw new IllegalArgumentException(
					"Role instance name is required!");

		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + DEPLOYMENTS
						+ ConstChars.Slash + deploymentName + ConstChars.Slash
						+ ROLE_INSTANCES + ConstChars.Slash + roleInstanceName
						+ "?comp=reimage"), HttpMethod.Post);
		request.setHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2010_10_28);

		return sendAsynchronousRequest(request, callback);

	}

	/**
	 * The Reboot Role Instance operation requests a reboot of a role instance
	 * that is running in a deployment. The Reboot Role Instance operation is an
	 * asynchronous operation.
	 * 
	 * @param serviceName
	 * @param deploySlotName
	 * @param roleInstanceName
	 * @param callback
	 * @return
	 */
	public String rebootRoleInstance(String serviceName,
			DeploymentSlotType deploySlotName, String roleInstanceName,
			AsyncResultCallback callback) {
		if (serviceName == null)
			throw new IllegalArgumentException("Service name is required!");

		if (deploySlotName == null)
			throw new IllegalArgumentException(
					"Deployment slot type is required!");

		if (roleInstanceName == null)
			throw new IllegalArgumentException(
					"Role instance name is required!");

		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + DEPLOYMENT_SLOTS
						+ ConstChars.Slash
						+ deploySlotName.getLiteral().toLowerCase()
						+ ConstChars.Slash + ROLE_INSTANCES + ConstChars.Slash
						+ roleInstanceName + "?comp=reboot"), HttpMethod.Post);
		request.setHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2010_10_28);
		return sendAsynchronousRequest(request, callback);
	}

	public String rebootRoleInstance(String serviceName, String deploymentName,
			String roleInstanceName, AsyncResultCallback callback) {
		if (serviceName == null)
			throw new IllegalArgumentException("Service name is required!");

		if (deploymentName == null)
			throw new IllegalArgumentException("Deployment name is required!");

		if (roleInstanceName == null)
			throw new IllegalArgumentException(
					"Role instance name is required!");

		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + DEPLOYMENTS
						+ ConstChars.Slash + deploymentName + ConstChars.Slash
						+ ROLE_INSTANCES + ConstChars.Slash + roleInstanceName
						+ "?comp=reboot"), HttpMethod.Post);
		request.setHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2010_10_28);
		return sendAsynchronousRequest(request, callback);
	}

	/**
	 * The Delete Hosted Service operation deletes the specified hosted service
	 * from Windows Azure.
	 * 
	 * @param serviceName
	 */
	public void deleteHostedService(String serviceName) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName), HttpMethod.Delete);
		try {
			request.setHeader(HeaderNames.ApiVersion,
					XmsVersion.VERSION_2010_10_28);
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return;
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * List all of the data center locations that are valid for your
	 * subscription.
	 * 
	 * @return a list of locations
	 */
	public List<Location> listLocations() {
		HttpUriRequest request = HttpUtilities.createHttpRequest(
				URI.create(getBaseUrl() + LOCATIONS), HttpMethod.Get);
		request.addHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2010_10_28);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseLocationsResponse(response
						.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The Update Hosted Service operation updates the label and/or the
	 * description for a hosted service in Windows Azure.
	 * 
	 * @param serviceName
	 */
	public void updateHostedService(String serviceName, String label,
			String description) {
		if (label == null && description == null)
			throw new IllegalArgumentException(
					"You must specify a value for either Label or Description, or for both.");

		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName), HttpMethod.Put);

		request.setHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2010_10_28);
		request.addHeader(HeaderNames.ContentType, APPLICATION_XML);

		String base64Label = "";
		if (label != null)
			base64Label = "<Label>" + Base64.encode(label.getBytes())
					+ "</Label>";

		if (description == null)
			description = "";
		else
			description = "<Description>" + description + "</Description>";

		String body = MessageFormat.format(UPDATE_HOSTED_SERVICE, base64Label,
				description);
		try {
			((HttpEntityEnclosingRequest) request)
					.setEntity(new ByteArrayEntity(body.getBytes()));
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (response.getStatusCode() == HttpStatus.SC_OK) {
				return;
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

	}

	/**
	 * The Create Hosted Service operation creates a new hosted service in
	 * Windows Azure.
	 * 
	 * @param serviceName
	 * @param configuration
	 * @return
	 */
	public void createHostedService(String serviceName, String label,
			String description, String location, String affinityGroup) {

		if (serviceName == null)
			throw new IllegalArgumentException("Service name is required!");
		if (label == null)
			throw new IllegalArgumentException("Service label is required!");

		if (isServiceExist(serviceName))
			throw new IllegalArgumentException("Service already exist!");

		if (description == null)
			description = "";

		String lc = "";
		if (location != null && location.length() > 0) {
			lc = MessageFormat.format(HOSTED_SERVICE_LOCATION, location);
			if (!isLocationExist(location))
				throw new IllegalArgumentException("Location is not exist!");
		}
		if (affinityGroup != null && affinityGroup.length() > 0) {
			if (lc.length() > 0) {
				throw new IllegalArgumentException(
						"Specify either Location or AffinityGroup, but not both!");
			} else {
				lc = MessageFormat.format(HOSTED_SERVICE_AFFINITYGROUP,
						affinityGroup);
			}
		}
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_HOSTEDSERVICES),
				HttpMethod.Post);

		request.setHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2010_10_28);
		request.addHeader(HeaderNames.ContentType, APPLICATION_XML);

		String base64Label = Base64.encode(label.getBytes());
		String body = MessageFormat.format(CREATE_HOSTED_SERVICE, serviceName,
				base64Label, description, lc);
		try {
			((HttpEntityEnclosingRequest) request)
					.setEntity(new ByteArrayEntity(body.getBytes()));
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (response.getStatusCode() == HttpStatus.SC_OK
					|| response.getStatusCode() == HttpStatus.SC_CREATED) {
				return;
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	
	
	
	private boolean isServiceExist(String serviceName) {
		boolean rs = false;
		List<HostedService> list = listHostedServices();
		if (list != null && list.size() > 0) {
			for (HostedService service : list) {
				if (service.getName().equalsIgnoreCase(serviceName)) {
					rs = true;
					break;
				}
			}
		}
		return rs;
	}

	private boolean isLocationExist(String location) {
		boolean rs = false;
		List<Location> list = listLocations();
		if (list != null && list.size() > 0) {
			for (Location loc : list) {
				if (loc.getName().equalsIgnoreCase(location)) {
					rs = true;
					break;
				}
			}
		}
		return rs;
	}

	@Override
	public void createAffinityGroup(String groupName, String label,
			String description, String location) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_AFFINITYGROUPS),
				HttpMethod.Post);
		if (groupName == null)
			throw new IllegalArgumentException("Group name is required!");

		if (label == null)
			throw new IllegalArgumentException("Label is required!");

		if (location == null)
			throw new IllegalArgumentException("Location is required!");

		if (description == null)
			description = "";

		try {
			request.setHeader(HeaderNames.ApiVersion,
					XmsVersion.VERSION_2011_02_25);
			request.addHeader(HeaderNames.ContentType, APPLICATION_XML);

			String base64Label = Base64.encode(label.getBytes());
			String body = MessageFormat.format(CREATE_AFFINITY_GROUP,
					groupName, base64Label, description, location);

			((HttpEntityEnclosingRequest) request)
					.setEntity(new ByteArrayEntity(body.getBytes()));
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (response.getStatusCode() == HttpStatus.SC_OK
					|| response.getStatusCode() == HttpStatus.SC_CREATED) {
				return;
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void deleteAffinityGroup(String groupName) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_AFFINITYGROUPS
						+ ConstChars.Slash + groupName), HttpMethod.Delete);
		try {
			request.setHeader(HeaderNames.ApiVersion,
					XmsVersion.VERSION_2011_02_25);
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return;
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void updateAffinityGroup(String groupName, String label,
			String description) {
		if (groupName == null)
			throw new IllegalArgumentException("Group name is required!");

		if (label == null)
			throw new IllegalArgumentException("Label is required!");

		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_AFFINITYGROUPS
						+ ConstChars.Slash + groupName), HttpMethod.Put);

		request.setHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2011_02_25);
		request.addHeader(HeaderNames.ContentType, APPLICATION_XML);

		String base64Label = Base64.encode(label.getBytes());

		if (description == null)
			description = "";
		else
			description = "<Description>" + description + "</Description>";

		String body = MessageFormat.format(UPDATE_AFFINITY_GROUP, base64Label,
				description);
		try {
			((HttpEntityEnclosingRequest) request)
					.setEntity(new ByteArrayEntity(body.getBytes()));
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (response.getStatusCode() == HttpStatus.SC_OK) {
				return;
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public List<SubscriptionOperation> listSubscriptionOperations(
			String startTimeFrame, String endTimeFrame) {
		return listSubscriptionOperations(startTimeFrame, endTimeFrame, null,
				null, -1);
	}

	@Override
	public List<SubscriptionOperation> listSubscriptionOperations(
			String startTimeFrame, String endTimeFrame, ObjectIdFilter filter,
			OperationState operationStatus, int limit) {
		if (startTimeFrame == null)
			throw new IllegalArgumentException("startTimeFrame is required!");
		if (!validateTimeFormat(startTimeFrame))
			throw new IllegalArgumentException(
					"The time specified for parameter startTimeFrame is invalid. Accepted formats are: [4DigitYear]-[2DigitMonth]-[2DigitDay]  or [4DigitYear]-[2DigitMonth]-[2DigitDay]T[2DigitHour]:[2DigitMinute]:2DigitSecond]Z or [4DigitYear]-[2DigitMonth]-[2DigitDay]T[2DigitMinute]:[2DigitSecond]:[7DigitsOfPrecision]Z .");

		if (endTimeFrame == null)
			throw new IllegalArgumentException("endTimeFrame is required!");
		if (!validateTimeFormat(endTimeFrame))
			throw new IllegalArgumentException(
					"The time specified for parameter endTimeFrame is invalid. Accepted formats are: [4DigitYear]-[2DigitMonth]-[2DigitDay]  or [4DigitYear]-[2DigitMonth]-[2DigitDay]T[2DigitHour]:[2DigitMinute]:2DigitSecond]Z or [4DigitYear]-[2DigitMonth]-[2DigitDay]T[2DigitMinute]:[2DigitSecond]:[7DigitsOfPrecision]Z .");

		String url = getBaseUrl() + "/operations?StartTime=" + startTimeFrame
				+ "&EndTime=" + endTimeFrame;

		if (filter != null) {
			url += "&ObjectIdFilter==/" + this.getSubscriptionId()
					+ filter.getFilter();
		}
		if (operationStatus != null) {
			url += "&OperationResultFilter=" + operationStatus.getLiteral();
		}

		String continuationToken = null;
		List<SubscriptionOperation> result = new ArrayList<SubscriptionOperation>();
		do {
			String token = continuationToken == null ? ""
					: "&ContinuationToken=" + continuationToken;
			HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
					URI.create(url + token), HttpMethod.Get);
			request.setHeader(HeaderNames.ApiVersion,
					XmsVersion.VERSION_2011_06_01);

			try {
				HttpWebResponse response = HttpUtilities.getSSLReponse(request,
						getSslSocketFactory());
				if (isRequestAccepted(response)) {
					continuationToken = parseSubscriptionOperation(response,
							result);
				} else {
					HttpUtilities.processUnexpectedStatusCode(response);
				}
			} catch (StorageException we) {
				throw HttpUtilities.translateWebException(we);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			if (limit >= 0 && result.size() >= limit)
				break;
		} while (continuationToken != null);

		if (limit >= 0 && result.size() > limit)
			return result.subList(0, limit);
		else
			return result;
	}

	String parseSubscriptionOperation(HttpWebResponse response,
			List<SubscriptionOperation> result) {
		// <OperationStatus><ID>ae6cbd80-c6d3-47c2-9819-a461a9ec2236</ID><Status>Succeeded</Status><HttpStatusCode>200</HttpStatusCode></OperationStatus>
		Document doc = XmlUtil.load(response.getStream());

		Element root = doc.getRootElement();
		Element operations = root
				.element(ServiceXmlElementNames.SubscriptionOperations);
		if (operations != null) {
			List xmlNodes = operations
					.elements(ServiceXmlElementNames.SubscriptionOperation);
			if (xmlNodes != null) {
				for (Iterator iterator = xmlNodes.iterator(); iterator
						.hasNext();) {

					Element node = (Element) iterator.next();

					String operationId = getChildNodeText(node,
							ServiceXmlElementNames.OperationId);

					String operationObjectId = getChildNodeText(node,
							ServiceXmlElementNames.OperationObjectId);

					String operationName = getChildNodeText(node,
							ServiceXmlElementNames.OperationName);

					List params = node
							.element(ServiceXmlElementNames.OperationParameters)
							.elements(ServiceXmlElementNames.OperationParameter);
					Map operationParameters = parseParameters(params);

					Element caller = node
							.element(ServiceXmlElementNames.OperationCaller);

					boolean usedServiceManagementApi = "true"
							.equalsIgnoreCase(getChildNodeText(
									caller,
									ServiceXmlElementNames.UsedServiceManagementApi)) ? true
							: false;
					String userEmailAddress = getChildNodeText(caller,
							ServiceXmlElementNames.UserEmailAddress);

					String clientIP = getChildNodeText(caller,
							ServiceXmlElementNames.ClientIP);

					String subscriptionCertificateThumbprint = getChildNodeText(
							caller,
							ServiceXmlElementNames.SubscriptionCertificateThumbprint);

					String status = getChildNodeText(node,
							ServiceXmlElementNames.OperationStatus,
							ServiceXmlElementNames.Status);

					String httpStatusCode = getChildNodeText(node,
							ServiceXmlElementNames.OperationStatus,
							ServiceXmlElementNames.HttpStatusCode);

					String operationStartedTime = getChildNodeText(node,
							ServiceXmlElementNames.OperationStartedTime);

					String operationCompletedTime = getChildNodeText(node,
							ServiceXmlElementNames.OperationCompletedTime);

					SubscriptionOperation op = new SubscriptionOperation();
					op.setOperationId(operationId);
					op.setOperationName(operationName);
					op.setOperationObjectId(operationObjectId);
					op.setOperationParameters(operationParameters);
					op.setUsedServiceManagementApi(usedServiceManagementApi);
					op.setUserEmailAddress(userEmailAddress);
					op.setClientIP(clientIP);
					op.setSubscriptionCertificateThumbprint(subscriptionCertificateThumbprint);
					op.setOperationStatus(OperationState.valueOf(status));
					op.setHttpStatusCode(httpStatusCode);
					op.setOperationStartedTime(parseTimestamp(operationStartedTime));
					op.setOperationCompletedTime(parseTimestamp(operationCompletedTime));
					result.add(op);
				}
			}
		}

		Element token = root.element(ServiceXmlElementNames.ContinuationToken);
		if (token != null)
			return token.getText();
		else
			return null;
	}

	public Timestamp parseTimestamp(String operationTime) {
		if (operationTime == null)
			return null;
		Timestamp time = null;
		try {
			time = Utilities.tryGetDateTimeFromTableEntry(operationTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	private Map<String, String> parseParameters(List params) {
		Map<String, String> result = new HashMap<String, String>();
		if (params != null) {
			for (Iterator iterator = params.iterator(); iterator.hasNext();) {
				Element node = (Element) iterator.next();
				String name = getChildNodeText(node,
						ServiceXmlElementNames.OperationParameterName);
				String value = getChildNodeText(node,
						ServiceXmlElementNames.OperationParameterValue);
				result.put(name, value);
			}
		}
		return result;
	}

	private static String getChildNodeText(Element e, String... names) {
		for (int i = 0; i < names.length; i++) {
			e = e.element(names[i]);
			if (e == null)
				break;
		}
		if (e != null)
			return e.getText();
		else
			return null;
	}

	private static boolean validateTimeFormat(String str) {
		Pattern p = Pattern
				.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}(T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]{7})?Z)?$");
		return p.matcher(str).matches();
	}

	@Override
	public void createStorageAccount(String accountName, String label,
			String description, String location, String affinityGroup,
			AsyncResultCallback callback) {
		if (accountName == null)
			throw new IllegalArgumentException(
					"Storage account name is required!");
		if (label == null)
			throw new IllegalArgumentException("Label is required!");

		if (description == null)
			description = "";

		String lc = "";
		if (location != null && location.length() > 0) {
			lc = MessageFormat.format(HOSTED_SERVICE_LOCATION, location);
			if (!isLocationExist(location))
				throw new IllegalArgumentException("Location is not exist!");
		}
		if (affinityGroup != null && affinityGroup.length() > 0) {
			if (lc.length() > 0) {
				throw new IllegalArgumentException(
						"Specify either Location or AffinityGroup, but not both!");
			} else {
				lc = MessageFormat.format(HOSTED_SERVICE_AFFINITYGROUP,
						affinityGroup);
			}
		}
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_STORAGESERVICE),
				HttpMethod.Post);

		request.setHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2011_06_01);
		request.addHeader(HeaderNames.ContentType, APPLICATION_XML);

		String base64Label = Base64.encode(label.getBytes());
		String body = MessageFormat.format(CREATE_STORAGE_SERVICE, accountName,
				base64Label, description, lc);
		((HttpEntityEnclosingRequest) request).setEntity(new ByteArrayEntity(
				body.getBytes()));
		sendAsynchronousRequest(request, callback);
	}

	@Override
	public void deleteStorageAccount(String accountName) {
		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_STORAGESERVICE
						+ ConstChars.Slash + accountName), HttpMethod.Delete);
		try {
			request.setHeader(HeaderNames.ApiVersion,
					XmsVersion.VERSION_2011_06_01);
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return;
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void updateStorageAccount(String accountName, String label,
			String description) {
		if (label == null && description == null)
			throw new IllegalArgumentException(
					"You must specify a value for either Label or Description, or for both.");

		HttpUriRequest request = HttpUtilities.createServiceHttpRequest(
				URI.create(getBaseUrl() + SERVICES_STORAGESERVICE
						+ ConstChars.Slash + accountName), HttpMethod.Put);

		request.setHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2011_06_01);
		request.addHeader(HeaderNames.ContentType, APPLICATION_XML);

		String base64Label = "";
		if (label != null)
			base64Label = "<Label>" + Base64.encode(label.getBytes())
					+ "</Label>";

		if (description == null)
			description = "";
		else
			description = "<Description>" + description + "</Description>";

		String body = MessageFormat.format(UPDATE_STORAGE_SERVICE, description,
				base64Label);
		try {
			((HttpEntityEnclosingRequest) request)
					.setEntity(new ByteArrayEntity(body.getBytes()));
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (response.getStatusCode() == HttpStatus.SC_OK) {
				return;
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	

}
