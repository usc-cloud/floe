package uk.co.magus.fourstore.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class TSVResultSet implements SPARQLResultSet{

	private List<Map<String, String>> resultSet;	//Final List that is returned to the caller

	public TSVResultSet(){
		resultSet = new ArrayList<Map<String, String>>();
	}
	
	@Override
	public List<Map<String, String>> getResultSet() {
		return resultSet;
	}
	
	@Override
	public void setResultSet(List<Map<String, String>> resultSet) {
		this.resultSet = resultSet;
	}
	
	/**
	 * Constructs the JSONResultSet object from the given stream
	 * @param ins InputStream from which this method can read JSON Objects 
	 */
	public void parse(InputStream ins) {
		
		try{
			//InputStream ins = new FileInputStream(new File("results.json"));
			InputStreamReader is = new InputStreamReader(ins);
			BufferedReader br = new BufferedReader(is);
			
			List<String> variables = new ArrayList<String>();
			String line = br.readLine();
			StringTokenizer stt = new StringTokenizer(line);
			String currVar = null;
			while(stt.hasMoreTokens()){
				currVar = stt.nextToken();
				variables.add(currVar.substring(1));
			}
			
			//System.out.println("Variables: "+ variables.toString());
			Map<String, String> elementMap = null;
			int currIndex = 0;
			String value = null;
			line = br.readLine();
			while(line != null) {
			    //System.out.println(line);
				elementMap = new HashMap<String, String>();
			    stt = new StringTokenizer(line, "\t<>\"");
				currVar = null; currIndex = 0;
				value = null;
				while(stt.hasMoreTokens()){
					currVar = variables.get(currIndex++);
					value = stt.nextToken();
					//System.out.print(value +" | ");
					elementMap.put(currVar, value);
				}
			    resultSet.add(elementMap);
			    line = br.readLine();
			}
		}catch(IOException ioe){
			ioe.getMessage();
			ioe.printStackTrace();
		}
	}	
}
