package REIT.act;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import restaurant.actives.CallableCookWholeOrder;
import restaurant.passives.Management;
import restaurant.passives.Order;
import REIT.pas.Customer;
import REIT.pas.CustomerGroupDetails;
import REIT.pas.DamageReport;

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
			double damage = group.sleepInAsset();
			// create damage report
			
			}	
	}
}


