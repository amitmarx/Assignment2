package bgu.spl.a2.sim;

import java.util.List;

/**
 * A class that represents a product produced during the simulation.
 */
public class Product {

	int startId;
	String name;
	Long finalId;
	/**
	* Constructor 
	* @param startId - Product start id
	* @param name - Product name
	*/
    public Product(long startId, String name){
    	startId = startId;
    	name = name;
	}

	/**
	* @return The product name as a string
	*/
    public String getName(){
    	return name;
	}

	/**
	* @return The product start ID as a long. start ID should never be changed.
	*/
    public long getStartId(){
    	return startId;
	}
    
	/**
	* @return The product final ID as a long. 
	* final ID is the ID the product received as the sum of all UseOn(); 
	*/
    public long getFinalId(){
		if(finalId==null){
			for(Product p : getParts()){

			}
		}
		return finalId;
	}

	/**
	* @return Returns all parts of this product as a List of Products
	*/
    public List<Product> getParts(){return null;}

	/**
	* Add a new part to the product
	* @param p - part to be added as a Product object
	*/
    public void addPart(Product p){}


}
