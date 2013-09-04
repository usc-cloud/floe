package uk.co.magus.fourstore.client;

/**
 *  Class for reading the XML data from the HttpURLConnection and returning a
 *  ResultSet of the items 
 *
 *  @Author Nikhil Rajguru
**/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLResultSet extends DefaultHandler implements SPARQLResultSet
{
//	static private Writer  out;
//	protected static final String XML_FILE_NAME = "results.xml";
   
   private List<Map<String, String>> resultSet;
   private String key;
   private String value;
   private Map<String, String> elementMap;		//For storing key,value pairs in each element temporarily 
   
   public XMLResultSet(){
	   resultSet = new ArrayList<Map<String, String>>();
	   key = "";
	   value = "";
	   elementMap = null;
   }
   
   @Override
   public List<Map<String, String>> getResultSet() {
	return resultSet;
   }
   
   @Override
   public void setResultSet(List<Map<String, String>> resultSet) {
	this.resultSet = resultSet;
   }
   
//   public static void main (String argv [])
//   {
//       // Use the default (non-validating) parser
//       SAXParserFactory factory = SAXParserFactory.newInstance();
//       try {
//           // Set up output stream
//           out = new OutputStreamWriter (System.out, "UTF8");
//
//           // Parse the input
//           SAXParser saxParser = factory.newSAXParser();
//           InputStream ins = new FileInputStream(new File(XML_FILE_NAME)); 
//           XMLResultSet result = new XMLResultSet();
//           saxParser.parse( ins, result );
//           
//           System.out.println(result.resultSet.size());
//           for(Map<String, String> element: result.resultSet){
//        	   System.out.println(element.size());
//        	   for(Map.Entry<String, String> me: element.entrySet())
//        		   System.out.println(me.getKey() +" -> "+ me.getValue());
//           }
//       } catch (Throwable t) {
//           t.printStackTrace ();
//       }
//       System.exit (0);
//   }
   
   //===========================================================
   // Methods in SAX DocumentHandler 
   //===========================================================

//   public void startDocument ()
//   throws SAXException
//   {
//       showData ("<?xml version='1.0'?>");
//       newLine();
//   }

//   public void endDocument ()
//   throws SAXException
//   {
//       try {
//           newLine();
//           out.flush ();
//       } catch (IOException e) {
//           throw new SAXException ("I/O error", e);
//       }
//   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) // (String name, AttributeList attrs)
   throws SAXException
   {
//       showData ("<"+qName);
//       if (attrs != null) {
//           for (int i = 0; i < attrs.getLength (); i++) {
//               showData (" ");
//               showData (attrs.getLocalName(i)+"=\""+attrs.getValue (i)+"\"");
//           }
//       }
//       showData (">");
     
       if(qName.equals("binding")){
    	   value = "";
    	   key = attrs.getValue(0);		//Change the key name every time we see the binding tag
       }
       else if(qName.equals("result")){
    	   key = "";
    	   elementMap = new HashMap<String, String>();		//Create a new Map element for each new result
       }
   }

   public void endElement (String uri, String localName, String qName)
   throws SAXException
   {
//       showData ("</"+qName+">");
       
       if(qName.equals("result")){
    	   resultSet.add(elementMap);			//Add the Map to the resultSet list once all entries in the result element are read
    	   elementMap = null;
       }
   }

   public void characters (char buf [], int offset, int len)
   throws SAXException
   {
       String s = new String(buf, offset, len);
//       showData (s);
       if(!s.contains("\n") && elementMap != null && !key.equals("")){
    	   value = s;
    	   elementMap.put(key, value);
       }	   
   }

/*   
   //===========================================================
   // Helpers Methods
   //===========================================================

   // Wrap I/O exceptions in SAX exceptions, to
   // suit handler signature requirements
   private void showData (String s)
   throws SAXException
   {
       try {
           out.write (s);
           out.flush ();
       } catch (IOException e) {
           throw new SAXException ("I/O error", e);
       }
   }

   // Start a new line
   private void newLine ()
   throws SAXException
   {
       String lineEnd =  System.getProperty("line.separator");
       try {
           out.write (lineEnd);
       } catch (IOException e) {
           throw new SAXException ("I/O error", e);
       }
   }
*/
}

