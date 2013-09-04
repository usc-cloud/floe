package edu.usc.pgroup.floe.applications.helloworld.pellets;
import edu.usc.pgroup.floe.api.exception.LandmarkException;
import edu.usc.pgroup.floe.api.framework.pelletmodels.*;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.api.stream.FEmitter;
import edu.usc.pgroup.floe.api.stream.FIterator;

public class WriterPellet implements StreamInStreamOutPellet{

	@Override
	public void invoke(FIterator arg0, FEmitter arg1, StateObject arg2) {
		while(true)
		{
			Object data = null;
			try {
				data = arg0.next();
			} catch (LandmarkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if(data == null) {
				System.out.println("null data");
				continue;
			}
			
			String message = (String) data;
			
			message += "processed message : '" + message +"'";
			System.out.println(message);
			arg1.emit(arg0);
		}
		
	}

}
