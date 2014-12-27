package REIT.act;

import java.util.concurrent.ExecutionException;

import REIT.pas.CustomerGroupDetails;

public class RunnableCustomerGroupManager implements Runnable {

	private CustomerGroupDetails group;
	private String name;
	
	public RunnableCustomerGroupManager(CustomerGroupDetails myGroup) {
		group = myGroup;
	}
		
	/**
	 * @Override run method.
	 * simulates a group manager - the staying in the asset and the updates that comes afterwards
	 */
	public void run() {
		// keep running until all requests are fullfilled
		while(group.anyMoreRequests()) { 

			// wait for a request to get handled by a clerk
			while (group.statOfCurrentRequest() == "INCOMPLETE")
				 try {
			            wait();
			        } catch (InterruptedException e) {}
			
			// wait for stay in asset to end and update asset's total damage and status
			try {
				group.sleepInAsset();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	/**
	 * checks if the manager fits to a given name
	 * @param name
	 * @return true if this is the manager name
	 */
	public boolean equals(String name) {
		return (this.name == name);
	}
}


