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

import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;

/***
 * <class>Fiber</class> Represents a communication channel between Two Containers
 * Underlying Behaviour (i.e. Push, Pull etc ) or Transport (TCP,JMS, etc) Will be handled by the implementation
 */
public interface Fiber<T> {
    /**
     * Initialize the Transport Fiber. Call this after Connection information is provided to the channel
     */
    public void init();

    /**
     * Read Message From the channel
     * @param timeOutMills Read time out in milliseconds.
     * @return Read Message {@link Message}
     */
    public  Message<T> read(long timeOutMills);

    /**
     * Write Message To the channel
     * @param message {@link Message} to be written
     */
    public  void write(Message<T> message);

    /**
     * Get Connection Info from the channel
     * @return  {@link ConnectionInfo}
     */
    public TransportInfoBase getTransportInfo();

    /**
     * Set the Connection information that needed to initialize the channel
     * @param inpConfig  @link ConnectionInfo
     */
	void setOtherEndConnectionPort(Port port);

	public Port getOtherEndConnectionPort();

    /**
     * Close the channel
     */
    public void close();


    public void setId(String id);

    public String getId();

}
