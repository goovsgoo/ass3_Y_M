package REIT.act;

import java.util.concurrent.*;
import REIT.pas.CustomerGroupDetails;
import REIT.pas.Management;

public class RunnableCustomerGroupManager implements Runnable {

	private CustomerGroupDetails group;
	private String name;
	
	public RunnableCustomerGroupManager(CustomerGroupDetails myGroup) {
		group = myGroup;
		name = myGroup.managerName();
	}
		
	/**
	 * @Override run method.
	 * simulates a group manager - the staying in the asset and the updates that comes afterwards
	 */
	public void run() {
		// keep running until all requests are fulfilled
		while(group.anyMoreRequests()) { 

			// wait for a request to get handled by a clerk
			//while (group.statOfCurrentRequest() != "InProgress");
			try {
				group.getAsset();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Management.LOGGER.info(new StringBuilder(group.managerName()).append(" is now get into asset ").toString());	
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
			//we need to pop the request
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


