package edu.usc.pgroup.floe.applications.iipipeline.pellets.utils;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class SGConstants {
	
	//publicstatic String STORE_URL = "http://128.125.124.67:8000";		//majave-04 VM
	//public static String STORE_URL = "http://128.125.225.130:8000";		//Losangeles VM
	///public static String STORE_URL = "http://128.125.224.121:8000";		//Losangeles VM test
	//public static String STORE_URL = "http://128.125.225.125:8000"; // Sacramento test
	
	//public static String STORE_URL = "http://128.125.124.67:8000";		//Losangeles VM	
	public static String XML_FILE_NAME =   "config/semanticConfigRoomEvent.xml";
											//"config/semanticConfigBuilding.xml";
											//"config/semanticConfigEquipment.xml";
											//"config/semanticConfigElectric.xml";			   
											
	public static int COUNT = 0;
	//static publicWriter out;
	static public Map<String, String> testMap = new HashMap<String, String>();
	static{
		testMap.put("BuildingNum", "101");
		testMap.put("BuildingCode", "EEB");
		testMap.put("BuildingName", "Electric Building");
		testMap.put("BuildingAddress", "USC USC USC");
		testMap.put("BuildingFloors", "20");
		testMap.put("YearBuilt", "1990");
		testMap.put("GrossArea", "10000");				//Net area
		
		testMap.put("floorCount", "2");
		
		testMap.put("Architect", "Tommy");		//Architect Entity
		testMap.put("Department", "Electical");
		
		testMap.put("Latitude", "77.2");
		testMap.put("Longitude", "123.5");
		
		testMap.put("SensorID", "testSensorID"+COUNT);
		
		testMap.put("Value", "220");			//ElectricalMeasurement -> hasMeasuredValue
		testMap.put("Date", "01:Jan:2012");		//ElectricalMeasurement -> hasMeasurementRecordedDate
		testMap.put("Time", "03:22:55");		//ElectricalMeasurement -> hasMeasurementRecordedTime
		
		testMap.put("Course", "Operating Systems");
		testMap.put("Starts", "19-01-2012");
		testMap.put("Ends", "01-05-2012");
		testMap.put("Days", "120");
		
		testMap.put("FloorNo", "2");
		testMap.put("RoomNo", "221");
		
		testMap.put("CampusCode", "UPC");
	}
	
	public static int COUNTER = 0;
	public static int SAMPLE_SIZE = 10;
	public static int BULK_LOAD_FREQUENT = 10;
	public static int BULK_LOAD_COMPLEX = 5;
	public static int BULK_LOAD_SIMPLE = 1;
	
	
	public static Properties semanticConfigMapping = new Properties();		//Lookup Table for semantic config files (.xmls)
	public static List<Map<String, String>> values = new ArrayList<Map<String, String>>();
	static{
		for(int i=0; i<SAMPLE_SIZE; i++){
			Map<String, String> valueMap = new HashMap<String, String>();
			valueMap.put("BuildingNum", "101");
			valueMap.put("BuildingCode", "EEB");
			valueMap.put("BuildingAddress", "USC USC USC");
			valueMap.put("BuildingFloors", "20");
			valueMap.put("YearBuilt", "1990");
			valueMap.put("GrossArea", "10000");
			
			valueMap.put("Architect", "Tommy");		//Architect Entity
			valueMap.put("Department", "Electical");
			
			valueMap.put("Latitude", "77.2");
			valueMap.put("Longitude", "123.5");
			
			String sensorID = "testSensorID"+ (COUNTER+""+i);
			valueMap.put("SensorID", sensorID);
			
			valueMap.put("Value", "22"+(COUNTER+ "" +i));			//ElectricalMeasurement -> hasMeasuredValue
			valueMap.put("Date", "01:Jan:2012");	//ElectricalMeasurement -> hasMeasurementRecordedDate
			valueMap.put("Time", "03:22:55");		//ElectricalMeasurement -> hasMeasurementRecordedTime
			
			valueMap.put("Course", "Operating Systems"+ i);
			valueMap.put("Starts", "19-01-2012");
			valueMap.put("Ends", "01-05-2012");
			valueMap.put("Days", "120");
			
			valueMap.put("FloorNo", "2"+i);
			valueMap.put("RoomNo", "221"+i);
			
			values.add(valueMap);
		}
		
		//TODO: The following should be read from a config file
		try
		{
			InputStream credentialsAsStream = new FileInputStream("config/Semantic.properties");			
			semanticConfigMapping.load(credentialsAsStream);	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		
		
	}
	
	public static enum SG_XML_TAG{
		DOCUMENT("Document"),
		ENTITY("Entity"),
			CLASS("class"),
			KEY("key"),
			STATUS("status"),
				STATUS_QUERY("Query"),
				STATUS_INSERT("Insert"),
		PROPERTY("Property"),
			MAPPING("mapping"),
			NAME("name");
		
		private final String tagName;
		
		SG_XML_TAG(String tag){
			this.tagName = tag;
		}
		
		public String getTagName(){
			return this.tagName;
		}
	};
	
   public static String getUUID(){
	   return UUID.randomUUID().toString();
   }

}