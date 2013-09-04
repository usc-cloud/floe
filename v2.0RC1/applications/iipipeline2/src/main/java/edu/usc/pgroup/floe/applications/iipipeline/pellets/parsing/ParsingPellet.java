/*
 * Copyright 2011, University of Southern California. All Rights Reserved.
 * 
 * This software is experimental in nature and is provided on an AS-IS basis only. 
 * The University SPECIFICALLY DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT 
 * LIMITATION ANY WARRANTY AS TO MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * This software may be reproduced and used for non-commercial purposes only, 
 * so long as this copyright notice is reproduced with each such copy made.
 */
package edu.usc.pgroup.floe.applications.iipipeline.pellets.parsing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.usc.pgroup.floe.api.framework.pelletmodels.StreamInStreamOutPellet;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.api.stream.FEmitter;
import edu.usc.pgroup.floe.api.stream.FIterator;
import edu.usc.pgroup.floe.applications.iipipeline.pellets.semantic.SemanticInfo;
/***
 * A Pellet is a user defined computation unit that can process a message.
 * By default, a pellet is assumed to be thread-safe to allow concurrent calls from multiple messages.
 *   
 * @author Sreedhar Natarajan (sreedhan@usc.edu)
 * @author Yogesh Simmhan (simmhan@usc.edu)
 * @version v0.1, 2012-01-11
 *
 */

public class ParsingPellet implements StreamInStreamOutPellet

{
		String pelletType;	
		Object stateObject;
		Properties props;
		private static Logger logger = Logger.getLogger(ParsingPellet.class.getName());
		public ParsingPellet()
		{
			//this.writable =  writableObj;
			//this.tag = tag;
			try
			{
				InputStream credentialsAsStream = new FileInputStream("config/Parsing.properties");
				props = new Properties();
				props.load(credentialsAsStream);				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}			
		}
		
		public static byte[] semanticToByte(SemanticInfo inpInfo) 
		{
			byte[] retBytes = null;
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			try 
			{
			    ObjectOutputStream outStream = new ObjectOutputStream(byteStream); 
			    outStream.writeObject(inpInfo);
			    outStream.flush(); 
			    outStream.close(); 
			    byteStream.close();
			    retBytes = byteStream.toByteArray ();
			}
			catch (IOException ex) 
			{
			    //TODO: Handle the exception
			}
			return retBytes;
		}
		public ParsingInfo byteToParsing (byte[] bytes)
		{
			ParsingInfo tempInfo = null;
			try 
			{
				ByteArrayInputStream byteInput = new ByteArrayInputStream (bytes);
				ObjectInputStream ois = new ObjectInputStream (byteInput);
				tempInfo = (ParsingInfo)ois.readObject();
			}
			catch (Exception ex) 
			{
				
			}
			return tempInfo;
		}
		
		
		/*public void invoke(Object inpByte,StateObject stateObject)
		{						
			try
			{
				ParsingInfo tempInfo = byteToParsing((byte[])inpByte);			
				ArrayList<HashMap<String,String>> mapOut = null;				
				if(props.containsKey(tempInfo.getDescription()))
				{
					
					if(tempInfo!=null)
					{
						if(tempInfo.fileType.contains("CSV"))					
						{							
							ArrayList<ParserExtractorData> parserData = new ArrayList<ParserExtractorData>();
							ParserExtractor newParser = new ParserExtractor(props.getProperty(tempInfo.getDescription()));
							parserData = newParser.parseFile();
							CSVParser cParser = new CSVParser(tempInfo.byteStream,",",parserData);						
							mapOut = cParser.parseFile();							
						}
						else if(tempInfo.fileType.contains("XLSX"))					
						{							
							ArrayList<ParserExtractorData> parserData = new ArrayList<ParserExtractorData>();							
							ParserExtractor newParser = new ParserExtractor(props.getProperty(tempInfo.getDescription()));
							parserData = newParser.parseFile();
							XLSXParser cParser = new XLSXParser(tempInfo.getByteStream(),parserData);						
							mapOut = cParser.parseFile();							
						}
						else if(tempInfo.fileType.contains("XML"))
						{
							XMLParser xParser = new XMLParser(tempInfo.getByteStream(),props.getProperty(tempInfo.getDescription()));				
							mapOut = xParser.parseFile();	
						}
						else if(tempInfo.fileType.contains("STREAM"))
						{
							mapOut = tempInfo.getMap();
							
						}
						for(Map<String,String> inpMap: mapOut)
						{
							SemanticInfo semanticInfo = new SemanticInfo();
							semanticInfo.setMap(inpMap);
							semanticInfo.setDescription(tempInfo.getDescription());
							
							/*
							Message<byte[]> tempMessage = new MessageImpl<byte[]>();
							tempMessage.putPayload(semanticToByte(semanticInfo));
							// Stream of SemanticInfo messages
							this.write(tempMessage);
							/
							
							this.write(semanticInfo);
						}								
					}					
				}	
				else
				{
					System.out.println("Unable to Parse the Contents. Parsing Configuration Unknown");
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}									
		}*/
		
		public String getPelletType()
		{
			return this.pelletType;
		}
		@Override
		public void invoke(FIterator in, FEmitter out, StateObject stateObject) {
			logger.log(Level.SEVERE, "Pellet invoked");
			try
			{
				
				System.out.println("parsing");
				ParsingInfo tempInfo =(ParsingInfo)in.next();			
				ArrayList<HashMap<String,String>> mapOut = null;				
				if(props.containsKey(tempInfo.getDescription()))
				{
					
					if(tempInfo!=null)
					{
						if(tempInfo.fileType.contains("CSV"))					
						{							
							ArrayList<ParserExtractorData> parserData = new ArrayList<ParserExtractorData>();
							ParserExtractor newParser = new ParserExtractor(props.getProperty(tempInfo.getDescription()));
							parserData = newParser.parseFile();
							CSVParser cParser = new CSVParser(tempInfo.byteStream,",",parserData);						
							mapOut = cParser.parseFile();							
						}
						else if(tempInfo.fileType.contains("XLSX"))					
						{							
							ArrayList<ParserExtractorData> parserData = new ArrayList<ParserExtractorData>();							
							ParserExtractor newParser = new ParserExtractor(props.getProperty(tempInfo.getDescription()));
							parserData = newParser.parseFile();
							XLSXParser cParser = new XLSXParser(tempInfo.getByteStream(),parserData);						
							mapOut = cParser.parseFile();							
						}
						else if(tempInfo.fileType.contains("XML"))
						{
							XMLParser xParser = new XMLParser(tempInfo.getByteStream(),props.getProperty(tempInfo.getDescription()));				
							mapOut = xParser.parseFile();	
						}
						else if(tempInfo.fileType.contains("STREAM"))
						{
							mapOut = tempInfo.getMap();
							
						}
						for(Map<String,String> inpMap: mapOut)
						{
							SemanticInfo semanticInfo = new SemanticInfo();
							semanticInfo.setMap(inpMap);
							semanticInfo.setDescription(tempInfo.getDescription());
							
							/*
							Message<byte[]> tempMessage = new MessageImpl<byte[]>();
							tempMessage.putPayload(semanticToByte(semanticInfo));
							// Stream of SemanticInfo messages
							this.write(tempMessage);
							*/
							out.emit(semanticInfo);
							//this.write(semanticInfo);
						}		
						//logger.log(Level.SEVERE, mapOut.toString());
						//System.out.println();
					}					
				}	
				else
				{
					System.out.println("Unable to Parse the Contents. Parsing Configuration Unknown");
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	
}
