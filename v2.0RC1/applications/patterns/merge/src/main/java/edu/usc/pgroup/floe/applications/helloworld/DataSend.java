package edu.usc.pgroup.floe.applications.helloworld;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.usc.pgroup.floe.api.communication.ConnectionInfo;
import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.communication.SinkChannel;
import edu.usc.pgroup.floe.impl.communication.MessageImpl;
import edu.usc.pgroup.floe.impl.communication.TCPSinkPushChannel;
import edu.usc.pgroup.floe.util.BitConverter;


public class DataSend {
	

	 

	public static void main(String[] args) {
		String location = "localhost";
		int port = Integer.parseInt("17322");
		ConnectionInfo tempConnectionInfo = new ConnectionInfo();
		tempConnectionInfo.setDestAddress(location);
		tempConnectionInfo.setOutPort(port);
		BlockingQueue<byte[]> tempQueue = new LinkedBlockingQueue<byte[]>();
		TCPSinkPushChannel tempConnection = new TCPSinkPushChannel(
				tempConnectionInfo);
		tempConnection.openConnection();

		Message<byte[]> tempMessage = new MessageImpl<byte[]>();
		tempMessage.putPayload(BitConverter.getBytes(new String("World")));
		
		// Send Equipment List Data First
		
		tempConnection.putMessage(tempMessage);
		tempConnection.closeConnection();
	}
}
