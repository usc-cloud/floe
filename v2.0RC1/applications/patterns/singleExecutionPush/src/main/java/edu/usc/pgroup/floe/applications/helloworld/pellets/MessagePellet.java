package edu.usc.pgroup.floe.applications.helloworld.pellets;
import edu.usc.pgroup.floe.api.framework.pelletmodels.*;
import edu.usc.pgroup.floe.api.state.StateObject;

public class MessagePellet implements Pellet{

	@Override
	public Object invoke(Object arg0, StateObject arg1) {
		
		String message = (String) arg0;
		
		message += "Hi " + arg0;
		System.out.println(message);		
		return message;
	}

}
