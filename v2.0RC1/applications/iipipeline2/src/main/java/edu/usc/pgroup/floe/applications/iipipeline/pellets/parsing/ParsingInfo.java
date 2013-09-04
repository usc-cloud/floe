package edu.usc.pgroup.floe.applications.iipipeline.pellets.parsing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ParsingInfo /*extends PelletInfo*/ implements Serializable 
{	
	static final long serialVersionUID = 1L;	
	String fileType;			// Type of the File, CSV,TXT,XML,XLSX
	String description;			// Descirption about Information Source Like Building,Sensor etc.. which will help find the Config File
	byte[] byteStream;
	ArrayList<HashMap<String,String>> inpMap;
	public ParsingInfo()
	{

	}
	public String getFileType()
	{
		return this.fileType;
	}
	public void setFileType(String inpType)
	{
		this.fileType = inpType;
	}
	public String getDescription()
	{
		return this.description;
	}
	public void setDescription(String descStr)
	{
		this.description = descStr;		
	}
	public byte[] getByteStream()
	{
		return this.byteStream;
	}
	public void setByteStream(byte[] byteStream)
	{
		this.byteStream = byteStream;
	}
	public ArrayList<HashMap<String,String>> getMap()
	{
		return this.inpMap;
	}
	public void setMap(ArrayList<HashMap<String,String>> inpMap)
	{
		this.inpMap = inpMap;
	}
}
