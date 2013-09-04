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
package edu.usc.pgroup.floe.applications.bsp;

import java.util.HashMap;
import java.util.Map;

public class SimpleBSPProcessorImpl implements BSPProcessor {


    String keys[] = {"p1", "p2"};

    int count = 0;


    public Map<String, Object> compute(Map<String, Object> value) {
        Map<String, Object> r = new HashMap<String, Object>();

        if (count < 5) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < 5000; i++) {
                r.put(keys[i % 2], "TEST" + i + " count : " + count);
            }
            System.out.println("Computing...");
        } else {
            return null;
        }

        return r;

    }
}
