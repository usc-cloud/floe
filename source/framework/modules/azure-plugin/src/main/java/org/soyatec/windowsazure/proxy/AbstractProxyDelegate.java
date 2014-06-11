package org.soyatec.windowsazure.proxy;

import org.soyatec.windowsazure.internal.util.HttpUtilities;

/**
 * 
 * This class will support proxy settings when making http requests.
 */
public abstract class AbstractProxyDelegate {

	/**
	 * Indicate whether the proxy settings exists.
	 * 
	 * @return
	 */
	public boolean isProxyEnabled() {
		return HttpUtilities.proxyExists();
	}

	/**
	 * Remove proxy settings if exists.
	 */
	public void disableProxy() {
		HttpUtilities.removeProxyConfig();
	}

	/**
	 * Update the proxy settings.
	 * 
	 * @param proxy
	 */
	public void setProxyConfiguration(ProxyConfiguration proxy) {
		HttpUtilities.setProxy(proxy);
	}
}
