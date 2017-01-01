/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import bgu.spl.a2.Deferred;
import bgu.spl.a2.Logger;
import bgu.spl.a2.VersionMonitor;
import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.ConfigOrder;
import bgu.spl.a2.sim.conf.ConfigTool;
import bgu.spl.a2.sim.conf.JsonConfiguration;
import bgu.spl.a2.sim.tools.ToolFactory;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
    private static WorkStealingThreadPool workStealingThreadPool;
    private static JsonConfiguration config;
    private static Warehouse warehouse;

    /**
     * Begin the simulation
     * Should not be called before attachWorkStealingThreadPool()
     */
    public static ConcurrentLinkedQueue<Product> start() {
        ConcurrentLinkedQueue<Product> result = new ConcurrentLinkedQueue<>();
        warehouse = new Warehouse(workStealingThreadPool);
        buildAllTools();
        warehouse.setPlans(config.plans);
        for (List<ConfigOrder> wave : config.waves) {
            handleWave(wave, result);
        }
        return result;
    }

    private static void handleWave(List<ConfigOrder> wave, ConcurrentLinkedQueue<Product> result) {
        int totalQuantities = getTotalQuantities(wave);
        VersionMonitor counter = new VersionMonitor();
        ConcurrentLinkedQueue<Deferred<Product>> deferredProducts = new ConcurrentLinkedQueue<>();

        for (ConfigOrder order : wave) {
            warehouse.setStartIds(order.getProductName(), order.getStartId());
            for (int i = 0; i < order.getQty(); i++) {
                Deferred<Product> product = warehouse.Manufacture(order.getProductName());
                deferredProducts.add(product);
                product.whenResolved(() -> {
                            counter.inc();
                        }
                );
            }
        }
        try {
            counter.await(totalQuantities - 1);
            for (Deferred<Product> deferred : deferredProducts) {
                result.add(deferred.get());
            }
        } catch (Exception e) {
            //TODO think about it might need to re-throw
        }
    }

    private static int getTotalQuantities(List<ConfigOrder> wave) {
        int result = 0;
        for (ConfigOrder order : wave) {
            result += order.getQty();
        }
        return result;
    }

    private static void buildAllTools() {
        for (ConfigTool tool : config.tools) {
            warehouse.addTool(
                    ToolFactory.create(tool.getName()),
                    tool.getQty()
            );
        }
    }

    private static void shutdown() {
        try {
            workStealingThreadPool.shutdown();
        } catch (Exception e) {
            Logger.Log(e.toString());
        }

    }

    /**
     * attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
     *
     * @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
     */
    public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool) {
        workStealingThreadPool = myWorkStealingThreadPool;
    }

    public static void main(String[] args) {
        setConfig(args[0]);
        attachWorkStealingThreadPool(
                new WorkStealingThreadPool(config.threads)
        );
        ConcurrentLinkedQueue<Product> products = start();
        writeProductsToFile("result.ser", products);
        shutdown();
    }

    private static void setConfig(String path) {
        Gson jsonSer = new Gson();
        String json;
        try {
            json = readStream(new FileReader(path));
        } catch (Exception e) {
            System.out.println("Could not find configuration file.");
            return;
        }
        Simulator.config = jsonSer.fromJson(json, JsonConfiguration.class);
    }

    private static String readStream(FileReader r) throws Exception {
        StringBuilder sb = new StringBuilder(512);

        int c = 0;
        while ((c = r.read()) != -1) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    private static void writeProductsToFile(String path, ConcurrentLinkedQueue<Product> products) {
        try {
            FileOutputStream fout = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(products);
            oos.close();
        } catch (Exception e) {
            Logger.Log(e.toString());
        }
    }
}
