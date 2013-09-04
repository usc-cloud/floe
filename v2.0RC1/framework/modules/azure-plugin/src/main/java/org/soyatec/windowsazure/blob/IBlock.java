package org.soyatec.windowsazure.blob;

/**
 * This interface represents a part of block blob. It contains information
 * returned from "Get Block List" operation.
 * 
 */
public interface IBlock {
	/**
	 * 
	 * @return The id of block in block list.
	 */
	public String getName();

	/**
	 * 
	 * @return The size of block in bytes.
	 */
	public String getSize();
}
