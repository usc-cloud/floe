package edu.usc.pgroup.floe.applications.iipipeline.pellets.semantic;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.co.magus.fourstore.client.exceptions.BadSGConfigFileException;
import edu.usc.pgroup.floe.applications.iipipeline.pellets.utils.SGConstants;

/**
 * This is a very specific class for certain type config files mentioned below
 * Class that reads a semanticConfigFile and a List of Map, to write a Bulk SELECT query, execute it and then write a bulk Insert Query.
 * This will be done mostly for inserting sensor readings, where we first lookup a set of SensorID/URI and then Insert their readings in
 * bulk Insert Data operation.
 * 
 *  Config file handled https://ganges.usc.edu/svn/pg/code/smartgrid/info-int/config/semanticConfigElectric.xml
 *  
 * @author nrajguru
 *
 */
//QueryWriterSelectInsertData
public class  SelectInsertDataQuery extends DefaultHandler  {
	
	static private String XML_FILE_NAME = "config/semanticConfigElectric.xml";   //"config/semanticConfigBuilding.xml"; 
	//"config/semanticConfigEquipment.xml"; 
	
	static private int COUNT = 0;
//	private Writer out;
	
//	static private Map<String, String> valueMap = new HashMap<String, String>();
//	static{
//		valueMap.put("BuildingNum", "101");
//		valueMap.put("BuildingCode", "EEB");
//		valueMap.put("BuildingAddress", "USC USC USC");
//		valueMap.put("BuildingFloors", "20");
//		valueMap.put("YearBuilt", "1990");
//		valueMap.put("GrossArea", "10000");
//		
//		valueMap.put("Architect", "Tommy");		//Architect Entity
//		valueMap.put("Department", "Electical");
//		
//		valueMap.put("Latitude", "77.2");
//		valueMap.put("Longitude", "123.5");
//		
//		valueMap.put("SensorID", "testSensorID"+COUNT);
//		
//		valueMap.put("Value", "220");			//ElectricalMeasurement -> hasMeasuredValue
//		valueMap.put("Date", "01:Jan:2012");	//ElectricalMeasurement -> hasMeasurementRecordedDate
//		valueMap.put("Time", "03:22:55");		//ElectricalMeasurement -> hasMeasurementRecordedTime
//	}
	
	private static String BASE_URI = "http://www.smartgrid.usc.edu/Ontology/2012.owl#";
	
	//Member variables
	private StringBuilder sparqlQuery;
	private StringBuilder whereClauseTriples;
	
	private Map<String, String> valueMap;
	
//	private StringBuilder selectQuery;		//Stores triples used in Bulk SELECT
//	private StringBuilder insertQuery;		//Store triples used in bulk INSERT DATA
	
	private String tagType;
	private String tagAttrClass;
	private String tagAttrKey;
	private String tagAttrStatus;
	private String tagAttrMapping;
	private String tagAttrName;
	
	private int entityLevel;

	//Members to keep track of URIs 
	private String currSubject;			//Variable to keep track of current Subject URI
	private Stack<String> subjectURIStack;			//Stack to store Subjects URI of all levels before the current one
	
	//Members to keep track of the current operation
	private String currOperation;
	private Stack<String> operationTypeStack;
	
	public SelectInsertDataQuery(Map<String, String> map) throws UnsupportedEncodingException {
		
		valueMap = map;
//		out = new OutputStreamWriter (System.out, "UTF8");
		sparqlQuery = new StringBuilder();
		whereClauseTriples = new StringBuilder();
		
//		selectQuery = new StringBuilder("");
//		insertQuery = new StringBuilder("");
		
		tagType = null;
		tagAttrClass = null;
		tagAttrKey = null;
		tagAttrStatus = null;
		tagAttrMapping = null;
		tagAttrName = null;
		entityLevel = -1;
		
		currSubject = null;
		subjectURIStack = new Stack<String>();
		
		currOperation = null;
		operationTypeStack = new Stack<String>();
	}
	
	public static void main(String[] args) {
       SAXParserFactory factory = SAXParserFactory.newInstance();
       try {
           // Set up output stream
//           	out = new OutputStreamWriter (System.out, "UTF8");
            System.out.println("Starting main()");
//           	
//           // Parse the input
//           SAXParser saxParser = factory.newSAXParser();
//           InputStream ins = new FileInputStream(new File(XML_FILE_NAME)); 
//           SelectInsertWhereQuery result = new SelectInsertWhereQuery();
//           saxParser.parse(ins, result);
//           ins.close();
//           
//           String query = result.getSparqlQuery();
//           System.out.println("SPARQL Query : "+ query);
           
       }catch(Exception e){
    	   e.getMessage();
    	   e.printStackTrace();
       }
	}
	
	public String getSparqlQuery(){
		return sparqlQuery.toString();
	}
	
	public String getWhereClauseTriples(){
		return whereClauseTriples.toString();
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
//	       try {
//	           newLine();
//	           out.flush ();
	           
	           if(whereClauseTriples.toString().equals("")){		//There should be something to query, if you are using this class
	        	   try{
	        		   throw new BadSGConfigFileException();			
				   }catch(BadSGConfigFileException be){
					   be.getMessage();
					   be.printStackTrace();
				   }
	           }
	           
//	           StringBuilder query = new StringBuilder("");
//        	   query.append("PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
//	           query.append(" SELECT * WHERE { \n");
//	           query.append(whereClauseTriples.toString());
//	           query.append("       } \n");
	           
//	           selectQuery.append(whereClauseTriples.toString());
	           
	           //System.out.println("SELECT Query: "+ whereClauseTriples.toString());
	           
//	           insertQuery.append(sparqlQuery.toString());
//        	   String tquery = sparqlQuery.toString();
//        	   sparqlQuery = new StringBuilder("");
//        	   sparqlQuery.append("PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
//               sparqlQuery.append("INSERT DATA { \n");
//               sparqlQuery.append(tquery);	
//               sparqlQuery.append(" } \n");
               
	           //System.out.println("Insert Query: "+ sparqlQuery.toString());
	           
//	       } catch (IOException e) {
//	           throw new SAXException ("I/O error", e);
//	       }
	   }
	   
	   public void startElement(String uri, String localName, String qName, Attributes attrs) // (String name, AttributeList attrs)
	   throws SAXException
	   {
//		   showData ("<"+qName);
//		   if (attrs != null) {
//		       for (int i = 0; i < attrs.getLength (); i++) {
//		           showData (" ");
//		           showData (attrs.getLocalName(i)+"=\""+attrs.getValue (i)+"\"");
//		       }
//		   }
//		   showData (">");

		  String parentSubject = null; 
		  tagType = qName;
		  entityLevel++;
//		  System.out.println(tagType); 
		  if(qName.equals(SGConstants.SG_XML_TAG.ENTITY.getTagName())){			//Entity tag
			   //Read all the attributes inside the tag
			   tagAttrStatus = attrs.getValue(SGConstants.SG_XML_TAG.STATUS.getTagName());
			   tagAttrClass = attrs.getValue(SGConstants.SG_XML_TAG.CLASS.getTagName());					
			   tagAttrKey = attrs.getValue(SGConstants.SG_XML_TAG.KEY.getTagName());
			   //System.out.println(tagAttrClass +"  "+ tagAttrKey +"  "+ tagAttrStatus);
			   
			   String subject = null;
			   String predicate = null;
			   String object = null;
			   String triple = null;
			   if(SGConstants.SG_XML_TAG.STATUS_QUERY.getTagName().equals(tagAttrStatus)) {
				   subject   = "?sensorURI"+ System.nanoTime();
				   predicate = "rdf:type ";
				   object 	 = tagAttrClass;
				   triple    = subject +" "+ predicate +" <"+ object +"> . \n";		//subject is a variable an not a URI, so do not surround it by <   >
				   whereClauseTriples.append("OPTIONAL { \n ");
				   whereClauseTriples.append(triple);
			   } else if(SGConstants.SG_XML_TAG.STATUS_INSERT.getTagName().equals(tagAttrStatus)) {
				   subject   = BASE_URI +"ran"+ SGConstants.getUUID();	//"ran"+ System.nanoTime();	//Create a unique URI for this Entity
				   predicate = "rdf:type";
				   object    = tagAttrClass;
				   if(subject.startsWith("?"))
					   triple    = subject +"  "+ predicate +" <"+ object +"> . \n";
				   else
					   triple    = "<"+subject +"> "+ predicate +" <"+ object +"> . \n";
			   }
			   
			   if(subjectURIStack.empty()){
				   parentSubject = null;
			   }
			   else {
				   parentSubject = subjectURIStack.peek();
			   }
			   
			   //Store the current subject's URI in order to restore it back later
			   subjectURIStack.push(subject);							
			   operationTypeStack.push(tagAttrStatus);
			   
			   currSubject = subject;			//Once an entity is done, restore the old entity's subject URI back
			   currOperation = tagAttrStatus;	//Restore the old operation type
			   
			   if(SGConstants.SG_XML_TAG.STATUS_INSERT.getTagName().equals(currOperation)){
				   sparqlQuery.append(triple);
				   if(parentSubject != null){
					   String backTriple = null;
					   if(parentSubject.startsWith("?") && subject.startsWith("?"))
						   backTriple = parentSubject +" <" + tagAttrClass +"> "+ subject +" . \n";		//Triple to link this entity with its parent
					   else if(parentSubject.startsWith("?"))
						   backTriple = parentSubject +" <" + tagAttrClass +"> <"+ subject +"> . \n";
					   else if(subject.startsWith("?"))
						   backTriple = "<"+ parentSubject +"> <" + tagAttrClass +"> "+ subject +" . \n";
					   else
						   backTriple = "<"+ parentSubject +"> <" + tagAttrClass +"> <"+ subject +"> . \n";
					   
					   sparqlQuery.append(backTriple);
				   }
			   }else if("Query".equals(currOperation)){
				   
			   }
		   }
		   else if(qName.equals(SGConstants.SG_XML_TAG.PROPERTY.getTagName())) {		//Property tag
			   //Read all the attributes inside the tag
			   tagAttrMapping = attrs.getValue(SGConstants.SG_XML_TAG.MAPPING.getTagName()); 
			   tagAttrName = attrs.getValue(SGConstants.SG_XML_TAG.NAME.getTagName());
			   //System.out.println(tagAttrMapping +"  "+ tagAttrName);
			   
			   String subject = (subjectURIStack.empty() ? null : subjectURIStack.peek());
			   try {
				   if(subject == null)
					   throw new BadSGConfigFileException();			//Every property should have a parent subject
			   }catch(BadSGConfigFileException be){
				   be.getMessage();
				   be.printStackTrace();
			   }
			   
			   String predicate = tagAttrName;			//getPredicate(tagAttrName);		//Extract the relation name from the name attribute 
			   String object = valueMap.get(tagAttrMapping.toLowerCase());		//Get the value corresponding to the 'mapping' attribute from the value map
			   
			   if(object == null)
			   {
				   object = valueMap.get(tagAttrMapping);		//Get the value corresponding to the 'mapping' attribute from the value map
			   }
			   if(SGConstants.SG_XML_TAG.STATUS_INSERT.getTagName().equals(currOperation)){
				   String triple = null;
				   if(subject.startsWith("?"))
					   triple = subject +"  <"+ predicate +">  ";
				   else
					   triple = "<"+subject +">  <"+ predicate +">  "; 
				   
				   if(object.startsWith("h")){
					   triple += "<"+ object +"> . \n";
				   }else if(object.startsWith("?")){
					   triple += object +" . \n";
				   }else {
					   triple += " '"+ object +"' . \n";
				   }
				   sparqlQuery.append(triple);
			   }else if(SGConstants.SG_XML_TAG.STATUS_QUERY.getTagName().equals(currOperation)){
				   String triple = subject +" <"+ predicate +"> '"+ object +"' . \n";
				   whereClauseTriples.append(triple);
				   whereClauseTriples.append(" } \n ");
			   }
		   }
		   //System.out.println();
	   }
	
	   private String getPredicate(String uri) {
		   return uri.substring(uri.lastIndexOf('/')+1);
	   }

	public void endElement (String uri, String localName, String qName)
	   throws SAXException
	   {
	       showData ("</"+qName+">");
	       
	       if(qName.equals(SGConstants.SG_XML_TAG.ENTITY.getTagName())) {
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
	       showData (s);

//	       if(!s.contains("\n") && elementMap != null && !key.equals("")){
//	    	   value = s;
//	    	   elementMap.put(key, value);
//	       }	   
	   }
	   
	   //===========================================================
	   // Helpers Methods
	   //===========================================================
	
	   // Wrap I/O exceptions in SAX exceptions, to
	   // suit handler signature requirements
	   private void showData (String s)
	   throws SAXException
	   {
//	       try {
//	           out.write (s);
//	           out.flush ();
//	       } catch (IOException e) {
//	           throw new SAXException ("I/O error", e);
//	       }
	   }
	
	   // Start a new line
	   private void newLine ()
//	   throws SAXException
	   {
//	       String lineEnd =  System.getProperty("line.separator");
//	       try {
//	    	   out.write (lineEnd);
//	       } catch (IOException e) {
//	    	   throw new SAXException ("I/O error", e);
//	       }
	   }
}
