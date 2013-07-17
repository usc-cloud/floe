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
package edu.usc.pgroup.floe.impl.communication;

import edu.usc.pgroup.floe.api.communication.SenderChannel;
import edu.usc.pgroup.floe.api.communication.SenderChannelStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DuplicateSenderChannelStrategy implements SenderChannelStrategy {
    private Map<String, SenderChannel> channelMap;

    @Override
    public void setChannels(Map<String, SenderChannel> channels) {
        this.channelMap = channels;


    }

    @Override
    public List<SenderChannel> getNextAvailableChannel() {
        ArrayList<SenderChannel> list = new ArrayList<SenderChannel>();
        list.addAll(channelMap.values());
        return list;
    }
}
