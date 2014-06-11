package org.soyatec.windowsazure.management;

/**
 * This class represents some basic information about storage services.
 * 
 * 
 */
public class StorageService {
	/**
	 * Url of storage account address
	 */
	private String url;

	/**
	 * Storage account name
	 */
	private String name;

	public StorageService() {
		super();
	}

	public StorageService(String name, String url) {
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

}
