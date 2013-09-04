package edu.usc.pgroup.floe.applications.iipipeline.pellets.parsing;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;


public class CSVParser 
{
	private String delimeter;
	private ArrayList<HashMap<String,String>> globalKeyValue;
	private ArrayList<ParserExtractorData> headerDetail;
	byte[] byteStream ;
	public CSVParser(byte[] inpStream,ArrayList<ParserExtractorData> parserFieldData)
	{
		this.byteStream = inpStream;
		this.delimeter = ",";
		this.headerDetail = parserFieldData;
	}
	public CSVParser(byte[] inpStream,String inpDelim,ArrayList<ParserExtractorData> parserFieldData)
	{
		this.byteStream = inpStream;
		this.delimeter = inpDelim;
		this.headerDetail = parserFieldData;
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
		ParsingInfo tempInfo = new ParsingInfo(); 
		ByteArrayInputStream bis = new ByteArrayInputStream(this.byteStream);
		BufferedReader buffRead = new BufferedReader(new InputStreamReader(bis));
		Iterator<ParserExtractorData> headerIter = this.headerDetail.iterator();
		String inpLine = "";
		StringTokenizer stToken = null;
		inpLine = buffRead.readLine();				
		while(headerIter.hasNext())
		{		
			int colCount = 0;
			ParserExtractorData tempObj = (ParserExtractorData) headerIter.next();
			stToken = new StringTokenizer(inpLine, ",");
			while(stToken.hasMoreTokens())
			{
				//display csv values
				String cellValue = stToken.nextToken();
				if(tempObj.getHeaderName().toLowerCase().contains(cellValue.toLowerCase()))
				{
					tempObj.setColNum(colCount);
					
					break;	
				}				
				colCount++;
			}			
		}
		this.globalKeyValue = new ArrayList<HashMap<String,String>>();
		Collections.sort(this.headerDetail,new customComparator());
		while((inpLine=buffRead.readLine())!=null)
		{
			headerIter = headerDetail.iterator();
			HashMap<String,String> keyValuePair = new HashMap<String,String>();						
			while(headerIter.hasNext())
			{
				int colCount = 0;
				ParserExtractorData tempObj = (ParserExtractorData) headerIter.next();				
				boolean quoteSet = false;				
				stToken = new StringTokenizer(inpLine, this.delimeter);
				String cellValue = "";
				while(stToken.hasMoreTokens())
				{
					//display csv values
					String currStr = stToken.nextToken();
					if(currStr.contains("\"")==true&&(quoteSet == false))
					{
						quoteSet = true;
						cellValue = currStr;					
						continue;
					}
					if(quoteSet == true)
					{
						cellValue = cellValue + currStr;
						if(currStr.contains("\"")==true)
						{
							quoteSet = false;							
							if(tempObj.getColNum() == colCount)
							{
								keyValuePair.put(tempObj.getHeaderTransform(), cellValue);
								//System.out.println(tempObj.getHeaderTransform() + " " + cellValue); 
								break;
							}
							colCount++;
						}					
					}
					else
					{
						cellValue = currStr;
						if(tempObj.getColNum() == colCount)
						{
							keyValuePair.put(tempObj.getHeaderTransform(), cellValue);
							//System.out.println(tempObj.getHeaderTransform() + " " + cellValue);
							break;
						}						
						colCount++;
					}							
				}			
			}
			if(keyValuePair.size() >0)
				globalKeyValue.add(keyValuePair);
		}

		
		return globalKeyValue;
	}
}
