package REIT.pas;

public class DamageReport {
/////////////////////////////////////////חסר פה הערות

	private Asset asset;
	private int damagePercentage;
	
	public DamageReport() {
		asset = null;
		damagePercentage = 0;
	}
	
	public void assignAsset(Asset theAsset) {
		asset = theAsset;
	}
	
	public void updateDamage(int damage) {
		damagePercentage = damage;
	}
	
}
