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

import restaurant.actives.RunnableDeliveryPerson;

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
		calculateToolsMaterials();
		
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
		LOGGER.info("Simulation Session Started.");				
		/*
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
		*/
	}
	
	/**
	 * Checks whether this house needed repair
	 * @param asset , to repair
	 */
	public synchronized void shouldRepair(Asset asset){
			if (asset.assetHealth()<=65)
				callMaintenanceMan(asset);	
	}
	/**
	 * fix the asset , execute new Maintenance personnel ,A limited number of maintenance personnel
	 * @param asset , to repair
	 */
	public synchronized void callMaintenanceMan(Asset asset){
		MaintenanceSemaphore.acquire(1);
		executor.execute(new RunnableMaintenanceRequest("avi" + latch,asset));
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
	 * Calculate tools and materials names for camper
	 */
	private void calculateToolsMaterials() {
		int i;
		for(i = 0;i< assetContentCollection.size();i++){
			
			HashMap<String, Integer> copyTools = assetContentCollection.get(i).returnCopyTools();
			HashMap<String, Integer> copyMaterials = assetContentCollection.get(i).returnCopyMaterials();

			for (Entry<String, Integer> entry : copyTools.entrySet()) {
				if( !toolNames.contains(entry.getKey()) )
						toolNames.add(entry.getKey());
			}
			for (Entry<String, Integer> entry : copyMaterials.entrySet()) {
				if( !materialNames.contains(entry.getKey()) )
					materialNames.add(entry.getKey());
			}
		}
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
	
	
}