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

/////////////////////////////////////////חסר פה הערות

public class CustomerGroupDetails {
	private final String groupManager;
	private Vector<RentalRequest> requests;
	private ArrayList<Customer> customers;
	private Management management = Management.sample();	
	
	public CustomerGroupDetails(String manager) {
		groupManager = manager;
		requests = new Vector<RentalRequest>();
		customers = new ArrayList<Customer>();
	}
	
	public boolean addRequest(RentalRequest request) {
		return requests.add(request);
	}
	
	public boolean addCustomer(Customer newGuy) {
		return customers.add(newGuy);	
	}
	
	public void sleepInAsset() throws InterruptedException, ExecutionException{
       	ExecutorService executor = Executors.newCachedThreadPool();
       	CompletionService<Double> ecs = new ExecutorCompletionService<Double>(executor);
       	final int numerOfCustomers = customers.size();
       	double totalDamage = 0;
       	
        for (int i = 0; i < numerOfCustomers; i++) {
        	Callable<Double> SingleStay = new CallableSimulateStayInAsset(customers.get(i),requests.firstElement().sendStayTime());
        	ecs.submit(SingleStay);
        }
        for (int i=0; i < numerOfCustomers ; ++i)
        {
            totalDamage += (double) ecs.take().get();
        }        
        executor.shutdown();
        updateAsset(totalDamage);
        DamageReport report = new DamageReport();
        report.assignAsset(sendRequest().linked());
        report.updateDamage(totalDamage);
        management
	}
	
	private void updateAsset(double damage) {
		requests.firstElement().updateStatus("InProgress");
		whereAreWe().braekTheHouse(damage);
	}
	
	public Asset whereAreWe() {
		return requests.firstElement().linked();
	}
	
	public boolean anyMoreRequests(){
		return requests.isEmpty();
	}
	
	public RentalRequest sendRequest() {
		if (anyMoreRequests())
			return requests.firstElement();
		else
			return null;
	}
	
	public String statOfCurrentRequest() {
		return requests.firstElement().statusReport();
	}
	
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
