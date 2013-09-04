package org.soyatec.windowsazure.blob;

import java.util.List;

import org.soyatec.windowsazure.blob.internal.Block;

/**
 * This interface represents a list of blocks. It contains information returned
 * from "Get Block List" operation.
 * 
 */
public interface IBlockList {
	/**
	 * 
	 * 
	 * @return The list of blocks that have been successfully committed to a
	 *         given blob with Put Block List.
	 */
	public List<Block> getCommittedBlocks();

	/**
	 * 
	 * @return The list of blocks that have been uploaded for a blob using Put
	 *         Block, but that have not yet been committed. These blocks are
	 *         stored in Windows Azure in association with a blob, but do not
	 *         yet form part of the blob.
	 */
	public List<Block> getUncommittedBlocks();
}
