package org.soyatec.windowsazure.management;

/**
 * 
 * The enum contains size of the role instance.
 * 
 */
public enum InstanceSize {
	ExtraSmall("ExtraSmall"), Small("Small"), Medium("Medium"), Large("Large"), ExtraLarge(
			"ExtraLarge");

	private final String literal;

	InstanceSize(String value) {
		this.literal = value;
	}

	/**
	 * @return the literal
	 */
	public String getLiteral() {
		return literal;
	}
}
