package bgu.spl.a2.sim;

import java.util.List;

/**
 * A class that represents a product produced during the simulation.
 */
public class Product implements java.io.Serializable{

	long startId;
	String name;
	Long finalId;
	List<Product> parts;
	/**
	* Constructor 
	* @param startId - Product start id
	* @param name - Product name
	*/
    public Product(long startId, String name){
		this.startId = startId;
		this.name = name;
	}
	public Product(long startId, String name,List<Product> parts){
		this.startId = startId;
		this.name = name;
		this.parts = parts;
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
		return finalId;
	}
	public void setFinalId(long finalId){
		this.finalId = finalId;
	}

	/**
	* @return Returns all parts of this product as a List of Products
	*/
    public List<Product> getParts(){return parts;}

	/**
	* Add a new part to the product
	* @param p - part to be added as a Product object
	*/
    public void addPart(Product p){}


}
