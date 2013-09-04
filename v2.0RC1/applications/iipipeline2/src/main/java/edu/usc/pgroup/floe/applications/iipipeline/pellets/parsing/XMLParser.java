package edu.usc.pgroup.floe.applications.iipipeline.pellets.parsing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class XMLParser implements Parser
{	
	private String xsltFilePath;
	byte[] byteStream;
	public XMLParser(byte[] byteStream,String xsltFile)
	{
		this.byteStream = byteStream;
		this.xsltFilePath = xsltFile;
	}
	public ArrayList<HashMap<String,String>> parseFile() throws Exception
	{
		ArrayList<HashMap<String,String>> globalMap = new ArrayList<HashMap<String,String>>();
        File xsltFile = new File(this.xsltFilePath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();        
       	//Parse the Source XML File
        ByteArrayInputStream inpStream = new ByteArrayInputStream(this.byteStream);
        Document document = builder.parse(inpStream);       	       	
       	//Use a Transformer for output
       	TransformerFactory tFactory = TransformerFactory.newInstance();
       	StreamSource stylesource = new StreamSource(xsltFile);
       	System.out.println(xsltFile);
       	Transformer transformer = tFactory.newTransformer(stylesource);
       	DOMSource xmlSource = new DOMSource(document);
       	//Pass the output of the Transformer to String
       	StreamResult xmlResult = new StreamResult(new StringWriter());       	                        	
        transformer.transform(xmlSource, xmlResult);        
        StringReader strReader = new StringReader(xmlResult.getWriter().toString());
        InputSource inpSource = new InputSource(strReader);
        Document parsedXML = builder.parse(inpSource);
        parsedXML.getDocumentElement().normalize();
        // Parse Nodes from the XML generated from XSL Tranformation
        NodeList nList = parsedXML.getElementsByTagName("Entity");
        for(int i=0;i<nList.getLength();i++)
        {
        	HashMap<String,String> tempMap = new HashMap<String,String>();
        	Node nNode = nList.item(i);        	
			if(nNode.getNodeType() == Node.ELEMENT_NODE)
			{
				// Get the Children of the Entity Tags for Key Value Pairs
	        	NodeList currList = nNode.getChildNodes();
	        	for(int j=0;j<currList.getLength();j++)
	        	{
	        		Node currNode = currList.item(j);
	        		if(currNode.getNodeType() == Node.ELEMENT_NODE)
	        		{	        			
	        			//System.out.println(currNode.getNodeName() + "  " + currNode.getTextContent());
	        			tempMap.put(currNode.getNodeName(),currNode.getTextContent().trim());
	        			
	        		}
	        	}		        	
			}
			Calendar calendar = Calendar.getInstance();
    		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    		SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm");
    		String dateNow = dateFormat.format(calendar.getTime());
    		String timeNow = dateFormat1.format(calendar.getTime());
    		tempMap.put("Date",dateNow);
            tempMap.put("Time",timeNow);            
			globalMap.add(tempMap);
        }
        // FOr Weather Alone Insert Date & Time
        
        
        return globalMap;

	}	
}
