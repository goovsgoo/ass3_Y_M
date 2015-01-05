package REIT.pas;

public class DamageReport {
	
/*
 * 2.12 DamageReport
Found in:
A damage report will be generated once a CustomerGroup nishes simulating its CallableSimulat-
eStayInAsset, and the damage percentage returned, then the RunnableCustomerGroupManager will
create the DamageReport object which will hold the following elds: (1) Asset (2) Damage percentage.
This object will be sent to the Management for further handling.
The damage percentage in the damage report is the sum of all damage percentages returned by all
the customers of the group.
 */

	private Asset asset;
	private double damagePercentage;
	
	public DamageReport() {
		asset = null;
		damagePercentage = 0;
	}
	
	/**
	 * link set to the report
	 * @param theAsset
	 */
	protected void assignAsset(Asset theAsset) {
		asset = theAsset;
	}
	
	/**
	 * update the damage in the report
	 * @param damage
	 */
	protected void updateDamage(double damage) {
		damagePercentage = damage;
	}
	
	/**
	 * return string print the damage report
	 * @param printDamageReport
	 */
	protected String printDamageReport() {
		StringBuilder printOut = new StringBuilder("-DamageReport-");
		printOut.append("asset : ").append(asset);
		printOut.append(" damagePercentage : ").append(damagePercentage);
		return printOut.toString();
	}
	
}
