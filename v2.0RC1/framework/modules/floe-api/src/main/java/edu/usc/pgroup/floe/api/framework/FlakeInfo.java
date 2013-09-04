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

package edu.usc.pgroup.floe.api.framework;

import edu.usc.pgroup.floe.api.communication.TransportInfoBase;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;

import javax.xml.bind.annotation.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This described the resources for a Flake within a container.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "FlakeInfo")
@XmlType(propOrder = {"flakeId", "pelletType", "pelletCount", "inBufferSize", "outBufferSize", "resources", "status", "inputPorts",
        "outputPorts", "averagePelletProcessingLatency", "outputThroughput", "throughputTrend",
        "inThroughPut","scaledOut"})
public class FlakeInfo {
	
    @XmlElement(name = "flakeId")
    String flakeId;
    @XmlElement(name = "pelletType")
    String pelletType;
    @XmlElement(name = "Resource")
    ResourceInfo resources;
    @XmlElement(name = "status")
    String status;
    @XmlElement(name = "pelletCount")
    int pelletCount;
    @XmlElement(name = "inBufferSize")
    int inBufferSize;
    @XmlElement(name = "outBufferSize")
    int outBufferSize;
    @XmlElement(name = "averageProcessingLatency")
    double averagePelletProcessingLatency;
    @XmlElement(name = "scaledOut")
    boolean scaledOut;

    @XmlElement(name = "inputPorts")
    List<Port> inputPorts;
    
    @XmlElement(name = "outputPorts")
    List<Port> outputPorts;

    @XmlTransient
    String version  ="0";

    @XmlTransient
    long incomingMessageCount;
    @XmlTransient
    long outgoingMessageCount;

    @XmlTransient
    double transientOutputThrouput;

    @XmlElement(name = "outputThroughput")
    // It is a weighted average with more weightage given to the current
            // throughput value.
            double outputThroughput =-1;

    @XmlElement(name = "inThroughput")
    double inThroughPut = -1;

    @XmlElement(name = "throughputTrend")
    private int throughputTrend;

    @XmlTransient
    long firstMessageInTimeStamp = -1;

    @XmlTransient
    long firstMessageOutTimeStamp = -1;

    @XmlTransient
    long lastInMessageTimeStamp = -1;




    @XmlTransient
    long currentMessageTimeStamp = -1;

    @XmlTransient
    long lastOutMessageTimeStamp = -1;

    @XmlTransient
    long lastReadOutMessageTimeStamp = -1;


    @XmlTransient
    long transientTimeStamp = -1;

    @XmlTransient
    long lastLatencySetTime = -1;

    @XmlTransient
    double latency= -1;

    @XmlTransient
    double inMessageCount= 0;

    @XmlTransient
    double outMessageCount= 0;

//    @XmlTransient
//   // PeriodicThrouputCalculator ptc;

    public FlakeInfo() {
//        ptc = new PeriodicThrouputCalculator(this);
//         ptc.start();
    }

    public FlakeInfo(String flakeId, String pelletType, ResourceInfo resources) {
        this.flakeId = flakeId;
        this.pelletType = pelletType;
        this.resources = resources;
        this.inputPorts = new ArrayList<Port>();
        this.outputPorts = new ArrayList<Port>();
        this.outputThroughput = -1;
        this.transientOutputThrouput = -1;
    }

    public int getThroughputTrend() {
        return throughputTrend;
    }

    public void setThroughputTrend(int t) {
        throughputTrend = t;
    }

    public double getOutputThroughput() {
        if((System.currentTimeMillis() - lastOutMessageTimeStamp > 8000) &&
                (System.currentTimeMillis() - lastInMessageTimeStamp > 2*latency)) {
            firstMessageOutTimeStamp = -1;
            outMessageCount = 0;
            return 0;
        }

        double tp = ((double)outMessageCount*1000)/((double)(lastOutMessageTimeStamp- firstMessageOutTimeStamp));
        this.outputThroughput = tp;
        return tp;
    }

    public void setOutputThroughput(double througput) {
        this.outputThroughput = througput;
    }

    public String getflakeId() {
        return this.flakeId;
    }

    public void setflakeId(String flakeID) {
        this.flakeId = flakeID;
    }

    public String getpelletType() {
        return this.pelletType;
    }

    public void setPelletType(String pelletType) {
        this.pelletType = pelletType;
    }

    public void setpelletType(String pelletType) {
        this.pelletType = pelletType;
    }

    public ResourceInfo getResourceInfo() {
        return this.resources;
    }

    public String getstatus() {
        return this.status;
    }

    public void setPelletCount(int inpCount) {
        this.pelletCount = inpCount;
    }

    public int getPelletCount() {
        return this.pelletCount;
    }


    public void setresources(ResourceInfo resource) {
        this.resources = resource;
    }

    public void setstatus(String inpStatus) {
        this.status = inpStatus;
    }

    public int getInBufferSize() {
        return this.inBufferSize;
    }

    public void setInBufferSize(int bufSize) {
        this.inBufferSize = bufSize;
    }

    public int getOutBufferSize() {
        return this.outBufferSize;
    }

    public void setOutBufferSize(int bufSize) {
        this.outBufferSize = bufSize;
    }

    
    public List<Port> getInputPorts() {
		return inputPorts;
	}
    public void setInputPorts(List<Port> inputPorts) {
		this.inputPorts = inputPorts;
	}
    
    public List<Port> getOutputPorts() {
		return outputPorts;
	}
    
    public void setOutputPorts(List<Port> outputPorts) {
		this.outputPorts = outputPorts;
	}
    
    public void addInputPort(Port inpPort) {
        if (inpPort != null)
            this.inputPorts.add(inpPort);
    }

    public void addSinkConnection(Port outPuPort) {
        if (outPuPort != null)
            this.outputPorts.add(outPuPort);
    }

    public void setAveragePelletProcessingLatency(double averagePelletProcessingLatency) {
        this.averagePelletProcessingLatency = averagePelletProcessingLatency;
    }

    public double getAveragePelletProcessingLatency() {
        return averagePelletProcessingLatency;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public synchronized void handleIncomingMessage() {
        inMessageCount++;
        long time= System.currentTimeMillis();
        lastInMessageTimeStamp = time;
        if(firstMessageInTimeStamp == -1) {
            firstMessageInTimeStamp = time;
        }



//        long currentTime = System.currentTimeMillis();
//
//        if(firstMessageInTimeStamp != -1) {
//            long diff = currentTime - firstMessageInTimeStamp;
//            if(diff > 0) {
//                double tp =((double)1000)/((double)diff);
//                if(inThroughPut > 0) {
//                    inThroughPut = (inThroughPut + tp)/2;
//                } else {
//                    inThroughPut = tp;
//                }
//            }
//        }
//
//        firstMessageInTimeStamp = currentTime;
    }

    public double getInThroughput() {

        if((System.currentTimeMillis() - lastInMessageTimeStamp > 8000) &&
                (System.currentTimeMillis() - lastInMessageTimeStamp > 2*latency)) {

            firstMessageInTimeStamp = -1;
            inMessageCount = 0;
            return 0;
        }

        double tp = ((double)inMessageCount*1000)/((double)(lastInMessageTimeStamp - firstMessageInTimeStamp));
        this.inThroughPut = tp;
        return tp;

    }

    public boolean isScaledOut() {
        return scaledOut;
    }

    public void setScaledOut(boolean scaledOut) {
        this.scaledOut = scaledOut;
    }

    public synchronized void handleOutgoingMessage() {
        // caluclate weighted average..
        long currentMsgTimeStamp = System.currentTimeMillis();

        outMessageCount++;
        lastOutMessageTimeStamp = currentMsgTimeStamp;
        if(firstMessageOutTimeStamp == -1) {
            firstMessageOutTimeStamp = currentMsgTimeStamp;
        }



//
//        if (lastOutMessageTimeStamp == -1) {
//            lastOutMessageTimeStamp = currentMsgTimeStamp;
//            return;
//        } else {
//            if (currentMsgTimeStamp - lastOutMessageTimeStamp > 0) {
//                double currentThroughput =  ((double)1000)/ ((double) (currentMsgTimeStamp - lastOutMessageTimeStamp));
//                if (outputThroughput == -1) {
//                    outputThroughput = currentThroughput;
//                } else {
//                    outputThroughput = (outputThroughput + currentThroughput) / 2;
//                }
//                transientOutputThrouput = outputThroughput;
//            }
//        }
//        lastOutMessageTimeStamp = currentMsgTimeStamp;

    }


    public double getLatency() {
        if(lastLatencySetTime == -1) {
            return latency;
        } else if((System.currentTimeMillis() - lastLatencySetTime) > 15*latency) {
            return -1;
        } else {
            return latency;
        }
    }

    public void setLatency(double latency) {
        this.latency = latency;
        this.lastLatencySetTime = System.currentTimeMillis();
    }

    public synchronized void AdjustThroughput(long halfLifeInMilliSeconds) {

        currentMessageTimeStamp = System.currentTimeMillis();

        if (outputThroughput != -1 && firstMessageInTimeStamp != -1 && transientOutputThrouput != -1) {
            outputThroughput = (transientOutputThrouput) * (currentMessageTimeStamp - firstMessageInTimeStamp) / (2 * halfLifeInMilliSeconds / 1000);
        }
    }

	public Port getOutputPort(String portName) {
		for(Port p : outputPorts)
		{
			if(p.getPortName().equalsIgnoreCase(portName))
			{
				return p;
			}
		}
		return null;
	}

	public Port getInputPort(String portName) {
		for(Port p : inputPorts)
		{
			if(p.getPortName().equalsIgnoreCase(portName))
			{
				return p;
			}
		}
		return null;
	}

	public void printInfo(PrintStream out) {
		out.println("flakeId: " + getflakeId());
		out.println("input ports");
		for(Port p : getInputPorts())
		{
			out.println(p.getPortName());
			out.println(p.getTransportType());
			out.println(p.getDataTransferMode());
			out.println(p.getTransportInfo().getParams());
			
			TransportInfoBase ts = p.getTransportInfo().getControlChannelInfo();
			if(ts == null)
				out.println("Not reciever.. so no control channel info");
			else
				out.println(ts.getParams());
			
			out.println("**********");
		}
	}
}
