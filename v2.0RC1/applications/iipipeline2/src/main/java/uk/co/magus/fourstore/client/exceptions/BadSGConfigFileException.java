package uk.co.magus.fourstore.client.exceptions;

public class BadSGConfigFileException extends Exception {
	
	public BadSGConfigFileException(){
		
	}
	
	public BadSGConfigFileException(String msg){
		super(msg);
	}
	
	 public String getMessage(String s){
		 return ("Bad smartgrid config file: "+ s);
	 }
	 
	 public String getMessage(){
		 return ("Bad smartgrid config file");
	 }
}
