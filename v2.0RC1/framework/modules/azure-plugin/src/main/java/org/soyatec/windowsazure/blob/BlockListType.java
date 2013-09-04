package org.soyatec.windowsazure.blob;

/**
 * It specifies Get Block List to return the list of committed blocks, the list
 * of uncommitted blocks, or both lists together. Valid values are committed,
 * uncommitted, or all.
 * 
 */
public enum BlockListType {

	Committed("committed"), Uncommitted("uncommitted"), All("all");

	private String literal;

	BlockListType(final String literal) {
		this.literal = literal;
	}

	/**
	 * Get the literal of the data type
	 * 
	 * @return the the literal of the data type string
	 */
	public String getLiteral() {
		return this.literal;
	}
}
