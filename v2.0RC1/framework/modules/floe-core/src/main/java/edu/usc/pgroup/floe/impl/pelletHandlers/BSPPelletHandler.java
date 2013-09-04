/*
 * Copyright 2011, University of Southern California. All Rights Reserved.
 * 
 * This software is experimental in nature and is provided on an AS-IS basis only. 
 * The University SPECIFICALLY DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT 
 * LIMITATION ANY WARRANTY AS TO MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * This software may be reproduced and used for non-commercial purposes only, 
 * so long as this copyright notice is reproduced with each such copy made.
 */
package edu.usc.pgroup.floe.impl.pelletHandlers;

import edu.usc.pgroup.floe.api.framework.pelletmodels.BSPPellet;
import edu.usc.pgroup.floe.impl.queues.BSPSinkQueue;
import edu.usc.pgroup.floe.impl.queues.SinkQueue;

public class BSPPelletHandler extends MapperPelletHandler {

    @Override
    public SinkQueue createSinkQueue() {
        return new BSPSinkQueue();
    }



    @Override
    public Class getPelletModel() {
        return BSPPellet.class;
    }
}
