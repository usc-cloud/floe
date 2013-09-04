package edu.usc.pgroup.floe.applications.iipipeline.pellets.semantic;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import uk.co.magus.fourstore.client.SPARQLResultSet;
import uk.co.magus.fourstore.client.Store;
import uk.co.magus.fourstore.client.Store.OutputFormat;
import edu.usc.pgroup.floe.api.framework.pelletmodels.StreamInStreamOutPellet;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.api.stream.FEmitter;
import edu.usc.pgroup.floe.api.stream.FIterator;
import edu.usc.pgroup.floe.applications.iipipeline.pellets.utils.IIPProperties;
import edu.usc.pgroup.floe.applications.iipipeline.pellets.utils.SGConstants;
//SelectInsertDataQuery
public class QueryWriterSelectInsertDataPellet implements StreamInStreamOutPellet
{

	//private static String STORE_URL = "http://128.125.124.67:8000";		//majave-04 VM
	//private static String STORE_URL = "http://128.125.224.121:8000";		//Losangeles VM
	
	private static String XML_FILE_NAME = "config/semanticConfigEquipment.xml";
											//"config/semanticConfigElectric.xml";  	 
											//"config/semanticConfigBuilding.xml"; 
											//	"config/semanticConfigEquipment.xml";	
	
	static int COUNTER = 0;
	static int SAMPLE_SIZE = 10;
	
	private static List<Map<String, String>> values = new ArrayList<Map<String, String>>();

	
static{
		
		for(int i=0; i<SAMPLE_SIZE; i++){
			Map<String, String> valueMap = new HashMap<String, String>();
			valueMap.put("BuildingNum", "101");
			valueMap.put("BuildingCode", "EEB");
			valueMap.put("BuildingAddress", "USC USC USC");
			valueMap.put("BuildingFloors", "20");
			valueMap.put("YearBuilt", "1990");
			valueMap.put("GrossArea", "10000");
			
			valueMap.put("Architect", "Tommy");		//Architect Entity
			valueMap.put("Department", "Electical");
			
			valueMap.put("Latitude", "77.2");
			valueMap.put("Longitude", "123.5");
			
			String sensorID = "testSensorID"+ (COUNTER+""+i);
			valueMap.put("SensorID", sensorID);
			
			valueMap.put("Value", "22"+(COUNTER+ "" +i));			//ElectricalMeasurement -> hasMeasuredValue
			valueMap.put("Date", "01:Jan:2012");	//ElectricalMeasurement -> hasMeasurementRecordedDate
			valueMap.put("Time", "03:22:55");		//ElectricalMeasurement -> hasMeasurementRecordedTime
			
			values.add(valueMap);
		}

		//TODO: The following should be read from a config file
		//semanticConfigMapping.put("Building.xml", "config/semanticConfigBuilding.xml");
		//semanticConfigMapping.put("Electric.xml", "config/semanticConfigElectric.xml");		
	}
	
	private StringBuilder selectQuery;		//Stores triples used in Bulk SELECT
	private StringBuilder insertQuery;		//Store triples used in bulk INSERT DATA
	private Store store;

	
	public QueryWriterSelectInsertDataPellet() throws MalformedURLException{
		selectQuery = new StringBuilder("");
		insertQuery = new StringBuilder("");
		store = new Store(IIPProperties.getStoreURL());
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

	private String getSemanticConfig(String configInfo) {		
		return SGConstants.semanticConfigMapping.getProperty(configInfo);
	}

	public String getSelectQuery(){
		
        StringBuilder query = new StringBuilder("");
        query.append("PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
        query.append(" SELECT * WHERE { \n");
        query.append(selectQuery.toString());
        query.append("       } \n");
        
        return query.toString();
	}
	
	public String getInsertDataQuery(){
		
	 	   StringBuilder query = new StringBuilder("");
	 	   query.append("PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
	       query.append("INSERT DATA { \n");
	       query.append(insertQuery.toString());	
	       query.append(" } \n");

	       return query.toString();
		}
	
	public void appendElement(Map<String, String> valueMap, InputStream ins){
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// Parse the input
        SAXParser saxParser;
		try {
			SelectInsertDataQuery result = new SelectInsertDataQuery(valueMap);
			saxParser = factory.newSAXParser();
			saxParser.parse(ins, result);
			
			selectQuery.append(result.getWhereClauseTriples()+ "\n");		//List of triples that go in the WHERE clause
			insertQuery.append(result.getSparqlQuery()+ "\n");				//List of triples that go in the INSERT DATA query
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Inserts the list of values in mapList as per the config file
	 * @return true if insert was successful, and false if failed 
	 * @param configFile	File that dictates the structure of the data that will be inserted
	 * @param mapList		List of values that are to be inserted in the store
	 * @throws IOException
	 */
	public boolean bulkLoad(String semanticConfigFile, List<Map<String, String>> mapList) throws IOException{
		
		//QueryWriterSelectInsertData qwriter = new QueryWriterSelectInsertData();
		for(Map<String, String> mValue: mapList){
			//Map<String, String> element = values.get(i); //readIIP(); 
			//ins.reset();
			InputStream ins = new FileInputStream(new File(semanticConfigFile));

			appendElement(mValue, ins);
			ins.close();
		}
		
		String query = getSelectQuery();
		//System.out.println("Select query: "+ query);
		SPARQLResultSet results = store.query(query, OutputFormat.SPARQL_XML, -1, true);
		// By Sreedhar System.out.println("SensorIDs retrieved = "+ results.getResultSet().size());
		try
		{
			if(results.getResultSet().size() > 0)
			{
				//System.out.println("results size() = "+ results.getResultSet().get(0).size());
			}	
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		
		//System.out.println("Results: \n"+ results.getResultSet().get(0).t);
		
		query = getInsertDataQuery();
		query = updateURIs(query, /*SPARQLResultSet*/results);			//Will substitute the values in the result for the URIs in INSERT DATA query
		
		// By Sreedhar System.out.println("INSERT DATA query after substitutions: "+ query);
		
		//Run the query against the store
		//System.out.println("Trying to Insert");
		String insertResponse = store.insert(query);
		// By Sreedhar System.out.println("Insert response: "+ insertResponse);

		return true;
	}
	
	
public void bulkLoad_test(int count) throws IOException{
		
		//QueryWriterSelectInsertData qwriter = new QueryWriterSelectInsertData();
		int i = 0;
		while(i < count){
			Map<String, String> element = values.get(i); //readIIP(); 
			//ins.reset();
			InputStream ins = new FileInputStream(new File(XML_FILE_NAME));
			appendElement(element, ins);
			ins.close();
			i++;
		}
		
		String query = getSelectQuery();
		// By SreedharSystem.out.println("Select query: "+ query);
		//Store store = new Store(SGConstants.STORE_URL);
		SPARQLResultSet results = store.query(query, OutputFormat.SPARQL_XML, -1, true);
		
		//System.out.println("results size() = "+ results.getResultSet().get(0).size());
		//System.out.println("Results: \n"+ results.getResultSet().get(0).t);
		
		query = getInsertDataQuery();
		query = updateURIs(query, /*SPARQLResultSet*/results);			//Will substitute the values in the result for the URIs in INSERT DATA query
		
		//System.out.println("INSERT DATA query after substitutions: "+ query);
		if(query != null)
			store.insert(query);
	}
	
private String updateURIs(String query, SPARQLResultSet results){
	
	String updatedQuery = "";
	StringTokenizer strtok = new StringTokenizer(query, "\n");
	if(strtok.hasMoreTokens())
		updatedQuery += strtok.nextToken() +" \n ";
	if(strtok.hasMoreTokens())
		updatedQuery += strtok.nextToken() +" \n ";

	//Read all the sensor vars and their values in a map
	Map<String, String> subMap = new HashMap<String, String>();
	for(Map<String, String> map: results.getResultSet()){
		for(Map.Entry<String, String> entry: map.entrySet()){
			String replaceMe = entry.getKey();
			String newValue = entry.getValue();
			subMap.put(replaceMe, newValue);
		}
	}
	
	int tripleCount = 0;
	String line1 = null, line2 = null, line3 = null, line4 = null, line5 = null;
	while(strtok.hasMoreTokens()){
		line1 = strtok.nextToken();
		if(line1.contains("}"))
			break;
		line2 = strtok.nextToken();
		line3 = strtok.nextToken();
		line4 = strtok.nextToken();
		line5 = strtok.nextToken();
		
		String sensorVar = (new StringTokenizer(line2, " ")).nextToken();
		sensorVar = sensorVar.substring(1);
		if(subMap.get(sensorVar) == null)
			continue;
		
		String replaceMe = sensorVar;
		String newValue = subMap.get(sensorVar);
		
		int pos = line2.indexOf(replaceMe);				
		pos--;
		line2 = line2.substring(0,pos)+ "<"+ newValue +">" + line2.substring(pos + 1 + replaceMe.length());
		
		updatedQuery += line1 + "\n ";
		updatedQuery += line2 + "\n ";
		updatedQuery += line3 + "\n ";
		updatedQuery += line4 + "\n ";
		updatedQuery += line5 + "\n\n ";
		tripleCount += 5;
	}
	
	if(tripleCount == 0)
		return null;
	
	updatedQuery += line1 + "\n ";
	return updatedQuery;
}
	//@Override
	public String getPelletType() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void invoke(FIterator in, FEmitter out, StateObject stateObject) {
		ArrayList<SemanticInfo> l = new ArrayList<SemanticInfo>();
		while (true) {
			try {

				Object infoObj = in.next();
				System.out.println("Got input : " + infoObj);
				SemanticInfo semanticInfo = (SemanticInfo) infoObj;
				
				l.add(semanticInfo);
				
				String configInfo = null;
				if (l.size() == SGConstants.BULK_LOAD_FREQUENT) {
					// System.out.println("Bulk Load");
					// By Sreedhar
					// System.out.println("Writing batch SELECT+INSERTDATA query");
					List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
					for (SemanticInfo e : l) {
						Map<String, String> m = e.getMap();
						mapList.add(m);
						configInfo = e.getDescription();
					}

					String semanticConfigFile = getSemanticConfig(configInfo); // Lookup
																				// a
																				// table
																				// to
																				// find
																				// which
																				// semantic
																				// config
																				// file
																				// to
																				// use
					
					QueryWriterSelectInsertDataPellet q = new QueryWriterSelectInsertDataPellet();
					try {
						q.bulkLoad(semanticConfigFile, mapList); //, ins2);
						//bulkLoad(semanticConfigFile, mapList); // , ins2);
						l.clear();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					/*
					 * Message<byte[]> msg = new MessageImpl<byte[]>();
					 * msg.putPayload(BitConverter.getBytes("frequent"));
					 * write(msg);
					 */
					// write("frequent");
					out.emit("frequent");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}


}