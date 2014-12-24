package REIT.act;

import java.util.concurrent.*;

import REIT.pas.*;

/*
 * 3.3 RunnableChef

Found in: Management

Note: Must be Runnable.

This object is our third active object. 
Fields: 
	(1) Chef Name 
	(2) Chef Efficiency Rating 
	(3) Endurance Rating 
	(4) Current Pressure 
	(5) Collection of Futures for Orders in Progress 
	(6) Pool of Threads.

This runnable receives new cook orders from the Management, and depending on dish difficulty,
endurance rating and current pressure, decides whether to accept or deny the request for cooking the
order.
The formula is as follows:
	if (Dish Difficulty <= Endurance Rating - Current Pressure) then the request is accepted, 
	and the cooking simulation procedure automatically begins.

Of course, the value of current pressure needs to be increased by the value of dish difficulty.

At any time, the chef may receive a shut down request. If received, the chef will  finish cooking the
orders he may be working on while denying new requests, once all currently being cooked orders are
complete, the thread exits.

Once an Order is  finished, the current pressure value needs to be decreased by the value of the
completed Order difficulty. Then the chef sends the  finished Order to the delivery department queue.

Chef Efficiency Rating can be one of three: (i) Good = 0.9 (ii) Neutral = 1.0 (iii) Bad = 1.1
Both Chef Endurance Rating and Current Pressure are of an integer type.
 */

public class RunnableMaintenanceRequest implements Runnable, Comparable<RunnableMaintenanceRequest> {
	
	final private String NAME;
	final private double EFFICIENCY;
	final private int ENDURANCE;
	private int pressure;
	private ExecutorService executor;
	private boolean active;
	private Management management = Management.instance();

	/**
	 * constructs a new RunnableChef object using a name, efficiency, endurance and pressure, an active field and executor.
	 * @param name
	 * @param efficiency
	 * @param endurance 
	 * @param pressure
	 * @param active - true if the chef is active (if not shut down).
	 * @param executor
	 */
	public RunnableMaintenanceRequest(String name, double rate, int endurance){
		this.NAME = name;
		this.EFFICIENCY = rate;
		this.ENDURANCE = endurance; 
		this.pressure = 0;
		this.active = true;
		executor = Executors.newCachedThreadPool();
	}
	
	/**
	 * @Override run method.
	 * simulates the order's distribution between the chefs.
	 */
	public void run() {
		
		while (this.active){
			synchronized (this){
			
				management.askForOrder(this);
				try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
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
