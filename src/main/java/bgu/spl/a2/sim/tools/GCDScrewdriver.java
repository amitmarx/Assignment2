package bgu.spl.a2.sim.tools;

import java.math.BigDecimal;
import java.math.BigInteger;

public class GCDScrewdriver extends ToolApplier {

    private static long reverse(long l){
        long returnVal=0;
        while (l>0){
            returnVal*=10;
            returnVal += l%10;
            l/=10;
        }
        return returnVal;
    }

    @Override
    long toolLogic(long id) {
        BigInteger bigId = BigInteger.valueOf(id);
        BigInteger bigReversedId = BigInteger.valueOf(reverse(id));

        return bigId.gcd(bigReversedId).longValue();
    }

    @Override
    public String getType() {
        return "gs-driver";
    }


}
