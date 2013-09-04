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
package edu.usc.pgroup.floe.util;

import edu.usc.pgroup.floe.api.framework.Container;
import edu.usc.pgroup.floe.api.framework.healthmanager.HeathInfo;
import edu.usc.pgroup.floe.impl.FloeRuntimeEnvironment;
import edu.usc.pgroup.floe.impl.ContainerImpl;
import edu.usc.pgroup.floe.impl.FlakeImpl;
import edu.usc.pgroup.floe.impl.communication.WeightInfo;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.util.Date;


public class ContainerMonitor implements Runnable {

    private Container container;

    private FlakeImpl flake;

    private Logger logger;

    private  Sigar sigar;

    private boolean messageStarted;

    private boolean start = false;



    public ContainerMonitor(Container container, FlakeImpl flake) {
        this.container = container;
        this.flake = flake;
        this.logger = Logger.getInstance();
        this.sigar = new Sigar();
    }


    private void logInfo(double cpu) {

        logger.LogInfo("ContainerHealth", container.getContainerInfo().getContainerId(),
                flake.getFlakeId(), new Date(), flake.getFlakeInfo().getLatency(),
                cpu, flake.getSourceQueue().getSize(), flake.getSinkQueue().getSize(),
                flake.getResources().getNumberOfCores(), flake.getFlakeInfo().getPelletCount(),
                flake.getFlakeInfo().getInThroughput(),
                flake.getFlakeInfo().getOutputThroughput());
    }

    private double getCpu() {
        synchronized (FloeRuntimeEnvironment.getEnvironment().getContainer()) {
            long pid = sigar.getPid();
            ProcCpu cpu = null;
            try {
                cpu = sigar.getProcCpu("" + pid);
            } catch (SigarException e) {
                throw new RuntimeException("Error while getting processor info :", e);
            }

            return cpu.getPercent();
        }

    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();




        while (!start) {

            try {
                double cpu = getCpu();
                logInfo(cpu);
                HeathInfo info = new HeathInfo(flake.getFlakeId(), cpu, flake.getFlakeInfo().getLatency(),
                        flake.getSourceQueue().getSize(), flake.getSinkQueue().getSize(),
                        flake.getResources().getNumberOfCores(), flake.getFlakeInfo().getInThroughput());
//                    ((ContainerImpl)FloeRuntimeEnvironment.getEnvironment().getContainer()).getHealthEventManager().

                if (messageStarted) {
                    ((ContainerImpl) FloeRuntimeEnvironment.getEnvironment().getContainer()).getHealthEventManager().
                            handleHealthUpdate(info);

                    WeightInfo weightInfo = new WeightInfo();
                    weightInfo.setBufferLength(flake.getSourceQueue().getSize());
                    weightInfo.setFlakeId(flake.getFlakeId());
                    weightInfo.setNodeId(flake.getNodeId());
                    weightInfo.setLatency(flake.getFlakeInfo().getLatency());
                    weightInfo.setPelletCount(flake.getFlakeInfo().getPelletCount());
                    flake.getCommunicationManager().updateWeightInfo(flake.getFlakeInfo(), weightInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {

            }
        }
    }

    public void stop() {
        start = false;
    }

    public void start() {
        start = true;
        new Thread(this).start();
    }

    public boolean isMessageStarted() {
        return messageStarted;
    }

    public void setMessageStarted(boolean messageStarted) {
        this.messageStarted = messageStarted;
    }
}
