package edu.usc.pgroup.floe.api.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import edu.usc.pgroup.floe.api.communication.TransportInfoBase;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "FloeInfo")
@XmlType(propOrder = { "floeID", "sourceInfo"})
public class StartFloeInfo 
{
	@XmlElement(name = "floeID")
	String floeID;
	
	@XmlElement(name = "sourceNodeTransport")
	SourceNodeTransportInfo sourceInfo;
	
	public String getFloeID()
	{
		return this.floeID;
	}
	public void setFloeID(String inpId)
	{
		this.floeID = inpId;
	}
	
	public void setSourceInfo(SourceNodeTransportInfo sourceInfo) {
		this.sourceInfo = sourceInfo;
	}
	
	public SourceNodeTransportInfo getSourceInfo() {
		return sourceInfo;
	}
	
	public void setSourceNodeTransport(
			Map<String, List<TransportInfoBase>> sourceNodeTransport) {
		this.sourceInfo = new  SourceNodeTransportInfo();
		sourceInfo.setSourceInfo(sourceNodeTransport);
	}
	
	@XmlRootElement(name = "SourceNodeTransportInfo")
	@XmlAccessorType(XmlAccessType.NONE)
	public static class SourceNodeTransportInfo
	{
		public Map<String, List<TransportInfoBase>> sourceNodeTransport = new HashMap<>();
		
		@XmlElement(name = "entry")
	    private MapEntry[] getMap() {
	        List<MapEntry> list = new ArrayList<MapEntry>();
	        for (Entry<String, List<TransportInfoBase>> entry : sourceNodeTransport.entrySet()) {
	            MapEntry mapEntry =new MapEntry();
	            mapEntry.key = entry.getKey();
	            mapEntry.value = entry.getValue();
	            list.add(mapEntry);
	        }
	        return list.toArray(new MapEntry[list.size()]);
	    }
	    
	    public void setSourceInfo(Map<String, List<TransportInfoBase>> sourceInfo) {
			this.sourceNodeTransport = sourceInfo;
		}

		private void setMap(MapEntry[] arr) {
	        for(MapEntry entry : arr) {
	            this.sourceNodeTransport.put(entry.key, entry.value);
	        }
	    }


	    public static class MapEntry {
	    	@XmlElement(name = "key")
	        public String key;
	        
	    	@XmlElement(name = "value")
	        public List<TransportInfoBase> value;
	    }

	}

	
}
