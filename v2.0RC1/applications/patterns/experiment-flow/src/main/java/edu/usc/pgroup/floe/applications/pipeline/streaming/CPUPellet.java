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
package edu.usc.pgroup.floe.applications.pipeline.streaming;

import edu.usc.pgroup.floe.api.exception.LandmarkException;
import edu.usc.pgroup.floe.api.exception.LandmarkPauseException;
import edu.usc.pgroup.floe.api.framework.pelletmodels.Pellet;
import edu.usc.pgroup.floe.api.framework.pelletmodels.StreamInStreamOutPellet;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.api.stream.FEmitter;
import edu.usc.pgroup.floe.api.stream.FIterator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CPUPellet implements StreamInStreamOutPellet {


    public static void calculatePi() {
        BigInteger firstFactorial;
        BigInteger secondFactorial;
        BigInteger firstMultiplication;
        BigInteger firstExponent;
        BigInteger secondExponent;
        int firstNumber = 1103;
        BigInteger firstAddition;
        BigDecimal currentPi = BigDecimal.ZERO;
        BigDecimal pi = BigDecimal.ONE;
        BigDecimal one = BigDecimal.ONE;
        int secondNumber = 2;
        double thirdNumber = Math.sqrt(2.0);
        int fourthNumber = 9801;
        BigDecimal prefix = BigDecimal.ONE;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        for (int i = 0; i < 500; i++) {
            firstFactorial = factorial(4 * i);
            secondFactorial = factorial(i);
            firstMultiplication = BigInteger.valueOf(26390 * i);
            firstExponent = exponent(secondFactorial, 4);
            secondExponent = exponent(BigInteger.valueOf(396), 4 * i);
            firstAddition = BigInteger.valueOf(firstNumber).add(firstMultiplication);
            currentPi = currentPi.add(new BigDecimal(firstFactorial.multiply(firstAddition)).
                    divide(new BigDecimal(firstExponent.multiply(secondExponent)), new MathContext(10000)));
            Date date = new Date();
            // System.out.println("Interation: " + i + " at " + dateFormat.format(date));
        }

        prefix = new BigDecimal(secondNumber * thirdNumber);
        prefix = prefix.divide(new BigDecimal(fourthNumber), new MathContext(1000));

        currentPi = currentPi.multiply(prefix, new MathContext(1000));

        pi = one.divide(currentPi, new MathContext(1000));

        System.out.println("Pi is: " + pi);

        return;
    }


    public static BigInteger factorial(int a) {

        BigInteger result = new BigInteger("1");
        BigInteger smallResult = new BigInteger("1");
        long x = a;
        if (x == 1) return smallResult;
        while (x > 1) {
            result = result.multiply(BigInteger.valueOf(x));

            x--;
        }
        return result;
    }

    public static BigInteger exponent(BigInteger a, int b) {
        BigInteger answer = new BigInteger("1");

        for (int i = 0; i < b; i++) {
            answer = answer.multiply(a);
        }

        return answer;
    }


    @Override
    public void invoke(FIterator fIterator, FEmitter emitter, StateObject stateObject) {
        while (true) {
            Object o = null;
            try {
                o = fIterator.next();
            } catch (LandmarkException e) {
                e.printStackTrace();
            } catch (LandmarkPauseException e) {
                e.printStackTrace();
            }
            calculatePi();
            System.out.println("CPU Pellet");
            if(o != null)
                emitter.emit(o);
        }
    }
}
