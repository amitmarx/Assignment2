package bgu.spl.a2.sim.tools;

public class ToolFactory {

    public static Tool create(String name){
        switch (name){
            case "gs-driver":
                return new GCDScrewdriver();
            case "np-hammer":
                return new NextPrimeHammer();
            default:
                return new RandomSumPliers();
        }
    }

}
