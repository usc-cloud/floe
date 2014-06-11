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
package org.soyatec.windowsazure.queue;

import java.net.URI;
import java.util.List;

import org.soyatec.windowsazure.authenticate.SharedKeyCredentials;
import org.soyatec.windowsazure.authenticate.StorageAccountInfo;
import org.soyatec.windowsazure.blob.IRetryPolicy;
import org.soyatec.windowsazure.blob.internal.RetryPolicies;
import org.soyatec.windowsazure.error.StorageException;
import org.soyatec.windowsazure.internal.constants.StandardPortalEndpoints;
import org.soyatec.windowsazure.internal.util.TimeSpan;
import org.soyatec.windowsazure.proxy.AbstractProxyDelegate;
import org.soyatec.windowsazure.queue.internal.Message;
import org.soyatec.windowsazure.queue.internal.QueueStorageRest;

/**
 * The entry point of the queue storage API
 * 
 */
public abstract class QueueStorageClient extends AbstractProxyDelegate{
	/**
	 * Indicates whether to use/generate path-style or host-style URIs
	 */
	private boolean usePathStyleUris;

	/**
	 * The base URI of the blob storage service
	 */
	private URI baseUri;

	/**
	 * The name of the storage account
	 */
	private String accountName;

	/**
	 * Authentication key used for signing requests
	 */
	protected String base64Key;

	/**
	 * The time out for each request to the storage service.
	 */
	private TimeSpan timeout;

	/**
	 * The retry policy used for retrying requests
	 */
	private IRetryPolicy retryPolicy;

	/**
	 * The default timeout
	 */
	public static final TimeSpan DefaultTimeout = TimeSpan.fromSeconds(30);

	/**
	 * The default retry policy
	 */
	public static final IRetryPolicy DefaultRetryPolicy = RetryPolicies
			.noRetry();

	/**
	 * The user account this queue lives in.
	 */
	private StorageAccountInfo accountInfo;

	/**
	 * Credentials (name and key) of a storage account
	 */
	private SharedKeyCredentials credentials;

	/**
	 * Factory method for QueueStorage
	 * 
	 * @param baseUri
	 *            The base URI of the blob storage service
	 * @param usePathStyleUris
	 *            If true, path-style URIs
	 *            (http://baseuri/accountname/containername/objectname) are
	 *            used. If false host-style URIs
	 *            (http://accountname.baseuri/containername/objectname) are
	 *            used, where baseuri is the URI of the service. If null, the
	 *            choice is made automatically: path-style URIs if host name
	 *            part of base URI is an IP addres, host-style otherwise.
	 * @param accountName
	 *            The name of the storage account
	 * @param base64Key
	 *            Authentication key used for signing requests
	 * @return A newly created QueueStorage instance
	 */
	public static QueueStorageClient create(URI baseUri,
			boolean usePathStyleUris, String accountName, String base64Key) {
		return new QueueStorageRest(baseUri, usePathStyleUris, accountName,
				base64Key);
	}
	
	/**
	 * Check if the Queue exists.
	 * 
	 * @param queueName
	 *            of the Table.
	 * @return true if the Queue exists, false otherwise.
	 * @throws StorageException
	 */
	public abstract boolean isQueueExist(String queueName)	throws StorageException;

	/**
	 * Factory method for QueueStorage
	 * 
	 * @param usePathStyleUris
	 *            If true, path-style URIs
	 *            (http://baseuri/accountname/containername/objectname) are
	 *            used. If false host-style URIs
	 *            (http://accountname.baseuri/containername/objectname) are
	 *            used, where baseuri is the URI of the service. If null, the
	 *            choice is made automatically: path-style URIs if host name
	 *            part of base URI is an IP addres, host-style otherwise.
	 * @param accountName
	 *            The name of the storage account
	 * @param base64Key
	 *            Authentication key used for signing requests
	 * @return A newly created QueueStorage instance
	 */
	public static QueueStorageClient create(boolean usePathStyleUris,
			String accountName, String base64Key) {
		URI hostUri = null;
		if (usePathStyleUris) {
			hostUri = URI.create(StandardPortalEndpoints.HttpProtocolPrefix
					+ StandardPortalEndpoints.DevQueueEndpoint);
		} else {
			hostUri = URI.create(StandardPortalEndpoints.HttpProtocolPrefix
					+ StandardPortalEndpoints.QueueStorageEndpoint);
		}
		return new QueueStorageRest(hostUri, usePathStyleUris, accountName,
				base64Key);
	}

	/**
	 * Factory method for QueueStorage
	 * 
	 * @param accountInfo
	 *            Account information
	 * @return A newly created QueueStorage instance
	 */
	public static QueueStorageClient create(StorageAccountInfo accountInfo) {
		return new QueueStorageRest(accountInfo.getBaseUri(), accountInfo
				.isUsePathStyleUris(), accountInfo.getAccountName(),
				accountInfo.getBase64Key());
	}
	
	/**
	 * Create a default message
	 * @return
	 */
	static public IMessage createMessage() {
		return new Message();
	}

	/**
	 * Create a default message with content
 	 * 
	 * @param content
	 * @return
	 */
	static public IMessage createMessage(String content) {
		return new Message(content);
	}

	/**
	 * Create a default message with content
	 * 
	 * @param content
	 * @return
	 */
	static public IMessage createMessage(byte[] content) {
		return new Message(content);
	}

	/**
	 * Constructor for queue storage.
	 * 
	 * @param baseUri
	 *            The base URI of the blob storage service
	 * @param usePathStyleUris
	 *            Indicates whether to use/generate path-style or host-style
	 *            URIs
	 * @param accountName
	 *            The name of the storage account
	 * @param base64Key
	 *            Authentication key used for signing requests
	 */
	protected QueueStorageClient(URI baseUri, boolean usePathStyleUris,
			String accountName, String base64Key) {
		this.baseUri = baseUri;
		this.usePathStyleUris = usePathStyleUris;
		this.accountName = accountName;
		this.base64Key = base64Key;
		this.timeout = DefaultTimeout;
		this.retryPolicy = DefaultRetryPolicy;
		this.accountInfo = new StorageAccountInfo(baseUri, usePathStyleUris,
				accountName, base64Key);
	}

	/**
	 * Get a reference to a Queue object with a specified name. This method does
	 * not make a call to the queue service.
	 * 
	 * @param queueName
	 *            The name of the queue
	 * @return A newly created queue object
	 */
	public abstract IQueue getQueue(String queueName)
			throws StorageException;

	/**
	 * Lists the queues within the account.
	 * 
	 * @return A list of queues
	 */
	public List<String> listQueues() throws StorageException {
		return listQueues(null);
	}

	/**
	 * Lists the queues within the account that start with the given prefix.
	 * 
	 * @param prefix
	 *            If prefix is null returns all queues.
	 * @return A list of queues.
	 */
	public abstract List<String> listQueues(String prefix)
			throws StorageException;

	/**
	 * Returns the time out for each request to the storage service.
	 * 
	 * @return The time out for each request to the storage service.
	 */
	public TimeSpan getTimeout() {
		return timeout;
	}

	/**
	 * Returns whether to use/generate path-style or host-style URIs.
	 * 
	 * @return whether to use/generate path-style or host-style URIs.
	 */
	public boolean isUsePathStyleUris() {
		return usePathStyleUris;
	}

	/**
	 * Specify whether to use/generate path-style or host-style URIs.
	 * 
	 * @param usePathStyleUris
	 *            whether to use/generate path-style or host-style URIs
	 */
	public void setUsePathStyleUris(boolean usePathStyleUris) {
		this.usePathStyleUris = usePathStyleUris;
	}

	/**
	 * Returns the base URI of the blob storage service.
	 * 
	 * @return The base URI of the blob storage service.
	 */
	public URI getBaseUri() {
		return baseUri;
	}

	/**
	 * Return the name of the storage account.
	 * 
	 * @return The name of the storage account.
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * Returns authentication key used for signing requests.
	 * 
	 * @return Authentication key used for signing requests.
	 */
	public String getBase64Key() {
		return base64Key;
	}

	/**
	 * Returns the retry policy used for retrying requests.
	 * 
	 * @return The retry policy used for retrying requests.
	 */
	public IRetryPolicy getRetryPolicy() {
		return retryPolicy;
	}

	/**
	 * Specify the retry policy used for retrying requests.
	 * 
	 * @param retryPolicy
	 *            The retry policy used for retrying requests
	 */
	public void setRetryPolicy(IRetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
	}

	/**
	 * Returns the user account this queue lives in.
	 * 
	 * @return The user account this queue lives in.
	 */
	public StorageAccountInfo getAccountInfo() {
		return accountInfo;
	}

	/**
	 * Specify the user account this queue lives in.
	 * 
	 * @param accountInfo
	 *            The user account this queue lives in.
	 */
	public void setAccountInfo(StorageAccountInfo accountInfo) {
		this.accountInfo = accountInfo;
	}

	/**
	 * Returns credentials (name and key) of a storage account.
	 * 
	 * @return credentials (name and key) of a storage account.
	 */
	public SharedKeyCredentials getCredentials() {
		return credentials;
	}

	/**
	 * Specify the credentials (name and key) of a storage account.
	 * 
	 * @param credentials
	 *            The credentials (name and key) of a storage account
	 */
	public void setCredentials(SharedKeyCredentials credentials) {
		this.credentials = credentials;
	}

	/**
	 * Specify the time out for each request to the storage service.
	 * 
	 * @param timeout
	 *            The time out for each request to the storage service.
	 */
	public void setTimeout(TimeSpan timeout) {
		this.timeout = timeout;
	}
}
