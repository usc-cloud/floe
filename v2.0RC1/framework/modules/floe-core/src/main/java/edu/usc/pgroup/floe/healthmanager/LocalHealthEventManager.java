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
package edu.usc.pgroup.floe.healthmanager;

import edu.usc.pgroup.floe.api.framework.healthmanager.HealthEventListener;
import edu.usc.pgroup.floe.api.framework.healthmanager.HealthEventManager;
import edu.usc.pgroup.floe.api.framework.healthmanager.HeathInfo;

import java.util.ArrayList;
import java.util.List;

public class LocalHealthEventManager implements HealthEventManager {


    private List<HealthEventListener> subscriptionList = new ArrayList<>();

    @Override
    public void handleHealthUpdate(HeathInfo heathInfo) {
        for(HealthEventListener listener : subscriptionList) {
            listener.updateReceived(heathInfo);
        }
    }

    @Override
    public void registerListener(HealthEventListener listener) {
        if (listener != null) {
            subscriptionList.add(listener);
        }
    }

    @Override
    public void unregisterListener(HealthEventListener listener) {
        if(listener != null) {
            if(subscriptionList.contains(listener)) {
                subscriptionList.remove(listener);
            }
        }
    }

}
