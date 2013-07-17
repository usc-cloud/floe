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

public class MessageFactory {

    public static final String DEFAULT_MESSAGE_IMPL = "edu.usc.pgroup.floe.impl.communication.MessageImpl";

    public static Message createMessage(String clzz) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException {

        Class msgCls = Class.forName(clzz);
        Object msg = msgCls.newInstance();
        if (msg instanceof Message) {
            return (Message) msg;
        } else {
            throw new IllegalArgumentException("Class " + clzz + " is not a valid Message implementation");
        }

    }

    public static Message createDefaultMessage() throws IllegalAccessException,
            InstantiationException, ClassNotFoundException {
        Class msgCls = Class.forName(DEFAULT_MESSAGE_IMPL);
        return (Message) msgCls.newInstance();

    }
}
