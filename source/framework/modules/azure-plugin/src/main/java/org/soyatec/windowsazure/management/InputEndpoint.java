package org.soyatec.windowsazure.management;

/**
 * This class represents an input endpoint exposed by the deployment.
 * 
 */
public class InputEndpoint {
	/**
	 * The name of the role that exposes this input endpoint.
	 */
	private String roleName;

	/**
	 * The virtual IP address that this input endpoint is exposed on.
	 */
	private String vip;

	/**
	 * The port this input endpoint is exposed on.
	 */
	private int port;

	public InputEndpoint(String roleName, String vip, int port) {
		super();
		this.roleName = roleName;
		this.vip = vip;
		this.port = port;
	}

	public InputEndpoint() {
		super();
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName
	 *            the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the vip
	 */
	public String getVip() {
		return vip;
	}

	/**
	 * @param vip
	 *            the vip to set
	 */
	public void setVip(String vip) {
		this.vip = vip;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

}
