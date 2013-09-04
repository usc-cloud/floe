package uk.co.magus.fourstore.client;

import java.util.List;
import java.util.Map;

public interface SPARQLResultSet {
	
	public List<Map<String, String>> getResultSet();
	public void setResultSet(List<Map<String, String>> resultSet); 
	   
}
