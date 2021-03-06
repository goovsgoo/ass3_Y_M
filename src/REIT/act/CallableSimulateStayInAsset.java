package REIT.act;

import java.util.concurrent.Callable;

import REIT.pas.Customer;
import REIT.pas.Management;

public class CallableSimulateStayInAsset implements Callable<Double>{
	
	private Customer oneCustomer;
	private long timeInAsset;
		
	public CallableSimulateStayInAsset(Customer thiGuy, long staying){
		oneCustomer = thiGuy;
		timeInAsset = staying;
	}
	
	/**
	 * @Override call method.
	 * calculate the damage the customer did while staying
	 */
	public Double call() throws Exception {
		Thread.sleep(timeInAsset*Management.TIMEMULTI*24);
		return oneCustomer.calculateDemage()*(timeInAsset);
	}
	
}
