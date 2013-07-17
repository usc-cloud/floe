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

import edu.usc.pgroup.floe.api.framework.healthmanager.HeathInfo;

public class FlakeState {


    public static final double HighCPUThreshold = 0.90;
    public static final double LowCPUThreshold = 0.10;



    public static final int maxStateLifeTime = 2;

    public enum EFlakeState {
        Normal,
        HighBuffHighCpu,
        HighBuffLowCpu,
        LowBuff
    }


    private EFlakeState currentState;

    private EFlakeState prevState;

    private EFlakeState tentativeState;

    public int stateLifeTime;

    public FlakeState() {
        currentState = EFlakeState.Normal;
        prevState = EFlakeState.Normal;
        stateLifeTime = 0;

    }

    public boolean updateCurrentState(HeathInfo heathInfo) {

        if (heathInfo == null) {
            return false;
        }

        double cpu = heathInfo.getCpuUsage();

        if ((cpu >= getHighCPUThreshold(heathInfo)) && isHighBuffer(heathInfo)) {
//YS: Do we need such complex logic for maintaining prev/tentative state? Can't we just maintain the latest state? Do we need to wait for the state to be maitained for 2 cycles before we use it? This has a problem of using a very old state as current state if there was no stability in any state for 2 cycles after that. Alternatively, we could maintain a list of last 'n' states as a moving window. 
            if (tentativeState == EFlakeState.HighBuffHighCpu) {
                if (stateLifeTime < maxStateLifeTime) {
                    stateLifeTime++;
                    return false;
                } else {
                    stateLifeTime = 0;
                    prevState = currentState;
                    currentState = tentativeState;
                    return true;
                }
            } else {
                tentativeState = EFlakeState.HighBuffHighCpu;
                stateLifeTime = 0;
                return false;
            }

        } else if (cpu <=getHighCPUThreshold(heathInfo) && isHighBuffer(heathInfo)) {

//            ******************HIGHBUFF_LOWCPU*************** cpu : 1.149173799E-314 coreCount : 1 Threshold : 0.1

            System.out.println("******************HIGHBUFF_LOWCPU*************** cpu : " + cpu +
                    " coreCount : " + heathInfo.getCoreCount() + " Threshold : " + getLowCPUThreshold(heathInfo));
            if (tentativeState == EFlakeState.HighBuffLowCpu) {
                if (stateLifeTime < maxStateLifeTime) {
                    stateLifeTime++;
                    return false;
                } else {
                    stateLifeTime = 0;
                    prevState = currentState;
                    currentState = tentativeState;
                    return true;
                }
            } else {
                tentativeState = EFlakeState.HighBuffLowCpu;
                stateLifeTime = 0;
                return false;
            }

        } else if (isLowBuffer(heathInfo)) {

            if (tentativeState == EFlakeState.LowBuff) {
                if(stateLifeTime < maxStateLifeTime) {
                    stateLifeTime++;
                    return false;
                } else {
                    stateLifeTime = 0;
                    prevState = currentState;
                    currentState = tentativeState;
                    return true;
                }
            } else {
                tentativeState = EFlakeState.LowBuff;
                stateLifeTime = 0;
                return false;
            }
        } else {
            if (tentativeState == EFlakeState.Normal) {
                if(stateLifeTime < maxStateLifeTime) {
                    stateLifeTime++;
                    return false;
                } else {
                    stateLifeTime = 0;
                    prevState = currentState;
                    currentState = tentativeState;
                    return true;
                }
            } else {
                tentativeState = EFlakeState.Normal;
                stateLifeTime = 0;
                return false;
            }
        }

    }

    public EFlakeState getCurrentState() {
        return currentState;
    }

    public EFlakeState getPrevState() {
        return prevState;
    }

    public EFlakeState getTentativeState() {
        return tentativeState;
    }

    public int getStateLifeTime() {
        return stateLifeTime;
    }


    private double getCycleQueueLength() {
        return 0.0;
    }

    private double getQueueLatency(HeathInfo heathInfo) {
        if(heathInfo.getLatency() >= 0 &&
                 heathInfo.getSourceQueueLength() >=0 && heathInfo.getInThroughPut()>=0) {

            return heathInfo.getLatency()* (heathInfo.getSourceQueueLength() +
                    4*maxStateLifeTime*heathInfo.getInThroughPut())/1000.0;

        } else {
            return 0.0;
        }
    }

    private boolean isHighBuffer(HeathInfo healthInfo) {

        double queueLatency = getQueueLatency(healthInfo);
        double perCoreQueueLatency = queueLatency/healthInfo.getCoreCount();

        if(4*maxStateLifeTime*1.15 < perCoreQueueLatency) {
            return true;
        }else {
            return false;
        }


    }

    private boolean isLowBuffer(HeathInfo healthInfo) {

        double queueLatency = getQueueLatency(healthInfo);

        int coreCount = healthInfo.getCoreCount();
        double thetaM = 0;
        if(coreCount == 1) {
            thetaM = queueLatency;
        } else {
            thetaM = queueLatency/(coreCount -1);
        }
        if(coreCount == 1) {
            if(0.45*4*maxStateLifeTime > thetaM) {
                return true;
            } else {
                return false;
            }
        }

        if(4*maxStateLifeTime > thetaM) {
            return true;
        }else {
            return false;
        }

    }

    private double getHighCPUThreshold(HeathInfo info) {
        return HighCPUThreshold*info.getCoreCount();
    }

    private double getLowCPUThreshold(HeathInfo info) {
        return LowCPUThreshold*info.getCoreCount();
    }

}
