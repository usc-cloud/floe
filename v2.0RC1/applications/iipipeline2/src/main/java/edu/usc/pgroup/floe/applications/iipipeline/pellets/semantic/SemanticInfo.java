package edu.usc.pgroup.floe.applications.iipipeline.pellets.semantic;

import java.io.Serializable;
import java.util.Map;

public class SemanticInfo /*extends PelletInfo*/ implements Serializable
{
	private static final long serialVersionUID = 1L;
	Map<String,String> mapValue;
	String description;
	public String getDescription()
	{
		return this.description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public void setMap(Map<String,String> inMap)
	{
		this.mapValue = inMap;
	}
	public Map<String,String> getMap()
	{
		return this.mapValue;
	}
	public String toString() {
		return this.mapValue.toString();
	}
}
