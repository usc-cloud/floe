package edu.usc.pgroup.floe.impl.manager.infraManager.infraHandler;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.TreeMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;


import com.sun.jersey.api.client.config.DefaultClientConfig;
import edu.usc.pgroup.floe.util.Constants;
import edu.usc.pgroup.floe.util.EucalyptusAccountInfo;
import edu.usc.pgroup.floe.util.Utils;

public class EucalyptusInfraHandler extends CloudInrfaHandler{

	
	private EucalyptusAccountInfo eucaAccount;
	
	public EucalyptusInfraHandler()
	{
		try {
			eucaAccount = new EucalyptusAccountInfo(Constants.EucalyptusConfigFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void deployInstance(Map<String, String> params)	
	{
		String imageId = params.get("imageId");
		String instanceType = params.get("instanceType");
		int instanceCount = params.get("instanceCount") == null? 1 : Integer.parseInt(params.get("instanceCount")) ;
		
		TreeMap<String,String> queryParams = new TreeMap<String,String>();
		queryParams.put("ImageId",imageId);
		queryParams.put("InstanceType",instanceType);
		queryParams.put("Timestamp",Utils.generateGMTTimeStamp());
		queryParams.put("MaxCount",Integer.toString(instanceCount));
		queryParams.put("MinCount",Integer.toString(instanceCount));
		queryParams.put("Action","RunInstances");
		String queryString = eucaAccount.generateQueryString(queryParams);		
		System.out.println(queryString);
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                true);
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

        Client c = Client.create(config);
		WebResource r = c.resource(queryString);
		c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		ClientResponse response = r.get(ClientResponse.class);		       
	}
	
	protected void releaseInstance(String instanceID)
	{
		TreeMap<String,String> queryParams = new TreeMap<String,String>();
		queryParams.put("Timestamp",Utils.generateGMTTimeStamp());
		queryParams.put("InstanceId.1",instanceID);
		queryParams.put("Action","TerminateInstances");
		String queryString = eucaAccount.generateQueryString(queryParams);
		System.out.println(queryString);
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                true);
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

        Client c = Client.create(config);
		WebResource r = c.resource(queryString);
		c.getProperties().get(ClientConfig.PROPERTY_FOLLOW_REDIRECTS);
		ClientResponse response = r.get(ClientResponse.class);
	}
	
	/*private void describeInstances()
	{
		TreeMap<String,String> queryParams = new TreeMap<String,String>();
		queryParams.put("Timestamp",Utils.generateGMTTimeStamp());
		queryParams.put("Action","DescribeInstances");
		String queryString = eucaAccount.generateQueryString(queryParams);
		Client c = Client.create();	
		WebResource r = c.resource(queryString);
		c.getProperties().get(ClientConfig.PROPERTY_FOLLOW_REDIRECTS);
		ClientResponse response = r.get(ClientResponse.class);
		String queryStrin = response.getEntity(String.class);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();		
		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(queryStrin.getBytes("UTF-8")));
		    NodeList list = doc.getElementsByTagName("dnsName");		    
		    List<String> ipList = new ArrayList<String>();
		    List<EucalyptusInstance> typeList = new ArrayList<EucalyptusInstance>();
		    for (int i=0; i<list.getLength(); i++) 
		    {		   
		    	Element element = (Element)list.item(i);
		    	ipList.add(element.getTextContent().trim());		    }
		    NodeList list1 = doc.getElementsByTagName("instanceType");
		    for (int i=0; i<list.getLength(); i++) 
		    {		  
		    	Element element = (Element)list1.item(i);
		    	EucalyptusInstance tempInstance = new EucalyptusInstance();
		    	tempInstance.setInstanceState(element.getTextContent().trim());
		    	typeList.add(tempInstance);
		    }		
		    Iterator<String> ipIter = ipList.iterator();
		    Iterator<EucalyptusInstance> typeIter = typeList.iterator();
		    while(ipIter.hasNext())
		    {
		    	this.requestedContainers.put(ipIter.next(), typeIter.next());	
		    }		    
		}		
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	private void releaseResources(List<ContainerInfo> releaseContainers)
	{
		Iterator<ContainerInfo> releaseIter = releaseContainers.iterator();
		while(releaseIter.hasNext())
		{
			this.destroyContainer(releaseIter.next().getContainerId());
		}
	}*/
	
	
}
