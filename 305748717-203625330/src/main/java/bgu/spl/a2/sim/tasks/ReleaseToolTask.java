package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.tools.Tool;

public class ReleaseToolTask extends Task<Product> {

    Warehouse warehouse;
    Tool tool;
    public ReleaseToolTask(Warehouse warehouse, Tool t) {
        this.warehouse = warehouse;
        this.tool = t;
    }

    @Override
    protected void start() {
        warehouse.releaseTool(tool);
    }
}
