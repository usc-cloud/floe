package edu.usc.pgroup.floe.applications.iipipeline.pellets.transport;

import java.io.Serializable;
import java.util.ArrayList;

public class SensorListHolder implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<SensorData> sensorList;
	public ArrayList<SensorData> getSensorList()
	{
		return this.sensorList;
	}
	public void setSensorList(ArrayList<SensorData> inpList)
	{
		this.sensorList = inpList;
	}
}
