package REIT.pas;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;

import REIT.act.*;

/*
 * 3.1 Management
This object contains the following fields: (1) Collection of clerk details (2) Collection of customer group
details (3) Assets (4) Warehouse (5) Collection RepairToolInformation (6) Collection of RepairMaterialInformation
Required Methods: addClerk(), addCustomerGroup(), addItemRepairTool(), addItemRepairMaterial()
Note: What is the best way to store each collection? HashMap? ArrayList? Vector? Queue?
BlockingQueue? LinkedList? Ask yourself where each which.
Management simulates the system as follows:
4
1. Runs each Clerk as RunnableClerk in a thread. Do you need an executor? I know you want
to! But do you need to?
2. Runs each CustomerGroup as RunnableCustomerGroupManager, in a thread. Do you need an
executor?
3. As long as there are RentalRequests to handle:
(a) Awaits all RunnableClerks to nish their day shift. How?
(b) For each damage report, retrieves the asset, and executes a new RunnableMaintainenceRequest
which simulates repairing the asset. Do you need an executor? Note: You need to wait
for all DamageReports to be retrieved.
(c) Once the handling is done, the Clerks are notied regarding the beginning of a new shift.
How?
 */

/**  @author Meni & Yoed
 * this class manage REIT.
 */
public class Management {
	private Assets assets;
	private Warehouse warehouse;
	private ArrayList<RunnableClerk> clerks;
	private ArrayList<CustomerGroupDetails> customerGroupDetails;
	private ArrayList<RunnableCustomerGroupManager> customerGroupManager;
	static public Logger LOGGER;
	private static Management SAMPLE = null;
	private ExecutorService executor;
	private int counter;
	private CountDownLatch latch;
	
	/**constructs a new Management object.
	 */
	private Management(){
		assets = Assets.sample();
		warehouse = Warehouse.sample();
		clerks= new ArrayList<RunnableClerk>();
		this.counter = 0;
		customerGroupManager = new ArrayList<RunnableCustomerGroupManager>();
		executor = Executors.newCachedThreadPool();
		customerGroupDetails = new ArrayList<CustomerGroupDetails>();
		
		try {
			LOGGER = Logger.getLogger(Management.class.getName());
			FileHandler handler = new FileHandler("REIT.log");
			handler.setFormatter(new SimpleFormatter());
			LOGGER.addHandler(handler);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** this method is a factury method.
	 *  cares that only once will be initialized an object
	 * @return sample of Management
	 */
	static public Management sample(){
		if (SAMPLE == null)
			SAMPLE = new Management();
		return SAMPLE;
	}
	
	
	
	/**
	 * start the REIT simulation
	 * shut down when CountDownLatch = 0
	 */
	public void start(){
		//Collections.sort(assets);
		assets.sort();											
		LOGGER.info("Simulation Session Started.");				//Yoed~~~~~~~~~~~~~~
		
		for (RunnableMaintenanceRequest chef : chefs){
			executor.execute(chef);
		}
		for (RunnableDeliveryPerson deliverer : deliveryPersons){
			executor.execute(deliverer);
		}
		latch = new CountDownLatch(counter);
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		shutdown();
		
	}
	
	/**
	 * simulates the order's distribution between chefs.
	 * @param chef
	 */
	public synchronized void askForOrder(RunnableMaintenanceRequest chef){
		for (int i = 0 ; i < orders.size() ; i++){
			Order order = orders.get(i);
			if (chef.willTake(order)){
				chef.cook(order);
				orders.remove(i);
				i--;
			} else {
				chef.refuse(order);
			}
		}
	}
	
	/**
	 * set the restaurant coordinates to Point2D.Double object.
	 * @param x
	 * @param y
	 */
	protected void coordinatesSetter(double x, double y){
		this.restaurantAddress.x = x;
		this.restaurantAddress.y = y;
	}
	
	/**
	 * send the restaurant address to an Order.
	 * @param order - the specific order that accepts the address.
	 */
	public void sendAddress(Order order){
		order.updateAddress(restaurantAddress.getX(), restaurantAddress.getY());
	}
	
	/**
	 * adds new chef to chefs arrayList.
	 * @param newChef
	 */
	protected void addChef(RunnableMaintenanceRequest newChef){
		chefs.add(newChef);
	}
	
	/**
	 * adds new delivery person to deliveryPersons arrayList.
	 * @param newDeliverer
	 */
	protected void addDeliveryPerson(RunnableDeliveryPerson newDeliverer){
		deliveryPersons.add(newDeliverer);
	}
	
	/**
	 * adds new dish to menu arrayList
	 * @param newDish
	 */
	protected void addDish(Dish newDish){
		menu.add(newDish);
	}
	
	/**
	 * finds a dish in the menu by his name.
	 * @param dishName
	 * @return if finds it- returns the Dish object that was found. else- returns null.
	 */
	protected Dish getDishByName(String dishName){
		for (Dish dish : menu){
			if (dish.equals(dishName)){
				return dish;
			}
		}
		return null;
	}
	
	/**
	 * adds a new order to orders PriorityBlockingQueue
	 * if the order to add is not a KILLER order (doesn't shut down the simulation) - add it also to Statistics and logger. 
	 * adds 1 to counter
	 * @param newOrder
	 */
	public synchronized void addOrder(Order newOrder){
		if (newOrder != KILLER){
			LOGGER.fine("order added.");
			Statistics.instance().addOrder(newOrder);
		}
		orders.add(newOrder);
		counter++;
	}
	
	/**
	 * retrieves (without remove) the head of deliveries PriorityBlockingQueue.
	 * @return the head of the queue.
	 */
	public Order checkDeliver(){
		return deliveries.peek();
	}
	

	/**
	 * passes a completed order to deliveries PriorityBlockingQueue.
	 * if a KILLER order - doesn't write to logger.
	 * @param order
	 */
	public void passToDelivery(Order order){
		if (order != KILLER)
			LOGGER.info(new StringBuilder("passing order to delivery:\n").append(order).toString());
		deliveries.offer(order);
	}
	
	/**
	 * retrieves and remove the head of deliveries PriorityBlockingQueue.
	 * @return the Order at the head of the queue. 
	 * @throws InterruptedException
	 */
	public Order takeDelivery() throws InterruptedException{
		Order order = deliveries.take();
		return order;
	}
	
	/**
	 * decrements the count of the latch, releasing all waiting threads if the count reaches zero.
	 * @param order
	 */
	public void report(Order order){
	//	latch.countDown();
	}
	
	/**
	 * shuts down the chefs and delivery persons when the simulation has finished.
	 */
	protected void shutdown(){
		//	for (RunnableChef chef : chefs){
		//		chef.shutdown();
		//	}
		//deliveries.offer(KILLER);
		//executor.shutdown();
	//	LOGGER.info("End of Simulation.");
	//	LOGGER.info(Statistics.instance().toString());
	}
	
}
