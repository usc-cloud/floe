package edu.usc.pgroup.floe.applications.iipipeline.pellets.parsing;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
public class XLSXParser implements Parser
{			
	private ArrayList<HashMap<String,String>> globalKeyValue;
	private ArrayList<ParserExtractorData> headerDetail;
	byte[] byteStream ;

	public XLSXParser(byte[] inpStream,ArrayList<ParserExtractorData> parserFieldData)
	{
		this.byteStream = inpStream;
		this.headerDetail = parserFieldData;		
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
	public class customComparator implements Comparator<ParserExtractorData> {
		@Override
		public int compare(ParserExtractorData obj1, ParserExtractorData obj2) 
		{
			
			return obj1.getColNum() - obj2.getColNum(); 			
		}
	}
	
    public ArrayList<HashMap<String,String>> parseFile() throws Exception
    {        

    	try
    	{    		
        	InputStream inpFile = new ByteArrayInputStream(this.byteStream);
        	XSSFWorkbook workBK = new XSSFWorkbook(inpFile);
        	XSSFSheet sheet = workBK.getSheetAt(0);;    
        	XSSFRow row;
        	XSSFCell cell;
        	
        	globalKeyValue = new ArrayList<HashMap<String,String>>();
    		//Find out the Column numbers of the Excel File which needs to 
    		// extracted
        	//System.out.println("Header Detail " + headerDetail.size());
    		Iterator<ParserExtractorData> headerIter = this.headerDetail.iterator();
    		Iterator rowIter = sheet.rowIterator();
    		row = (XSSFRow)rowIter.next();								
    		int colCount = 0;
    		while(headerIter.hasNext())
    		{
    			Iterator cellIter = row.cellIterator();
    			ParserExtractorData tempObj = (ParserExtractorData) headerIter.next();
    			colCount = 0;
    			while(cellIter.hasNext())
    			{
    				cell = (XSSFCell)cellIter.next();
    				String cellValue = cell.getRichStringCellValue().toString().toLowerCase();				
    				if(tempObj.getHeaderName().contains(cellValue.toLowerCase()))
    				{
    					tempObj.setColNum(colCount);					
    					break;
    				}
    				colCount++;
    			}							
    		}
    		// Sort the data based on the Column No
    		Collections.sort(this.headerDetail,new customComparator());
    		
    		// Generate the Key Value Pair form the data contained in file.
    		//row = (XSSFRow)rowIter.next();
    		
    		while(rowIter.hasNext())
    		{
    			
    			row = (XSSFRow)rowIter.next();
    			Iterator cellIter = row.cellIterator();
    			cell = (XSSFCell)cellIter.next();			
    			headerIter = headerDetail.iterator();
    			HashMap<String,String> keyValuePair = new HashMap<String,String>();
    			while(headerIter.hasNext())
    			{
    				ParserExtractorData headObj = (ParserExtractorData)headerIter.next();							
    				while(cell.getColumnIndex()< headObj.getColNum())
    				{
    					if(cellIter.hasNext())
    						cell = (XSSFCell)cellIter.next();
    					else
    						break;
    				}
    				if(cell.getColumnIndex() == headObj.getColNum())
    				{
    					if(cell.getCellType()==0)
    					{
    						String tem;
    						double temp = (double) cell.getNumericCellValue();
    						keyValuePair.put(headObj.getHeaderTransform(),Double.toString(temp));
    						//System.out.println(headObj.getHeaderTransform() + Double.toString(temp));
    					}
    					else if(cell.getCellType()==1)
    					{
    						keyValuePair.put(headObj.getHeaderTransform(),cell.getRichStringCellValue().toString());
    						//System.out.println(headObj.getHeaderTransform() + cell.getRichStringCellValue().toString());
    					}
    					if(cellIter.hasNext())
    						cell = (XSSFCell)cellIter.next();
    				}										
    			}// End of Row Iteration
    			if(keyValuePair.size() >0)
    				globalKeyValue.add(keyValuePair);
    		}	
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	
		return globalKeyValue;
    }
    
}
