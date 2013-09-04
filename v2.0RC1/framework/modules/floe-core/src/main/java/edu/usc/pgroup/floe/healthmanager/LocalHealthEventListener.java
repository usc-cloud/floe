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

import edu.usc.pgroup.floe.api.framework.FlakeInfo;
import edu.usc.pgroup.floe.api.framework.floegraph.Node;
import edu.usc.pgroup.floe.api.framework.healthmanager.HealthEventListener;
import edu.usc.pgroup.floe.api.framework.healthmanager.HeathInfo;
import edu.usc.pgroup.floe.impl.FloeRuntimeEnvironment;
import edu.usc.pgroup.floe.util.Logger;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class LocalHealthEventListener implements HealthEventListener {
    private ConcurrentHashMap<String, FlakeState> flakeStates = new ConcurrentHashMap<>();
    private static final int maxCoreCount = 4;

    private Action prevAction = Action.none;

    private int scaleOutInterval = Integer.MAX_VALUE / 2;
    private int sclaeDownInterval = Integer.MAX_VALUE / 2;
    private int scaleUpFailCounter = 0;
    private int scaleDownFailCounter = 0;
    private int scaleOutBackOffRatio = 1;


    private int maxCpus;
    public LocalHealthEventListener() {
        this.maxCpus = Runtime.getRuntime().availableProcessors();
    }

    @Override
    public void updateReceived(HeathInfo info) {

        FlakeState state = null;
        if (flakeStates.containsKey(info.getFlakeId())) {
            state = flakeStates.get(info.getFlakeId());
        } else {
            state = new FlakeState();
            flakeStates.put(info.getFlakeId(), state);
        }

        boolean stateChange = state.updateCurrentState(info);

        if (stateChange) {

            scaleOutInterval += FlakeState.maxStateLifeTime * 4;
            sclaeDownInterval += FlakeState.maxStateLifeTime * 4;
            switch (state.getCurrentState()) {
                case HighBuffHighCpu: {
                    sclaeDownInterval = 0;
                    scaleDownFailCounter=0;
                    Logger.getInstance().LogInfo("State,HighBufferHighCPU," + System.currentTimeMillis());
                    if (info.getLatency() != -1) {
                        int requiredCores = (int)Math.ceil(info.getLatency() * (info.getSourceQueueLength() +
                                FlakeState.maxStateLifeTime * 4 * info.getInThroughPut()) / (double) (FlakeState.maxStateLifeTime * 4 * 1000));

                        if (requiredCores - info.getCoreCount()> 0)
                            if (!increaseCoreCount(info.getFlakeId(), requiredCores - info.getCoreCount(),info)&& isHighRate(info)) {
                                scaleUpFailCounter++; //YS: we may want to check for isHighRate before we increment...especially since we do that for IO bound (see HighBuffLowCpu)
                                //CW : Done
                                if (scaleUpFailCounter >= 4*scaleOutBackOffRatio && isHighRate(info)) {

                                    if(FloeRuntimeEnvironment.getEnvironment().getContainer().getContainerInfo().
                                            getavailableResources().getNumberOfCores() >=1) {
                                        return ;
                                    }
                                    scaleOut(info.getFlakeId(),info);
                                    scaleOutInterval = 0;
                                    scaleUpFailCounter = 0;
                                    sclaeDownInterval = 0;
                                    scaleOutBackOffRatio *=2;
                                }  else {
                                    Logger.getInstance().LogAction("ScaleOutDeferred",info.getFlakeId(),new Date(),
                                            info.getCoreCount(),info.getLatency(),
                                            info.getSourceQueueLength(),info.getInThroughPut());
                                }
                            }
                    }
                    break;
                }

                case HighBuffLowCpu: {
                    sclaeDownInterval = 0;
                    scaleDownFailCounter=0;
                    //slowly scale up of prev is scale up scale out
                    //if prev is scale out and time between last scaleout less then 2T ignore
                    Logger.getInstance().LogInfo("State,HighBufferLowCPU,"+System.currentTimeMillis());
                    if (info.getCoreCount() < maxCpus / 2) {
                        if (!increaseCoreCount(info.getFlakeId(), 1, info)&&isHighRate(info)) {
                            scaleUpFailCounter++;
                            if (scaleUpFailCounter >= 4*scaleOutBackOffRatio && isHighRate(info)) {//YS: Checking isHighRate is not necessary here since we already checked 2 lines back
                                //CW : I m not doing this change to be consistent with the HighBuffHighCpu case.

                                scaleOut(info.getFlakeId(), info);
                                scaleOutInterval = 0;
                                scaleUpFailCounter = 0;
                                sclaeDownInterval = 0;
                                scaleOutBackOffRatio*=2;
                            } else {
                                Logger.getInstance().LogAction("ScaleOutDeferred",info.getFlakeId(),new Date(),
                                        info.getCoreCount(),info.getLatency(),
                                        info.getSourceQueueLength(),info.getInThroughPut());
                            }
                        }
                    } else {
                    	//YS: Why are we using scaleOutInterval rather than just use scaleUpFailCounter? Seems like an unnecessary variable with different behavior.
                        //CW : Done adding differed for Scale Out for I/O scenario.
                        scaleUpFailCounter++;
                        if (scaleUpFailCounter > 4*scaleOutBackOffRatio &&(scaleOutInterval > 4 * FlakeState.maxStateLifeTime * 4*scaleOutBackOffRatio && isHighRate(info))) {
                            scaleOut(info.getFlakeId(), info);
                            scaleOutInterval = 0;
                            scaleUpFailCounter = 0;
                            sclaeDownInterval = 0;
                            scaleOutBackOffRatio*=2;
                        }  else {
                            Logger.getInstance().LogAction("ScaleOutDeferred",info.getFlakeId(),new Date(),
                                    info.getCoreCount(),info.getLatency(),
                                    info.getSourceQueueLength(),info.getInThroughPut());
                        }
                    }

                    break;
                }

                case LowBuff: {
                    scaleOutBackOffRatio = 1;
                    scaleOutInterval = 0;
                    scaleUpFailCounter = 0; 
                    Logger.getInstance().LogInfo("State,LowBuffer,"+System.currentTimeMillis());
                    if (sclaeDownInterval > 2 * FlakeState.maxStateLifeTime * 2) {
                        if (!decreaseCoreCount(info.getFlakeId(), 1,info)) {
                            scaleDownFailCounter++;
                            if (scaleDownFailCounter >= 4 && info.getCoreCount() ==1) {
                                scaleIn(info.getFlakeId(), info);
                                sclaeDownInterval = 0;
                                scaleOutInterval = 0;
                                scaleUpFailCounter = 0;
                                scaleDownFailCounter = 0;
                            } else {
                                Logger.getInstance().LogAction("ScaleInDeferred",info.getFlakeId(),new Date(),
                                        info.getCoreCount(),info.getLatency(),
                                        info.getSourceQueueLength(),info.getInThroughPut());
                            }
                        }
                    } else {
                        Logger.getInstance().LogAction("ScaleDownDeferred",info.getFlakeId(),new Date(),
                                info.getCoreCount(),info.getLatency(),
                                info.getSourceQueueLength(),info.getInThroughPut());
                    }
                    break;
                }

                case Normal: {

                    Logger.getInstance().LogInfo("State,Normal,"+System.currentTimeMillis());
                    Logger.getInstance().LogAction("NONE", info.getFlakeId(), new Date(), -1,
                            info.getLatency(), info.getSourceQueueLength(),info.getInThroughPut());
                    sclaeDownInterval = 0;
                    scaleOutInterval = 0;
                    scaleUpFailCounter = 0;
                    scaleDownFailCounter =0;
                    scaleOutBackOffRatio =1;
                }


            }


        }
    }


    private boolean scaleOut(String flakeId,HeathInfo info) {

        System.out.println("*************SCALE OUT**************");
        Logger.getInstance().LogAction("ScaleOut",info.getFlakeId(),new Date(),1,
                info.getLatency(),info.getSourceQueueLength(),info.getInThroughPut());
        Node.ResourceInfo resourceInfo = new Node.ResourceInfo();
        resourceInfo.setNumberOfCores(1);
        return FloeRuntimeEnvironment.getEnvironment().getContainer().scaleOut(flakeId, resourceInfo);
    }

    private boolean scaleIn(String flakeId, HeathInfo info) {
        System.out.println("*************SCALE IN**************");
        Logger.getInstance().LogAction("ScaleIn",info.getFlakeId(),new Date(),info.getCoreCount(),
                info.getLatency(),info.getSourceQueueLength(),info.getInThroughPut());
      //  return FloeRuntimeEnvironment.getEnvironment().getContainer().scaleIn(flakeId);
        return false;
    }

    private boolean increasePelletCount(String flakeId, int count,
                                        HeathInfo info) {
        System.out.println("*************SCALE UP PELLET COUNT**************");
        Logger.getInstance().LogAction("PelletCountUp",info.getFlakeId(),new Date(),count,
                info.getLatency(),info.getSourceQueueLength(),info.getInThroughPut());
        FlakeInfo flakeInfo = getFlakeInfo(flakeId);
        return FloeRuntimeEnvironment.getEnvironment().getContainer().
                updatePalletCount(flakeId, flakeInfo.getPelletCount() + count);
                //YS: this does not return false unless a lower bound check fails (0)

    }

    private boolean increaseCoreCount(String flakeId, int coreCount, HeathInfo info) {
        System.out.println("*************SCALE UP**************");
        Logger.getInstance().LogAction("ScaleUp",info.getFlakeId(),new Date(),coreCount,
                info.getLatency(),info.getSourceQueueLength(),info.getInThroughPut());
        FlakeInfo flakeInfo = getFlakeInfo(flakeId);
        Node.ResourceInfo resourceInfo = new Node.ResourceInfo();
        resourceInfo.setNumberOfCores(coreCount);
        boolean val = FloeRuntimeEnvironment.getEnvironment().getContainer().
                updateFlakeResources(flakeId, resourceInfo) ;
        //YS: The pellet counts should only increase by the number of cores successfully increased, not coreCount. Otherwise, you could end up with pelletCounts that are in the hundreds.
        //YS: So if val==true, pelletCount=coreCount * 4; else pelletCount is smaller
        int currentPelletCount =  flakeInfo.getPelletCount();
        int currentCoreCount = flakeInfo.getResourceInfo().getNumberOfCores();


        boolean val2 = increasePelletCount(flakeId, currentCoreCount*4 - currentPelletCount, info);
        return val&&val2;

    }

    private boolean decreaseCoreCount(String flakeId, int coreCount , HeathInfo info) {
        System.out.println("*************SCALE DOWN**************");
        Logger.getInstance().LogAction("ScaleDown",info.getFlakeId(),new Date(),coreCount,
                info.getLatency(),info.getSourceQueueLength(),info.getInThroughPut());
        FlakeInfo flakeInfo = getFlakeInfo(flakeId);
        if (flakeInfo.getResourceInfo().getNumberOfCores() > 1) {
            Node.ResourceInfo resourceInfo = new Node.ResourceInfo();
            resourceInfo.setNumberOfCores(-1 * coreCount);
            boolean return_ = FloeRuntimeEnvironment.getEnvironment().getContainer().
                    updateFlakeResources(flakeId, resourceInfo) && decreasePelletCount(flakeId, coreCount*4,info);
            if(return_) {
                sclaeDownInterval = 0;
            }
            return return_;
        }

        return false;
    }

    private boolean decreasePelletCount(String flakeId, int pelletCount , HeathInfo info) {
        System.out.println("*************SCALE DOWN PELLET COUNT**************");
        Logger.getInstance().LogAction("PelletCountDown",info.getFlakeId(),new Date(),pelletCount,
                info.getLatency(),info.getSourceQueueLength(),info.getInThroughPut());
        FlakeInfo flakeInfo = getFlakeInfo(flakeId);
        if (flakeInfo.getPelletCount() > 1) {
            return FloeRuntimeEnvironment.getEnvironment().getContainer().
                    updatePalletCount(flakeId, (flakeInfo.getPelletCount() - pelletCount) > 1 ?
                            (flakeInfo.getPelletCount() - pelletCount) : 1 );
        }

        return false;
    }

    private FlakeInfo getFlakeInfo(String flakeId) {
        for (FlakeInfo flake : FloeRuntimeEnvironment.getEnvironment().getContainer().listFlakes()) {
            if (flakeId.equals(flake.getflakeId())) {
                return flake;
            }
        }
        return null;
    }

    private enum Action {
        scale_up,
        scale_down,
        scale_out,
        scale_in,
        none
    }

    private boolean isHighRate(HeathInfo info) {
        if(info.getLatency() >0 && info.getInThroughPut() > 0 ) {
            double theta = info.getLatency()*info.getInThroughPut()*2*FlakeState.maxStateLifeTime/1000; //YS: Should we not be dividing by 2 instead of multiplying by 2? This may be a big bug causing rapid scale-out!
            if(theta > 4*FlakeState.maxStateLifeTime) {
                return true;
            }
        }

       return false;
    }
}
