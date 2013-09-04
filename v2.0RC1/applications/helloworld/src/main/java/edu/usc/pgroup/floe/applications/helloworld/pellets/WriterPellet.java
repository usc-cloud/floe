package edu.usc.pgroup.floe.applications.helloworld.pellets;
import java.util.Date;

import edu.usc.pgroup.floe.api.exception.LandmarkException;
import edu.usc.pgroup.floe.api.framework.pelletmodels.*;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.api.stream.FEmitter;
import edu.usc.pgroup.floe.api.stream.FIterator;

public class WriterPellet implements StreamInStreamOutPellet{

	@Override
	public void invoke(FIterator arg0, FEmitter arg1, StateObject arg2) {
		Date timeOfCreation = new Date();
		while(true)
		{
			Object data = null;
			try {
				data = arg0.next();
			} catch (Exception e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			    break;
            }
			
			
			if(data == null) {
				System.out.println("null data");
				continue;
			}
			
			String message = (String) data;
			
			message +="|WWW|";
			System.out.println("toc:"+timeOfCreation + " msg:" + message);
			arg1.emit(message);
		}
		
	}

}
