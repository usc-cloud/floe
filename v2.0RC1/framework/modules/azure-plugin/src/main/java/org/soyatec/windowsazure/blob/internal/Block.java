package org.soyatec.windowsazure.blob.internal;

import org.soyatec.windowsazure.blob.IBlock;

/**
 * This class implements IBlock interface. It contains information returned from
 * "Get Block List" operation.
 * 
 */
public class Block implements IBlock {
	private String name;
	private String size;

	public Block() {
	}

	public Block(String name, String size) {
		this.name = name;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	void setSize(String size) {
		this.size = size;
	}

}
