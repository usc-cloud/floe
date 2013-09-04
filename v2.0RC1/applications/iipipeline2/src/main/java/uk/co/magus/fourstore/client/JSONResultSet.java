package uk.co.magus.fourstore.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class JSONResultSet implements SPARQLResultSet 
{
	
	private List<Map<String, String>> resultSet;	//Final List that is returned to the caller

	public JSONResultSet(){
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
			JsonFactory jsonFactory = new JsonFactory(); 		// or, for data binding, org.codehaus.jackson.mapper.MappingJsonFactory 
			JsonParser jp = jsonFactory.createJsonParser(ins); 	// or URL, Stream, Reader, String, byte[]
			
			List<String> variables = new ArrayList<String>();
			JSONResultSet helper = new JSONResultSet();
			
			if (!jp.hasCurrentToken()) {
				jp.nextToken();
			}
			helper.assertToken(JsonToken.START_OBJECT, jp.getCurrentToken()); // main object
			
			helper.assertToken(JsonToken.FIELD_NAME, jp.nextToken()); 		// 'head'
			helper.verifyFieldName(jp, "head");

			helper.assertToken(JsonToken.START_OBJECT, jp.nextToken()); 	// 'head' object
			
			helper.assertToken(JsonToken.FIELD_NAME, jp.nextToken()); 		// 'vars'
			helper.verifyFieldName(jp, "vars");
			
			helper.assertToken(JsonToken.START_ARRAY, jp.nextToken()); 		// 'vars' array
			
			jp.nextToken();
			while(jp.getCurrentToken() != JsonToken.END_ARRAY ){
				helper.assertToken(JsonToken.VALUE_STRING, jp.getCurrentToken());	// vars[i]
				variables.add(jp.getText());
				jp.nextToken();
			}       
	        
	        helper.assertToken(JsonToken.END_OBJECT, jp.nextToken()); 		// 'head' object
	        
	        //Next read the results section of the JSON document
	        helper.assertToken(JsonToken.FIELD_NAME, jp.nextToken()); 		// 'results'
			helper.verifyFieldName(jp, "results");
	        helper.assertToken(JsonToken.START_OBJECT, jp.nextToken()); 	// 'results' object
	        
			helper.assertToken(JsonToken.FIELD_NAME, jp.nextToken()); 		// 'bindings'
			helper.verifyFieldName(jp, "bindings");
			helper.assertToken(JsonToken.START_ARRAY, jp.nextToken()); 		// 'bindings' array

			jp.nextToken();
			while(jp.getCurrentToken() != JsonToken.END_ARRAY){
				helper.assertToken(JsonToken.START_OBJECT, jp.getCurrentToken()); 	// Every result inside the bindings array is a new object
				Map<String, String> resultElement = new HashMap<String, String>();
				
				for(int k=0; k < variables.size(); k++){
					
					helper.assertToken(JsonToken.FIELD_NAME, jp.nextToken());	// bindings[i]
					String currVar = jp.getCurrentName();
					
					helper.assertToken(JsonToken.START_OBJECT, jp.nextToken()); // 'ith variable' object in the result
					jp.nextToken();
					while(jp.getCurrentToken() != JsonToken.END_OBJECT){

						helper.assertToken(JsonToken.FIELD_NAME, jp.getCurrentToken()); // 'type'
						//helper.verifyFieldName(jp, "type");
				        String key = jp.getCurrentName();		//Can be a "uri" or "literal" or 3 more types (http://www.w3.org/TR/rdf-sparql-json-res/  Section 3a)
						
						helper.assertToken(JsonToken.VALUE_STRING, jp.nextToken());
				        String value = helper.getAndVerifyText(jp);
				        
				        jp.nextToken();
				        
				        /**
				         * Currently only handling one type of key, i.e. "literal". All the values are returned as
				         * Strings in a List<Map<String, String>>. The user of the resultSet should know how to read
				         * the values from the Map and parse back to appropriate data type.
				         * 
				         * As per W3C spec for JSON output (http://www.w3.org/TR/rdf-sparql-json-res/  Section 3a), 
				         * we can have 4 more types of keys, namely 
				         * 1] RDF URI Reference U 
				         * 		JSON: "name" : {"type":"uri", "value":"U""}
				         * 2] RDF Literal S 
				         * 		JSON: "name" : {"type":"literal", "value":" S "}
				         * 3] RDF Literal S with language L
				         * 		JSON: "name" : {"type":"literal", "xml:lang":" L ", "value":" S"}
				         * 4] RDF Typed Literal S with datatype URI D
				         * 		JSON: "name" : {"type":"typed-literal", "datatype":" D ", "value":" S "}
				         * 5] Blank Node label I
				         * 		JSON: "name" : {"type":"bnode", "value":" I "} 
				         */
				        if("value".equals(key)){
				        	resultElement.put(currVar, value);		//Add current key, value pair to the map
				        }
					}
			        helper.assertToken(JsonToken.END_OBJECT, jp.getCurrentToken()); 		// 'ith variable' object in the result
				}
				
				helper.assertToken(JsonToken.END_OBJECT, jp.nextToken()); 
				jp.nextToken();
				resultSet.add(resultElement);		//Add the current MapElement to the resultSet List
			}       
	        helper.assertToken(JsonToken.END_ARRAY, jp.getCurrentToken()); 		// 'bindings' array
	        
			helper.assertToken(JsonToken.END_OBJECT, jp.nextToken()); 		// 'results' object
			
	        helper.assertToken(JsonToken.END_OBJECT, jp.nextToken()); 		// main object

	        jp.close(); 			// ensure resources get cleaned up timely and properly
		}catch(FileNotFoundException nfe){
			nfe.getMessage();
			nfe.printStackTrace();
		}catch(JsonParseException je){
			je.getMessage();
			je.printStackTrace();
		}catch(IOException ioe){
			ioe.getMessage();
			ioe.printStackTrace();
		}
	}
	
	/*
    /**********************************************************
    /* Additional assertion methods
    /**********************************************************
     */

    protected void assertToken(JsonToken expToken, JsonToken actToken)
    		throws JsonParseException
    {
        if (actToken != expToken) {
            fail("Expected token "+expToken+", current token "+actToken);
        }
    }

    protected void assertToken(JsonToken expToken, JsonParser jp)
    		throws JsonParseException
    {
        assertToken(expToken, jp.getCurrentToken());
    }

    protected void assertType(Object ob, Class<?> expType)
    		throws JsonParseException
    {
        if (ob == null) {
            fail("Expected an object of type "+expType.getName()+", got null");
        }
        Class<?> cls = ob.getClass();
        if (!expType.isAssignableFrom(cls)) {
            fail("Expected type "+expType.getName()+", got "+cls.getName());
        }
    }
    
//    private void verifyIntToken(JsonToken t, boolean requireNumbers)
//    		throws JsonParseException
//    {
//        if (t == JsonToken.VALUE_NUMBER_INT) {
//            return;
//        }
//        if (requireNumbers) { // to get error
//            assertToken(JsonToken.VALUE_NUMBER_INT, t);
//        }
//        // if not number, must be String
//        if (t != JsonToken.VALUE_STRING) {
//            fail("Expected INT or STRING value, got "+t);
//        }
//    }
    
    protected void verifyFieldName(JsonParser jp, String expName)
    		throws JsonParseException, IOException
	{
    	if(!expName.equals(jp.getText()) || !expName.equals(jp.getCurrentName()))
    		fail("Missmatched field name. Expected "+ expName +", got "+ jp.getText());
	}
    
    /**
     * Method that gets textual contents of the current token using
     * available methods, and ensures results are consistent, before
     * returning them
     */
    protected String getAndVerifyText(JsonParser jp)
        throws IOException, JsonParseException
    {
        // Ok, let's verify other accessors
        int actLen = jp.getTextLength();
        char[] ch = jp.getTextCharacters();
        String str2 = new String(ch, jp.getTextOffset(), actLen);
        String str = jp.getText();

        if (str.length() !=  actLen) {
            fail("Internal problem (jp.token == "+jp.getCurrentToken()+"): jp.getText().length() ['"+str+"'] == "+str.length()+"; jp.getTextLength() == "+actLen);
        }
        if(!str.equals(str2)){
        	fail("String access via getText(), getTextXxx() must be the same");
        }
        return str;
    }
    
    protected void fail(String msg) throws JsonParseException{
    	System.out.println(msg);
    	//System.exit(-1);
    	throw new JsonParseException(msg, null);
    }
}
