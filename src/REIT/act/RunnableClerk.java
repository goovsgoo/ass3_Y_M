package REIT.act;

import java.awt.geom.Point2D;

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
	private long startDayClock;
	private Management management = Management.sample();
	
	/**
	 * constructs a new clerk
	 * @param clerkName
	 * @param clerckLocation
	 */
	public RunnableClerk(String clerkName, Point2D.Double clerckLocation){
		this.NAME = clerkName;
		this.location = clerckLocation;
		startDayClock=0;
	}
	
	/**
	 * @Override run method.
	 * simulates a clerk receiving asset and matching request and handling them
	 */	
	public void run() {
		
		startDayClock = System.currentTimeMillis();
		
		for(;;){
			RentalRequest requestMatching = null;
			while (requestMatching == null) {
				if(endOfDay(startDayClock)){
				 	Management.LOGGER.info(new StringBuilder("the clerk: ").append(NAME).append(" stop to work for today").toString());	
					try { management.clerksLatchEject(); } catch (Exception e) {e.printStackTrace();}
				}
				try { requestMatching = management.findAssetRequestMatch(); } catch (Exception e) {e.printStackTrace();}
			}
			
			if (requestMatching == Management.DEADEND){
				try { management.clerksLatchCountDown();} catch (Exception e) {e.printStackTrace();}
				this.shutdown();
				break;
			}
			
			Asset asset = requestMatching.linked();
			this.goConfirmAsset(asset);
			try {
				requestMatching.notifyBooking(); } catch (InterruptedException e) {e.printStackTrace();}

		 	Management.LOGGER.info(new StringBuilder("the clerk: ").append(NAME).append(" back to base").toString());	
		}
	}
	
	/**
	 * 
	 * @return true if Clock work under 8 hours 
	 */
	 private boolean endOfDay(long startTime) {
		 if (System.currentTimeMillis() - startTime  > 8 * Management.TIMEMULTI)
			 return true;
		 else 
			 return false;
	}

	/**
	 * simulate the time that passes as the clerk go to "verify" the asset to rent
	 * @param asset
	 */
	private void goConfirmAsset(Asset asset){
		int distance = calculateDistance(asset);
		Management.LOGGER.info(new StringBuilder(NAME).append(NAME).append(" going to asset number - ").append(asset.assetID()).toString());
		long timeToFullFulfil = walkTo(distance);
		Management.LOGGER.info(new StringBuilder(NAME).append(NAME).append(" arrive to ").append(asset.assetID()).append(" at ").append(timeToFullFulfil).toString());
		asset.updateStatus();
	}
	
	/**
	 * calculate the distance between the clerk and the asset 
	 * @param asset - that the clerk will go to 
	 * @return the distance between them
	 */
	private int calculateDistance(Asset asset) {
		double y = Math.pow(this.location.getY()-asset.adress().getY(),2);
		double x = Math.pow(this.location.getX()-asset.adress().getX(),2);
		return (int)(Math.sqrt(y+x));
	}

	/**	
	 * simulates walk to asset
	 * @return 
	 */
	public long walkTo(int distance){
		long startTime = System.currentTimeMillis();
		
		try {
			Thread.sleep(distance*Management.TIMEMULTI);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		long finishTime = System.currentTimeMillis();
			
		try {
			Thread.sleep(distance*Management.TIMEMULTI);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return (finishTime - startTime);  
	}
	

	/**
	 * overrides toString method.
	 */
	public String toString(){
		StringBuilder printOut = new StringBuilder();
		printOut.append("New Clerk : ").append(NAME).append(" [location: ").append(location.getX()).append(",").append(location.getX()).append("]");
		return printOut.toString();
	}
	
	private void shutdown(){
		Management.LOGGER.info(new StringBuilder("the clerk ").append(NAME).append(" is now exit from Simulation ").toString());	
	}

}

