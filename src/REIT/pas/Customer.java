package REIT.pas;

import java.util.Random;

public class Customer {
	
/*
 * 3.2 Customer
This object contains the following elds: (1) Name (2) Vandalism type (3) Minimum damage (4)
Maximum damage.
Where vandalism type can be of three types: (i) Arbitrary (ii) Fixed (iii) None as detailed in
2.12.
The customer calculates damage percentage to the asset. If the damage type is xed, then the
average of minimumDamage and maximumDamage is returned. The customer does not alter the
health of the asset, only the RunnableCustomerGroupManager.
 */
	
	private final String name;
	private final String vandalismType;
	private final double minDemage;
	private final double maxDemage;
	
	public Customer(String customerName, String type, double min, double max) {
		name = customerName;
		vandalismType = type;
		minDemage = min;
		maxDemage = max;
	}
	
	/**
	 * calculates the damages caused by customer according to his type
	 * @return the damage caused by customer
	 */
	public double calculateDemage() {
		if (vandalismType.equals("Arbitrary")) {
			Random rand = new Random();
			return (minDemage + (maxDemage - minDemage) * rand.nextDouble());
		}
		else if (vandalismType.equals("Fixed")) {
			return ((maxDemage - minDemage)) / 2;
		}
		else {
			return 0.5;
		}
	}
}
