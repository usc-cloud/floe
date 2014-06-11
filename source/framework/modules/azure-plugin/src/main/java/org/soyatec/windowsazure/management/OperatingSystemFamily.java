package org.soyatec.windowsazure.management;

import java.util.List;

public class OperatingSystemFamily {
	/**
	 * Indicates which operating system family this version belongs to. A value
	 * of 1 corresponds to the Windows Azure guest operating system that is
	 * substantially compatible with Windows Server 2008 SP2. A value of 2
	 * corresponds to the Windows Azure guest operating system that is
	 * substantially compatible with Windows Server 2008 R2.
	 */
	private String name;

	/**
	 * A label for the operating system family.
	 */
	private String label;

	private List<OperatingSystem> operatingSystems;

	/**
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return the label
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
	 * @return the operatingSystems
	 */
	public List<OperatingSystem> getOperatingSystems() {
		return operatingSystems;
	}

	/**
	 * Set the operatingSystems
	 * 
	 * @param operatingSystems
	 */
	public void setOperatingSystems(List<OperatingSystem> operatingSystems) {
		this.operatingSystems = operatingSystems;
	}

}
