package edu.usc.pgroup.floe.applications.helloworld;


import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.communication.Sender;
import edu.usc.pgroup.floe.api.communication.TransportInfoBase;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;
import edu.usc.pgroup.floe.impl.communication.ChannelFactory;
import edu.usc.pgroup.floe.impl.communication.MessageImpl;
import edu.usc.pgroup.floe.impl.communication.SenderImpl;
import edu.usc.pgroup.floe.impl.communication.SimpleRoundRobinSenderStrategy;
import edu.usc.pgroup.floe.impl.communication.TCPTransportInfo;
import edu.usc.pgroup.floe.util.BitConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;



public class DataSend {




    public static void main(String[] args) {

        String location = "localhost";

        int port = Integer.parseInt("65357");
        int controlPort = Integer.parseInt("65359");
        

        TCPTransportInfo otherEndTransportInfo = new TCPTransportInfo();        
        otherEndTransportInfo.setHostAddress(location);
        otherEndTransportInfo.setTcpListenerPort(port);
        
        
        TCPTransportInfo otherEndControlInfo = new TCPTransportInfo();
        otherEndControlInfo.setHostAddress(location);
        otherEndControlInfo.setTcpListenerPort(controlPort);
        
        otherEndTransportInfo.setControlChannelInfo(otherEndControlInfo);
        
        Port otherEndport = new Port();
        otherEndport.setPortName("In1");
        otherEndport.setTransportType("TCP");
        otherEndport.setDataTransferMode("Push");

        otherEndport.setTransportInfo(otherEndTransportInfo);
        
        BlockingQueue<byte[]> tempQueue = new LinkedBlockingQueue<byte[]>();

        Map<String,Object> params = new HashMap<String, Object>();
        params.put(ChannelFactory.KEY,null);
        params.put(ChannelFactory.QUEUE,null);
        params.put(ChannelFactory.SERVER_SIDE,"false");

        Sender sender = new SenderImpl(null);
        sender.init();
        sender.createChannel(otherEndport, params);
        sender.start();
        Message<byte[]> tempMessage = new MessageImpl<byte[]>();
        byte[] payLoad = BitConverter.getBytes(new String("World"));
        tempMessage.putPayload(payLoad);

        // Send Equipment List Data First
        sender.setSenderStrategy(new SimpleRoundRobinSenderStrategy());
        sender.send(tempMessage);
        sender.stop();


    }
}
