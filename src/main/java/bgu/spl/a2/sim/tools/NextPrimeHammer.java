package bgu.spl.a2.sim.tools;

import java.math.BigInteger;

public class NextPrimeHammer extends ToolApplier {

    @Override
    long toolLogic(long id) {
        BigInteger bigId = BigInteger.valueOf(id);
        return bigId.nextProbablePrime().longValue();
    }

    @Override
    public String getType() {
        return "NextPrimeHammer";
    }
}
