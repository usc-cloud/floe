package edu.usc.pgroup.floe.applications.iipipeline.pellets.semantic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.co.magus.fourstore.client.SPARQLResultSet;
import uk.co.magus.fourstore.client.Store;
import uk.co.magus.fourstore.client.Store.OutputFormat;
import edu.usc.pgroup.floe.api.framework.pelletmodels.Pellet;
import edu.usc.pgroup.floe.applications.iipipeline.pellets.utils.IIPProperties;
import edu.usc.pgroup.floe.applications.iipipeline.pellets.utils.SGConstants;

public class InsertEquipmentPellet implements Pellet {
	private static final Logger logger = Logger.getLogger(InsertEquipmentPellet.class.getName());
	// public InsertEquipment(SingleWritable writable, String tag)
	// {
	// this.tag = tag;
	// this.writable = writable;
	// }

	//private static String STORE_URL = IIPProperties.getStoreURL(); // majave-04 VM

	/**
	 * @param args
	 * @throws InterruptedException
	 */

	public static enum EQUIPMENT_KEY {
		CAMPUS("CampusCode"), BUILDING("Building"), ROOM_NO("RoomNo"), FLOOR(
				"Floor"),

		SENSORID("SensorID"), OBSERVABLE_FEATURE("ObservableFeature"), UNIT(
				"Unit"), ASSOCIATION("Association"),

		NOTES("Notes"), TYPE("Type"), EQUIPMENT_SPEC("EquipmentSpec"), EQUPMENT_ID(
				"EquipmentID"), EQUPMENT_CLASS("EquipmentClass");

		private String key;

		EQUIPMENT_KEY(String key) {
			this.key = key;
		}

		public String toString() {
			return key;
		}
	}

	public static enum ASSOCIATION_TYPE { // Lists the possible entities an
											// ObservableThing can be

		BUILDING("Building"), FLOOR("Floor"), ROOM("Room"),

		EQUIPMENT("Equipment");

		private String key;

		ASSOCIATION_TYPE(String key) {
			this.key = key;
		}

		public String toString() {
			return key;
		}
	}

	private void insertEquipment(Store store) throws InterruptedException {
		String COUNTER = "1";
		String EQUIP_COUNTER = "2";

		String obsFeature = "KiloWattLoadObservable" + COUNTER;

		Map<String, String> equipment = new HashMap<String, String>();

		equipment.put(EQUIPMENT_KEY.CAMPUS.toString(), "UPC");
		equipment.put(EQUIPMENT_KEY.BUILDING.toString(), "EEB" + COUNTER);
		equipment.put(EQUIPMENT_KEY.ROOM_NO.toString(), "B11" + COUNTER);
		equipment.put(EQUIPMENT_KEY.FLOOR.toString(), "2" + COUNTER);

		equipment.put(EQUIPMENT_KEY.SENSORID.toString(), "SCB_FCU001RmActSp"
				+ EQUIP_COUNTER); // Username

		equipment.put(EQUIPMENT_KEY.OBSERVABLE_FEATURE.toString(), obsFeature); // Category
		equipment.put(EQUIPMENT_KEY.UNIT.toString(), "KiloWattUnit" + COUNTER);
		equipment.put(EQUIPMENT_KEY.ASSOCIATION.toString(), "Room"); // With
																		// what
																		// is
																		// this
																		// sensor
																		// associated
																		// with
																		// ...
																		// room,
																		// equipment,
																		// building
		equipment.put(EQUIPMENT_KEY.NOTES.toString(), "Building KW load"
				+ COUNTER); // A short description about the Feature
		equipment.put(EQUIPMENT_KEY.TYPE.toString(), "PointValue"); // Sensor
																	// Type ...
																	// property
																	// of a
																	// sensor

		equipment.put(EQUIPMENT_KEY.EQUIPMENT_SPEC.toString(), "Room"); // Property
																		// of
																		// Equipment
																		// Entity
		equipment
				.put(EQUIPMENT_KEY.EQUPMENT_ID.toString(), "VAVB209" + COUNTER); // 'EquipmentDesignation'
																					// -
																					// Property
																					// of
																					// Equipment
																					// Entity
																					// ...
																					// unique
																					// ID
																					// for
																					// each
																					// equipemnt
		equipment.put(EQUIPMENT_KEY.EQUPMENT_CLASS.toString(), "VAVBox"); // Equipment
																			// type

		if (!insertEquipment(store, equipment)) {
			System.out.println("Error While inserting Equipment "
					+ equipment.get("EquipmentID"));
		}
	}

	private boolean validateMapValues(Map<String, String> valueMap) {

		if (valueMap.get(EQUIPMENT_KEY.SENSORID.toString()) == null
				|| valueMap.get(EQUIPMENT_KEY.ASSOCIATION.toString()) == null
				|| valueMap.get(EQUIPMENT_KEY.UNIT.toString()) == null
				|| valueMap.get(EQUIPMENT_KEY.OBSERVABLE_FEATURE.toString()) == null
				|| valueMap.get(EQUIPMENT_KEY.CAMPUS.toString()) == null) {
			System.out.println("Validation failed for valueMap "
					+ valueMap
					+ " sensor id : "
					+ valueMap.get(EQUIPMENT_KEY.SENSORID.toString())
					+ "Association "
					+ valueMap.get(EQUIPMENT_KEY.ASSOCIATION.toString())
					+ " Unit "
					+ valueMap.get(EQUIPMENT_KEY.UNIT.toString())
					+ " Observable feature "
					+ valueMap.get(EQUIPMENT_KEY.OBSERVABLE_FEATURE.toString())
					+ " campus "
					+ valueMap.get(EQUIPMENT_KEY.CAMPUS.toString()
							));
			return false;
		}

		// Optional fields
		// valueMap.get(EQUIPMENT_KEY.EQUPMENT_ID.toString().toLowerCase()) ==
		// null
		// valueMap.get(EQUIPMENT_KEY.EQUIPMENT_SPEC.toString().toLowerCase())
		// == null

		return true;
	}

	/**
	 * Builds a Parameterized custom query for Equipment (and other related
	 * entities like Sensor, place(building/floor/room), ObservableThing,
	 * ObservableFeature) and inserts it in the RDF store.
	 * 
	 * @param store
	 *            an object to run SELECT and Update queries to the Store.
	 * @param equipment
	 *            a key value object with keys from enum EQUIPMENT_KEY
	 * @return true - if triples are inserted successfully. Also return true if
	 *         there were triples to insert (all existed already) false - When
	 *         the input map is empty
	 * @throws InterruptedException
	 */
	public boolean insertEquipment(Store store, Map<String, String> equipment)
			throws InterruptedException {
		if (equipment == null) {
			System.out.println("Returning false as equipment is null");
			return false;
		}

		if (!validateMapValues(equipment)) { // Check if all the required fields
												// are present in the Map
			// System.out.println("Validation failed so returning false");
			return false;
		}

		int tripleCount = 0; // Incremented every time we add a new triple that
								// is to be inserted

		boolean newEquipment = false;
		boolean newPlace = false;
		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX dbpns: <http://dbpedia.org/ontology/> \n");
		query.append("INSERT DATA { \n");

		String equipmentID = equipment.get(EQUIPMENT_KEY.EQUPMENT_ID.toString()
				);
		String equipmentClass = equipment.get(EQUIPMENT_KEY.EQUPMENT_CLASS
				.toString());
		String equipmentSpec = equipment.get(EQUIPMENT_KEY.EQUIPMENT_SPEC
				.toString());
		String equipmentNotes = equipment.get(EQUIPMENT_KEY.NOTES.toString()
				);

		String equipmentURI = lookUpEquipment(store, equipment.get(equipmentID));
		if (equipmentURI == null && equipmentID != null
				&& equipmentSpec != null && equipmentClass != null) {
			// System.out.println("Adding new Equiment to Store");
			newEquipment = true;
			equipmentURI = "ran" + getUUID();

			query.append("		sgns:" + equipmentURI
					+ " rdf:type 			sgns:Equipment . \n");
			query.append("		sgns:" + equipmentURI + " sgns:hasID 			'"
					+ equipmentID + "' . \n");
			query.append("		sgns:" + equipmentURI
					+ " sgns:equipmentClass sgns:" + equipmentClass + " . \n");
			query.append("		sgns:" + equipmentURI + " sgns:equipmentType 	'"
					+ equipmentSpec + "' . \n");
			tripleCount += 4;
			if (equipmentNotes != null) {
				query.append("		sgns:" + equipmentURI + " sgns:equipmentNote '"
						+ equipmentNotes + "' . \n\n");
				tripleCount++;
			}
		}
		if (equipmentURI != null)
			equipmentURI = trimURI(equipmentURI); // Remove the sgns part from
													// the URI, if present


		
		String campusCode = equipment.get(EQUIPMENT_KEY.CAMPUS.toString()
				);
		String building = equipment.get(EQUIPMENT_KEY.BUILDING.toString()
				);
		String floor = equipment.get(EQUIPMENT_KEY.FLOOR.toString()
				);
		String roomNo = equipment.get(EQUIPMENT_KEY.ROOM_NO.toString()
				);
		if ("Many".equalsIgnoreCase(roomNo))
			roomNo = null;

		String buildingURI = null;
		String placeURI = lookupPlace(store, building, floor, roomNo);
		if (placeURI == null) {
			newPlace = true;
			// System.out.println("Adding new Place to Store");
			if (building != null && floor != null && roomNo != null) {

				buildingURI = "ran" + getUUID();
				query.append("		sgns:" + buildingURI
						+ "  rdf:type  sgns:Building . \n");
				query.append("		sgns:" + buildingURI
						+ "  dbpns:hasBuildingCode  '" + building + "' . \n");

				String floorURI = "ran" + getUUID();
				query.append("		sgns:" + buildingURI + "  dbpns:Floor  sgns:"
						+ floorURI + " . \n");
				query.append("		sgns:" + floorURI
						+ "  rdf:type  dbpns:Floor . \n");
				query.append("		sgns:" + floorURI + "  dbpns:hasFloorNo  '"
						+ floor + "' . \n");

				String roomURI = "ran" + getUUID();
				query.append("		sgns:" + floorURI + "  dbpns:Room  sgns:"
						+ roomURI + " . \n");
				query.append("		sgns:" + roomURI
						+ "  rdf:type  dbpns:Room . \n");
				query.append("		sgns:" + roomURI + "  dbpns:hasRoomNo  '"
						+ roomNo + "' . \n");
				placeURI = roomURI;
				tripleCount += 8;
			} else if (building != null && floor != null) {
				buildingURI = "ran" + getUUID();
				query.append("		sgns:" + buildingURI
						+ "  rdf:type  dbpns:Building . \n");
				query.append("		sgns:" + buildingURI
						+ "  dbpns:hasBuildingCode  '" + building + "' . \n");

				String floorURI = "ran" + getUUID();
				query.append("		sgns:" + buildingURI + "  dbpns:Floor  sgns:"
						+ floorURI + " . \n");
				query.append("		sgns:" + floorURI
						+ "  rdf:type  dbpns:Floor . \n");
				query.append("		sgns:" + floorURI + "  dbpns:hasFloorNo  '"
						+ floor + "' . \n");
				placeURI = floorURI;
				tripleCount += 5;
			} else if (building != null) {
				buildingURI = "ran" + getUUID();
				query.append("		sgns:" + buildingURI
						+ "  rdf:type  dbpns:Building . \n");
				query.append("		sgns:" + buildingURI
						+ "  dbpns:hasBuildingCode  '" + building + "' . \n");
				placeURI = buildingURI;
				tripleCount += 2;
			}
			// ??? //??? should this be added. This causes the placeURI to have
			// two rdf:type. One of type sgns:place and other of
			// rdf:Room/Floor/Building
			// query.append("		sgns:"+ placeURI +" rdf:type  sgns:place . \n");
		}
		System.out.println(building + placeURI);
		placeURI = trimURI(placeURI); // Remove the sgns prefix from the URI, if
										// present

		if (equipmentURI != null && (newEquipment || newPlace)) { // If anyone
																	// them is
																	// new
																	// insert a
																	// triple
			query.append("		sgns:" + equipmentURI
					+ "  sgns:isInstalledIn  sgns:" + placeURI + "  . \n\n");
			tripleCount += 1;
		}

		// Add a triple for Campus and Building relation
		if (buildingURI == null) { // Lookup the building URI as the variable
									// remains null when we find a placeURI
									// above
			buildingURI = lookupBuilding(store, building);
			if (buildingURI == null) {
				buildingURI = "ran" + getUUID();
				query.append("		sgns:" + buildingURI
						+ "  rdf:type  dbpns:Building . \n");
				query.append("		sgns:" + buildingURI
						+ "  dbpns:hasBuildingCode  '" + building + "' . \n");
				placeURI = buildingURI;
				tripleCount += 2;
			}
			buildingURI = trimURI(buildingURI);
		}

		String campusURI = lookupCampus(store, campusCode);
		if (campusURI == null) { // Insert if not exists
			campusURI = "ran" + getUUID(); // lookup first and insert if not
											// exists !!!
			// System.out.println("Adding new Campus to Store");
			query.append("		sgns:" + campusURI
					+ " rdf:type  dbpns:Campus . \n ");
			query.append("		sgns:" + campusURI + " dbpns:hasCampusCode   '"
					+ campusCode + "' . \n");
			query.append("		sgns:" + campusURI + " dbpns:Building  sgns:"
					+ buildingURI + " . \n");
			tripleCount += 3;
		} else {
			campusURI = trimURI(campusURI); // Remove the sgns prefix from the
											// URI, if present
			boolean campusBuildingRelation = lookUpCampusBuildingRelation(
					store, campusURI, buildingURI);
			if (!campusBuildingRelation) { // If no relation is present then
											// insert a new one
				query.append("		sgns:" + campusURI + " dbpns:Building  sgns:"
						+ buildingURI + " . \n");
				tripleCount += 1;
			}
		}

		// 'Association' tells what the ObservableThing is associated with ... a
		// place (room/floor/building) or a equipment.
		String obsThingURI = null;
		String obsFeatureURI = null;

		boolean newObsFeature = false;
		boolean newObsThing = false;
		if (equipment.get(EQUIPMENT_KEY.ASSOCIATION.toString())
				.equalsIgnoreCase(ASSOCIATION_TYPE.EQUIPMENT.toString())) {
			obsThingURI = lookupObservableThing(store, equipmentURI); // Insert
																		// if
																		// not
																		// exists
			if (obsThingURI == null) {
				newObsThing = true;
				// System.out.println("Adding new Observable Thing to Store");
				obsThingURI = "ran" + getUUID();
				query.append("		sgns:" + obsThingURI
						+ " rdf:type  sgns:ObservableThing . \n");
				tripleCount += 1;
			}
			obsThingURI = trimURI(obsThingURI); // Remove the sgns prefix from
												// the URI, if present
			if (newEquipment || newObsThing) {
				query.append("		sgns:" + equipmentURI
						+ "  sgns:observableISA  sgns:" + obsThingURI
						+ "  . \n");
				tripleCount += 1;
			}
			obsFeatureURI = lookupObsFeature(store,
					equipment.get(EQUIPMENT_KEY.OBSERVABLE_FEATURE.toString()
							), equipmentURI); // hasObservableFeature
		} else if (equipment.get(
				EQUIPMENT_KEY.ASSOCIATION.toString())
				.equalsIgnoreCase(ASSOCIATION_TYPE.ROOM.toString())
				|| equipment.get(
						EQUIPMENT_KEY.ASSOCIATION.toString())
						.equalsIgnoreCase(ASSOCIATION_TYPE.FLOOR.toString())
				|| equipment.get(
						EQUIPMENT_KEY.ASSOCIATION.toString())
						.equalsIgnoreCase(ASSOCIATION_TYPE.BUILDING.toString())) {
			obsThingURI = lookupObservableThing(store, placeURI); // Insert if
																	// not
																	// exists
			if (obsThingURI == null) {
				newObsThing = true;
				// System.out.println("Adding new Observable Thing to Store");
				obsThingURI = "ran" + getUUID();
				query.append("		sgns:" + obsThingURI
						+ " rdf:type  sgns:ObservableThing . \n");
				tripleCount += 1;
			}
			obsThingURI = trimURI(obsThingURI); // Remove the sgns prefix from
												// the URI, if present
			if (newEquipment || newObsThing) {
				query.append("		sgns:" + placeURI
						+ "  sgns:observableISA  sgns:" + obsThingURI
						+ "  . \n");
				tripleCount += 1;
			}
			// ?observableThingURI sgns:obsISA placeURI

			obsFeatureURI = lookupObsFeature(store,
					equipment.get(EQUIPMENT_KEY.OBSERVABLE_FEATURE.toString()
							), placeURI); // hasObservableFeature
		}

		// If no such observable feature exists then create a new one
		if (obsFeatureURI == null) {
			newObsFeature = true;
			// System.out.println("Adding new Observable Feature to Store"); //
			// By Sreedhar
			obsFeatureURI = "ran" + getUUID(); // lookup first and insert if not
												// exists !!!
			query.append("		sgns:"
					+ obsFeatureURI
					+ " rdf:type sgns:"
					+ equipment.get(EQUIPMENT_KEY.OBSERVABLE_FEATURE.toString()
							) + " . \n");
			tripleCount += 1;
		}
		obsFeatureURI = trimURI(obsFeatureURI); // Remove the sgns prefix from
												// the URI, if present

		if (newObsThing || newObsFeature) { // Add a new relation if any of them
											// are new
			query.append("		sgns:" + obsThingURI
					+ "  sgns:hasAObservableFeature  sgns:" + obsFeatureURI
					+ "  . \n\n");
			tripleCount += 1;
		}

		// ObsFeature can be measured by multiple sensors ... need to insert
		// triples accordingly !
		String sensorURI = lookupSensor(store, obsFeatureURI,
				equipment.get(EQUIPMENT_KEY.SENSORID.toString()),
				equipment.get(EQUIPMENT_KEY.UNIT.toString()));
		if (sensorURI == null) { // Insert if not exists
			sensorURI = "ran" + getUUID(); // lookup first and insert if not
											// exists !!!
			// System.out.println("Adding new Sensor to Store"); // By Sreedhar
			query.append("		sgns:" + sensorURI + " rdf:type sgns:Sensor . \n");
			query.append("		sgns:"
					+ sensorURI
					+ " sgns:hasSensorID '"
					+ equipment.get(EQUIPMENT_KEY.SENSORID.toString()
							) + "' . \n");
			query.append("		sgns:"
					+ sensorURI
					+ " sgns:hasUnit  '"
					+ equipment
							.get(EQUIPMENT_KEY.UNIT.toString())
					+ "' . \n");
			query.append("		sgns:" + sensorURI + " sgns:measures  sgns:"
					+ obsFeatureURI + " . \n"); // This takes care of one to
												// many relation between Sensor
												// and ObsFeature
			tripleCount += 4;
		}
		sensorURI = trimURI(sensorURI); // Remove the sgns prefix from the URI,
										// if present

		// query.append("		\n");
		query.append("	} ");

		// System.out.println("INSERT DATA update for Equipment: \n"+
		// query.toString()); // By Sreedhar
		if (tripleCount == 0)
			return true; // Nothing to insert so return with a success

		try {
			String response = store.insert(query.toString());
			// By Sreedhar System.out.println("Insert Equipment response: "+
			// response);

			// //TODO //What does Store.insert() return on success ???
			// if(response != null)
			// return false;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static String getUUID() throws InterruptedException {

		return SGConstants.getUUID();
		// Thread.currentThread().sleep(10);
		// return Long.toString(System.currentTimeMillis());
	}

	/**
	 * Remove the sgns part from the URI, if present
	 * 
	 * @param uri
	 * @return
	 */
	private String trimURI(String uri) {

		if (uri.startsWith("http:")) {
			int pos = uri.indexOf('#') + 1;
			String id = uri.substring(pos);
			return id;
		}
		return uri;
	}

	/**
	 * Fetch the sensorURI for the given sensorID and observable feature
	 * 
	 * @param store
	 * @param obsFeatureURI
	 * @param sensorID
	 * @return
	 */
	private String lookupSensor(Store store, String obsFeatureURI,
			String sensorID, String unit) {

		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("SELECT ?sensorURI \n WHERE { \n");
		query.append("		?sensorURI  sgns:hasSensorID '" + sensorID + "' . \n");
		query.append("		?sensorURI  sgns:hasUnit '" + unit + "' . \n");
		query.append("		?sensorURI  rdf:type sgns:Sensor . \n");
		query.append("		?sensorURI  sgns:measures sgns:" + obsFeatureURI
				+ " . \n");
		query.append("		} \n");

		String sparqlQ = query.toString();
		SPARQLResultSet resultSet = null;
		try {
			resultSet = store.query(sparqlQ, OutputFormat.SPARQL_XML, -1, true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println("SELECT Query = "+ sparqlQ); // By Sreedhar

		List<Map<String, String>> results = resultSet.getResultSet();
		// By Sreedhar System.out.println("ResultSet Size = "+ results.size());
		if (results.size() < 1) {
			return null;
		} else {
			// System.out.println(results.get(0).get("sensorURI")); // By
			// Sreedhar
			return results.get(0).get("sensorURI");
		}

		// return
		// "http://www.smartgrid.usc.edu/Ontology/2012.owl#rantestSensorURI";
		// return null;
	}

	
	private String Normalize(String s)
	{
		return s.replace(' ', '_');
	}
	/**
	 * Fetch the URI for observable feature.
	 * 
	 * @param store
	 * @param obsFeature
	 * @param entityURI
	 *            This can be placeURI or equipmentURI
	 * @return
	 */
	private String lookupObsFeature(Store store, String obsFeature,
			String entityURI) { // hasObservableFeature

		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("SELECT ?obsFeatureURI WHERE { \n");
		query.append("		sgns:" + entityURI
				+ "  sgns:observableISA  ?obsThingURI . \n");
		query.append("		?obsThingURI  rdf:type  sgns:ObservableThing . \n");
		query.append("		?obsThingURI  sgns:hasAObservableFeature ?obsFeatureURI . \n");
		query.append("		?obsFeatureURI  rdf:type sgns:" + Normalize(obsFeature) + " . \n");	//Hack to convert space to _
		query.append("		} \n");

		String sparqlQ = query.toString();
		SPARQLResultSet resultSet = null;
		try {
			//System.out.println("Exception query is " + sparqlQ);
			resultSet = store.query(sparqlQ, OutputFormat.SPARQL_XML, -1, true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println("SELECT Query = "+ sparqlQ); // By Sreedhar

		List<Map<String, String>> results = resultSet.getResultSet();
		// By Sreedhar System.out.println("ResultSet Size = "+ results.size());
		if (results.size() < 1) {
			return null;
		} else {
			// System.out.println(results.get(0).get("obsFeatureURI")); // By
			// Sreedhar
			return results.get(0).get("obsFeatureURI");
		}
		// return null;
		// return
		// "http://www.smartgrid.usc.edu/Ontology/2012.owl#rantestObsFeatureURI";
	}

	/**
	 * Lookup the URI for a equipment
	 * 
	 * @param store
	 * @param equipmentID
	 * @return
	 */
	private String lookUpEquipment(Store store, String equipmentID) {

		if (equipmentID == null)
			return null;

		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("SELECT ?equipmentURI \n WHERE { \n");
		query.append("		?equipmentURI  sgns:hasID  '" + equipmentID + "' . \n");
		query.append("		?equipmentURI  rdf:type  sgns:Equipment . \n");
		query.append("		} \n");

		String sparqlQ = query.toString();
		SPARQLResultSet resultSet = null;
		try {
			resultSet = store.query(sparqlQ, OutputFormat.SPARQL_XML, -1, true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println("SELECT Query = "+ sparqlQ); // By Sreedhar

		List<Map<String, String>> results = resultSet.getResultSet();
		// By Sreedhar System.out.println("ResultSet Size = "+ results.size());
		if (results.size() < 1) {
			return null;
		} else {
			// System.out.println(results.get(0).get("equipmentURI")); // By
			// Sreedhar
			return results.get(0).get("equipmentURI");
		}

	}

	/**
	 * Returns true if a relation between the two URIs is found, else returns
	 * false
	 * 
	 * @param store
	 * @param campusURI
	 * @param buildingURI
	 * @return
	 */
	private boolean lookUpCampusBuildingRelation(Store store, String campusURI,
			String buildingURI) {
		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX dbpns: <http://dbpedia.org/ontology/> \n");
		query.append("SELECT ?relationType \n WHERE { \n");
		query.append("		sgns:" + campusURI + "  	dbpns:Building 	sgns:"
				+ buildingURI + ". \n");
		query.append("		sgns:" + campusURI
				+ "  	?relationType  		dbpns:Campus   . \n");
		query.append("		sgns:" + buildingURI
				+ "  ?relationType 		dbpns:Building  . \n");
		query.append("		} \n");

		String sparqlQ = query.toString();
		SPARQLResultSet resultSet = null;
		try {
			resultSet = store.query(sparqlQ, OutputFormat.SPARQL_XML, -1, true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// By Sreedhar System.out.println("SELECT Query = "+ sparqlQ);

		List<Map<String, String>> results = resultSet.getResultSet();
		// By Sreedhar System.out.println("ResultSet Size = "+ results.size());
		if (results.size() < 1) {
			System.out.println("Results size less than 1 so returning false");
			return false;
		} else {
			// By Sreedhar
			// System.out.println(results.get(0).get("relationType"));
			return true;
		}
	}

	/**
	 * Fetch the CampusURI for the given CampusCode
	 * 
	 * @param store
	 * @param campusCode
	 * @return
	 */
	private String lookupCampus(Store store, String campusCode) {

		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX dbpns: <http://dbpedia.org/ontology/> \n");
		query.append("SELECT ?campusURI \n WHERE { \n");
		query.append("		?campusURI  dbpns:hasCampusCode '" + campusCode
				+ "' . \n");
		query.append("		?campusURI  rdf:type dbpns:Campus  . \n");
		query.append("		} \n");

		String sparqlQ = query.toString();
		SPARQLResultSet resultSet = null;
		try {
			resultSet = store.query(sparqlQ, OutputFormat.SPARQL_XML, -1, true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println("SELECT Query = "+ sparqlQ); // By Sreedhar

		List<Map<String, String>> results = resultSet.getResultSet();
		// By Sreedhar System.out.println("ResultSet Size = "+ results.size());
		if (results.size() < 1) {
			return null;
		} else {
			// System.out.println(results.get(0).get("campusURI")); // By
			// Sreedhar
			return results.get(0).get("campusURI");
		}
		// return
		// "http://www.smartgrid.usc.edu/Ontology/2012.owl#rantestSensorURI";
		// return null;
	}

	/**
	 * Fetch the CampusURI for the given CampusCode
	 * 
	 * @param store
	 * @param campusCode
	 * @return
	 */
	private String lookupBuilding(Store store, String buildingCode) {

		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX dbpns: <http://dbpedia.org/ontology/> \n");
		query.append("SELECT ?buildingURI \n WHERE { \n");
		query.append("		?buildingURI  dbpns:hasBuildingCode  '" + buildingCode
				+ "' . \n");
		query.append("		?buildingURI  rdf:type dbpns:Building  . \n");
		query.append("		} \n");

		String sparqlQ = query.toString();
		SPARQLResultSet resultSet = null;
		try {
			resultSet = store.query(sparqlQ, OutputFormat.SPARQL_XML, -1, true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println("SELECT Query = "+ sparqlQ); // By Sreedhar

		List<Map<String, String>> results = resultSet.getResultSet();
		// System.out.println("ResultSet Size = "+ results.size()); // By
		// Sreedhar
		if (results.size() < 1) {
			return null;
		} else {
			// System.out.println("Returning buildingURI = "+results.get(0).get("buildingURI"));
			// // By Sreedhar
			return results.get(0).get("buildingURI");
		}
		// return
		// "http://www.smartgrid.usc.edu/Ontology/2012.owl#rantestSensorURI";
		// return null;
	}

	private String lookupPlace(Store store, String building, String floor,
			String roomNo) {

		if (building == null)
			return null;

		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		query.append("PREFIX dbpns: <http://dbpedia.org/ontology/> \n");
		query.append("SELECT ?placeURI WHERE { \n");

		if (building != null && floor != null && roomNo != null) {
			query.append("		?buildingURI  rdf:type  dbpns:Building . \n");
			query.append("		?buildingURI  dbpns:hasBuildingCode  '" + building
					+ "' . \n");
			query.append("		?buildingURI  dbpns:Floor  ?floorURI  . \n");

			query.append("		?floorURI  rdf:type  dbpns:Floor . \n");
			query.append("		?floorURI  dbpns:hasFloorNo  '" + floor + "' . \n");
			query.append("		?floorURI  dbpns:Room  ?placeURI  . \n");

			query.append("		?placeURI  rdf:type  dbpns:Room . \n");
			query.append("		?placeURI  dbpns:hasRoomNo  '" + roomNo + "' . \n");
		} else if (building != null && floor != null) {
			query.append("		?buildingURI  rdf:type  dbpns:Building . \n");
			query.append("		?buildingURI  dbpns:hasBuildingCode  '" + building
					+ "' . \n");
			query.append("		?buildingURI  dbpns:Floor  ?placeURI  . \n");

			query.append("		?placeURI  rdf:type  dbpns:Floor . \n");
			query.append("		?placeURI  dbpns:hasFloorNo  '" + floor + "' . \n");
		} else if (building != null) {
			query.append("		?placeURI  rdf:type  dbpns:Building . \n");
			query.append("		?placeURI  dbpns:hasBuildingCode  '" + building
					+ "' . \n");
		}
		query.append("		} \n");

		String sparqlQ = query.toString();
		SPARQLResultSet resultSet = null;
		try {
			resultSet = store.query(sparqlQ, OutputFormat.SPARQL_XML, -1, true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println("SELECT Query = "+ sparqlQ);

		List<Map<String, String>> results = resultSet.getResultSet();
		// System.out.println("ResultSet Size = "+ results.size());
		if (results.size() < 1) {
			return null;
		} else {
			// By Sreedhar System.out.println(results.get(0).get("placeURI"));
			return results.get(0).get("placeURI");
		}

		// return null;
		// return
		// "http://www.smartgrid.usc.edu/Ontology/2012.owl#rantestplaceURI";
	}

	/**
	 * Lookup the URI for observable thing associated with the passed entity
	 * (place/equipment)
	 * 
	 * @param store
	 * @param entityURI
	 *            This can be a equipmentURI or placeURI
	 * @return
	 */
	private String lookupObservableThing(Store store, String entityURI) {

		StringBuilder query = new StringBuilder("");
		query.append("PREFIX sgns: <http://www.smartgrid.usc.edu/Ontology/2012.owl#> \n");
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");

		query.append("SELECT ?obsThingURI WHERE { \n");
		query.append("		sgns:" + entityURI
				+ "  sgns:observableISA  ?obsThingURI . \n");
		query.append("		?obsThingURI  rdf:type  sgns:ObservableThing . \n");
		query.append("		} \n");

		String sparqlQ = query.toString();
		SPARQLResultSet resultSet = null;
		try {
			resultSet = store.query(sparqlQ, OutputFormat.SPARQL_XML, -1, true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// By Sreedhar System.out.println("SELECT Query = "+ sparqlQ);

		List<Map<String, String>> results = resultSet.getResultSet();
		// By Sreedhar System.out.println("ResultSet Size = "+ results.size());
		if (results.size() < 1) {
			return null;
		} else {
			// By Sreedhar
			// System.out.println(results.get(0).get("obsThingURI"));
			return results.get(0).get("obsThingURI");
		}

		// return null;
		// return
		// "http://www.smartgrid.usc.edu/Ontology/2012.owl#rantestObsThingURI";
	}

	// @Override
	public String getPelletType() {
		// TODO Auto-generated method stub
		return null;
	}

	public SemanticInfo byteToSemantic(byte[] bytes) {
		SemanticInfo tempInfo = null;
		try {
			ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(byteInput);
			tempInfo = (SemanticInfo) ois.readObject();
		} catch (Exception ex) {

		}
		return tempInfo;
	}

	@Override
	public Object invoke(Object in,
			edu.usc.pgroup.floe.api.state.StateObject stateObject) {
		logger.log(Level.SEVERE, "Pellet getting invoked");
		Store store = null;
		try {
			store = new Store(IIPProperties.getStoreURL());
		} catch (MalformedURLException e) {
			e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			e.getMessage();
			e.printStackTrace();
		}

		SemanticInfo semanticInfo = ((SemanticInfo) in);
		Map<String, String> mapOut = semanticInfo.getMap();

		// Add the row to the store
		try {
			if (!this.insertEquipment(store, mapOut)) {
				System.out.println("Error While inserting Equipment "
						+ mapOut.get("EquipmentID"));
			} else {
				/*
				 * Message<byte[]> msg = new MessageImpl<byte[]>();
				 * msg.putPayload(BitConverter.getBytes("equipment"));
				 * write(msg);
				 */
				// write("equipment");
				return "equipment";
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
