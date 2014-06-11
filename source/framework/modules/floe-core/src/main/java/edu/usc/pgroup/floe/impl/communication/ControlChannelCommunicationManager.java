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
package edu.usc.pgroup.floe.impl.communication;

import edu.usc.pgroup.floe.api.communication.Sender;
import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.communication.Receiver;
import edu.usc.pgroup.floe.api.framework.Flake;
import edu.usc.pgroup.floe.api.framework.FlakeInfo;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;
import edu.usc.pgroup.floe.api.util.BitConverter;

import java.util.List;

public class ControlChannelCommunicationManager {

    public static final String ADD_PREDECESSOR_TYPE = "update_predecessor_type";
    public static final String REMOVE_PREDECESSOR_TYPE = "remove_predecessor_type";
    public static final String UPDATE_SENDER_CHANNEL = "updateSenderChannel";

    private Flake currentFlake;

    public ControlChannelCommunicationManager(Flake flake) {

        this.currentFlake = flake;
    }

    /**
     * This will be called with the Flake information from the newly added Flake
     * @param flakeInfo
     */
    public void updatePredecessors(FlakeInfo flakeInfo) {

        List<Port> ports = flakeInfo.getInputPorts();

        for(Port p : ports) {
            
        	String portName = p.getPortName();
            if(portName == null) {
                portName = "NULL";
            }

            Receiver receiver = currentFlake.getReceivers().get(portName);

            
            byte [] payload = BitConverter.getBytes(p);
            MessageImpl<byte[]> message = new MessageImpl<byte[]>();
            message.setTag(ADD_PREDECESSOR_TYPE);
            message.putPayload(payload);
            receiver.publishControlMessage(message);
        }
    }

    public void disconnectPredecessors(FlakeInfo flakeInfo) {

        List<Port> ports = flakeInfo.getInputPorts();

        for(Port p : ports) {

            String portName = p.getPortName();
            if(portName == null) {
                portName = "NULL";
            }

            Receiver receiver = currentFlake.getReceivers().get(portName);


            byte [] payload = BitConverter.getBytes(p);
            MessageImpl<byte[]> message = new MessageImpl<byte[]>();
            message.setTag(REMOVE_PREDECESSOR_TYPE);
            message.putPayload(payload);
            receiver.publishControlMessage(message);
        }
    }


    public void updateWeightInfo(FlakeInfo flakeInfo,WeightInfo info) {

        List<Port> ports = flakeInfo.getInputPorts();

        for(Port p : ports) {

            String portName = p.getPortName();
            if(portName == null) {
                portName = "NULL";
            }

            Receiver receiver = currentFlake.getReceivers().get(portName);
            byte [] payload = BitConverter.getBytes(info);
            MessageImpl<byte[]> message = new MessageImpl<byte[]>();
            message.setTag(UPDATE_SENDER_CHANNEL);
            message.putPayload(payload);
            receiver.publishControlMessage(message);

        }
    }
    public synchronized void controlSignalReceived(Message message,Sender sender) {

        if(message == null || sender == null) {
            return;
        }
        String type = message.getTag();

        if (ADD_PREDECESSOR_TYPE.equals(type)) {
            byte[] payload = (byte[]) message.getPayload();

            Port otherEndConnectionPort = (Port) BitConverter.getObject(payload);
            sender.connect(otherEndConnectionPort,otherEndConnectionPort.getFlakeId());
        } else if (REMOVE_PREDECESSOR_TYPE.equals(type)) {

            byte[] payload = (byte[]) message.getPayload();

            Port otherEndConnectionPort = (Port) BitConverter.getObject(payload);
            String fiberId =((SenderImpl)sender).getFlakeIdtoFiberIdMap().get(otherEndConnectionPort.getFlakeId());

            sender.getSenderChannel(otherEndConnectionPort.getNodeId()).getFiber(fiberId).close();
            sender.getSenderChannel(otherEndConnectionPort.getNodeId()).getFibers().remove(fiberId);


            sender.getSenderChannel(otherEndConnectionPort.getNodeId()).getControlFiber(fiberId).close();
            sender.getSenderChannel(otherEndConnectionPort.getNodeId()).removeControlFiber(fiberId);



        } else if(UPDATE_SENDER_CHANNEL.equals(type)) {

            byte[] payload = (byte[]) message.getPayload();


            WeightInfo weightInfo = (WeightInfo) BitConverter.getObject(payload);
            String fiberId =((SenderImpl)sender).getFlakeIdtoFiberIdMap().
                    get(weightInfo.getFlakeId());

            sender.getSenderChannel(weightInfo.getNodeId()).
                    updateSenderStatus(fiberId,message);

        }



    }

}
