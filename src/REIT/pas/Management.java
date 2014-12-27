package REIT.pas;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.*;

import javax.swing.text.StyleContext.SmallAttributeSet;

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
	private Semaphore MaintenanceSemaphore;
	private ArrayList<CustomerGroupDetails> customerGroupDetails;
	private ArrayList<RunnableCustomerGroupManager> customerGroupManager;
	static public Logger LOGGER;
	static public Asset DEADEND = new Asset(0, "end", 0, null, 0);
	private static Management SAMPLE = null;
	private ExecutorService executor;
	private int counter;
	private CountDownLatch latch;
	private Vector<String> toolNames;
	private Vector<String> materialNames;

	
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
		MaintenanceSemaphore = new Semaphore(5, true);
		toolNames=new Vector<>();
		materialNames=new Vector<>();
		//calculateToolsMaterials();
		
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
	
	public void reportRequestDone(RentalRequest request) {
		request.updateStatus();
		request.linked().updateStatus();
	}
	
	/**
	 * start the REIT simulation
	 * shut down when CountDownLatch = 0
	 */
	public void start(){
		assets.sort();											
		LOGGER.info("Simulation Session Started.");				
		///what next????
	}
	
	/**
	 * Checks whether this house needed repair
	 * @param asset , to repair
	 * @throws InterruptedException 
	 */
	public synchronized void shouldRepair(Asset asset) throws InterruptedException{
			if (asset.assetHealth()<=65)
				callMaintenanceMan(asset);	
	}
	/**
	 * fix the asset , execute new Maintenance personnel ,A limited number of maintenance personnel
	 * @param asset , to repair
	 * @throws InterruptedException 
	 */
	public synchronized void callMaintenanceMan(Asset asset) throws InterruptedException{
		MaintenanceSemaphore.acquire();
		executor.execute(new RunnableMaintenanceRequest("avi" + latch,asset));
	}
	
	/**
	 * realse Maintenance personnel 
	 */
	public synchronized void endOfFixing() {
		MaintenanceSemaphore.release();
	}
	
	/**
	 * find a matching asset and request
	 * if found link them and update their status
	 * @return the request whom we found a proper asset
	 */
	public synchronized RentalRequest findAssetRequestMatch() {
		Assets assets = Assets.sample();
		boolean found = false;
		int i = 0;
		Asset matchingAsset = null;
		RentalRequest groupRequest = null;
		while (!found && i < customerGroupDetails.size()) {
			groupRequest = customerGroupDetails.get(i).sendRequest();
			matchingAsset = assets.FindFitRequest(groupRequest.minSizeRequested());
			if (matchingAsset != null) {found = true;}
			++i;
		}
		if (found) {
			groupRequest.LinkAsset(matchingAsset);
			groupRequest.updateStatus();
			matchingAsset.updateStatus();
		}
		else 
			groupRequest = null;
		return groupRequest;
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
	 * get copy of list tools name
	 * @return copyToolNames, copy of name of tools
	 */
	public Vector<String> returnCopyTools() {
		Vector<String> copyToolsNames = new Vector<String>(toolNames);
		return copyToolsNames;
	}
	
	/**
	 * get copy of list cMaterials name
	 * @return copyMaterialsNames, copy of name of Materials
	 */
	public Vector<String> returnCopyMaterial() {
		Vector<String> copyMaterialsNames = new Vector<String>(materialNames);
		return copyMaterialsNames;
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
}
