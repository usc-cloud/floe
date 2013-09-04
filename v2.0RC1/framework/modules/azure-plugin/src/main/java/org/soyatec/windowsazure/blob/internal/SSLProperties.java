/**
 * Copyright  2011 Fujitsu Limited
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

/**
 * This class contains information about keystore and truststore settings, which
 * is used to create https connections.
 * 
 */
public final class SSLProperties {
	private static String keyStore;
	private static String keyStorePasswd;
	private static String trustStore;
	private static String trustStorePasswd;
	private static String keyAlias;
	private static boolean ssl = false;

	public static synchronized void setSSLSettings(String _keystore,
			String _keystorePasswd, String _truststore,
			String _truststorepasswd, String _keyalias) {
		keyStore = _keystore;
		keyStorePasswd = _keystorePasswd;
		trustStore = _truststore;
		trustStorePasswd = _truststorepasswd;
		keyAlias = _keyalias;
		ssl = true;
	}

	public static synchronized void clearSSLSettings() {
		keyStore = "";
		keyStorePasswd = "";
		trustStore = "";
		trustStorePasswd = "";
		keyAlias = "";
		ssl = false;
	}

	public static synchronized String getKeyStore() {
		return keyStore;
	}

	public static synchronized String getKeyStorePasswd() {
		return keyStorePasswd;
	}

	public static synchronized String getTrustStore() {
		return trustStore;
	}

	public static synchronized String getTrustStorePasswd() {
		return trustStorePasswd;
	}

	public static synchronized String getKeyAlias() {
		return keyAlias;
	}

	public static synchronized boolean isSSL() {
		return ssl;
	}
}
