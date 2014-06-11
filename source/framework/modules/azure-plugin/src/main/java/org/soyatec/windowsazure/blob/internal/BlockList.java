package org.soyatec.windowsazure.blob.internal;

import java.util.ArrayList;
import java.util.List;

import org.soyatec.windowsazure.blob.IBlockList;

public class BlockList implements IBlockList{

	private List<Block> committedBlocks;
	private List<Block> uncommittedBlocks;
	
	public BlockList(){
		committedBlocks = new ArrayList<Block>();
		uncommittedBlocks = new ArrayList<Block>();
	}
	
	void addCommittedBlock(Block block){
		committedBlocks.add(block);
	}
		
	void addUncommittedBlock(Block block){
		uncommittedBlocks.add(block);
	}
	
	public List<Block> getCommittedBlocks(){
		return committedBlocks;
	}
	
	public List<Block> getUncommittedBlocks(){
		return uncommittedBlocks;		
	}
}
