package bgu.spl.a2.sim.tools;

import java.math.BigInteger;

public class NextPrimeHammer extends ToolApplier {

    @Override
    public String getType() {
        return "np-hammer";
    }

    @Override
    public long toolLogic(long id) {

        long v =id + 1;
        while (!isPrime(v)) {
            v++;
        }

        return v;
    }
    private boolean isPrime(long value) {
        if(value < 2) return false;
        if(value == 2) return true;
        long sq = (long) Math.sqrt(value);
        for (long i = 2; i <= sq; i++) {
            if (value % i == 0) {
                return false;
            }
        }
        return true;
    }
}
