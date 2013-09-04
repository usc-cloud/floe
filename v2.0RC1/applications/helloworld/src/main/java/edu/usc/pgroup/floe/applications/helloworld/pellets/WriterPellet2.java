package edu.usc.pgroup.floe.applications.helloworld.pellets;
import edu.usc.pgroup.floe.api.exception.LandmarkException;
import edu.usc.pgroup.floe.api.framework.pelletmodels.*;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.api.stream.FEmitter;
import edu.usc.pgroup.floe.api.stream.FIterator;

public class WriterPellet2 implements StreamInStreamOutPellet{

	@Override
	public void invoke(FIterator arg0, FEmitter arg1, StateObject arg2) {
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
			
			message +="|W2W|";
			System.out.println(message);
			arg1.emit(message);
		}
		
	}

}
