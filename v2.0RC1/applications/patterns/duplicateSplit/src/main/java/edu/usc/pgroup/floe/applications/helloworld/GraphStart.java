package edu.usc.pgroup.floe.applications.helloworld;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;

import edu.usc.pgroup.floe.api.framework.FloeGraph.EdgeList;
import edu.usc.pgroup.floe.api.framework.FloeGraph.NodeList;
import edu.usc.pgroup.floe.api.framework.StartFloeInfo;
import edu.usc.pgroup.floe.api.framework.rest.RestFlakeCreationInfo;
import edu.usc.pgroup.floe.api.framework.rest.RestFloeInfo;

public class GraphStart 
{
	public static void main(String[] args)
	{
		try
		{
			//Get The Node List From File
			JAXBContext ctx = JAXBContext.newInstance(NodeList.class);	
			Unmarshaller um = ctx.createUnmarshaller();
			NodeList tempNodeList = (NodeList) um.unmarshal(new StreamSource(new File("FloeGraph/NodeList.xml")));

			// Get the Edge List From File
			JAXBContext ctx1 = JAXBContext.newInstance(EdgeList.class);	
			Unmarshaller um1 = ctx1.createUnmarshaller();
			EdgeList tempEdgeList = (EdgeList) um1.unmarshal(new StreamSource(new File("FloeGraph/EdgeList.xml")));
			
			RestFloeInfo tempFloeInfo = new RestFloeInfo();
			tempFloeInfo.setEdge(tempEdgeList);
			tempFloeInfo.setNode(tempNodeList);
			Client c = Client.create();
			WebResource r = c.resource("http://localhost:45000/Coordinator/createFloe");																
			c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
			ClientResponse response ;
			c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
			response = r.post(ClientResponse.class, tempFloeInfo);	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
}
