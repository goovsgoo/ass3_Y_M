package REIT.pas;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.CompletionService;
//import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;

import REIT.act.CallableSimulateStayInAsset;

/*
 * 2.10 CustomerGroupDetails
Found in: RunnableCustomerGroupManager
This object will hold the details of the customer groups parsed from input les.
Fields: (1) Collection of Rental Requests (2) Collection of Customers (3) Group manager name
Methods: (1) addCustomer (2) addRentalRequest
 */

public class CustomerGroupDetails {
	private final String groupManager;
	private Vector<RentalRequest> requests;
	private ArrayList<Customer> customers;
	private Management management = Management.sample();	
	private Statistics statistics = Statistics.instance();
	public CustomerGroupDetails(String manager) {
		groupManager = manager;
		requests = new Vector<RentalRequest>();
		customers = new ArrayList<Customer>();
	}
	
	/**
	 * @param request - request to add
	 * @return true if could add, false otherwise
	 */
	public boolean addRequest(RentalRequest request) {
		return requests.add(request);
	}

	/**
	 * @return the manager name
	 */
	public String managerName() {
		return groupManager;
	}
	
	/**
	 * @param newGuy - customer to be added
	 * @return true if could add, false otherwise
	 */
	public boolean addCustomer(Customer newGuy) {
		return customers.add(newGuy);	
	}
	
	/**
	 * simulates the staying time in asset upon finding one
	 * after staying  - updates the relevant status and damage for further handling 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void sleepInAsset() throws InterruptedException, ExecutionException{
       	ExecutorService executor = Executors.newCachedThreadPool();
       	CompletionService<Double> ecs = new ExecutorCompletionService<Double>(executor);
       	final int numerOfCustomers = customers.size();
       	double totalDamage = 0;
        
       	requests.firstElement().updateStatus();
       	whereAreWe().updateStatus();
       	
        for (int i = 0; i < numerOfCustomers; i++) {
        	Callable<Double> SingleStay = new CallableSimulateStayInAsset(customers.get(i),requests.firstElement().sendStayTime());
        	ecs.submit(SingleStay);
        }
		Management.LOGGER.info(new StringBuilder(groupManager).append(" was sleep in asset - ")
				.append(requests.firstElement().linked().assetID()).append(" for ")
				.append(requests.firstElement().sendStayTime()).append(" days ").toString());	
        for (int i=0; i < numerOfCustomers ; ++i)
        {
            totalDamage += (double) ecs.take().get();
        }        
        if (totalDamage>100)
        	totalDamage=100;
        executor.shutdown();
        statistics.addRequest(requests.firstElement());
        statistics.addReward(calculateReward()*numerOfCustomers);
        updateAssetAndRequest(totalDamage);
	}
	
	/**
	 * @return the reward that REIT will gain from the staying of the group
	 */
	private int calculateReward() {
		return whereAreWe().cost() * requests.firstElement().period();
	}

	/**
	 * updates the damage caused to the asset in the current request
	 * and removes the request from the requests list
	 * @param damage caused while staying
	 */
	private void updateAssetAndRequest(double damage) {
		
		whereAreWe().breakThehouse(damage);
		
		 if (damage >= 35)
	        	management.increaseNumAssetToFix();
	        else
	        	requests.firstElement().linked().updateStatus();
	        
		requests.firstElement().updateStatus();
		
        DamageReport report = new DamageReport();
        report.assignAsset(requests.firstElement().linked());
        report.updateDamage(damage);
        
		requests.removeElementAt(0);
		management.decrementRequestCounter();
		try {
			management.requestLatchEject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the asset linked to the current request
	 */
	public Asset whereAreWe() {
		return requests.firstElement().linked();
	}
	
	/**
	 * @return true if the group has any requests left
	 */
	public boolean anyMoreRequests(){
		return !requests.isEmpty();
	}

	/**
	 * @return the current request of the group
	 */
	public RentalRequest sendRequest() {
		if (anyMoreRequests())
			return requests.firstElement();
		else
			return null;
	}

	/**
	 * @return the status of the current request
	 */
	public String statOfCurrentRequest() {
		return requests.firstElement().statusReport();
	}

	/**
	 * @return how many people are in the group
	 */
	public int groupSize() {
		return customers.size(); 
	}
	
	/**
	 * @override toString method  
	 */
	public String toString(){
		StringBuilder printOut = new StringBuilder();
		printOut.append("New customers gruop : ").append(groupManager).append(" ,Requests :");
		for(int i = 0;i< requests.size();i++){
			printOut.append("[").append(requests.get(i).toString()).append("]");
		}
		printOut.append(" ,customers: ");
		for(int i = 0;i< customers.size();i++){
			printOut.append("[").append(customers.get(i).toString()).append("]");
		}
		return printOut.toString();
		
	}
	
	/**
	 * Take the first request and wait for booking 
	 * @throws Exception 
	 */
	public void getAsset() throws Exception {
		requests.firstElement().waitForBooking();
	}

}
