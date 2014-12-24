package REIT.pas;

public class DamageReport {
/////////////////////////////////////////חסר פה הערות

	private Asset asset;
	private double damagePercentage;
	
	public DamageReport() {
		asset = null;
		damagePercentage = 0;
	}
	
	protected void assignAsset(Asset theAsset) {
		asset = theAsset;
	}
	
	protected void updateDamage(double damage) {
		damagePercentage = damage;
	}
	
}
