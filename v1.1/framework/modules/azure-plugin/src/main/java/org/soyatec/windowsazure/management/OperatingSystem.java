package org.soyatec.windowsazure.management;

/**
 * This class contains information about the family an operating system belongs
 * to.
 * 
 * @author yyang
 * 
 */
public class OperatingSystem {
	/**
	 * The operating system version. This value corresponds to the configuration
	 * value for specifying that your service is to run on a particular version
	 * of the Windows Azure guest operating system.
	 */
	private String version;

	/**
	 * Label for the operating system version.
	 */
	private String label;

	/**
	 * Indicates whether this operating system version is the default version
	 * for a service that has not otherwise specified a particular version. The
	 * default operating system version is applied to services that are
	 * configured for auto-upgrade. An operating system family has exactly one
	 * default operating system version at any given time, for which the
	 * IsDefault element is set to true; for all other versions, IsDefault is
	 * set to false.
	 */
	private boolean _default;

	/**
	 * Indicates whether this operating system version is currently active for
	 * running a service. If an operating system version is active, you can
	 * manually configure your service to run on that version.
	 */
	private boolean active;

	/**
	 * Indicates which operating system family this version belongs to. A value
	 * of 1 corresponds to the Windows Azure guest operating system that is
	 * substantially compatible with Windows Server 2008 SP2. A value of 2
	 * corresponds to the Windows Azure guest operating system that is
	 * substantially compatible with Windows Server 2008 R2.
	 */
	private String family;

	/**
	 * Label for the operating system family.
	 */
	private String familyLabel;

	/**
	 * 
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Set the version
	 * 
	 * @param version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * 
	 * @return label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set the label
	 * 
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * 
	 * @return true or false
	 */
	public boolean isDefault() {
		return _default;
	}

	/**
	 * Set the default1
	 * 
	 * @param default1
	 */
	public void setDefault(boolean default1) {
		_default = default1;
	}

	/**
	 * 
	 * @return true or false
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * set the active
	 * 
	 * @param active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getFamilyLabel() {
		return familyLabel;
	}

	public void setFamilyLabel(String familyLabel) {
		this.familyLabel = familyLabel;
	}

}
