package bgu.spl.a2.sim;

import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.Deferred;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A class representing the warehouse in your simulation
 * 
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 */
public class Warehouse {

	Map<String,Pair<Tool,Integer>> tools;
	Map<String,ManufactoringPlan> plans;
	Map<Tool,ConcurrentLinkedQueue<Deferred<Tool>>> waitingList;
	/**
	* Constructor
	*/
    public Warehouse(){
    	waitingList = new HashMap<>();
		tools = new HashMap<>();
		plans = new HashMap<>();
	}

	/**
	* Tool acquisition procedure
	* Note that this procedure is non-blocking and should return immediatly
	* @param type - string describing the required tool
	* @return a deferred promise for the  requested tool
	*/
    public synchronized Deferred<Tool> acquireTool(String type){
    	Deferred<Tool> returnValue = new Deferred<>();
    	Pair<Tool,Integer> toolAmountPair =  tools.get(type);
		Tool toolInToolbox = toolAmountPair.getKey();
		int amount = toolAmountPair.getValue();

    	if(amount>0){
    		tools.put(type,new Pair<>(toolInToolbox, amount-1));
			returnValue.resolve(toolInToolbox);
		}
		else {
    		waitingList.get(toolInToolbox).add(returnValue);
		}
		return returnValue;
	}

	/**
	* Tool return procedure - releases a tool which becomes available in the warehouse upon completion.
	* @param tool - The tool to be returned
	*/
    public synchronized void releaseTool(Tool tool){
    	Deferred<Tool> waitingForTool = waitingList.get(tool).poll();
    	if(waitingForTool!=null){
    		waitingForTool.resolve(tool);
		}
		else {
    		addTool(tool,1);
		}
	}

	
	/**
	* Getter for ManufactoringPlans
	* @param product - a string with the product name for which a ManufactoringPlan is desired
	* @return A ManufactoringPlan for product
	*/
    public ManufactoringPlan getPlan(String product){
    	return plans.get(product);
	}
	
	/**
	* Store a ManufactoringPlan in the warehouse for later retrieval
	* @param plan - a ManufactoringPlan to be stored
	*/
    public void addPlan(ManufactoringPlan plan){
    	plans.put(plan.getProductName(),plan);
	}
    
	/**
	* Store a qty Amount of tools of type tool in the warehouse for later retrieval
	* @param tool - type of tool to be stored
	* @param qty - amount of tools of type tool to be stored
	*/
    public synchronized void addTool(Tool tool, int qty){
		Pair<Tool,Integer> toolInToolbox = tools.get(InstanceToClassString(tool));
		if(toolInToolbox==null){
			toolInToolbox = new Pair<>(tool,qty);
		}
		else {
			tools.put(InstanceToClassString(tool),
					new Pair<Tool,Integer>(tool,toolInToolbox.getValue()+qty));
		}
		if(!waitingList.containsKey(tool)){
			waitingList.put(tool,new ConcurrentLinkedQueue<>());
		}

	}
	private String InstanceToClassString(Object o){
    	return o.getClass().getName().toString();
	}

}
