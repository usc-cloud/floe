package edu.usc.pgroup.floe.applications.iipipeline.pellets.semantic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import edu.usc.pgroup.floe.api.framework.pelletmodels.SingleInStreamTupleOutPellet;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.api.stream.FTupleEmitter;

public class SemanticRoutingPellet implements SingleInStreamTupleOutPellet 
{
	private final static Logger logger = Logger.getLogger(SemanticRoutingPellet.class.getName());
	//@Override
	public String getPelletType() {
		// TODO Auto-generated method stub
		return null;
	}
	public static byte[] semanticToByte(SemanticInfo inpInfo) 
	{
		byte[] retBytes = null;
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try 
		{
		    ObjectOutputStream outStream = new ObjectOutputStream(byteStream); 
		    outStream.writeObject(inpInfo);
		    outStream.flush(); 
		    outStream.close(); 
		    byteStream.close();
		    retBytes = byteStream.toByteArray ();
		}
		catch (IOException ex) 
		{
			ex.printStackTrace();
		}
		return retBytes;
	}	
	public SemanticInfo byteToSemantic (byte[] bytes)
	{
		SemanticInfo transportInfo = null;
		try 
		{
			ByteArrayInputStream byteInput = new ByteArrayInputStream (bytes);
			ObjectInputStream ois = new ObjectInputStream (byteInput);
			transportInfo = (SemanticInfo)ois.readObject();
		}
		catch (Exception ex) 
		{
	  
		}
		return transportInfo;
	}
	
	@Override
	public void invoke(Object in, FTupleEmitter out,
			StateObject stateObject) {

		logger.info("Sematic routing pellet invoked with object " + in);
		SemanticInfo semanticInfo = (SemanticInfo)in;
		Map<String,Object> currMap = new HashMap<String,Object>();
		
		if(semanticInfo.getDescription().contains("Equipment"))
		{
			currMap.put("ComplexInsert", semanticInfo);
		}
		else if(semanticInfo.getDescription().contains("Measurement"))
		{			
			currMap.put("FrequentInsert", semanticInfo);
		}
		else
		{
			currMap.put("SimpleInsert", semanticInfo);
		}
		//System.out.println("semantic");
		//this.write(currMap);
		
		out.emit(currMap);		
	}
}
