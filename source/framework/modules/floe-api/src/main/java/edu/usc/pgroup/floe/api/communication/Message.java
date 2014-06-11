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
package edu.usc.pgroup.floe.api.communication;

import java.util.Map;

/***
 * This is a wrapper for the user defined input and output messages that flow on channels
 */
public interface Message<T> {

    public String getTag();
    public Long getTimeStampMilliSecs();
    public void setTag(String t);
    public void setTimeStampMilliSecs(Long ts);
    public String getKey();
    public void setKey(String key);
    public void setLandMark(boolean landmark);
    public boolean getLandMark();


    public T getPayload();
    public void putPayload(T payload);

    /**
     * Set a Message Context property
     * @param name property Name
     * @param value propertyValue
     */
    public void setProperty(String name,Object value);

    /**
     * Get Message context properties
     * @return get All the message context properties
     */
    public Map<String,Object> getProperties();

    /**
     * Get a message context property with a given name
     * @param name property name
     * @return Property value associated with the given name
     */
    public Object getProperty(String name);

}
