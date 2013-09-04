/* Extracts Field Name from the XML file and returns an ArrayList of ParserExtractData
 * objects which specify the fields from the source data to be extarcted and the 
 * transformation rule to be taken care of.
 */
package edu.usc.pgroup.floe.applications.iipipeline.pellets.parsing;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ParserExtractor 
{
	private String parserConfigFile;
	public ParserExtractor(String filePath)
	{
		this.parserConfigFile = filePath;
	}
	private static String getTagValue(String sTag, Element eElement)
	{		
		if(eElement.getElementsByTagName(sTag).item(0) != null)
		{
			NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();		    
		  	Node nValue = (Node) nlList.item(0);
		  	return nValue.getNodeValue();
		}
		else
		{
			return "";
		}			    	       
	}
	public ArrayList<ParserExtractorData> parseFile() throws Exception
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(this.parserConfigFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("Entity");
		ArrayList<ParserExtractorData> headerDetail = new ArrayList<ParserExtractorData>();
		
		//Gather Information about which Fields to extract from
		//the XLSX File mentioned in the Config File		
		for(int i=0;i<nList.getLength();i++)
		{
			Node nNode = nList.item(i);
			String headName="",transformName="";
			if(nNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element eElement = (Element)nNode;
				headName = getTagValue("HeaderName",eElement).toLowerCase();
				transformName = getTagValue("Transformation",eElement);				
				ParserExtractorData infoObj = new ParserExtractorData();
				infoObj.setHeaderName(headName);
				infoObj.setHeaderTransform(transformName);
				headerDetail.add(infoObj);
			}
			
			System.out.println(headName + " " + transformName);
		}
		
		return headerDetail;
	}
}
