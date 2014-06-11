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
package edu.usc.pgroup.floe.api.framework;


/**
 * Deployer take care of updating the pallet implementation in case of an update.
 * Deployer can have many implementations based on the deployment Type. (ex : .jar deployer )
 */
public interface Deployer<T> {

    public void init(Container container);

    public void onUpdate(T artifact);

    public void onCreate(T artifact);

    public void onDelete(T artifact);

    public void addFake(String palletType,String flakeId);

    public void removeFlake(String palletType);
}
