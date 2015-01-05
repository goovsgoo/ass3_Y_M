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
	private int ID;
	private Assets assets = Assets.sampleAsset();

	
	public RunnableMaintenanceRequest(int id){
		management = Management.sample();
		this.active = true;
		executor = Executors.newCachedThreadPool();
		this.ID = id;
	}
	
	/**
	 * @Override run method.
	 */
	public void run()  {
		long timeForFix = System.currentTimeMillis();
		boolean needBreak = false;
		for(;;){
			Asset assetToFix = null;
			while (assetToFix == null) {
				
				if (System.currentTimeMillis() - timeForFix >= 16000){
					synchronized(this){
						this.shutdown();
						needBreak=true;
						break;
					}
				}
				
				assetToFix = assets.findAssetToFix();
			}
			if(needBreak){break;}
			asset = assetToFix;
			Management.LOGGER.info(new StringBuilder(ID).append(" started fix at ").append(asset.toString()).toString());
	
			try {
				asset.fixAsset(); } catch (InterruptedException e) {e.printStackTrace();}	
		}
		
		
	}
	
	/**
	 * shutdown runnable maintenance
	 */
	public void shutdown(){
		synchronized(this.getClass()){
			management.decreaseNumMaintenanceMen();
			Management.LOGGER.info(new StringBuilder("the maintenance man ").append(ID).append(" is end the work for today.").toString());
				if(management.getNumMaintenanceMen()==0){		
					try { management.clerksLatchCountDown(); } catch (Exception e) {e.printStackTrace();}
					
				}
		}
	}
	
	/**
	 * @Override toString method.
	 */
	public String toString(){
		return this.ID+" "+active;
	}

	/**
	 * shutdown Executor
	 */
	public void shutdownExecutor() {
		executor.shutdown();
	}
	
}
