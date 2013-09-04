package uk.co.magus.fourstore.client.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import uk.co.magus.fourstore.client.Store;
import uk.co.magus.fourstore.examples.Insert;

public class ModelQueries {

	//private static String STORE_URL = "http://128.125.124.67:8000";		//majave-04 VM
	private static String STORE_URL = "http://128.125.224.121:8000";		//Losangeles VM
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		
		System.out.println("Starting Main()");
		Store store = null;
		try{
			store = new Store(STORE_URL);
		
//			  String sparql1 = getINSERTTemperature(20);			//Min = 6
//		      String response1 = store.insert(sparql1);
//		      System.out.println("Insert Query: \n"+ sparql1);
//		      System.out.println("INSERT Response: "+ response1);
		      
		      String sparql2 = getSelectTemperature();
		      //read back the inserted triples
		      String response2 = store.query(sparql2, -1);
		      System.out.println("Getting inserted triples back: Query = "+ sparql2);
		      System.out.println("SELECT Query Result: \n"+ response2);
	          System.out.println("Main() Done");
		} catch (MalformedURLException e) {
			e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			e.getMessage();
			e.printStackTrace();
		}       
	}

	private static String buildingCodeSelect = "RTH1";
	
	private static String getSelectTemperature(){
		
		String startDate = "01:Jan:2011";
		String endDate 	 = "17:Sep:2012";
		String qBC  = buildingCodeSelect + "0";
		String qBC2 = buildingCodeSelect + "1";
		
		String query = "";
		query += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
		query += "PREFIX sgns: <http://www.smartgrid.usc.edu/ontology/2012.owl#> \n";
		query += "SELECT ?temperature ?measuredDate   WHERE { \n";
		query += "	  ?buildingURI rdf:type sgns:Building . \n";
		query += "	  ?buildingURI sgns:hasBuildingCode '"+ qBC +"' . \n";
		query += "	  ?buildingURI sgns:hasBuildingCode ?buidlingCode . \n";
//		query += "	  FILTER ( ?buildingCode = '"+ qBC +"' || ?buildingCode = '"+ qBC2 +"')  \n";
//		query += "	  FILTER ( ?buildingCode = '"+ qBC +"' ) \n";
//		query += "         UNION 									\n";		
//		query += "	  { FILTER ( ?buildingCode = '"+ qBC2 +"' ) } \n";
		query += "	  ?buildingURI sgns:observableIsA ?obsThing . \n";
		query += "	  ?obsThing sgns:hasAObservableFeature  ?obsFeature . \n"; 
		query += "	  ?obsFeature rdf:type sgns:TemperatureObservable . \n";
		query += "	  ?sensor  sgns:measures ?obsFeature . \n";
		query += "	  ?sensor sgns:hasMeasurementObservation ?observation . \n";
		query += "	  ?observation sgns:hasMeasuredValue ?temperature . \n";
		query += "	  ?observation sgns:hasMeasuredTimeStamp ?measuredDate . \n";
		query += "	  FILTER(?measuredDate >= \""+ startDate +"\" && ?measuredDate <= \"" + endDate + "\") \n";

//		query += "	  ?obsThing sgns:hasAObservableFeature  ?obsFeature2 . \n"; 
//		query += "	  ?obsFeature2 rdf:type sgns:KiloWattLoadObservable . \n";
//		query += "	  ?sensor2  sgns:measures ?obsFeature2 . \n";
//		query += "	  ?sensor2 sgns:hasMeasurementObservation ?observation2 . \n";
//		query += "	  ?observation2 sgns:hasMeasuredValue ?kilowattReading . \n";
//		query += "	  ?observation2 sgns:hasMeasuredTimeStamp ?measuredDate2 . \n";
//		query += "	  FILTER(?measuredDate2 >= \""+ startDate +"\" && ?measuredDate2 <= \"" + endDate + "\") ";
		query += "		} \n";

		return query;
	}
	
	private static String getINSERTTemperature(int count) throws InterruptedException{
		
//		String startDate = "01:Jan:2011";
//		String endDate 	 = "17:Sep:2012";
		
		int LIST_SIZE = 6;
		String[] dateList = new String[count];
		dateList[0] = "11:Jan:2011"; dateList[1] = "12:Jan:2011"; 
		dateList[2] = "10:Feb:2011"; dateList[3] = "11:Feb:2011"; 
		dateList[4] = "15:Sep:2011"; dateList[5] = "16:Sep:2011";
		
		Random generator = new Random();
		Set<String> dates = new HashSet<String>();
		for(int i=0; i<count; i++){
			int rand = generator.nextInt(LIST_SIZE);
			dates.add(dateList[rand]); 			
		}
		
		String query = "";
		query += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
		query += "PREFIX sgns: <http://www.smartgrid.usc.edu/ontology/2012.owl#> \n";
		query += "INSERT DATA { \n";
		
		for(int i = 0; i < count; i++){
		
			String buildingURI = "ran"+ Insert.getUUID();
			String obsThingURI = "ran"+ Insert.getUUID();
			//sgns:TemperatureObservable
			String obsFeatureURI  = "ran"+ Insert.getUUID();
			String sensorURI  = "ran"+ Insert.getUUID();
			String observationURI = "ran"+ Insert.getUUID();
			//sgns:KiloWattLoadObservable
			String obsFeatureURI2  = "ran"+ Insert.getUUID();
			String sensorURI2  = "ran"+ Insert.getUUID();
			String observationURI2 = "ran"+ Insert.getUUID();
			
			String buildingCode = buildingCodeSelect +i;
			String mesauredValue = "7"+i;
			
			query += "	  sgns:"+ buildingURI +" rdf:type sgns:Building . \n";
			query += "	  sgns:"+ buildingURI +" sgns:hasBuildingCode  '"+ buildingCode +"' . \n";
			query += "	  sgns:"+ buildingURI +" sgns:observableIsA sgns:"+ obsThingURI +" . \n";
			query += "	  sgns:"+ obsThingURI +" sgns:hasAObservableFeature sgns:"+ obsFeatureURI +" . \n"; 
			query += "	  sgns:"+ obsFeatureURI +" rdf:type sgns:TemperatureObservable . \n";
			query += "	  sgns:"+ sensorURI +" sgns:measures sgns:"+ obsFeatureURI +" . \n";
			query += "	  sgns:"+ sensorURI +" sgns:hasMeasurementObservation sgns:"+ observationURI +" . \n";
			query += "	  sgns:"+ observationURI +" sgns:hasMeasuredValue '"+ mesauredValue +"' . \n";
			query += "	  sgns:"+ observationURI +" sgns:hasMeasuredTimeStamp '"+ dateList[i] +"' . \n";
			
			query += "	  sgns:"+ obsThingURI +" sgns:hasAObservableFeature sgns:"+ obsFeatureURI2 +" . \n"; 
			query += "	  sgns:"+ obsFeatureURI2 +" rdf:type sgns:KiloWattLoadObservable . \n";
			query += "	  sgns:"+ sensorURI2 +" sgns:measures sgns:"+ obsFeatureURI2 +" . \n";
			query += "	  sgns:"+ sensorURI2 +" sgns:hasMeasurementObservation sgns:"+ observationURI2 +" . \n";
			query += "	  sgns:"+ observationURI2 +" sgns:hasMeasuredValue '"+ mesauredValue +"' . \n";
			query += "	  sgns:"+ observationURI2 +" sgns:hasMeasuredTimeStamp '"+ dateList[i] +"' . \n\n";
		}
		query += "		} \n";
		return query;
	}
	
}
