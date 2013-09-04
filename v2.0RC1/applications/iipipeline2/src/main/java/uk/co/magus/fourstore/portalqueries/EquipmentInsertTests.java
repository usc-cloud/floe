
package uk.co.magus.fourstore.portalqueries;

import uk.co.magus.fourstore.examples.Insert;

public class EquipmentInsertTests {

	//Place
	public static String testPlaceInsert() throws InterruptedException{
	
		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("INSERT DATA { \n");
		
		//Lookup Place
		String building = "testBuilding"+ Insert.COUNTER;
		String floor = "testFloor"+ Insert.COUNTER;
		String roomNo = "testRoom"+ Insert.COUNTER;
		
		String buildingURI = "ran"+ Insert.getUUID();			
		query.append("		sgns:"+ buildingURI +"  rdf:type  sgns:Building . \n");
		query.append("		sgns:"+ buildingURI +"  sgns:hasBuildingCode  '"+ building +"' . \n");
		
		String floorURI = "ran"+ Insert.getUUID();			
		query.append("		sgns:"+ buildingURI +"  sgns:Floor  sgns:"+ floorURI +" . \n");
		query.append("		sgns:"+ floorURI +"  rdf:type  sgns:Floor . \n");
		query.append("		sgns:"+ floorURI +"  sgns:hasFloorNo  '"+ floor +"' . \n");
		
		String roomURI = "ran"+ Insert.getUUID();	
		query.append("		sgns:"+ floorURI +"  sgns:Room  sgns:"+ roomURI +" . \n");
		query.append("		sgns:"+ roomURI +"  rdf:type  sgns:Room . \n");
		query.append("		sgns:"+ roomURI +"  sgns:hasRoomNo  '"+ roomNo +"' . \n");
		
		query.append("		} \n");
		return query.toString();
	}

	public static String testPlaceLookUp(){
		
		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("SELECT * WHERE { \n");

		//Lookup Place
		String building = "testBuilding"+ Insert.COUNTER;
		String floor = "testFloor"+ Insert.COUNTER;
		String roomNo = "testRoom"+ Insert.COUNTER;
		
		query.append("		?buildingURI  rdf:type  sgns:Building . \n");
		query.append("		?buildingURI  sgns:hasBuildingCode  '"+ building +"' . \n");
		query.append("		?buildingURI  sgns:Floor  ?floorURI  . \n");
		
		query.append("		?floorURI  rdf:type  sgns:Floor . \n");
		query.append("		?floorURI  sgns:hasFloorNo  '"+ floor +"' . \n");
		query.append("		?floorURI  sgns:Room  ?placeURI  . \n");
		
		query.append("		?placeURI  rdf:type  sgns:Room . \n");
		query.append("		?placeURI  sgns:hasRoomNo  '"+ roomNo +"' . \n");
		
		query.append("		} \n");
		return query.toString();
	}
	
	//Equipment
	public static String testEquipmentInsert() throws InterruptedException{
		
		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("INSERT DATA { \n");
	
		String equipmentURI = "ran"+ Insert.getUUID();
		String equipmentID = "testEquipmentID"+ Insert.COUNTER;
		
		query.append("		sgns:" + equipmentURI +"  sgns:hasID  '"+ equipmentID +"' . \n");
		query.append("		sgns:" + equipmentURI +"  rdf:type  sgns:Equipment . \n");
		query.append("		} \n");
		
		return query.toString();
	}
	
	public static String testEquipmentLookUp(){
		
		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("SELECT * WHERE { \n");

		String equipmentID = "testEquipmentID"+ Insert.COUNTER;
		query.append("		?equipmentURI  sgns:hasID  '"+ equipmentID +"' . \n");
		query.append("		?equipmentURI  rdf:type  sgns:Equipment . \n");
		query.append("		} \n");
		return query.toString();
	}
	
	//Obs Thing
	public static String testObsThingInsert() throws InterruptedException{
		
		//		sgns:ran1334529878324  rdf:type  sgns:Building . 
		//		sgns:ran1334529878334  rdf:type  sgns:Floor . 
		//		sgns:ran1334529878344  rdf:type  sgns:Room . 
		
		String placeURI = "ran1334529878344";
		String obsThingURI = "ran"+ Insert.getUUID();
		
		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("INSERT DATA { \n");
	
		query.append("		sgns:"+ obsThingURI +" rdf:type  sgns:ObservableThing . \n");
		query.append("		sgns:"+ placeURI +"  sgns:observableISA  sgns:"+ obsThingURI +"  . \n");
		query.append("		} \n");
		return query.toString();
	}

	public static String testObsThingLookUp(){
		
		//		sgns:ran1334529878324  rdf:type  sgns:Building . 
		//		sgns:ran1334529878334  rdf:type  sgns:Floor . 
		//		sgns:ran1334529878344  rdf:type  sgns:Room . 
		
		String entityURI = "ran1334529878344";
		
		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		
		query.append("SELECT ?obsThingURI WHERE { \n");
		query.append("		sgns:"+ entityURI +"  sgns:observableISA  ?obsThingURI . \n");
		query.append("		?obsThingURI  rdf:type  sgns:ObservableThing . \n");
		query.append("		} \n");
		
		return query.toString();
	}
	
	//Obs Feature
	public static String testObsFeatureInsert() throws InterruptedException{
		
		String entityURI = "ran1334529878344";			//Place or Equipment
		String obsThingURI = "ran1334531321049";		//Value from a previous test run
		String obsFeatureURI = "ran"+ Insert.getUUID();
		String obsFeatureClass = "KilowattObservable";
		
		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("INSERT DATA { \n");
		query.append("		sgns:"+ entityURI +"   sgns:observableISA   sgns:" + obsThingURI +" . \n");   
		query.append("		sgns:"+ obsThingURI +"  sgns:hasAObservableFeature  sgns:"+ obsFeatureURI +"  . \n\n");
		query.append("		sgns:"+ obsFeatureURI +" rdf:type sgns:" + obsFeatureClass +" . \n");
		
		query.append("		} \n");
		
		return query.toString();
	}
	
	public static String testObsFeatureLookUp(){
		
		String entityURI = "ran1334529878344";			//place or equipment
		String obsFeatureClass = "KilowattObservable";
		
		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		
		query.append("SELECT ?obsFeatureURI WHERE { \n");
		query.append("		sgns:"+ entityURI +"  sgns:observableISA  ?obsThingURI . \n");
		query.append("		?obsThingURI  rdf:type  sgns:ObservableThing . \n");
		query.append("		?obsThingURI  sgns:hasAObservableFeature ?obsFeatureURI . \n");
		query.append("		?obsFeatureURI  rdf:type sgns:"+ obsFeatureClass +" . \n");
		
		query.append("		} \n");
		
		return query.toString();
	}
	
	
	//Sensor
	public static String testSensorInsert() throws InterruptedException{

		String sensorURI = "ran"+ Insert.getUUID();		//lookup first and insert if not exists !!!
		String sensorID = "testSensorID"+ Insert.COUNTER;
		String unit = "testUnit"+ Insert.COUNTER;
		
		String obsFeatureURI = "ran"+ Insert.getUUID();	
		
		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("INSERT DATA { \n");
		query.append("		sgns:"+ sensorURI +" rdf:type sgns:Sensor . \n");
		query.append("		sgns:"+ sensorURI +" sgns:hasSensorID '"+ sensorID +"' . \n");
		query.append("		sgns:"+ sensorURI +" sgns:hasUnit  '"+ unit +"' . \n");
		query.append("		sgns:"+ sensorURI +" sgns:measures  sgns:"+ obsFeatureURI +" . \n");		//This takes care of one to many relation between Sensor and ObsFeature
		
		query.append("		} \n");
		
		return query.toString();
	}
	
	public static String testSensorSelect(){
		
		String sensorID = "testSensorID"+ Insert.COUNTER;
		String unit = "testUnit"+ Insert.COUNTER;
		
		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("SELECT * WHERE { \n");
		query.append("		?sensorURI  sgns:hasSensorID '"+ sensorID +"' . \n");
		query.append("		?sensorURI  sgns:hasUnit '"+ unit +"' . \n");
		query.append("		?sensorURI  rdf:type sgns:Sensor . \n");
//		query.append("		?sensorURI  sgns:measures sgns:"+ obsFeatureURI +" . \n");
		
		query.append("		} \n");
		
		return query.toString();
	}
}