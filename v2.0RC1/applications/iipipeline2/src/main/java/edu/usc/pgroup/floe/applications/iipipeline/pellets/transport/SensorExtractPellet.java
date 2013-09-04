package edu.usc.pgroup.floe.applications.iipipeline.pellets.transport;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.pgroup.floe.api.exception.LandmarkException;
import edu.usc.pgroup.floe.api.exception.LandmarkPauseException;
import edu.usc.pgroup.floe.api.framework.pelletmodels.StreamInStreamOutPellet;
import edu.usc.pgroup.floe.api.stream.FEmitter;
import edu.usc.pgroup.floe.api.stream.FIterator;

public class SensorExtractPellet implements StreamInStreamOutPellet
{
	//@Override
	public String getPelletType() 
	{
		return null;
	}
	public TransportInfo byteToTransport (byte[] bytes)
	{
		TransportInfo transportInfo = null;
		try 
		{
			ByteArrayInputStream byteInput = new ByteArrayInputStream (bytes);
			ObjectInputStream ois = new ObjectInputStream (byteInput);
			transportInfo = (TransportInfo)ois.readObject();
		}
		catch (Exception ex) 
		{
	  
		}
		return transportInfo;
	}

	public static byte[] sensorDataToByte(SensorListHolder inpInfo) 
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
	public static byte[] objectToByte(Object inpInfo) 
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

	@Override
	public void invoke(FIterator in, FEmitter out,
			edu.usc.pgroup.floe.api.state.StateObject stateObject) {
		Object inpObj = null;
		System.out.println("extract0");
		
		try {
			inpObj = in.next();
		} catch (LandmarkException | LandmarkPauseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//check inpObj != null.. 
		TransportInfo transportInfo = (TransportInfo)inpObj;
		
		String syntheticServer = "sacramento.usc.edu";
		String actualServer = "calemsa.usc.edu";
		System.out.println("Invoked Sensor Extract");
		if(transportInfo!=null)
		{			
			try
			{
				int counter = 0;
				HashMap<String,ArrayList<SensorData>> sensorMap = new HashMap<String,ArrayList<SensorData>>();
				int actualCount = 0;
				int syntheticCount = 0;				
				sensorMap.put(syntheticServer, new ArrayList<SensorData>());
				sensorMap.put(actualServer, new ArrayList<SensorData>());
				ByteArrayInputStream bis = null;
				BufferedReader buffRead =null;
				SensorListHolder sensorHolder = new SensorListHolder();
				while(true)
				{					
					bis = new ByteArrayInputStream(transportInfo.getByteStream());
					buffRead= new BufferedReader(new InputStreamReader(bis));
					String inpLine = "";						
					inpLine = buffRead.readLine();
					System.out.println("extract");
					while((inpLine=buffRead.readLine())!=null)
					{									
						String finalStr = "";
						if(inpLine.length()>0)
						{
							String[] tempArr = inpLine.split(",");
							if(tempArr[2].trim().matches(syntheticServer))
							{								
								ArrayList<SensorData> tempList = sensorMap.get(syntheticServer);								
								SensorData tempSensorData = new SensorData();
								tempSensorData.sensorName = tempArr[0];
								tempSensorData.sensorPoint = tempArr[1];
								tempSensorData.serverName = tempArr[2];
								tempList.add(tempSensorData);
								syntheticCount++;
								System.out.println("extract cnt:" + syntheticCount);
								if(syntheticCount == 10)
								{
									// Write the Data to the Buffer
									/*Message<byte[]> tempMessage = new MessageImpl<byte[]>();
									 * sensorHolder.setSensorList(tempList);
									 * tempMessage.putPayload(sensorDataToByte(sensorHolder));
									 */			// Modified Pellet Logic
									sensorHolder.setSensorList(tempList);																		
									
									out.emit(sensorHolder);
									
									syntheticCount = 0;
									sensorMap.put(syntheticServer, new ArrayList<SensorData>());
									//out.write(sensorHolder);
									
								}
							}
							else
							{
								ArrayList<SensorData> tempList = sensorMap.get(actualServer);								
								SensorData tempSensorData = new SensorData();								
								tempSensorData.sensorName = tempArr[0];
								tempSensorData.sensorPoint = tempArr[1];
								tempSensorData.serverName = tempArr[2];
								tempList.add(tempSensorData);
								actualCount++;
								if(actualCount == 10)
								{
									// Write the Data to the Buffer
									/*Message<byte[]> tempMessage = new MessageImpl<byte[]>();
									sensorHolder.setSensorList(tempList);
									tempMessage.putPayload(sensorDataToByte(sensorHolder));
									this.write(tempMessage);
									*/
									sensorHolder.setSensorList(tempList);
									//this.write(sensorHolder);
									out.emit(sensorHolder);
									actualCount = 0;
									sensorMap.put(actualServer, new ArrayList<SensorData>());
								}
							}
						}					
					}
					Thread.currentThread().sleep((transportInfo.getFrequency()));
				}			
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
	}
}
