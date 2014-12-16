package REIT.pas;
import java.util.concurrent.*;

/*
 * 2.3 KitchenTool

This object will hold information of a single kitchen tool type. Each kitchen tool has: (1) Name (2)

Quantity. This item is not consumed in the process, but returned once its use is done.
 */

/**
 * 
 * @author Shimrit Zabib
 * this class simulates a kitchen tool.
 *
 */
public class RepairTool {
	
	final private String NAME;
	final private int QUANTITY;
	private Semaphore kitchenToolSemaphore;
	
	/**
	 * constructs a new KitchenTool object using a name and quantity.
	 * @param name
	 * @param quantity
	 * @param Semaphore
	 */
	public RepairTool(){
		NAME = "";
		QUANTITY = 0;
		kitchenToolSemaphore = new Semaphore(QUANTITY, true);
	}
	
	public RepairTool(String name, int quantity){
		this.NAME = name;
		this.QUANTITY = quantity;
		kitchenToolSemaphore = new Semaphore(quantity, true);
	}
	
	/**
	 * compares between KitchenTool objects by name.
	 * @param other KitchenTool to compare.
	 * @return true if the names match.
	 * @return false if the names don't match.
	 */
	protected boolean equals(RepairTool other){
		return this.NAME.equals(other.NAME);
	}
	
	/**
	 * compares between this.name and a String.
	 * @param otherName to compare to this.name.
	 * @return true if the names match.
	 * @return false if the names don't match.
	 */
	protected boolean equals(String otherName){
		return this.NAME.equals(otherName);
	}
	
	/**
	 * checks the available quantity. used for testing.
	 * @return this.quantity.
	 */
	protected int quantity(){
		return this.kitchenToolSemaphore.availablePermits();
	}
	
	/**
	 * simulates acquiring a single piece of the KitchenTool by FIFO, using a Semaphore.
	 * @param numOfPermits - how much KitchenTools to acquire.
	 */
	protected void acquire(int numOfPermits){
        Management.LOGGER.finest(new StringBuilder("attempting to acquire ").append(NAME).toString());
		//this kitchenTool is trying to acquire a semaphore.
		try {
			kitchenToolSemaphore.acquire(numOfPermits);
			Management.LOGGER.finer(new StringBuilder(NAME).append(" was aqcuired.").toString());
		} catch (InterruptedException e)
		{
			return;
		}
	}
	
	/**
	 * releases the permits of kitchenTool Semaphore after finished using .
	 * @param numOfPermits - number of permits that are released.
	 */
	protected void release(int numOfPermits){
		kitchenToolSemaphore.release(numOfPermits);
		Management.LOGGER.finer(new StringBuilder(NAME).append(" was released.").toString());
	}
	
	/**
	 * overrides toString method  
	 */
	public String toString(){
		return this.NAME;
	}
}
