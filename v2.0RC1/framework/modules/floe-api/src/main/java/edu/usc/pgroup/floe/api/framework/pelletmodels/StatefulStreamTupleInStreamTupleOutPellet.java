package edu.usc.pgroup.floe.api.framework.pelletmodels;

import edu.usc.pgroup.floe.api.state.FState;
import edu.usc.pgroup.floe.api.stream.FEmitter;
import edu.usc.pgroup.floe.api.stream.FIterator;

import java.util.Map;

public interface StatefulStreamTupleInStreamTupleOutPellet {
	public void invoke(FIterator<Map<String, Object>> in, FEmitter<Map<String, Object>> out, FState s);
}
