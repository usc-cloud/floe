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

public class SenderFactory {
    public static final String DEFAULT_SENDER = "edu.usc.pgroup.floe.impl.communication.SenderImpl";
    public static final String DEFAULT_SENDER_STRATEGY = "edu.usc.pgroup.floe.impl.communication.DuplicateSenderChannelStrategy";
    public static Sender createSender(String clazz,String strategy) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException {


        Class senderCls = Class.forName(clazz);
        Object sender = senderCls.newInstance();
        if (sender instanceof Sender) {
            Sender s = (Sender)sender;
            SenderChannelStrategy str  = null;
            Class stratergy = Class.forName(strategy == null ? DEFAULT_SENDER_STRATEGY : strategy);
            str = (SenderChannelStrategy)stratergy.newInstance();
            s.setSenderStrategy(str);
            return s;
        } else {
            throw new IllegalArgumentException("Class " + clazz + " is not a valid sender implementation");
        }
    }

    public static Sender createDefaultSender() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class senderCls = Class.forName(DEFAULT_SENDER);
        Object sender = senderCls.newInstance();
        Sender s = (Sender)sender;
        SenderChannelStrategy str  = null;
        Class stratergy = Class.forName(DEFAULT_SENDER_STRATEGY);
        str = (SenderChannelStrategy)stratergy.newInstance();
        s.setSenderStrategy(str);
        return (Sender) sender;
    }

}
