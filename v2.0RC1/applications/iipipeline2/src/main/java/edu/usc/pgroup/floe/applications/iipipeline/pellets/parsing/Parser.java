package edu.usc.pgroup.floe.applications.iipipeline.pellets.parsing;
import java.util.ArrayList;
import java.util.HashMap;

public interface Parser 
{
	public ArrayList<HashMap<String,String>> parseFile() throws Exception;
}
