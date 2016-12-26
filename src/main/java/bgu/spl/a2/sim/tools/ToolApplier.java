package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.util.Random;
import java.util.function.Function;

public abstract class ToolApplier implements Tool {
    @Override
    public long useOn(Product p) {
        long value=0;
        for(Product part : p.getParts()){
            value+=Math.abs(toolLogic(part.getFinalId()));

        }
        return value;
    }

    abstract long toolLogic(long id);
}