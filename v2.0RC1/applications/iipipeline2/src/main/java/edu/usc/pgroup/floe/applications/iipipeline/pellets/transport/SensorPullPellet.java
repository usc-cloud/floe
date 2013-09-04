package edu.usc.pgroup.floe.applications.iipipeline.pellets.transport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import edu.usc.pgroup.floe.api.framework.pelletmodels.Pellet;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.applications.iipipeline.pellets.parsing.ParsingInfo;
import edu.usc.pgroup.floe.applications.iipipeline.pellets.utils.EBIClient;

public class SensorPullPellet implements Pellet
{

	/*public  SensorPull(SingleWritable writable, String tag)
	{		
		this.tag = tag;
		this.writable = writable;
	}*/
	
	public SensorListHolder byteToArrayList (byte[] bytes)
	{
		SensorListHolder transportInfo = null;
		try 
		{
			ByteArrayInputStream byteInput = new ByteArrayInputStream (bytes);
			ObjectInputStream ois = new ObjectInputStream (byteInput);
			transportInfo = (SensorListHolder)ois.readObject();
		}
		catch (Exception ex) 
		{
	  
		}
		return transportInfo;
	}
	public static byte[] sensorDataToByte(ArrayList<SensorData> inpInfo) 
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
	public static byte[] parsingInfoToByte(ParsingInfo inpInfo) 
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
	//@Override
	public String getPelletType() {
		// TODO Auto-generated method stub
		return null;
	}
	private double[]  fetchFromActualEBI(String sensorList)
	{
		double[] retVal = new double[10];
		try
		{
			Socket           client    = new Socket("128.125.225.131", 5000);
	        DataOutputStream socketOut = new DataOutputStream(client.getOutputStream());
	        DataInputStream  socketIn  = new DataInputStream(client.getInputStream());
	        DataInputStream  console   = new DataInputStream(System.in);
	        //System.out.println("Connected to " +  ". Enter text:");
	        boolean done = false;	                  
	        socketOut.writeBytes(sensorList+"\n");
	        socketOut.flush();     
	        String retStr = "";
	        while((retStr = socketIn.readLine())!=null)
	        {	        
	        	//System.out.println(retStr);
	        	break;
	        }   
	        String[] retList = retStr.split(";"); 
	        for(int i=0;i<10;i++)
	        {
	        	retVal[i] = Float.parseFloat(retList[i]);
	        }	        
	        socketIn.close(); client.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return retVal;
	}
	private double[] fetchFromEBIServer(String[] sensorList) {
		
		double values[] = null;
		EBIClient client = new EBIClient();

		try {
			client.Initialize("sacramento.usc.edu", 5001);
			values = client.getDataPoint(sensorList, null);
			client.Disconnect();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return values;
	}

	
	public Object invoke(Object in,
		StateObject stateObject) 
	{
		SensorListHolder sensorHolder = (SensorListHolder)in;
		if(sensorHolder!=null)
		{
			ArrayList<SensorData> sensorList = sensorHolder.getSensorList();
			String[] sensorFetchList = new String[10];
			double[] values = null;
			SensorData tempData = sensorList.get(0);
			if(tempData.getServer().matches("sacramento.usc.edu"))
			{
				for(int i=0;i<10;i++)
				{
					tempData = sensorList.get(i);
					sensorFetchList[i] = tempData.getSensor();					
				}
				values = fetchFromEBIServer(sensorFetchList);
			}
			else
			{
				String tempStr = "";
				for(int i=0;i<10;i++)
				{
					tempData = sensorList.get(i);
					sensorFetchList[i] = tempData.getSensor();
					tempStr = tempStr+tempData.getSensor() + "-" + tempData.getSensorPoint()+";";
					
				}
				tempStr = tempStr.substring(0,tempStr.length()-1);
				values = fetchFromActualEBI(tempStr);
				//values = fetchFromEBIServer(sensorFetchList);
			}
			ArrayList<HashMap<String,String>> arrList = new ArrayList<HashMap<String,String>>();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm");
			String dateNow = dateFormat.format(calendar.getTime());
			String timeNow = dateFormat1.format(calendar.getTime());
			HashMap<String,String> sendMap;
			for(int i=0;i<10;i++)
			{				
				sendMap = new HashMap<String,String>();
				sendMap.clear();
				sendMap.put("sensorid",sensorFetchList[i]);
				sendMap.put("measurement",String.valueOf(values[i]));
				sendMap.put("date",dateNow);
				sendMap.put("time",timeNow);
				arrList.add(sendMap);
			}
			ParsingInfo retParseInfo = new ParsingInfo();
			retParseInfo.setMap(arrList);
			retParseInfo.setFileType("STREAM");
			retParseInfo.setDescription("Measurement");
			/*Message<byte[]> msg = new MessageImpl<byte[]>();
			msg.putPayload(BitConverter.getBytes(retParseInfo));
			write(msg);*/ // Modified Pellet  Logic
			
			//this.write(retParseInfo);
			return retParseInfo;			
		}
		return null;
	}
}
