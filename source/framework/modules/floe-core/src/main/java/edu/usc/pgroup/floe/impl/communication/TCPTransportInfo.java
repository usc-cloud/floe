package edu.usc.pgroup.floe.impl.communication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import edu.usc.pgroup.floe.api.communication.TransportInfoBase;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ConnectionInfo")
@XmlType(propOrder = { "params","controlChannelInfo"})
public class TCPTransportInfo extends TransportInfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7995741943672665105L;
	
	/*private String sourceAddress;
	private String destinationAddress;
	
	private int tcpInputPort;
	private int tcpOutputPort;*/
	
	public TCPTransportInfo() {
		
	}
	public TCPTransportInfo(TransportInfoBase inpConfig) {
		setParams(inpConfig.getParams());
		setControlChannelInfo(inpConfig.getControlChannelInfo());
	}

	public void setHostAddress(String hostAddress) {
		params.put("hostAddress", hostAddress);
	}
	
	public String getHostAddress() {
		return params.get("hostAddress");
	}
		
	public void setTcpListenerPort(int tcpListenerPort) {
		params.put("tcpListenerPort", String.valueOf(tcpListenerPort));
	}
	
	public int getTcpListenerInputPort() {
		return Integer.parseInt(params.get("tcpListenerPort"));
	}			
}
