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
package edu.usc.pgroup.floe.api.framework.floegraph;


import javax.xml.bind.annotation.*;

import edu.usc.pgroup.floe.api.communication.TransportInfoBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Node")
@XmlType(propOrder = { "nodeId", "pelletType","singleton","resource","inPorts","outPorts"})
public class Node {

    String nodeId;

    String pelletType;

    boolean singleton = false;

    @XmlElement(name = "Resource")
    ResourceInfo resource;

    @XmlElement(name = "InPorts")
    InPorts inPorts;

    @XmlElement(name = "OutputPorts")
    OutPorts outPorts;



    public String getNodeId() {
		return nodeId;
	}
    public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
    public String getPelletType() {
		return pelletType;
	}
    public void setPelletType(String pelletType) {
		this.pelletType = pelletType;
	}
    public ResourceInfo getResource() {
		return resource;
	}
    public void setResource(ResourceInfo resource) {
		this.resource = resource;
	}
    public InPorts getInPorts() {
		return inPorts;
	}
    public void setInPorts(InPorts inPorts) {
		this.inPorts = inPorts;
	}
    public OutPorts getOutPorts() {
		return outPorts;
	}

    public void setOutPorts(OutPorts outPorts) {
		this.outPorts = outPorts;
	}

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @XmlRootElement(name = "Resource")
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {"numberOfCores","optionalConfiguration"})
    public static class ResourceInfo {

        public int numberOfCores;
        
        @XmlElement(name = "Configuration")
        public Configuration optionalConfiguration;

        public ResourceInfo() {
        	numberOfCores = 0;
		}
        
        public ResourceInfo(int i) {
        	numberOfCores = i;
		}

		public int getNumberOfCores() {
            return numberOfCores;
        }

        public void setNumberOfCores(int numberOfCores) {
            this.numberOfCores = numberOfCores;
        }

		public Configuration getOptionalConfiguration() {
			return optionalConfiguration;
		}
		
		public void setOptionalConfiguration(Configuration optionalConfiguration) {
			this.optionalConfiguration = optionalConfiguration;
		}
    }

    @XmlRootElement(name = "InPorts")
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {"ports"})
    public static class InPorts {

        @XmlElement(name = "Port")
        List<Port> ports;

        public List<Port> getPorts() {
            return ports;
        }

        public void setPorts(List<Port> ports) {
            this.ports = ports;
        }
    }

    @XmlRootElement(name = "OutPorts")
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {"ports"})
    public static class OutPorts {

        @XmlElement(name = "Port")
        List<Port> ports;

        public List<Port> getPorts() {
            return ports;
        }

        public void setPorts(List<Port> ports) {
            this.ports = ports;
        }

    }

    @XmlRootElement(name = "Ports")
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {"portName",
    		"dataTransferMode",
    		"transportType",
    		"connectionInfo","sendChannelStratergy","fiberStratergy","nodeId","flakeId"})

    public static class Port implements Serializable{

        /**
		 * 
		 */
		private static final long serialVersionUID = 5882126056026027247L;
		String portName;
		String nodeId;
        String dataTransferMode;
        String transportType;
        TransportInfoBase connectionInfo;
        String sendChannelStratergy;
        String fiberStratergy;
        String flakeId;

        public String getFlakeId() {
            return flakeId;
        }

        public void setFlakeId(String flakeId) {
            this.flakeId = flakeId;
        }

        public String getPortName() {
            return portName;
        }

        public void setPortName(String portName) {
            this.portName = portName;
        }

        
        public void setNodeId(String nodeId) {
			this.nodeId = nodeId;
		}
        
        public String getNodeId() {
			return nodeId;
		}
        
		public void setDataTransferMode(String dataTransferMode) {
			this.dataTransferMode = dataTransferMode;
		}
		public String getDataTransferMode() {
			return dataTransferMode;
		}
		
		public void setTransportType(String transportType) {
			this.transportType = transportType;
		}
		public String getTransportType() {
			return transportType;
		}
		
		public void setTransportInfo(TransportInfoBase connectionInfo) {
			this.connectionInfo = connectionInfo;
		}
		
		public TransportInfoBase getTransportInfo() {
			return connectionInfo;
		}

        public String getSendChannelStratergy() {
            return sendChannelStratergy;
        }

        public void setSendChannelStratergy(String sendChannelStratergy) {
            this.sendChannelStratergy = sendChannelStratergy;
        }

        public String getFiberStratergy() {
            return fiberStratergy;
        }

        public void setFiberStratergy(String fiberStratergy) {
            this.fiberStratergy = fiberStratergy;
        }
    }
    
    @XmlRootElement(name = "Configuration")
    @XmlAccessorType(XmlAccessType.NONE)  
    public static class Configuration {
    	
    	public Map<String, String> params = new HashMap<String, String>();

        @XmlElement(name = "param")
        private MapEntry[] getMap() {
            List<MapEntry> list = new ArrayList<MapEntry>();
            for (Entry<String, String> entry : params.entrySet()) {
                MapEntry mapEntry =new MapEntry();
                mapEntry.key = entry.getKey();
                mapEntry.value = entry.getValue();
                list.add(mapEntry);
            }
            return list.toArray(new MapEntry[list.size()]);
        }
        
        
        public Map<String,String> getParams()
        {
        	return params;
        }
        
        private void setMap(MapEntry[] arr) {
            for(MapEntry entry : arr) {
                this.params.put(entry.key, entry.value);
            }
        }

        public static class MapEntry {
            @XmlAttribute
            public String key;
            @XmlValue
            public String value;
        }
	
    }

}
