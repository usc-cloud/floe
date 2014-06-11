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
package org.soyatec.windowsazure.authenticate;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.soyatec.windowsazure.constants.AuthenticationSchemeNames;
import org.soyatec.windowsazure.error.StorageClientException;
import org.soyatec.windowsazure.error.StorageErrorCode;
import org.soyatec.windowsazure.internal.MessageCanonicalizer;
import org.soyatec.windowsazure.internal.MessageCanonicalizer2;
import org.soyatec.windowsazure.internal.ResourceUriComponents;
import org.soyatec.windowsazure.internal.constants.HeaderNames;
import org.soyatec.windowsazure.internal.constants.HttpStatusConstant;
import org.soyatec.windowsazure.internal.constants.XmsVersion;
import org.soyatec.windowsazure.internal.util.Logger;
import org.soyatec.windowsazure.internal.util.Utilities;

/**
 * Objects of this class contain the credentials (name and key) of a storage
 * account.
 * 
 * http://msdn.microsoft.com/en-us/library/dd179428.aspx All authenticated
 * requests must include the Coordinated Universal Time (UTC) timestamp for the
 * request. You can specify the timestamp either in the x-ms-date header, or in
 * the standard HTTP/HTTPS Date header. If both headers are specified on the
 * request, the value of x-ms-date is used as the request's time of creation.
 * The storage services ensure that a request is no older than 15 minutes by the
 * time it reaches the service. This guards against certain security attacks,
 * including replay attacks. When this check fails, the server returns response
 * code 403 (Forbidden).
 * 
 */
public class SharedKeyCredentials {

	private static final String HMACSHA256 = "HmacSHA256";
	private static final String UTF8_CHARSET = "UTF-8";

	private String accountName;
	private byte[] key;

	protected SharedKeyCredentials() {
	}

	/**
	 * Create a SharedKeyCredentials object given an account name and a shared
	 * key.
	 * 
	 * @param accountName
	 * @param key
	 */
	public SharedKeyCredentials(String accountName, byte[] key) {
		this.accountName = accountName;
		this.key = key;
	}

	/**
	 * Get the api version from request.
	 * 
	 * @param request
	 * @return api version
	 */
	private String getApiVersion(HttpRequest request) {
		Header header = request.getFirstHeader(HeaderNames.ApiVersion);
		if (header == null)
			return "";
		return header.getValue();
	}

	/**
	 * Signs the request appropriately to make it an authenticated request. Note
	 * that this method takes the URI components as decoding the URI components
	 * requires the knowledge of whether the URI is in path-style or host-style
	 * and a host-suffix if it's host-style.
	 * 
	 * @param request
	 * @param uriComponents
	 */
	public void signRequest(HttpRequest request,
			ResourceUriComponents uriComponents) {
		if (request == null) {
			throw new IllegalArgumentException("request is null.");
		}

		String version = getApiVersion(request);

		String message = version
				.equalsIgnoreCase(XmsVersion.VERSION_2009_09_19) ? MessageCanonicalizer2
				.canonicalizeHttpRequest(request, uriComponents)
				: MessageCanonicalizer.canonicalizeHttpRequest(request,
						uriComponents);
		String computedBase64Signature = computeMacSha(message);
		request.addHeader(HeaderNames.Authorization, MessageFormat.format(
				"{0} {1}:{2}",
				AuthenticationSchemeNames.SharedKeyAuthSchemeName,
				getAccountName(), computedBase64Signature));
	}

	/**
	 * Signs requests using the SharedKeyLite authentication scheme with is used
	 * for the table storage service.
	 * 
	 * @param request
	 * @param uriComponents
	 */
	public void signRequestForSharedKeyLite(HttpRequest request,
			ResourceUriComponents uriComponents) {
		if (request == null) {
			throw new IllegalArgumentException("request is null.");
		}
		// add the date header to the request
		String dateString = Utilities.getUTCTime();
		request.addHeader(HeaderNames.StorageDateTime, dateString);
		// compute the signature and add the authentication scheme
		String message = MessageCanonicalizer
				.canonicalizeHttpRequestForSharedKeyLite(request,
						uriComponents, dateString);
		String computedBase64Signature = computeMacSha(message);
		request.addHeader(HeaderNames.Authorization, MessageFormat.format(
				"{0} {1}:{2}",
				AuthenticationSchemeNames.SharedKeyLiteAuthSchemeName,
				accountName, computedBase64Signature));
	}

	/**
	 * Create the signature with permissions, start, expiry,
	 * canonicalizedResource and identifier.
	 * 
	 * @param permissions
	 * @param start
	 * @param expiry
	 * @param canonicalizedResource
	 * @param identifier
	 * @return signSharedAccessUrl
	 */
	public String signSharedAccessUrl(String permissions, String start,
			String expiry, String canonicalizedResource, String identifier) {
		StringBuilder stringToSign = new StringBuilder();
		stringToSign.append(trimToEmpty(permissions)).append('\n');
		stringToSign.append(trimToEmpty(start)).append('\n');
		stringToSign.append(trimToEmpty(expiry)).append('\n');
		stringToSign.append(trimToEmpty(canonicalizedResource)).append('\n');
		stringToSign.append(trimToEmpty(identifier));
		return computeMacSha(stringToSign.toString());
	}

	/**
	 * If the string is null, return empty string, else removes white spaces
	 * from both sides of a string.
	 * 
	 * @param str
	 * @return string after trim.
	 */
	private String trimToEmpty(String str) {
		if (str == null) {
			return "";
		} else {
			return str.trim();
		}
	}

	private String computeMacSha(String canonicalizedString) {
		Mac mac;
		try {
			if (getKey() == null) {
				throw new StorageClientException(
						StorageErrorCode.AccountNotFound,
						"The Windows Azure storage account credentials contains invalid values.",
						HttpStatusConstant.DEFAULT_STATUS, null, null);
			}
			mac = Mac.getInstance(HMACSHA256);
			mac.init(new SecretKeySpec(getKey(), mac.getAlgorithm()));
			byte[] dataToMAC = canonicalizedString.getBytes(UTF8_CHARSET);
			mac.update(dataToMAC);
			byte[] result = mac.doFinal();
			return Base64.encode(result);
		} catch (NoSuchAlgorithmException e) {
			Logger.error("NoSuchAlgorithmException", e);
		} catch (InvalidKeyException e) {
			Logger.error("InvalidKeyException", e);
		} catch (UnsupportedEncodingException e) {
			Logger.error("UnsupportedEncodingException", e);
		}
		return null;
	}

	/**
	 * @return the account name
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * @return key
	 */
	public byte[] getKey() {
		return key;
	}

}
