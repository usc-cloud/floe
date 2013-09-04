package edu.usc.pgroup.floe.applications.iipipeline.pellets.transport;

import java.io.Serializable;

public class SensorData implements Serializable 
{
	private static final long serialVersionUID = 1L;
	String sensorName;
	String sensorPoint;
	String serverName;
	public String getSensor()
	{
		return this.sensorName;
	}
	public String getServer()
	{
		return this.serverName;
	}
	public void setSensor(String inpSensor)
	{
		this.sensorName = inpSensor;
	}
	public void setServer(String inpServer)
	{
		this.serverName = inpServer;
	}
	public void setSensorPoint(String inpPoint)
	{
		this.sensorPoint = inpPoint;
	}
	public String getSensorPoint()
	{
		return this.sensorPoint;
	}

}
