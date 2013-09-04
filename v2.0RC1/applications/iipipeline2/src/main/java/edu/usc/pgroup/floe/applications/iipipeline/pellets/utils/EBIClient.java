package edu.usc.pgroup.floe.applications.iipipeline.pellets.utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class EBIClient {

	Socket clientSock;
	BufferedReader inReader;
	BufferedWriter outWriter;
	
	
	public EBIClient() {

	}

	public boolean Initialize(String hostName, int port) throws Exception{

		InetAddress addr = InetAddress.getByName(hostName);
			
			// This constructor will block until the connection succeeds
		clientSock = new Socket(addr, port);
			
		outWriter =   new BufferedWriter(
		               new OutputStreamWriter(clientSock.getOutputStream(), "UTF-8")
		              );
		     
		inReader = new BufferedReader(new InputStreamReader(clientSock.getInputStream(),"UTF-8"));
		
		return true;
	}
	
	public double[] getDataPoint(String[] sensorName,String[] featureDesc) throws IOException
	{
		String data = "";
		for(int i = 0; i < sensorName.length; i++)
		{
			data += sensorName[i];
			if(i != sensorName.length - 1)
				data += ",";
		}
		
		outWriter.write(data);
		outWriter.flush();
		
		String sensorValuesStr = inReader.readLine();
		
		String[] sensorValues = sensorValuesStr.split(",");
		int n = sensorValues.length;
		double[] values = new double[n];
		for(int i = 0; i < n; i++)
		{
			values[i] = Double.parseDouble(sensorValues[i]);
		}
		
		return values;		
	}

	public boolean Disconnect()
	{
		try {
			clientSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}
}
