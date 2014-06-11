package edu.usc.pgroup.floe.api.framework.pelletmodels;

import edu.usc.pgroup.floe.api.state.StateObject;

import java.util.Map;

public interface TupleInTupleOutPellet {
	public Map<String, Object> invoke(Map<String, Object> in, StateObject stateObject);
}
