package edu.usc.pgroup.floe.applications.helloworld;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;

import edu.usc.pgroup.floe.api.framework.floegraph.FloeGraph;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GraphStart
{
    public static void main(String[] args)
    {
        try
        {
            //Get The Node List From File
            JAXBContext ctx = JAXBContext.newInstance(FloeGraph.class);
            Unmarshaller um = ctx.createUnmarshaller();
            FloeGraph fg = (FloeGraph) um.unmarshal(new StreamSource(new File("FloeGraph/helloWorldGraph.xml")));

            /*
        	FloeGraph fg = new FloeGraph();
        	
        	NodeList nlist = new NodeList();
        	List<Node> nodes = new ArrayList<>();
        	
        	Node n1 = newNode("1");
        	
        	
        	nlist.setNodeList(nodes);
          */
            
            Client c = Client.create();
            WebResource r = c.resource("http://localhost:45000/Coordinator/createFloe");
            c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
            ClientResponse response ;
            c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
            response = r.post(ClientResponse.class, fg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

    }

	/*private static Node newNode(String nid) {
		Node n1 = new Node();
    	n1.setnodeId(nid);
    	n1.setpelletType("helloworld");
    	n1.setNodecount(1);
    	
    	ResourceInfo r = new ResourceInfo();
    	r.setNumCores(1);
    	
    	n1.setresources(r);
    	
    	
    	return n1;
	}*/
}
