package edu.usc.pgroup.floe.applications.iipipeline.pellets.transport;

import java.io.Serializable;

public class TransportInfo /*extends PelletInfo*/ implements Serializable
{	
	private String transportProtocol;			// Specifies whether it is HTTP,FTP,LocalHost,Stream			 
	private String fileType;					// Specifies the format of the file XML,CSV,TXT,XLSX etc...
	private byte[] byteStream;	// contains chunks of information. for Stream Data
	private String fileLocation;				// specifies the filepath stored in local file
	private String description;					// specifies the contents of the data //Building,Sensor,Equipment,
	private int frequency;						// Specify the number of times a day this should pull data // For Building Data etc ..	 
	public static final long serialVersionUID = 148;
	public TransportInfo()
	{
	}
	public TransportInfo(String fileType,String fileLocation)
	{
		this.fileType = fileType;
		this.fileLocation = fileLocation;
	}
	public String getTransportProtocol()
	{
		return this.transportProtocol;
	}
	public void setTransportProtocol(String transportProtocol)	
	{
		this.transportProtocol= transportProtocol;
	}
	public String getDescription()
	{
		return description;
	}	
		
	public void setDescription(String strInp)
	{
		this.description = strInp;
	}
	public void setFileType(String inpType)
	{
		this.fileType = inpType;
	}
	public String getFileType()
	{
		return this.fileType;
	}
	public void setFileLocation(String inpLocation)
	{
		this.fileLocation = inpLocation;
	}
	public String getFileLocation()
	{
		return this.fileLocation;
	}
	public void setByteStream(byte[] byteStream)
	{
		this.byteStream = byteStream;
	}
	public byte[] getByteStream()
	{
		return this.byteStream;
	}
	public int getFrequency()
	{
		return this.frequency;
	}
	public void setFrequence(int frequency)
	{
		this.frequency = frequency;
	}
}
