package edu.usc.pgroup.floe.applications.iipipeline.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.communication.Sender;
import edu.usc.pgroup.floe.api.framework.floegraph.Node;
import edu.usc.pgroup.floe.applications.iipipeline.pellets.transport.TransportInfo;
import edu.usc.pgroup.floe.impl.communication.ChannelFactory;
import edu.usc.pgroup.floe.impl.communication.DuplicateSenderChannelStrategy;
import edu.usc.pgroup.floe.impl.communication.MessageImpl;
import edu.usc.pgroup.floe.impl.communication.SenderImpl;
import edu.usc.pgroup.floe.impl.communication.TCPTransportInfo;

public class MeasurementSend {
	public static TransportInfo byteToTransport(byte[] bytes) {
		TransportInfo tempInfo = null;
		try {
			ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(byteInput);
			tempInfo = (TransportInfo) ois.readObject();
		} catch (Exception ex) {

		}
		return tempInfo;
	}

	public static byte[] transportToByte(TransportInfo inpInfo) {
		byte[] retBytes = null;
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream outStream = new ObjectOutputStream(byteStream);
			outStream.writeObject(inpInfo);
			outStream.flush();
			outStream.close();
			byteStream.close();
			retBytes = byteStream.toByteArray();
		} catch (IOException ex) {
			// TODO: Handle the exception
		}
		return retBytes;
	}

	public static byte[] getFileInfo(String filePath) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			InputStream inStrm = new FileInputStream(filePath);

			byte[] buf = new byte[1024];
			int buffLen = 0;
			while ((buffLen = inStrm.read(buf)) > 0) {
				byteStream.write(buf, 0, buffLen);
			}
			byteStream.close();
			return byteStream.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	public static void main(String[] args) {
		String location = "localhost";
		int port = Integer.parseInt("48513");
		int controlPort = Integer.parseInt("48515");
		
		
		TCPTransportInfo otherEndTransportInfo = new TCPTransportInfo();
        otherEndTransportInfo.setHostAddress(location);
        otherEndTransportInfo.setTcpListenerPort(port);


        TCPTransportInfo otherEndControlInfo = new TCPTransportInfo();
        otherEndControlInfo.setHostAddress(location);
        otherEndControlInfo.setTcpListenerPort(controlPort);

        otherEndTransportInfo.setControlChannelInfo(otherEndControlInfo);
        
        Node.Port otherEndport = new Node.Port();
        otherEndport.setPortName("in");
        otherEndport.setTransportType("TCP");
        otherEndport.setDataTransferMode("Push");
        
        otherEndport.setTransportInfo(otherEndTransportInfo);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(ChannelFactory.KEY, null);
        params.put(ChannelFactory.QUEUE, null);
        params.put(ChannelFactory.SERVER_SIDE, "false");
        
        Sender sender = new SenderImpl();
        sender.init(params);
        sender.connect(otherEndport, null);
        Message<byte[]> tempMessage = new MessageImpl<byte[]>();
        
        
		TransportInfo tempInfo = new TransportInfo();
		tempInfo.setDescription("Measurement");
		tempInfo.setFileType("CSV");
		tempInfo.setTransportProtocol("stream");
		tempInfo.setFrequence(1000 * 60);
		tempInfo.setByteStream(getFileInfo("data/sensorsToExtract.csv"));
		
		tempMessage.putPayload(transportToByte(tempInfo));
		
		sender.setSenderStrategy(new DuplicateSenderChannelStrategy());
		sender.send(tempMessage);
		
	}
}
