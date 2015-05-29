package com.experiment.sightwords.util;

import java.util.Random;

/**
 * Created by amciver on 2/21/15.
 */
public class Randomizer {

    private static Random mRandom = new Random();

    public static int getRandomNumber(int max){
       return mRandom.nextInt(max);
    }

    private Randomizer(){

    }
}
