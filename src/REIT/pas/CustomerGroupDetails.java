package REIT.pas;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

public class CustomerGroupDetails {
	private final String groupManager;
	private Vector<RentalRequest> request;
	private ArrayList<Customer> customers;
		
	public CustomerGroupDetails(String manager) {
		groupManager = manager;
		requests = new PriorityBlockingQueue<RentalRequest>();
		customers = new ArrayList<Customer>();
	}
	
	public boolean addRequest(RentalRequest request) {
		return requests.offer(request);
	}
	
	public boolean addCustomer(Customer newGuy) {
		return customers.add(newGuy);	
	}
	  	   
	/**
	 * for overrides toString method  
	 */
	public String toString(){
		return this.groupManager;
	}

}
