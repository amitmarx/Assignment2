/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.JsonConfiguration;
import com.google.gson.Gson;
import com.oracle.javafx.jmx.json.JSONReader;

import java.io.FileReader;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
    /**
     * Begin the simulation
     * Should not be called before attachWorkStealingThreadPool()
     */
    public static ConcurrentLinkedQueue<Product> start() {
        return null;
    }

    /**
     * attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
     *
     * @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
     */
    public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool) {
    }

    public static void main(String[] args) throws Exception {
        Gson jsonSer = new Gson();
        A a = new A(9);
        a.a = 9;
        String s;
        A b;
        JsonConfiguration conf;
        String json = readStream(new FileReader("/Users/amitm/dev/spl/Assignment2/src/main/java/bgu/spl/a2/sim/conf/config.json"));
        conf = jsonSer.fromJson(json, JsonConfiguration.class);
//			s = jsonSer.toJson(a);
//			b = jsonSer.fromJson(s,A.class);
    }

    public static String readStream(FileReader r) throws Exception {
        StringBuilder sb = new StringBuilder(512);

        int c = 0;
        while ((c = r.read()) != -1) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    public static class A {
        private int a;

        public A(int a) {
            this.a = a;
        }

        public A() {
        }

        public int getA() {
            return a;
        }
    }
}
