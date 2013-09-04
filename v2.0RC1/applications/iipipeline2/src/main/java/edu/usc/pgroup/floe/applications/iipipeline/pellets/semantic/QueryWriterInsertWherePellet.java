package edu.usc.pgroup.floe.applications.iipipeline.pellets.semantic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import uk.co.magus.fourstore.client.Store;
import edu.usc.pgroup.floe.api.framework.pelletmodels.StreamInStreamOutPellet;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.api.stream.FEmitter;
import edu.usc.pgroup.floe.api.stream.FIterator;
import edu.usc.pgroup.floe.applications.iipipeline.pellets.utils.IIPProperties;
import edu.usc.pgroup.floe.applications.iipipeline.pellets.utils.SGConstants;

public class QueryWriterInsertWherePellet implements StreamInStreamOutPellet
{
	private static Logger logger = Logger.getLogger(QueryWriterInsertWherePellet.class.getName());
	Store store;
	
	public QueryWriterInsertWherePellet() throws MalformedURLException
	{
		try
		{
			store = new Store(IIPProperties.getStoreURL());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
	}
		
	public String getInsertQuery(String insertQuery){
		
	 	   StringBuilder query = new StringBuilder("");
	 	   query.append("PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
	       //query.append("INSERT { \n");
	       query.append(insertQuery+ " \n\n ");	
	       //query.append(" } \n");
	       return query.toString();
		}
	public SemanticInfo byteToSemantic (byte[] bytes)
	{
		SemanticInfo tempInfo = null;
		try 
		{
			ByteArrayInputStream byteInput = new ByteArrayInputStream (bytes);
			ObjectInputStream ois = new ObjectInputStream (byteInput);
			tempInfo = (SemanticInfo)ois.readObject();
		}
		catch (Exception ex) 
		{
	  
		}
		return tempInfo;
	}
	
	@Override
	public void invoke(FIterator in, FEmitter out, StateObject stateObject) {

		ArrayList<SemanticInfo> arrList = new ArrayList<SemanticInfo>();
		while (true) {
			try {
				Object data = in.next();
				SemanticInfo semanticInfo = (SemanticInfo) data;
				logger.log(Level.SEVERE, "Got input semantic info " + semanticInfo.toString());
				
				arrList.add(semanticInfo);
				
				String configInfo = null;
				if (arrList.size() == SGConstants.BULK_LOAD_SIMPLE) {
					// By Sreedhar
					// System.out.println("Writing batch INSERT WHERE query");
					List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
					for (SemanticInfo e : arrList) {
						Map<String, String> m = e.getMap();
						mapList.add(m);
						configInfo = e.getDescription();
					}

					String semanticConfigFile = getSemanticConfig(configInfo); // Lookup table to find which semantic file to use
																				 
					//QueryWriterInsertWherePellet q = new QueryWriterInsertWherePellet();
					InputStream ins2;
					try {
						 //q.bulkLoad(semanticConfigFile, mapList); //, ins2);
						bulkLoad(semanticConfigFile, mapList); // , ins2);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					/*
					 * catch (FileNotFoundException e) { e.printStackTrace(); }
					 * catch (IOException e) { e.printStackTrace(); }
					 */
					arrList.clear(); // Clear the Contents once the data has
										// been inserted
					//stateObject.setState(arrList);

					/*
					 * Message<byte[]> msg = new MessageImpl<byte[]>();
					 * msg.putPayload(BitConverter.getBytes("simple"));
					 * write(msg);
					 */

					// write("simple");
					out.emit("Simple");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}		
	}
	
	private String getSemanticConfig(String configInfo) {
		return SGConstants.semanticConfigMapping.getProperty(configInfo);
	}
	
	/**
	 * Inserts the list of values in mapList as per the config file
	 *  
	 * @param configFile	File that dictates the structure of the data that will be inserted
	 * @param mapList		List of values that are to be inserted in the store
	 * @throws IOException
	 */
	public void bulkLoad(String semanticConfigFile, List<Map<String, String>> mapList) throws IOException{
		
		//QueryWriterSelectInsertData qwriter = new QueryWriterSelectInsertData();
		
		logger.log(Level.SEVERE, "bulkLoad");
		StringBuilder insertQuery =  new StringBuilder();		//Store triples used in bulk INSERT DATA
		for(Map<String, String> mValue: mapList){
			//Map<String, String> element = values.get(i); //readIIP(); 
			//ins.reset();
			logger.log(Level.SEVERE, mValue.toString());
			InputStream ins = new FileInputStream(new File(semanticConfigFile));
			appendElement(mValue, ins, insertQuery);
			ins.close();
		}
		
		String query = getInsertQuery(insertQuery.toString());
		logger.log(Level.INFO, query);
	
		// By Sreedhar System.out.println("INSERT WHERE query: "+ query);
		
		//Run the query against the store
		
		String insertResponse = store.insert(query);
		// By Sreedhar System.out.println("Insert response: "+ insertResponse);
	}
	
	public void appendElement(Map<String, String> valueMap, InputStream ins, StringBuilder insertQuery){
		
	       SAXParserFactory factory = SAXParserFactory.newInstance();
	       try {
	           // Parse the input
	           SAXParser saxParser = factory.newSAXParser();
	           InsertWhereQuery result = new InsertWhereQuery(valueMap);
	        // By Sreedhar System.out.println("Null Stearm " + ins);
	           saxParser.parse(ins, result);
	           ins.close();
	           
	           String query = result.getSparqlQuery();
	        // By Sreedhar System.out.println("\nSPARQL Query : "+ query);
	           
	           insertQuery.append(result.getSparqlQuery()+ " \n ");				//List of triples that go in the INSERT DATA query
	       }catch(Exception e){
	    	   e.getMessage();
	    	   e.printStackTrace();
	       }
	}

	public String getPelletType() {
		// TODO Auto-generated method stub
		return null;
	}	
}