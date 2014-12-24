package REIT.pas;

import java.util.Random;

public class Customer {

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
	
	public double calculateDemage() {
		if (vandalismType.equals("ARBITRARY")) {
			Random rand = new Random();
			return (minDemage + (maxDemage - minDemage) * rand.nextDouble());
		}
		else if (vandalismType.equals("FIXED")) {
			return ((maxDemage - minDemage)) / 2;
		}
		else {
			return 0.5;
		}
	}
}
