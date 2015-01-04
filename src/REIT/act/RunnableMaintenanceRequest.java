package REIT.act;

import java.util.concurrent.*;

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

public class RunnableMaintenanceRequest implements Runnable {
	
	private ExecutorService executor;
	private boolean active;
	private Asset asset ;
	private Management management ;
	private Warehouse warehouse ;
	private int ID;
	private Assets assets = Assets.sample();
	
	/**
	 * constructs new RunnableMaintenanceRequest object
	 * @param name
	 */
	public RunnableMaintenanceRequest(int id){
		// asset = assetToFix;
		management = Management.sample();
		warehouse = Warehouse.sample();
		this.active = true;
		executor = Executors.newCachedThreadPool();
		this.ID = id;
	}
	
	/**
	 * @Override run method.
	 * simulates the order's distribution between the chefs.
	 */
	public void run()  {	
		Asset assetToFix = null;
		while (assetToFix == null) {
			assetToFix = assets.findAssetToFix();
		}
		asset = assetToFix;
		Management.LOGGER.finer(new StringBuilder("started fix at ").append(asset.toString()).toString());
		// fix the asset
		try {
			asset.fixAsset();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Management.LOGGER.fine(new StringBuilder(ID).append(" is SHUTTING DOWN...").toString());
	}
	
	/**
	 * when all orders were cooked - shuts down the chefs (update the active field to false).
	 */
	public synchronized void shutdown(){
		if (active){
		//	this.active = false;
		//	this.notifyAll();
		//	Management.LOGGER.finer(new StringBuilder(NAME).append(" is DEACTIVATING.").toString());
		}
	}
	
	/**
	 * overrides toString method.
	 */
	public String toString(){
		return this.ID+" "+active;
	}
	
}
