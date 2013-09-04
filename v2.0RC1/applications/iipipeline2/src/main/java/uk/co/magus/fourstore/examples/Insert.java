/**
 * Copyright (c) 2009, Magus Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package uk.co.magus.fourstore.examples;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

import uk.co.magus.fourstore.client.Store;

public class Insert {

	//private static String STORE_URL = "http://128.125.124.67:8000";		//majave-04 VM
	private static String STORE_URL = "http://128.125.224.121:8000";		//Losangeles VM
	
	public static String COUNTER = "9";
	
	private static String testData = "testData"+ System.currentTimeMillis();
    private static String sparql1 = "PREFIX Sensor: <http://cei.usc.edu/Equipment.owl#>" + 
			"INSERT DATA {   " +
			" <http://www.cei.usc.edu/SmartGrid.owl#test1> Sensor:test2 \""+ testData +"_1\" ." +
			" <http://www.cei.usc.edu/SmartGrid.owl#test1> Sensor:test2 \""+ testData +"_2\" ." +
			" <http://www.cei.usc.edu/SmartGrid.owl#test1> Sensor:test2 \""+ testData +"_3\" ." +
			" <http://www.cei.usc.edu/SmartGrid.owl#test1> Sensor:test2 \""+ testData +"_4\" ." +
			"			  }";
    private static String sparql2 = "PREFIX Sensor: <http://cei.usc.edu/Equipment.owl#>" + 
    		" SELECT * " +
    		" WHERE {" +
    		"      ?contributor Sensor:test2 ?temp ." +							 
    		"      }";
	
	/**
	 * @author Dan Hanley - dan.hanley @ magus.co.uk
     *
	 */
	public static void main(String[] args) {

		Store store;
		try {
			System.out.println("Start");
			
			//URL of the machine where the httpd is running for the graph in which you want to insert a triple
			store = new Store("http://localhost:8000");		
			
			String response1 = store.insert(sparql1);
            //System.out.println("Done ... INSERT Response time = "+ (System.currentTimeMillis() - time1) +"ms");
            System.out.println(response1);
            
            //read back the inserted triple
            String response2 = store.query(sparql2);
            System.out.println("SELECT Query Result: \n"+ response2);
            
			System.out.println("Done");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static String getUUID() throws InterruptedException{
		
		return UUID.randomUUID().toString();
		
//		Thread.currentThread().sleep(10);
//		return Long.toString(System.currentTimeMillis());
	}

}
