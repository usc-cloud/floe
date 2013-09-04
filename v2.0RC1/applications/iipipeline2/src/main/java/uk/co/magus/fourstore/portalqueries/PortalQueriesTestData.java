
package uk.co.magus.fourstore.portalqueries;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.StringTokenizer;

import uk.co.magus.fourstore.client.Store;
import edu.usc.pgroup.floe.applications.iipipeline.pellets.utils.IIPProperties;


public class PortalQueriesTestData {

	private static int COUNT = 1;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		//Lat long data
	//	insertLatLong();
	//	readLatLong();
		
		//Building test Data
		//insertBuildings(20);
		
		//KWR data
		//insertKWH("data/export-2012-03-31.csv", "BC1", 112);
		//insertKWH("data/export-2012-03-31.csv", "MHP", 45);
		//insertKWH("data/export-2012-04-16.csv", "MHP", 45);
		
		//insertKWH("data/export-2012-03-31.csv", "TCC", 310);
		//insertKWH("data/export-2012-04-16.csv", "TCC", 310);
		
		String startDate = "03/31/2012";
		String endDate   = "04/16/2012";
		String startTime = "00:00";
		String endTime   = "00:15";
		readKWH("TCC", startDate, endDate, startTime, endTime);
		
	}//End of main()
	
	private static String getBuildingInsertQuery(int count) throws InterruptedException{
		
		StringBuilder query = new StringBuilder("");
		
		query.append("PREFIX Building: <http://dbpedia.org/ontology/> \n");
		query.append("PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX gmlns: <http://purl.org/ifgi/gml/0.1/> \n");
		query.append("INSERT DATA { \n");
		
		for(int i=0; i < count; i++){
			String buildingSubject = "http://cei.usc.edu/smartgridData#ran"+ System.nanoTime() + i;		//Generate a unique ID for each building instance
			query.append("	<"+ buildingSubject +"> rdf:type Building:Building . \n");
			query.append("	<"+ buildingSubject +"> Building:hasBuildingNo \""+ ("10000"+ COUNT + "" +i) + "\" . \n");
			query.append("	<"+ buildingSubject +"> Building:hasBuildingCode \""+ ("BC"+ COUNT + "" +i) + "\" . \n");
			query.append("	<"+ buildingSubject +"> Building:address \""+ ("Some LA Address"+ COUNT + "" +i) + "\" . \n");
			query.append("	<"+ buildingSubject +"> Building:elevation \""+ ("20"+ i) + "\" . \n");				//Elevation = # of floors in the building
			query.append("	<"+ buildingSubject +"> Building:buildingstartdate \""+ ("199"+i) + "\" . \n");
			query.append("	<"+ buildingSubject +"> Building:floorArea \""+ ("1000"+i) + "\" . \n");
			
			Thread.currentThread().sleep(10);
			String architectSubject = "http://cei.usc.edu/smartgridData#ran"+ System.currentTimeMillis()+ i;		//Generate a unique ID for Architect Entity for the building
			query.append("	<"+ buildingSubject +"> Building:Architect <"+ architectSubject + "> . \n");
			query.append("	<"+ architectSubject +"> rdf:type Building:Architect . \n");
			query.append("	<"+ architectSubject +"> Building:birthname \""+ ("John Smith"+ COUNT + "" +i) + "\" . \n\n");
			
			String gmlURI = "ran"+ System.nanoTime();
			String triple = " <"+ buildingSubject +">  gmlns:GeometryProperty  sgns:"+ gmlURI +" . \n";
			query.append(triple);
			
			triple = " sgns:"+ gmlURI +"  rdf:type  <http://purl.org/ifgi/gml/0.1/GeometryProperty> . \n";
			query.append(triple);
			
			String lat = "10"+(COUNT+i);
			triple  = " sgns:"+ gmlURI + " sgns:hasLatitude  '"+ lat +"' . \n";
			query.append(triple);
			
			String longi = "40"+(COUNT+i);
			triple  = " sgns:"+ gmlURI + " sgns:hasLongitude  '"+ longi +"' . \n";
			query.append(triple);
		}
		query.append("} ");
		
		return query.toString();
	}
	
	private static void insertBuildings(int count) throws MalformedURLException, ProtocolException, IOException, InterruptedException{
		
        String sparql1 = getBuildingInsertQuery(count);
		System.out.println(sparql1);

		Store store = null;
		try {
			store = new Store(IIPProperties.getStoreURL());		
//			String response = store.insert(sparql1);
//			System.out.println("Insert response: "+ response);
		} catch (MalformedURLException e) {
			e.getMessage();
			e.printStackTrace();
		}
		
		StringBuilder sparql2 = new StringBuilder("");
		sparql2.append("PREFIX Building: <http://dbpedia.org/ontology/> \n");
		sparql2.append("PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		sparql2.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n");
		sparql2.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		sparql2.append("PREFIX gmlns: <http://purl.org/ifgi/gml/0.1/> \n");
		sparql2.append(" SELECT ?BuildingCode ?GrossArea ?latitude  ?longitude  \n");
	    sparql2.append(" WHERE { \n");
	    sparql2.append("     ?contributor rdf:type ?BuildingEntityClass . \n");
	    sparql2.append("     ?contributor Building:hasBuildingNo ?BuildingNum . \n");	//\"100000\"
//	    sparql2.append(" 	 FILTER(xsd:integer(?BuildingNum) > 9999) \n");
//	    sparql2.append("     ?contributor ?BuildingProperty \"BC1\" . \n");
	    sparql2.append("     ?contributor Building:hasBuildingCode ?BuildingCode . \n");
	    sparql2.append("     ?contributor Building:address ?Address . \n");
	    sparql2.append("     ?contributor Building:elevation ?Elevation . \n");
	    sparql2.append("     ?contributor Building:buildingstartdate ?YearBuilt . \n");
	    sparql2.append("     ?contributor Building:floorArea ?GrossArea . \n");
	    
	    sparql2.append("     ?contributor Building:Architect ?architect . \n");
	    sparql2.append("     ?architect rdf:type ?ArchitectEntityClass . \n");
	    sparql2.append("     ?architect Building:birthname ?ArchitectName . \n");
	    
	    sparql2.append("			?contributor  gmlns:GeometryProperty ?gml . \n");
	    sparql2.append("			?gml  sgns:hasLatitude  ?latitude . \n");
	    sparql2.append("			?gml  sgns:hasLongitude  ?longitude . \n");
	    
	    sparql2.append(" } ");
		
	    System.out.println("SPARQL Query: "+ sparql2.toString());
		String response2 = store.query(sparql2.toString(), -1);
		System.out.println("Results: \n"+ response2);
		
	}	

	
	private static void readLatLong() {
		
		String buildingCode = "EEB";
		
		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX gmlns: <http://purl.org/ifgi/gml/0.1/> \n");
		query.append("SELECT ?latitude ?longitude WHERE { \n");
		query.append("			?building  <http://dbpedia.org/ontology/hasBuildingNo> '"+ buildingCode +"'  .\n");
		query.append("			?building  gmlns:GeometryProperty ?gml . \n");
		query.append("			?gml  sgns:hasLatitude  ?latitude . \n");
		query.append("			?gml  sgns:hasLongitude  ?longitude . \n");
		query.append("	 } \n");
		
		String sparql = query.toString();
		System.out.println("\nSelectQuery: "+ sparql);
		
		Store store = null;
		try {
			
			store = new Store(IIPProperties.getStoreURL());		
			String response = store.query(sparql, -1);
			System.out.println("Select response: "+ response);
		} catch (MalformedURLException e) {
			e.getMessage();
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void insertLatLong() throws IOException{
		
		System.out.println("Starting insertLatLong()");
		
		FileReader reader = new FileReader("data/latlong.csv");
        BufferedReader read = new BufferedReader(reader);
		
		StringBuilder insertQuery = new StringBuilder("");
		insertQuery.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		insertQuery.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		insertQuery.append("PREFIX gmlns: <http://purl.org/ifgi/gml/0.1/> \n");
		insertQuery.append("INSERT DATA { \n");
		
		String buildingCode = null;
		String lat = null;
		String longi = null;
		String line = read.readLine();		//First line is the column headers
		line = read.readLine();
		while(line != null) {
			
			System.out.println(line);
			if(line == null || line.startsWith(","))
				continue;
			
			StringTokenizer tok = new StringTokenizer(line, ",");
			
			buildingCode = tok.nextToken();
			lat = tok.nextToken();
			longi = tok.nextToken();
			
			//buildingCode = "testBuildingCode111";
			
			String buildingURI = "ran"+ System.nanoTime();
			String gmlURI = "ran"+ System.nanoTime();
			
			String triple = " sgns:"+ buildingURI + " rdf:type <http://dbpedia.org/ontology/Building> . \n"; 
			insertQuery.append(triple);
			
			triple  = " sgns:"+ buildingURI + " <http://dbpedia.org/ontology/hasBuildingNo> '"+ buildingCode +"' . \n";
			insertQuery.append(triple);
			
			triple = " sgns:"+ buildingURI + " gmlns:GeometryProperty sgns:"+ gmlURI +" . \n";
			insertQuery.append(triple);
			
			triple  = " sgns:"+ gmlURI + " sgns:hasLatitude  '"+ lat +"' . \n";
			insertQuery.append(triple);
			
			triple  = " sgns:"+ gmlURI + " sgns:hasLongitude  '"+ longi +"' . \n";
			insertQuery.append(triple);
			
			line = read.readLine();
		}

		insertQuery.append(" 	} \n");
		String sparql = insertQuery.toString();
		System.out.println("InsertQuery: "+ sparql);
		
		Store store = null;
		try {
			store = new Store(IIPProperties.getStoreURL());		
			String response = store.insert(sparql);
			System.out.println("Insert response: "+ response);
		} catch (MalformedURLException e) {
			e.getMessage();
			e.printStackTrace();
		}
		
	}
	
	private static void readKWH( String buildingCode, String startDate, String endDate, String startTime, String endTime) {
		
		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n");
		//query.append("SELECT (MAX(xsd:double(?kwhReading)) AS ?KWh_MAX)  WHERE { \n");
		query.append("SELECT ?kwhReading  WHERE { \n");
		query.append(" ?building  <http://dbpedia.org/ontology/hasBuildingCode> '"+ buildingCode +"'  .\n");
		
		String 	triple = " ?building  sgns:KiloWattLoadObservable ?kwhURI . \n";
		query.append(triple);
	
		triple = " ?kwhURI  rdf:type sgns:ElectricalMeasurement . \n"; 
		query.append(triple);
		
		triple  = " ?kwhURI  sgns:hasMeasurementRecordedDate  ?date . \n";		//^^xsd:date
		query.append(triple);
		
		triple  = " ?kwhURI  sgns:hasMeasurementRecordedTime  ?time . \n";    //^^xsd:time
		query.append(triple);
		
		triple = "  FILTER ( ?date >= '"+ startDate +"' && ?date <= '"+ endDate + "')  \n";
		query.append(triple);
		
//		triple = "  FILTER ( ?time >= '"+ startTime +"' && ?time <= '"+ endTime + "')  \n";
//		query.append(triple);
		
		triple  = " ?kwhURI  sgns:hasMeasuredValue  ?kwhReading   . \n\n";
		query.append(triple);

		query.append("	 } \n");
		
		String sparql = query.toString();
		System.out.println("\nSelectQuery: "+ sparql);
		
		Store store = null;
		try {
			store = new Store(IIPProperties.getStoreURL());		
			String response = store.query(sparql, -1);
			System.out.println("Select response: "+ response);
		} catch (MalformedURLException e) {
			e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void insertKWH(String kwhReadingFile, String buildingCode, int buildingNo) throws IOException{
		
		System.out.println("Starting insertKWH(): BuildingCode = "+ buildingCode +"  Building# = "+ buildingNo +"  file = "+ kwhReadingFile);
		
		FileReader reader = new FileReader(kwhReadingFile);
        BufferedReader read = new BufferedReader(reader);
		
		StringBuilder insertQuery = new StringBuilder("");
		insertQuery.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		insertQuery.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		insertQuery.append("PREFIX gmlns: <http://purl.org/ifgi/gml/0.1/> \n");
		insertQuery.append("INSERT DATA { \n");
		
		String date = null;
		String time = null;
		String kwhReading = null;
		
		int readingCount = 0;
		String line = read.readLine();		//First line is the column headers
		line = read.readLine();
		while(line != null) {
			
			//System.out.println(line);
			
			StringTokenizer tok = new StringTokenizer(line, ", ");
			
			if(!tok.hasMoreTokens()){
				line = read.readLine();
				continue;
			}	
			if(!(buildingNo+"").equals(tok.nextToken())){
				line = read.readLine();
				continue;
			}
			
			if(!tok.hasMoreTokens()){
				line = read.readLine();
				continue;
			}
			date = tok.nextToken();
			
			if(!tok.hasMoreTokens()){
				line = read.readLine();
				continue;
			}
			time = tok.nextToken();
			
			if(!tok.hasMoreTokens()){
				line = read.readLine();
				continue;
			}
			kwhReading = tok.nextToken();
			
			readingCount++;
			System.out.println(date +"   "+ time +"   "+ kwhReading);
			
			//buildingCode = "testBuildingCode111";
			
			String buildingURI = "ran"+ System.nanoTime();
			String kwhURI = "ran"+ System.nanoTime();
			
			String triple = " 	sgns:"+ buildingURI + " rdf:type <http://dbpedia.org/ontology/Building> . \n"; 
			insertQuery.append(triple);
			
			triple  = " 	sgns:"+ buildingURI + " <http://dbpedia.org/ontology/hasBuildingCode> '"+ buildingCode +"' . \n";
			insertQuery.append(triple);
			
			triple = " 	sgns:"+ buildingURI + " sgns:KiloWattLoadObservable sgns:"+ kwhURI +" . \n";
			insertQuery.append(triple);
		
			triple = " 	sgns:"+ kwhURI + " rdf:type sgns:ElectricalMeasurement . \n"; 
			insertQuery.append(triple);
			
			triple  = " 	sgns:"+ kwhURI + " sgns:hasMeasurementRecordedDate  '"+ date +"' . \n";		//^^xsd:date
			insertQuery.append(triple);
			
			triple  = " 	sgns:"+ kwhURI + " sgns:hasMeasurementRecordedTime  '"+ time +"' . \n";    //^^xsd:time
			insertQuery.append(triple);
		
			triple  = " 	sgns:"+ kwhURI + " sgns:hasMeasuredValue  '"+ kwhReading +"' . \n\n";
			insertQuery.append(triple);
			
			line = read.readLine();
		}

		insertQuery.append(" 	} \n");
		String sparql = insertQuery.toString();
		System.out.println("InsertQuery: "+ sparql);
		
		System.out.println("# of Readings = "+ readingCount);
		
		Store store = null;
		try {
			store = new Store(IIPProperties.getStoreURL());		
			String response = store.insert(sparql);
			System.out.println("Insert response: "+ response);
		} catch (MalformedURLException e) {
			e.getMessage();
			e.printStackTrace();
		}
		
	}
	
	
}
