package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Deferred;
import bgu.spl.a2.Logger;
import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ManufactureTask extends Task<Product>{

    ManufactoringPlan plan;
    Warehouse wareHouse;
    List<Product> parts;
    long startId;
    public ManufactureTask(ManufactoringPlan plan, Warehouse wareHouse,long startId) {
        this.plan = plan;
        this.wareHouse = wareHouse;
        this.parts = new ArrayList<>();
        this.startId = startId;
    }

    @Override
    protected void start() {
        ManafacutreParts(()->useTools());

    }
    private void ManafacutreParts(Runnable callback){
        List<ManufactureTask> manufacturePartsTasks = new ArrayList<>();

        for(int i=0;i<plan.getParts().length;i++){
            manufacturePartsTasks.add(
                    new ManufactureTask(wareHouse.getPlan(plan.getParts()[i]),wareHouse,startId+1)
            );
            this.spawn(manufacturePartsTasks.get(i));
        }
        this.whenResolved(manufacturePartsTasks,()->{
            for(ManufactureTask manufactureTask : manufacturePartsTasks){
                parts.add(manufactureTask.getResult().get());
            }
            Logger.Log("All parts for "+ plan.getProductName()+ " were manufactured.");
            callback.run();
        });
    }
    private void useTools(){
        Product finalProduct = new Product(startId,plan.getProductName(),parts);
        if(plan.getTools().length==0){
            finalProduct.setFinalId(startId);
            this.complete(finalProduct);
            return;
        }

        AtomicInteger counter = new AtomicInteger(0);
        AtomicLong finalId = new AtomicLong(startId);

        for(String toolName: plan.getTools()){
            Deferred<Tool> tool = wareHouse.acquireTool(toolName);
            tool.whenResolved(()->
                    {
                        counter.incrementAndGet();
                        finalId.addAndGet(tool.get().useOn(finalProduct));
                        spawn(new ReleaseToolTask(wareHouse, tool.get()));

                        if(counter.get()==plan.getTools().length){
                            finalProduct.setFinalId(finalId.get());
                            this.complete(finalProduct);
                        }
                    }
            );
        }

    }
}
