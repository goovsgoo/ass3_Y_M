package REIT.pas;
import java.util.concurrent.*;

/*
 * 2.7 RepairTool

This object is destined to be in the warehouse. It will hold the name of the tool, as well as the
current quantity found in the warehouse. Ensure thread safety in this object. Why? How?

 */

/**
 * 
 * @author Meni & Yoed
 * this class simulates a kitchen tool.
 *
 */
public class RepairTool {
	
	final private String NAME;
	final private int QUANTITY;
	private Semaphore RepairToolSemaphore;
	
	/**
	 * constructs a new Repair Tool object using a name and quantity.
	 * @param name
	 * @param quantity
	 * @param Semaphore
	 */
	public RepairTool(String name, int quantity){
		this.NAME = name;
		this.QUANTITY = quantity;
		RepairToolSemaphore = new Semaphore(quantity, true);
	}
	
	public RepairTool(){
		NAME = "";
		QUANTITY = 0;
		RepairToolSemaphore = new Semaphore(QUANTITY, true);
	}
	
	
	/**
	 * compares between this.name and a String.
	 * @param otherName to compare to this.name.
	 * @return true if the names are equal.
	 * @return false else.
	 */
	protected boolean equals(String otherName){
		return this.NAME.equals(otherName);
	}
	
	/**
	 * compares between Repair Tool objects by name.
	 * @param other RepairTool to compare.
	 * @return true if the names match.
	 * @return false else.
	 */
	protected boolean equals(RepairTool other){
		return this.NAME.equals(other.NAME);
	}
	
	/**
	 * checks the available quantity. used for testing.
	 * @return this.quantity.
	 */
	protected int quantity(){
		// quantity of tool is actually stored as number of aviable permits 
		return this.RepairToolSemaphore.availablePermits();
	}

	/**
	 * releases a RepairTool after finish use
	 * @param numOfPermits, number of permits that are released.
	 */
	protected void release(int numOfTools){
		RepairToolSemaphore.release(numOfTools);
		Management.LOGGER.finer(new StringBuilder(NAME).append(" was released.").toString());
	}

	/**
	 * simulates acquiring a RepairTool, using a Semaphore.
	 * @param numOfPermits - how much RepairTool to acquire.
	 */
	protected void acquire(int numOfTools){
        Management.LOGGER.finest(new StringBuilder("attempting to acquire ").append(NAME).toString());
		//this RepairTool is trying to acquire a semaphore.
		try 
		{
			RepairToolSemaphore.acquire(numOfTools);
			Management.LOGGER.finer(new StringBuilder(NAME).append(" was aqcuired.").toString());
		} 
		catch (InterruptedException e)
		{
			return;
		}
	}
		
	/**
	 * overrides toString method  
	 */
	public String toString(){
		return this.NAME;
	}
}
