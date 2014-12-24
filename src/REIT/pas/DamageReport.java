package REIT.pas;

public class DamageReport {

	private Asset asset;
	private int damagePercentage;
	
	public DamageReport() {
		asset = null;
		damagePercentage = 0;
	}
	
	protected void assignAsset(Asset theAsset) {
		asset = theAsset;
	}
	
	protected void updateDamage(int damage) {
		damagePercentage = damage;
	}
	
}
