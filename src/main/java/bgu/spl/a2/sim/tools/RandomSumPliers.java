package bgu.spl.a2.sim.tools;


import bgu.spl.a2.sim.Product;

import java.util.Random;

public class RandomSumPliers extends ToolApplier {


    @Override
    long toolLogic(long id) {
        long returnVal=0;
        Random rnd = new Random(id);
        for(int i=0; i<id%10000; i++){
            returnVal+=rnd.nextInt();
        }
        return returnVal;
    }

    @Override
    public String getType() {
        return "RandomSumPliers";
    }
}
