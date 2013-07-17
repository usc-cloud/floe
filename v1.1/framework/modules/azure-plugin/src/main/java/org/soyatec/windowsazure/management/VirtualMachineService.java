package org.soyatec.windowsazure.management;

public class VirtualMachineService {
	/**
	 * Url of hosted service address
	 */
	private String url;

	/**
	 * The name for the virtual machine service that is unique to the subscription. It is
	 * also used as the prefix of service URL.
	 */
	private String name;

	public VirtualMachineService() {
		super();
	}

	public VirtualMachineService(String name, String url) {
		super();
		this.name = name;
		this.url = url;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VirtualMachine [name=" + name + ", url=" + url + "]";
	}
}
