package REIT.act;

import java.util.concurrent.ExecutionException;

import REIT.pas.CustomerGroupDetails;

public class RunnableCustomerGroupManager implements Runnable {

	private CustomerGroupDetails group;
	
	public void run() {
		while(group.anyMoreRequests()) { // keep runnig until all requests are fullfilled

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
}


