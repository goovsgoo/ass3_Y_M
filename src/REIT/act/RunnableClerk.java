package REIT.act;

import java.awt.geom.Point2D;


import REIT.actives.RunnableChef;
import REIT.pas.*;

/*
 * 3.4 RunnableDeliveryPerson
Found in: Management
Note: Must be Runnable.

This object will hold the current  fields: 
(1) Delivery Person Name 
(2) Restaurant Address 
(3) Speed of Delivery Person 
(4) Collection of Orders that have been delivered

It will simulate retrieving a new order to deliver as follows:
  
  Once an order is available for delivery, it is retrieved.
  
  Calculate distance between the restaurant and customer address, divide by speed. The distance
	is calculated as follows: Your restaurant has a pair of coordinates (x1,y1) and each order has a
	customer address which also is a pair of coordinates (x2,y2). In order to calculate the distance
	between the two points, use Euclidean distance; then round it to the nearest integer. This number
	will be in milliseconds. This is the expected delivery time.

  Actual delivery time: the time that was actually spent using Date() polling right when they
	receive the delivery item, and after delivery is complete:
  		Acquire item to deliver.
  		Poll time.
  		Deliver item.
  		Poll time.

  Sleep distance time - to destination.

  Mark order as delivered. If the (actual cook time + actual delivery time) > 115% * (expected
	cook time + expected delivery time), then you receive 50% of the reward, otherwise, you receive
	100% of the reward.

  Sleep distance time - to home.

  Repeat cycle. The delivery person will shutdown only when a shutdown request is received.
 */

public class RunnableClerk implements Runnable{
	final private String NAME; 
	private Point2D.Double location;
	// final private double SPEED; 
	// private Statistics rewardStatistics = Statistics.instance();
	private boolean active;
	private Management management = Management.instance();

	
	/**
	 * constructs a new RunnableDeliveryPerson object using a name, restaurantAddress, speed and ordersToDelive.
	 * @param deliveryPersonName
	 * @param speed
	 * @param x,y - coordinates of the restaurantAddress
	 */
	public RunnableClerk(String clerkName, Point2D.Double clerckLocation){
		this.NAME = clerkName;
		this.location = clerckLocation;
		// this.SPEED = speed;
		this.active = true;
	}
	
	/**
	 * @Override run method.
	 * simulates ...
	 */	
	public void run() {
		while (this.active){
			try {
				Asset asset = management.findAssetRequestMathc();					
				if (order == Management.DEADEND){
					this.shutdown();
					management.passToAvailableAssets(asset);
				} else {
					Management.LOGGER.info(new StringBuilder(NAME).append(" RECEIVED ").append(order).toString());
					this.goConfirmAsset(asset);
					management.reportRequestDone(asset);
				}
			}
			catch (InterruptedException e) {
				//TODO
			}
		}
		Management.LOGGER.info(new StringBuilder(NAME).append(" is SHUTTING DOWN...").toString());
	}
	
	 
	/**
	 * commits delivery of one order.
	 * calculates the final reward of this order (by comparing the actual and expected cook time and delivery time).
	 * @param order
	 */
	private void goConfirmAsset(Asset asset){
		// order.deliver();
		// calculate distance from REIT to asset
		int distance = calculateDistance(asset);
		long timeToFullFulfil = checking(distance);
		Management.LOGGER.info(new StringBuilder(NAME).append(" COLLECTED REWARD: ").append(finalReward).append(" out of ").append(order.calcTotalReward()).toString());
	}
	
	/**	
	 * calculates the ACTUAL time to deliver an order from to the restaurant to the customer.
	 * @return actualDeliveryTime in milliseconds.
	 */
	public long checking(int distance){
		// TODO do we calculate the time like that????????????
		long startTime = System.currentTimeMillis();
		
		try {
			Thread.sleep(distance);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long finishTime = System.currentTimeMillis();
			
		try {
			Thread.sleep(distance);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return (finishTime - startTime);  
	}
	

	/**
	 * calculates the distance between the restaurant to the customer.
	 * @return distance
	 */
	public int calculateDistance(Asset asset){
		// management.sendAddress(this);
		int dist = (int)(Math.sqrt((this.location.getY()-asset.location.getY())*(this.location.getY()-asset.location.getY())) + ((this.location.getX()-asset.location.getX())*(this.location.getX()-asset.location.getX())));
		return dist;
	}
	
	/**
	 * overrides toString method.
	 */
	public String toString(){
		return this.NAME;
	}
	
	/**
	 * when all orders were delivered - shuts down the delivery persons (update the active field to false).
	 */
	public void shutdown(){
		this.active = false;
		Management.LOGGER.finer(new StringBuilder(NAME).append(" is DEACTIVATING.").toString());
	}

}

