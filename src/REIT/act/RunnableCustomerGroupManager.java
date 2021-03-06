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
	
		while(group.anyMoreRequests()) { 
			try {
				group.getAsset();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Management.LOGGER.info(new StringBuilder(group.managerName()).append(" is now get into asset ").toString());	
			try {
				group.sleepInAsset(); } catch (InterruptedException e) {e.printStackTrace();} catch (ExecutionException e) {e.printStackTrace();}
		}	
		Management.LOGGER.info(new StringBuilder(group.managerName()).append(" is now exit from Simulation ").toString());	
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


