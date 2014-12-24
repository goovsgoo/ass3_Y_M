package REIT.act;

import java.util.concurrent.*;

import restaurant.passives.Dish;
import restaurant.passives.Warehouse;

import REIT.pas.*;

/*
 * 4.3 RunnableMaintenanceRequest
Found in: Management
Note: Must be Runnable.
This object will hold the current elds: (1) Collection of RepairToolInformation (2) Collection of
RepairMaterialInformation (3) Asset (4) Warehouse.
From the Asset, we retrieve the damaged contents. An asset content item is damaged if its health
is below 65%. Then, the total repair cost in time, is calculated, where for each item the percentage
of the damage is multiplied by the repairCost multiplier found in the AssetContent. Then:
1. Acquire the required repair tools and their quantity. Note: The warehouse will always have
enough tools.
6
2. Acquire the required repair materials and their quantity. Note: The warehouse will always have
enough materials
3. Sleep the cost in milliseconds after rounding it to nearest long.
4. Release the acquired repair tools.
(a) Mark Asset xed. By returning the health of all its contents to 100.
You generate one RunnableMaintenanceRequest per damaged asset.
 */

public class RunnableMaintenanceRequest implements Runnable, Comparable<RunnableMaintenanceRequest> {
	
	private ExecutorService executor;
	private boolean active;
	private Assets assets ;
	private Management management ;
	private Warehouse warehouse ;
	final private String NAME;

	/**
	 * constructs new RunnableMaintenanceRequest object
	 * @param name
	 */
	public RunnableMaintenanceRequest(String name){
		assets = Assets.sample();
		management = Management.sample();
		warehouse = Warehouse.sample();
		this.active = true;
		executor = Executors.newCachedThreadPool();
		this.NAME = name;
	}
	
	/**
	 * @Override run method.
	 * simulates the order's distribution between the chefs.
	 */
	public void run() {
		
		while (this.active){
			synchronized (this){
			
				management.askForFix(this);
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		executor.shutdown();
		Management.LOGGER.fine(new StringBuilder(NAME).append(" is SHUTTING DOWN...").toString());
	}
	
	/**
	 * simulates the cooking process of one order (if willTake method returns true).
	 * @param order
	 */
	public void cook(Order order){
		Management.LOGGER.info(new StringBuilder(NAME).append(" ACCEPTED ").append(order).toString());
		this.pressure = order.addPressure(pressure);
		
		final Future<Order> takenOrder = executor.submit(new CallableCookWholeOrder(order, EFFICIENCY));
		
		executor.execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (!takenOrder.isDone());
				
				try {
					finish(takenOrder.get());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * simulates the process of releasing an order when finished cooking.
	 * @param order
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	protected synchronized void finish(Order order) throws InterruptedException, ExecutionException{
		this.pressure = order.releasePressure(pressure);
		Management.LOGGER.info(new StringBuilder(NAME).append(" FINISHED COOKING ").append(order).toString());
		management.passToDelivery(order);
		this.notifyAll();
	}
	
	/**
	 * determines whether the chef will take the order or not
	 * @param order
	 * @return true if difficultyRating <= endurance.
	 * @return false if difficultyRating > endurance.
	 */
	public boolean willTake(Order order){
		if (order == null)
			return false;
		return order.checkDifficulty(this.ENDURANCE-this.pressure);
	}
	
	/**
	 * when difficultyRating > endurance - the chef refuses to take the order.
	 * @param order
	 */
	public void refuse(Order order){
		Management.LOGGER.info(new StringBuilder(NAME).append(" REFUSED ").append(order).toString());
	}
	
	/**
	 * when all orders were cooked - shuts down the chefs (update the active field to false).
	 */
	public synchronized void shutdown(){
		if (active){
			this.active = false;
			this.notifyAll();
			Management.LOGGER.finer(new StringBuilder(NAME).append(" is DEACTIVATING.").toString());
		}
	}
	
	/**
	 * overrides toString method.
	 */
	public String toString(){
		return this.NAME+" "+active;
	}

	/**
	 * @Override compareTo method
	 * compare between this endurance and other's.
	 */
	public int compareTo(RunnableMaintenanceRequest other) {
		// TODO Auto-generated method stub
		return this.ENDURANCE - other.ENDURANCE;
	}
	
}
