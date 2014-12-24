package REIT.pas;

public class Customer {
/////////////////////////////////////////חסר פה הערות
	private final String name;
	private final String vandalismType;
	private final int minDemage;
	private final int maxDemage;
	
	public Customer(String customerName, String type, int min, int max) {
		name = customerName;
		vandalismType = type;
		minDemage = min;
		maxDemage = max;
	}
	
}
