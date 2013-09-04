package edu.usc.pgroup.floe.applications.iipipeline.pellets.semantic;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;

import edu.usc.pgroup.floe.api.exception.LandmarkException;
import edu.usc.pgroup.floe.api.exception.LandmarkPauseException;
import edu.usc.pgroup.floe.api.framework.pelletmodels.StreamInStreamOutPellet;
import edu.usc.pgroup.floe.api.stream.FEmitter;
import edu.usc.pgroup.floe.api.stream.FIterator;

public class InsertCounterPellet implements StreamInStreamOutPellet {

	@Override
	public void invoke(FIterator in, FEmitter out,
			edu.usc.pgroup.floe.api.state.StateObject stateObject) {

		while (true) {

			String s;
			try {
				s = (String) in.next();
				System.out.println(s);
				Client c = Client.create();
				WebResource r = c
						.resource("http://mojave-01.usc.edu/SGWeb/PerfRestService.svc/incrementCounter");
				c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS,
						true);
				r.get(String.class);
			} catch (LandmarkException | LandmarkPauseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
