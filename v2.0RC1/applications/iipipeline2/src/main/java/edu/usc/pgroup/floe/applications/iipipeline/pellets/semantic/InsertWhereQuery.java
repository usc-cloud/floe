package edu.usc.pgroup.floe.applications.iipipeline.pellets.semantic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.co.magus.fourstore.client.SPARQLResultSet;
import uk.co.magus.fourstore.client.Store;
import uk.co.magus.fourstore.client.exceptions.BadSGConfigFileException;
import edu.usc.pgroup.floe.applications.iipipeline.pellets.utils.IIPProperties;
import edu.usc.pgroup.floe.applications.iipipeline.pellets.utils.SGConstants;

public class InsertWhereQuery extends DefaultHandler /*implements MapInputPellet,  PelletFunction*/{
	
	//private static String STORE_URL = "http://128.125.124.67:8000";		//majave-04 VM
	//private static String STORE_URL = "http://128.125.224.121:8000";		//Losangeles VM
//	private static String STORE_URL = IIPProperties.getStoreURL();		//Losangeles VM
		
	static private String XML_FILE_NAME = "config/semanticConfigBuilding.xml";
						// "config/semanticConfigCampus.xml";  //; //"config/semanticConfigRoomEvent.xml"; //"config/semanticConfigEquipment.xml";   
											//"config/semanticConfigElectric.xml";			   
											
	static private int COUNT = 0;
	//static private Writer out;
	static private Map<String, String> testMap = new HashMap<String, String>();
	
	static{
		testMap.put("BuildingNum", "1");
		testMap.put("BuildingCode", "ADM");
		testMap.put("BuildingName", "GEORGE FINLEY BOVARD ADMINISTRATION BUILDING & KENNETH NORRIS JR. AUDITORIUM");
		testMap.put("BuildingAddress", "3551 TROUSDALE PARKWAY LOS ANGELES CA 90089");
		testMap.put("BuildingFloors", "4+B");
		testMap.put("YearBuilt", "1919");
		testMap.put("GrossArea", "97406");				//Net area
		
		testMap.put("floorCount", "4+B");
		
		testMap.put("Architect", "John Parkinson");		//Architect Entity
		testMap.put("Department", "Electical");
		
		testMap.put("Latitude", "77.2");
		testMap.put("Longitude", "123.5");
		
		testMap.put("SensorID", "testSensorID"+COUNT);
		
		testMap.put("Value", "220");			//ElectricalMeasurement -> hasMeasuredValue
		testMap.put("Date", "01:Jan:2012");		//ElectricalMeasurement -> hasMeasurementRecordedDate
		testMap.put("Time", "03:22:55");		//ElectricalMeasurement -> hasMeasurementRecordedTime
		
		testMap.put("Course", "Operating Systems");
		testMap.put("Starts", "19-01-2012");
		testMap.put("Ends", "01-05-2012");
		testMap.put("Days", "120");
		
		testMap.put("FloorNo", "2");
		testMap.put("RoomNo", "221");
		
		testMap.put("CampusCode", "UPC");
	}
	
	private static String BASE_URI = "http://www.smartgrid.usc.edu/Ontology/2012.owl#";
	StringBuilder sparqlQuery = new StringBuilder("");
	StringBuilder whereClauseTriples = new StringBuilder("");
	//StringBuilder minusClauseTriples = new StringBuilder("");
	
	public static enum SG_XML_TAG{
		DOCUMENT("Document"),
		ENTITY("Entity"),
			CLASS("class"),
			KEY("key"),
			STATUS("status"),
				STATUS_QUERY("Query"),
				STATUS_INSERT("Insert"),
		PROPERTY("Property"),
			MAPPING("mapping"),
			NAME("name");
		
		private final String tagName;
		
		SG_XML_TAG(String tag){
			this.tagName = tag;
		}
		
		String getTagName(){
			return this.tagName;
		}
	};
	
	private String tagType;
	private String tagAttrClass;
	private String tagAttrKey;
	private String tagAttrStatus;
	private String tagAttrMapping;
	private String tagAttrName;
	
	private int entityLevel;
	
	private StringBuilder lookUpQuery;
	private boolean firstEntityProperty;
	//Members to keep track of URIs 
	private String currSubject;			//Variable to keep track of current Subject URI
	private Stack<String> subjectURIStack;			//Stack to store Subjects URI of all levels before the current one
	
	private Store store;
	
	//Members to keep track of the current operation
	private String currOperation;
	private Stack<String> operationTypeStack;
	
	private Map<String, String> valueMap;
	
	public InsertWhereQuery(Map<String, String> map) throws MalformedURLException{
		
		valueMap = map;
		tagType = null;
		tagAttrClass = null;
		tagAttrKey = null;
		tagAttrStatus = null;
		tagAttrMapping = null;
		tagAttrName = null;
		entityLevel = -1;
		
		lookUpQuery = new StringBuilder("");
		firstEntityProperty = false;
		
		currSubject = null;
		subjectURIStack = new Stack<String>();
		
		store = new Store(IIPProperties.getStoreURL());
		
		currOperation = null;
		operationTypeStack = new Stack<String>();
	}
	
//	@Override
//	public void invoke(byte[] object){
//		
//	}
	
	public static void main(String[] args) {
		
//		ParserExtractor newParser = new ParserExtractor("data/parsingConfigscbPointsxlsx.xml");
//		ArrayList<ParserExtractorData> parserData = null;
//		ArrayList<HashMap<String, String>> mapOut = null;
//		try {
//			parserData = newParser.parseFile();
//			XLSXParser parser = new XLSXParser("data/scbPoints.xlsx", parserData);
//			mapOut = parser.parseFile();
//		} catch (Exception e) {
//			e.getMessage();
//			e.printStackTrace();
//		}
		
       SAXParserFactory factory = SAXParserFactory.newInstance();
       try {
           // Set up output stream
//           out = new OutputStreamWriter (System.out, "UTF8");
           
           // Parse the input
           SAXParser saxParser = factory.newSAXParser();
           InputStream ins = new FileInputStream(new File(XML_FILE_NAME)); 
           InsertWhereQuery result = new InsertWhereQuery(testMap);
           saxParser.parse(ins, result);
           ins.close();
           
           String query = result.getSparqlQuery();
        // By Sreedhar System.out.println("\nSPARQL Query : "+ query);
           
       }catch(Exception e){
    	   e.getMessage();
    	   e.printStackTrace();
       }
	}
	
	public String getSparqlQuery(){
		return sparqlQuery.toString();
	}
	
	   //===========================================================
	   // Methods in SAX DocumentHandler 
	   //===========================================================
	
	   public void startDocument ()
	   throws SAXException
	   {
	       //showData ("<?xml version='1.0'?>");
	       //newLine();
	   }
	
	   public void endDocument ()
	   throws SAXException
	   {
//	           newLine();
//	           out.flush ();
	           
	           if(whereClauseTriples.toString().equals("") ){  	//&& minusClauseTriples.toString().equals("")){
	        	   
	        	   String query = sparqlQuery.toString();
	        	   sparqlQuery = new StringBuilder("");
	        	   //sparqlQuery.append("PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n ");
	               sparqlQuery.append("INSERT DATA { \n ");
	               sparqlQuery.append(query);	
	               sparqlQuery.append(" }; \n ");
	           }else {
	        	   
	        	   String query = sparqlQuery.toString();
	        	   sparqlQuery = new StringBuilder("");
	        	   //sparqlQuery.append("PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n ");
	               sparqlQuery.append("INSERT { \n ");
	               sparqlQuery.append(query);	
	               sparqlQuery.append(" } \n ");
	               
	               sparqlQuery.append(" WHERE { \n ");
		           sparqlQuery.append(whereClauseTriples.toString());
//		           sparqlQuery.append("     MINUS { \n ");
//		           sparqlQuery.append("     "+minusClauseTriples.toString());
//		           sparqlQuery.append("      } \n ");
		           sparqlQuery.append("       }; \n ");
	           }
	   }

//	   showData ("<"+qName);
//	   if (attrs != null) {
//	       for (int i = 0; i < attrs.getLength (); i++) {
//	           showData (" ");
//	           showData (attrs.getLocalName(i)+"=\""+attrs.getValue (i)+"\"");
//	       }
//	   }
//	   showData (">");
	   
	   public void startElement(String uri, String localName, String qName, Attributes attrs) // (String name, AttributeList attrs)
	   throws SAXException
	   {
		  String parentSubject = null; 
		  tagType = qName;
		  entityLevel++;
		  //System.out.println(tagType); 
		  if(qName.equals(SG_XML_TAG.ENTITY.getTagName())) {			//Entity tag
			  lookUpQuery = new StringBuilder(""); 
			  //Read all the attributes inside the tag
			   tagAttrStatus = attrs.getValue(SG_XML_TAG.STATUS.getTagName());
			   tagAttrClass = attrs.getValue(SG_XML_TAG.CLASS.getTagName());					
			   tagAttrKey = attrs.getValue(SG_XML_TAG.KEY.getTagName());
			   //System.out.println(tagAttrClass +"  "+ tagAttrKey +"  "+ tagAttrStatus);
			  
			   if(subjectURIStack.empty()){
				   parentSubject = null;
			   }
			   else {
				   parentSubject = subjectURIStack.peek();
			   }
			   
			   String subject = null;
			   String predicate = null;
			   String object = null;
			   String triple = null;
			   
			   if(SG_XML_TAG.STATUS_QUERY.getTagName().equals(tagAttrStatus)) {
				   
				   subject   = "?URI"+ System.nanoTime();		//getUUID();
				   predicate = "rdf:type ";
				   object 	 = tagAttrClass;
				   triple    = subject +" "+ predicate +" <"+ object +"> . \n ";		//subject is a variable an not a URI, so do not surround it by <   >
				   whereClauseTriples.append(triple);
				   if(parentSubject != null){
					   String backTriple = null;
					   if(parentSubject.startsWith("?") && subject.startsWith("?"))
						   backTriple = parentSubject +" <" + tagAttrClass +"> "+ subject +" . \n ";		//Triple to link this entity with its parent
					   else if(parentSubject.startsWith("?"))
						   backTriple = parentSubject +" <" + tagAttrClass +"> <"+ subject +"> . \n ";
					   else if(subject.startsWith("?"))
						   backTriple = "<"+ parentSubject +"> <" + tagAttrClass +"> "+ subject +" . \n ";
					   else
						   backTriple = "<"+ parentSubject +"> <" + tagAttrClass +"> <"+ subject +"> . \n ";
					   
					   whereClauseTriples.append(backTriple);
				   }
			   } else if(SG_XML_TAG.STATUS_INSERT.getTagName().equals(tagAttrStatus)) {
				   		
					//First lookup the entity in store
					subject   = "?URI"+ System.nanoTime();			//getUUID(); 	//Create a unique URI for this Entity
					//subject   = BASE_URI + "ran"+ System.nanoTime();			//getUUID(); 	//Create a unique URI for this Entity  
					
					predicate = "rdf:type";
					object    = tagAttrClass;
					if(subject.startsWith("?"))
						triple    = subject +"  "+ predicate +" <"+ object +"> . \n ";
					else
						triple    = "<"+subject +"> "+ predicate +" <"+ object +"> . \n ";
			   }
			   
			   //Book keeping: Store the current subject's URI in order to restore it back later
			   subjectURIStack.push(subject);							
			   operationTypeStack.push(tagAttrStatus);
			   
			   currSubject = subject;			//Once an entity is done, restore the old entity's subject URI back
			   currOperation = tagAttrStatus;	//Restore the old operation type
			   
			   if(SG_XML_TAG.STATUS_INSERT.getTagName().equals(currOperation)){
				   
				   firstEntityProperty = true;
				   lookUpQuery.append(triple);
				   //sparqlQuery.append(triple);

				   //System.out.println("####"+ triple);
				   
//				   if(triple.startsWith("?"))
//					   minusClauseTriples.append(triple);
				   if(parentSubject != null){
					   String backTriple = null;
					   if(parentSubject.startsWith("?") && subject.startsWith("?"))
						   backTriple = parentSubject +" <" + tagAttrClass +"> "+ subject +" . \n ";		//Triple to link this entity with its parent
					   else if(parentSubject.startsWith("?"))
						   backTriple = parentSubject +" <" + tagAttrClass +"> <"+ subject +"> . \n ";
					   else if(subject.startsWith("?"))
						   backTriple = "<"+ parentSubject +"> <" + tagAttrClass +"> "+ subject +" . \n ";
					   else
						   backTriple = "<"+ parentSubject +"> <" + tagAttrClass +"> <"+ subject +"> . \n ";
					   
					   //sparqlQuery.append(backTriple);
					   lookUpQuery.append(backTriple);
				   }
			   }else if("Query".equals(currOperation)){
				   
			   }
		   }
		   else if(qName.equals(SG_XML_TAG.PROPERTY.getTagName())) {		//Property tag
			   //Read all the attributes inside the tag
			   tagAttrMapping = attrs.getValue(SG_XML_TAG.MAPPING.getTagName()); 
			   tagAttrName = attrs.getValue(SG_XML_TAG.NAME.getTagName());
			   //System.out.println(tagAttrMapping +"  "+ tagAttrName);
			   
			   String subject = (subjectURIStack.empty() ? null : subjectURIStack.peek());
			   try {
				   if(subject == null)
					   throw new BadSGConfigFileException();			//Every property should have a parent subject
			   }catch(BadSGConfigFileException be){
				   be.getMessage();
				   be.printStackTrace();
			   }
			   			   
			   String predicate = tagAttrName;					//getPredicate(tagAttrName);		//Extract the relation name from the name attribute 
			   String object = valueMap.get(tagAttrMapping.toLowerCase());		//Get the value corresponding to the 'mapping' attribute from the value map

			   //..hack .. change
			   if(object == null)
			   {
				   object = valueMap.get(tagAttrMapping);	
			   }
			   
			   // By Sreedhar System.out.println(valueMap.size() + "  " + tagAttrMapping + " " +  object);
			   if(SG_XML_TAG.STATUS_INSERT.getTagName().equals(currOperation)){
				   
				   String triple = null;
				   if(subject.startsWith("?"))
					   triple = subject +"  <"+ predicate +">  ";
				   else
					   triple = "<"+subject +">  <"+ predicate +">  "; 				   
				   if(object.startsWith("h")){
					   triple += "<"+ object +"> . \n ";
				   }else if(object.startsWith("?")){
					   triple += object +" . \n ";
				   }else {
					   triple += " '"+ object +"' . \n ";
				   }
				   
				   if(firstEntityProperty){			//Do a lookup for only the first property
					   firstEntityProperty = false;
					   
//					   String storeSubject = subjectURIStack.pop();
//					   String parentURI = subjectURIStack.peek();
//					   subjectURIStack.push(storeSubject);
//					   String parentTriples = getParentTriples(whereClauseTriples.toString(), parentURI);		//Fetch the triples for this entity's parent
//					   lookUpQuery.append(parentTriples);
//					   
					   lookUpQuery.append(triple);
					   
					   String entityURI = null;
					   entityURI = lookUpURI(lookUpQuery.toString(), subject);		//LookUp the URI for this entity. If not present returns null
					// By Sreedhar System.out.println("entityURI = "+ entityURI);
					   if(entityURI == null){		//If not present create a new one
						   entityURI = BASE_URI + "ran"+ SGConstants.getUUID();		//System.nanoTime();	 	//Create a unique URI for this Entity 
					   }
					// By Sreedhar System.out.println("entityURI = "+ entityURI +", subject = "+ subject);
					// By Sreedhar System.out.println("Before swapping: testTriples\n "+ lookUpQuery.toString());
					   
					   //Swap entityURI for subject in lookUpQuery and from subjectStack
					   String newTriples = replaceURI(lookUpQuery.toString(), entityURI, subject);
					   sparqlQuery.append(newTriples);
					   
					// By Sreedhar System.out.println("After swapping: testTriples\n "+ newTriples);
					   
					   subjectURIStack.pop();
					   subjectURIStack.push(entityURI);
					   
				   }else
					   sparqlQuery.append(triple);

//				   System.out.println("####"+ triple);
//				   if(triple.startsWith("?"))
//					   minusClauseTriples.append(triple);
			   }else if(SG_XML_TAG.STATUS_QUERY.getTagName().equals(currOperation)){
				   String triple = subject +" <"+ predicate +"> '"+ object +"' . \n ";
				   whereClauseTriples.append(triple);
			   }
		   }
		   //System.out.println();
	   }
	
	   /**
	    * Gets all the triples associated with the URI
	    * @param triples
	    * @param uri
	    * @return String containing all the triples that contain the given URI
	    */
	   private String getParentTriples(String triples, String uri) {
		
		   String retTriples = null;
		   StringTokenizer tok = new StringTokenizer(triples, "\n");
		   while(tok.hasMoreTokens()){
			   String token = tok.nextToken();
			   if(token.contains(uri)){
				   retTriples += token+"\n";
			   }
		   }
		   return retTriples;
	   }

	/**
	    * Replaces every occurrence of oldURI with newURI in triples
	    * @param triples
	    * @param newURI
	    * @param oldURI
	    * @return
	    */
	   private String replaceURI(String triples, String newURI, String oldURI) {
		   
		   StringTokenizer tok = new StringTokenizer(triples, " ");
		   String newTriples = "";
		   while(tok.hasMoreTokens()){
			   String token = tok.nextToken();
			   if(token.startsWith("?") && oldURI.equals(token)){
				   token = "<"+newURI+">";
			   }
			   
			   newTriples += " "+token+" ";
		   }
		   return newTriples;
	   }

	private String lookUpURI(String triples, String subject) {
		   
		   StringBuilder query = new StringBuilder("");
		   query.append("PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n ");
           query.append("SELECT "+ subject +" { \n ");
           query.append(triples);	
           query.append(" } \n ");
		   
           String sparqlQ = query.toString();
        // By Sreedhar System.out.println("Select Query: \n"+ sparqlQ);
           try {
			SPARQLResultSet resultSet = store.query(sparqlQ, Store.OutputFormat.SPARQL_XML, -1, true);
			List<Map<String, String>> results = resultSet.getResultSet();
			if(results.size() != 0){
				// By Sreedhar System.out.println("Found a matching triple in store. Returning URI");
				// By Sreedhar System.out.println("Matched URI: "+ results.get(0).get(subject.substring(1)));
				return results.get(0).get(subject.substring(1));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
           
        // By Sreedhar System.out.println("Did not find a matching triple in store. Returning null");
		   //return "testSGNSran123456";
		   //return query.toString();
           return null;
	}

	private String getPredicate(String uri) {
		   return uri.substring(uri.lastIndexOf('/')+1);
	   }

	public void endElement (String uri, String localName, String qName)
	   throws SAXException
	   {
//	       showData ("</"+qName+">");
	       
	       if(qName.equals(SG_XML_TAG.ENTITY.getTagName())) {
	    	   if(subjectURIStack.empty()){
	    		   currSubject = null;					//Done processing all the Entity tags, next will be the final Document closing tag
	    	   }else {
	    		   subjectURIStack.pop();					//Pop this Entity's URI from the stack
	    	   }
	    	   
	    	   if(operationTypeStack.empty()){
	    		   currOperation = null;
	    	   }else {
	    		   operationTypeStack.pop();					//Pop the current operation type from the stack
	    	   }
	    	   entityLevel--;
	       }
	       //else if()
	       tagType = null;
	       
//	   	   if(qName.equals("result")){
//		   		resultSet.add(elementMap);			//Add the Map to the resultSet list once all entries in the result element are read
//		   		elementMap = null;
//	       }
	   }
	
	   public void characters (char buf [], int offset, int len)
	   throws SAXException
	   {
	       String s = new String(buf, offset, len);
//	       showData (s);

//	       if(!s.contains("\n ") && elementMap != null && !key.equals("")){
//	    	   value = s;
//	    	   elementMap.put(key, value);
//	       }	   
	   }
	
	   //===========================================================
	   // Helpers Methods
	   //===========================================================
	
	   private static String getUUID(){
		   return SGConstants.getUUID();	//  UUID.randomUUID().toString();
	   }
	   
	   // Wrap I/O exceptions in SAX exceptions, to
	   // suit handler signature requirements
//	   private void showData (String s)
//	   throws SAXException
//	   {
//	       try {
//	           out.write (s);
//	           out.flush ();
//	       } catch (IOException e) {
//	           throw new SAXException ("I/O error", e);
//	       }
//	   }
	
	   // Start a new line
//	   private void newLine ()
//	   throws SAXException
//	   {
//	       String lineEnd =  System.getProperty("line.separator");
//	       try {
//	    	   out.write (lineEnd);
//	       } catch (IOException e) {
//	    	   throw new SAXException ("I/O error", e);
//	       }
//	   }
}