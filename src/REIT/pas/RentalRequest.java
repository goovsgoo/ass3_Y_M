package REIT.pas;

import REIT.act.RunnableCustomerGroupManager;


/*2.13 RentalRequest
Found in: Management
This object will hold the information of a rental request.
Fields: (1) Id (2) Asset type (3) Asset size (4) Duration of stay (5) Asset (6) Request status
1. Asset type: The type of the asset requested by the customer group manager.
2. Asset size: The size of the asset requested by the customer group manager. Note: The asset to
be booked can be larger than the requested size. However, be sure to choose the smallest one
possible of the ones found.
3. Duration of stay: Duration is in days, where each day is 24 seconds in our simulation process..
4. Asset: The asset itself. Which was found by the RunnableClerk and will be used by the Runnable-
CustomerGroupManager.
5. Request status: One of four possible options:
(a) Incomplete: If the request has not been handled yet.
(b) Fulfilled: If the request has been fullled, but the customer has not used it yet.
(c) InProgress: The customer is occupying the asset currently.
(d) Complete: The customer has left the asset.
Note: This object will be sent to CallableSimulateStayInAsset which will simulate the rental process
of the request.*/ 


public class RentalRequest implements Comparable<RentalRequest>{
	private final String ID;
	private final String assetType;
	private final int assetSize;
	private final long stay;		//why this is "long" type???
	private Asset assetLinked;
	private String status;
	private String groupManager;
	
	public RentalRequest(String id, String type, int size, long duration) {
		this.ID = id;
		this.assetType = type;
		this.assetSize = size;
		this.stay = duration;
		this.assetLinked = null;
		this.status = "Incomplete";
	}
	
	/**
	 * updated the request status to the next status in order
	 */
	protected void updateStatus() {
		if(status=="Incomplete")
			status="Fulfilled";
		else if(status=="Fulfilled")
			status="InProgress";
		else if(status=="InProgress")
			status="Complete";
		else if(status=="Complete")
			status="Incomplete";
	}

	/**
	 * adds group manager name (from the group that is asking the request
	 * @param group
	 */
	public void assignGroupManager(CustomerGroupDetails group) {
		groupManager = group.managerName();
	}
	
	/**
	 * @return the time the group wish to stay
	 */
	public long sendStayTime(){
		return stay;
	}
	
	/**
	 * assign an asset to request upon finding one
	 * @param assetFound, the appropriate asset to fulfill the request.
	 */
	public void LinkAsset(Asset assetFound) {
		this.assetLinked = assetFound;
	}
	
	/**
	 * the minimum asset size to fulfill the request.
	 * @return minimum number of rooms in the requested asset 
	 */
	public int minSizeRequested() {
		return assetSize;
	}
	
	/**
	 * @return the request handling status
	 */
	public String statusReport(){
		return status;
	}
	
	/**
	 * @return the asset that the system found fitting to the request
	 */
	public Asset linked() {
		return assetLinked;
	}
	
	/**
	 * Overriding the compareTo method
	 * @return Difference of sizes
	 */
	public int compareTo(RentalRequest other){
		return this.assetSize - other.assetSize;
	}
	 
	/**
	 * @return the name of the group manager requesting
	 */
	public String owner() {
		return groupManager;
	}
	
	/**
	 * for overrides toString method  
	 */
	public String toString(){
		StringBuilder printOut = new StringBuilder();
		printOut.append("Rental Request: ");
		printOut.append("[ID: ").append(ID).append("][Asset type: ").append(assetType).append("][assetSize: ").append(assetSize);
		printOut.append("][Stay: ").append(stay).append("][Asset linked: ").append(assetLinked).append("][Status: ").append(status);
		printOut.append("][Group manager: ").append(groupManager);
		return printOut.toString();
	}

	
	synchronized public void waitForBooking() throws InterruptedException {
		wait();
	}
	
	synchronized public void notifyBooking() throws InterruptedException {
		notify();
	}

	/**
	 * @return the time wich they wish to stay
	 */
	public int period() {
		return (int) stay;
	}
}
