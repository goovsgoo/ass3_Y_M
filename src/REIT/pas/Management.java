package REIT.pas;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Vector;
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
	private ArrayList<AssetContent> assetContent;
	

	private Vector<String> toolNames;
	private Vector<String> materialNames;
	
	private ArrayList<RunnableClerk> clerks;
	private CountDownLatch clerksLatch;

	private  int day = 0;
	
	private Vector<RunnableMaintenanceRequest> MaintenanceMen;
	private int numMaintenanceMen;

	private int numAssetToFix;
	
	
	private ArrayList<CustomerGroupDetails> customerGroupDetails;
	private ArrayList<RunnableCustomerGroupManager> customerGroupManager;
	
	static public Logger LOGGER;
	static public RentalRequest DEADEND = new RentalRequest("end","end",0,0);
	private static Management SAMPLE = null;
	private ExecutorService executor;
	
	private int requestCounter;
	private CountDownLatch requestLatch;
	private int end = 0;
	

	private Management(){
		
		assets = Assets.sampleAsset();
		assetContent = new ArrayList<AssetContent>();
		
		toolNames=new Vector<>();
		materialNames=new Vector<>();
		
		clerks= new ArrayList<RunnableClerk>();
		
		MaintenanceMen = new Vector<RunnableMaintenanceRequest>();
		numMaintenanceMen=0;
		numAssetToFix=0;
		
		customerGroupManager = new ArrayList<RunnableCustomerGroupManager>();
		customerGroupDetails = new ArrayList<CustomerGroupDetails>();
		
		executor = Executors.newCachedThreadPool();
		
		try {
			LOGGER = Logger.getLogger(Management.class.getName());
			FileHandler handler = new FileHandler("REIT.log");
			handler.setFormatter(new SimpleFormatter());
			handler.setLevel(Level.ALL);
			LOGGER.addHandler(handler);
			LOGGER.setLevel(Level.FINER);
			} 
		catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DEADEND.LinkAsset( new Asset(-1,"end",0,new Point2D.Double(0, 0),0));
		
		
		this.requestCounter = 0; 
	}
	
	/** this method is a factory method.
	 *  cares that only once will be initialized an object
	 * @return sample of Management
	 */
	static public Management sample(){
		if (SAMPLE == null)
			SAMPLE = new Management();
		return SAMPLE;
	}
	
	public void reportRequestDone(RentalRequest request) {
		request.updateStatus();
		request.linked().updateStatus();
	}
	
	/**
	 * start the REIT simulation
	 * shut down when end = 1
	 */
	public void start(){
		assets.sort();	
		LOGGER.info("Simulation Session Started.");	
		
		requestLatch = new CountDownLatch(requestCounter);
		
		for (RunnableCustomerGroupManager groupManager : customerGroupManager){
			executor.execute(groupManager);
		}
		for (RunnableClerk clerk : clerks){
			executor.execute(clerk);
		}
		resetClerkLatch();
		day =0;

		shutdown();
		
	}
	
	
	
	/**
	 * find a matching asset and request
	 * if found link them and update their status
	 * @return the request whom we found a proper asset
	 * @throws Exception 
	 */
	public  RentalRequest findAssetRequestMatch() throws Exception {
		synchronized(this.getClass()){
			if(requestCounter==0){
				return DEADEND;
			}
			boolean found = false;
			int i = 0;
			Asset matchingAsset = null;
			RentalRequest groupRequest = null;
			while (!found && i < customerGroupDetails.size()) {
				groupRequest = customerGroupDetails.get(i).sendRequest();
				if(groupRequest!=null && groupRequest.statusReport() == "Incomplete"){
					matchingAsset = assets.FindFitRequest(groupRequest.minSizeRequested());
					if (matchingAsset != null) {found = true;}
				}
				++i;
			}
			if (found) {
			 	LOGGER.info(new StringBuilder("we find [group:asset] [").append(groupRequest.owner()).append(":").append(matchingAsset.assetID()).append("]").toString());	
				groupRequest.LinkAsset(matchingAsset);
				groupRequest.updateStatus();
				matchingAsset.updateStatus();
				decrementRequestCounter();
			}
			else 
				groupRequest = null;
			return groupRequest;
		}
	}
	
	
	/**
	 *  shutdown Management
	 */
	protected void shutdown(){
		while(end==0);
		try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
		while(numMaintenanceMen != 0);
		executor.shutdown();
		LOGGER.info("End of Simulation.");
		LOGGER.info(Statistics.instance().toString());
	}

	
	/**
	 * Collects all existing material names in the project
	 * @param materialName
	 */
	public void addMaterialNameToColl(String materialName) {
		if(!materialNames.contains(materialName))
			materialNames.add(materialName);
	}
	/**
	 * Collects all existing tool names in the project
	 * @param toolName
	 */
	public void addToolNameToColl(String toolName) {
		if(!toolNames.contains(toolName))
			toolNames.add(toolName);
	}
	
	

	/**
	 * add a new asset to the asset collection
	 * @param newAsset
	 */
	public void addAsset(Asset newAsset) {
		assets.addNewAsset(newAsset);
	}

	/**
	 * find a manger in ther manger list based on his name
	 * @param name - the manager name
	 * @return the manager itself
	 */
	public RunnableCustomerGroupManager findManager(String name) {
		for (RunnableCustomerGroupManager manager : customerGroupManager){
			if (manager.equals(name))
				return manager;
		}
		return null;
	}
	
	/**
	 * decrease the request counter by one 
	 */
	public void decrementRequestCounter() {
		requestCounter--;
		if(requestCounter==0){
			end=1;
		}
	}
	
	/**
	 * get the request counter
	 */
	public int getRequestCounter() {
		return requestCounter;
	}

	/**
	 * adds group to the group collection
	 * @param newCustomerGroup
	 */
	public void addGroup(CustomerGroupDetails newCustomerGroup) {
		customerGroupDetails.add(newCustomerGroup);
		customerGroupManager.add(new RunnableCustomerGroupManager(newCustomerGroup));
	}

	/**
	 * adds a new clerk to the array
	 * @param runnableClerk
	 */
	public void addClerk(RunnableClerk runnableClerk) {
		clerks.add(runnableClerk);
	}
	
	/**
	 * adds a new AssetContent to the array
	 * @param newAssetContent
	 */
	public void addAssetContent(AssetContent newAssetContent) {
		assetContent.add(newAssetContent);
	}

	/**
	 * initialize the number of requests in the system (after we anwer the we can shutdown all) 
	 * @param totalNumberOfRentalRequests
	 */
	public void setCount(int totalNumberOfRentalRequests) {
		requestCounter = totalNumberOfRentalRequests;
	}

	/**
	 * initialize the Maintenance Men (with ids 0,1,2...)
	 * @param numberOfMaintenancePersons - amount of Maintenance Men
	 */
	public void setNumberOfMaintenanceMen(int numberOfMaintenancePersons) {
		for (int i = 0; i < numberOfMaintenancePersons; i++) {
			RunnableMaintenanceRequest newMan = new RunnableMaintenanceRequest(i);
			MaintenanceMen.add(newMan);
		}
	}

	/**
	 * find Asset Content By Name
	 * @param assetContentName
	 * @return AssetContent
	 */
	public AssetContent findAssetContentByName(String assetContentName) {
			int i;
			for(i = 0;i< assetContent.size() || assetContent.get(i).equals(assetContentName);i++)
			{
			if(assetContent.get(i).equals(assetContentName))
				return assetContent.get(i);
			}
			return null;
		}
	
	/**
	 * Increase clerksLatch by 1
	 * @throws Exception
	 */
	public void clerksLatchCountDown() throws Exception {
		if(clerksLatch.getCount()>0)
			clerksLatch.countDown();	
	}
	
	/**
	 * all the clerks wait for the next day
	 * @throws Exception
	 */
	public void clerksLatchEject() throws Exception {
		synchronized(this){
			clerksLatchCountDown();
			if(clerksLatch.getCount() == 1){
				for (RunnableMaintenanceRequest maintenance : MaintenanceMen){
					executor.execute(maintenance);
					numMaintenanceMen++;
				}
				day = 0;
			}
		}
		clerksLatch.await();
		resetClerkLatch();
	}
	
	/**
	 * all the clerks wait for the next day
	 * @throws Exception
	 */
	public void requestLatchEject() throws Exception {
		requestLatch.countDown();
	}

	/**
	 * 
	 * @return number of asset to fix
	 */
	public int getNumAssetToFix() {
		return numAssetToFix;
	}
	/**
	 * increase number of asset To Fix by 1
	 */
	public void increaseNumAssetToFix() {
		numAssetToFix++;
	}
	
	/**
	 * decrease number of asset To Fix by 1
	 */
	public void decreaseNumAssetToFix() {
		numAssetToFix--;
	}
	
	/**
	 * 
	 * @return
	 */
	public int numberOfInitMaintenanceMen() {
		return MaintenanceMen.size();
	}
	

	/**
	 * decrease number of Maintenance Men by 1
	 */
	public void decreaseNumMaintenanceMen() {
		numMaintenanceMen--;
	}
	

	/**
	 * get number of Maintenance Men
	 */
	public int getNumMaintenanceMen() {
		return numMaintenanceMen;
	}
	
	/**
	 * reset Clerk Latch to number of clerks + num maintenance men
	 */
	public void resetClerkLatch() {
		if (day == 0){
			clerksLatch = new CountDownLatch(clerks.size()+1);
			day=1;
		}
	}

	
	
}
