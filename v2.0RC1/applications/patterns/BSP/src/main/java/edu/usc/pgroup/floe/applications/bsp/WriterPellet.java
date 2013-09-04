package edu.usc.pgroup.floe.applications.bsp;
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
			
			BSPMessage message = (BSPMessage)data;

			byte[] a= message.getData();
	        if(a==null) {
	            a = new String("|WWW|").getBytes();
	        } else {
	            a = (new String(a) + "|WWW|").getBytes();
	        }
	        message.setData(a);
			
			
			System.out.println("toc:"+timeOfCreation + " **MSG**:" + new String(message.getData()));
			arg1.emit(message);
		}
		
	}

}
