package REIT.pas;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
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
	private CountDownLatch mainLatch;
	private Management management = Management.sample();	
	
	public CustomerGroupDetails(String manager) {
		groupManager = manager;
		requests = new Vector<RentalRequest>();
		customers = new ArrayList<Customer>();
	}
	
	protected void linkLatch(CountDownLatch latch) {
		mainLatch = latch;
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
	protected String managerName() {
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
       	// updates request and asset
       	requests.firstElement().updateStatus();
		requests.firstElement().linked().updateStatus();
       	
        for (int i = 0; i < numerOfCustomers; i++) {
        	Callable<Double> SingleStay = new CallableSimulateStayInAsset(customers.get(i),requests.firstElement().sendStayTime());
        	ecs.submit(SingleStay);
        }
        for (int i=0; i < numerOfCustomers ; ++i)
        {
            totalDamage += (double) ecs.take().get();
        }        
        executor.shutdown();

        updateAssetAndRequest(totalDamage);
        DamageReport report = new DamageReport();
        report.assignAsset(sendRequest().linked());
        report.updateDamage(totalDamage);
        // management.shouldRepair(sendRequest().linked());
        // TODO remove request after we are done with it
	}
	
	/**
	 * updates the damage caused to the asset in the current request
	 * and removes the request from the requests list
	 * @param damage caused while staying
	 */
	private void updateAssetAndRequest(double damage) {
		requests.firstElement().updateStatus();
		requests.firstElement().linked().updateStatus();
		requests.removeElementAt(0);
		mainLatch.countDown();
		System.out.println("yay, we handled a request");
		whereAreWe().breakThehouse(damage);
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
	 * for overrides toString method  
	 */
	public String toString(){
		return this.groupManager;
	}

}
