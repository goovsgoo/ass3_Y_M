package REIT.pas;

import java.lang.Math;
//import java.awt.Point;
import java.awt.geom.Point2D;
//import java.util.Date;
import java.util.HashMap;
//import java.util.Vector;
import java.util.concurrent.*;

import restaurant.passives.Dish;

//import restaurant.actives.RunnableCookOneDish;


/*
 * 2.1 Asset
Found in: Management
This object will hold information of a single asset.
An asset object must hold the following fields: (1) Name (2) Type (3) Location (4) Collection of
AssetContent (5) Status (6) Cost per night (7) Size

1. Type: The type will be one of a list of types provided as an input file.
2. Location: Coordinates are to be stored as (x,y) coordinates in two dimensional space. The
distance is to be calculated as the Euclidean distance between two different coordinates.
3. Status: One of three different options: (i) AVAILABLE (ii) BOOKED (iii) OCCUPIED (iv)
UNAVAILABLE. Where the asset is vacant if it is not currently being used. Booked, where the
asset has been booked by a customer group but not occupied yet. Occupied, if the properly is
currently being used by a customer, and unavailable if the asset is not t for renting, as long it is
not repaired. Repairing a asset requires a collection of exhaustible repair materials, and different
repair tools, and will be detailed later on.
4. Size: How many people the asset can t in.
 */

/** 
 * @author Meni & Yoed
 * this class simulates an Asset.
 */
public class Asset implements Comparable<Asset>{

	final private String TYPE;
	final private int ID;
	final private Point2D.Double LOCATION;
	private HashMap<AssetContent,Integer> AssetContentCollection;	
	final private String STATUS;
	final private int COST;
	final private int SIZE;
	
	private Management management = Management.instance();
	
	
	/**
	 * constructs a new Asset object by name.
	 * @param id, name of asset
	 * @param assetType, type of asset
	 * @param assetSize, size of asset by the amount of people 
	 * @param location, (x,y) of asset
	 * @param assetCostPerNight, cost per night
	 */
	public Asset(int id,String assetType,int assetSize,Point2D.Double location,int assetCostPerNight){
		this.ID = id;
		this.TYPE = assetType;
		this.SIZE = assetSize;
		this.LOCATION = location;
		this.COST = assetCostPerNight;
		AssetContentCollection = new HashMap<AssetContent, Integer>();
	}
																		//Yoed!!!!!!!!!!!!!!!!!!
	/**adds new Content and repairMultiplier to the AssetContentCollection
	 */
	protected void addNewContent(AssetContent content,int repairMultiplier){
		if (content == null)
			throw new RuntimeException("You cannot add null content to asset");
		if (!AssetContentCollection.containsKey(content))
			AssetContentCollection.put(content,new Integer(repairMultiplier));	
	}
	
	/**
	 * add the difficulty of a new dish when we add him to the map.
	 * @param i- the difficulty of a dish that we get from the method sendDifficulty in Dish class.
	 */
	protected void addDifficulty(int difficulty){
		difficultyRating += difficulty;
	}
	
	/**
	 * executes the preparation of the order using CountDownLatch (waits for all dishes in this order).
	 * calculates the whole order preparation time.
	 * @param factor- chef's efficiency factor.
	 * @throws InterruptedException
	 */
	public void prepare(double factor) throws InterruptedException{
		CountDownLatch latch = new CountDownLatch(this.orderOfDish.size());
		this.cookingStartTime = System.currentTimeMillis();

		Management.LOGGER.finer(new StringBuilder("started cooking ").append(ORDER_ID).append(" at ").append(cookingStartTime).toString());
		ExecutorService executor = Executors.newFixedThreadPool(this.orderOfDish.size());

		this.orderStatus = "InProgress";
		Management.LOGGER.info(new StringBuilder(ORDER_ID).append(" is now IN PROGRESS.").toString());

		for (Dish dish : orderOfDish.keySet()){
			Management.LOGGER.fine(new StringBuilder("cooking ").append(dish).toString());
			executor.execute(new RunnableCookOneDish(dish, orderOfDish.get(dish), factor, latch));
		}

		latch.await();
		Management.LOGGER.fine(new StringBuilder("all dishes of ").append(ORDER_ID).append(" ready.").toString());
		executor.shutdown();
		
		this.cookingFinishTime = System.currentTimeMillis();
		Management.LOGGER.finer(new StringBuilder("finished cooking ").append(ORDER_ID).append(" at ").append(cookingFinishTime).toString());
		this.orderStatus = "Complete";
		Management.LOGGER.info(new StringBuilder(ORDER_ID).append(" is COMPLETE.").toString());

		Management.LOGGER.fine(new StringBuilder("cooking time of ").append(ORDER_ID).append(" = ").append(cookingFinishTime-cookingStartTime).toString());
	}
	
	/**
	 * calculate the actual cook time using system.currentTimeMillis().
	 * @return actualCookTime.
	 */
	public int actualCookTime(){
		return (int) (this.cookingFinishTime - this.cookingStartTime);
	}
	
	/**
	 * compare between the difficulty of this order and the endurance of a chef.  
	 * @param endurance
	 * @return true if difficultyRating <= endurance.
	 * @return false if difficultyRating > endurance.
	 */
	public boolean checkDifficulty(int endurance){
		return this.difficultyRating <= endurance;
	}
	
	/**
	 * adds the difficultyRating to chef's pressure when he takes this order.
	 * @param pressure
	 * @return current pressure.
	 */
	public int addPressure(int pressure){
		return pressure + this.difficultyRating;
	}
	
	/**
	 * subtracts the difficultyRating from chef's pressure when he finish cooking this order.
	 * @param pressure
	 * @return current pressure.
	 */
	public int releasePressure(int pressure){
		return pressure - this.difficultyRating;
	}

	/**
	 * @Override compareTo method.
	 * compare between this difficultyRating and other's.
	 */
	public int compareTo(Asset other) {
		return this.difficultyRating - other.difficultyRating;
	}
	
	/**
	 * calculates the maximal cook time summing the cookTime of all dishes in this order.
	 * @return maxCookTime
	 */
	public int calcTotalCookTime(){
		maxCookTime = 0;
		for (Dish dish : orderOfDish.keySet()){
			int dishCookTime = dish.sendCookTime(this);
			 if (dishCookTime > maxCookTime){
				 maxCookTime = dishCookTime;
			 }
		}
		return maxCookTime;
	}
	/**
	 * gets the dish reward from the method "send reward" in Dish class (replace getter).
	 * @param reward
	 */
	protected void sumReward(int reward){
		totalReward += reward;
	}
	
	/**
	 * calculates the total reward of this order summing the reward of all dishes in this order.
	 * @return totalReward
	 */
	public int calcTotalReward(){
		totalReward = 0;
		for (Dish dish : orderOfDish.keySet()){
			dish.sendReward(this, orderOfDish.get(dish));
		}
	return totalReward;
	}
	
	/**
	 * calculates the distance between the restaurant to the customer.
	 * @return distance
	 */
	public int calcDeliveryDistance(){
		management.sendAddress(this);
		int distance = (int)(Math.sqrt((y_coordinateOfRestaurant-customerAddress.getY())*(y_coordinateOfRestaurant-customerAddress.getY()) + (x_coordinateOfRestaurant-customerAddress.getX())*(x_coordinateOfRestaurant-customerAddress.getX())));
		return distance;
	}
	
	/**
	 * updates the restaurant address from Management class.
	 * @param x
	 * @param y
	 */
	public void updateAddress(double x, double y){
		x_coordinateOfRestaurant = x;
		y_coordinateOfRestaurant = y;
	}
	
	/**	
	 * calculates the ACTUAL time to deliver an order from to the restaurant to the customer.
	 * @return actualDeliveryTime in milliseconds.
	 */
	public void calcActualDeliveryTime(){
		deliveryStartTime = System.currentTimeMillis();
		
		try {
			Thread.sleep(this.calcDeliveryDistance());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		deliveryFinishTime = System.currentTimeMillis();
			
		try {
			Thread.sleep(this.calcDeliveryDistance());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * sends actualDeliveryTime to RunnableDeliveryPerson.
	 * @return actualDeliveryTime.
	 */
	public int actualDeliveryTime(){
		return (int) (this.deliveryFinishTime - this.deliveryStartTime);
	}
	
	/**
	 * updates the orderStatus to delivered and calculate the delivery time.
	 */
	public void deliver(){
		this.orderStatus = "Delivered";
		Management.LOGGER.info(new StringBuilder(ORDER_ID).append(" was DELIVERED.").toString());

		this.calcActualDeliveryTime();
	}
	
	/**
	 * compares between this.name and a String.
	 * @param name- name of the order to compare.
	 * @return true if the names match.
	 * @return false if the names don't match.
	 */
	public boolean equals(String name){
		return this.ORDER_ID.equals(name);
	}
	
	/**
	 * overrides toString method.
	 */
	public String toString(){
		StringBuilder printOut = new StringBuilder();
		printOut.append(ORDER_ID).append(":\tDifficulty: ").append(difficultyRating).append(",\tStatus: ").append(orderStatus).append(",\tCustomer Address: [").append(customerAddress.getX()).append(", ").append(customerAddress.getY()).append("],\t Dishes in Order: ");
		for (Dish dish : orderOfDish.keySet()){
			printOut.append(dish.toString()).append("(").append(orderOfDish.get(dish)).append(") ");
		}
		return printOut.toString();
	}
	


}
