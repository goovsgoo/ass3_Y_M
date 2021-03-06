package REIT.pas;


import java.lang.Math;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Vector;

/*
 * 2.1 Asset
Found in: Management
This object will hold information of a single asset.
An asset object must hold the following fields: (1) Name (2) Type (3) Location (4) Collection of
AssetContent (5) Status (6) Cost per night (7) Size

1. Type: The type will be one of a list of types provided as an input file.
2. Location: Coordinates are to be stored as (x,y) coordinates in two dimensional space. The
distance is to be calculated as the Euclidean distance between two different coordinates.
3. Status: One of three different options: (i) AVAILABLE (ii) BOOKED (iii) OCCUPIED (iv)
UNAVAILABLE. Where the asset is vacant if it is not currently being used. Booked, where the
asset has been booked by a customer group but not occupied yet. Occupied, if the properly is
currently being used by a customer, and unavailable if the asset is not t for renting, as long it is
not repaired. Repairing a asset requires a collection of exhaustible repair materials, and different
repair tools, and will be detailed later on.
4. Size: How many people the asset can t in.
 */

/** 
 * @author Meni & Yoed
 * this class simulates an Asset.
 */
public class Asset implements Comparable<Asset>{

	final private String TYPE;
	final private int ID;
	final private Point2D.Double LOCATION;
	private Vector<AssetContent> assetContentCollection;	
	private String status;
	final private int COST;
	final private int SIZE;
	private double health;
	private Management management ;
	boolean fixing;
	
	private static Warehouse warehouse = Warehouse.sample();
	
	
	/**
	 * constructs a new Asset object by name.
	 * @param id, name of asset
	 * @param assetType, type of asset
	 * @param assetSize, size of asset by the amount of people 
	 * @param location, (x,y) of asset
	 * @param assetCostPerNight, cost per night
	 */
	public Asset(int id,String assetType,int assetSize,Point2D.Double location,int assetCostPerNight){
		this.ID = id;
		this.TYPE = assetType;
		this.SIZE = assetSize;
		this.LOCATION = location;
		this.COST = assetCostPerNight;
		assetContentCollection = new Vector<AssetContent>();
		status="AVAILABLE";
		health=100;
		fixing = false;
	}
	
		
	/**
	 * return the asset size.
	 * @return asset size.
	 */
	protected int assetSize() {
		return SIZE;
	}
	/**
	 * show health of asset
	 * @return health
	 */
	protected double assetHealth() {
		return health;
	}
	
	/**
	 * Estimate the break of the house after a group of renters gone
	 */
	protected void breakThehouse(double demage) {
		health = health - demage;
		Management.LOGGER.info(new StringBuilder("asset-").append(ID).append(" health is ").append(health).toString());	
	}
	
	/**
	 * 
	 * @return ID
	 */
	public int assetID() {
		return ID;
	}
	
	/**
	 * Updates the state of the house, according to the order
	 */
	public  void updateStatus() {
		if(status=="AVAILABLE")
			status="BOOKED";
		else if(status=="BOOKED")
			status="OCCUPIED";
		else if(status=="OCCUPIED")
			status="UNAVAILABLE";
		else if(status=="UNAVAILABLE")
			status="AVAILABLE";
		Management.LOGGER.info(new StringBuilder("asset - ").append(ID).append(" is now ").append(status).toString());
	}
	
	/**
	 * for maintains man use - take from warehouse all the stuff you need to repair the asset's contents
	 * calculate the time of the repair
	 * sleep that time
	 * release all the tools used back to the warehouse
	 * @throws InterruptedException 
	 */
	public void fixAsset() throws InterruptedException {
		management = Management.sample();
		
		warehouse.acquire(this);
		long cost = calculateCost(); 
		Thread.sleep(cost);
		warehouse.release(this);
		health = 100;
		updateStatus();
		changeFixingStatus();
		management.decreaseNumAssetToFix();
		Management.LOGGER.info(new StringBuilder("Fixed asset - ").append(ID).toString());
	}
	
	/**
	 * @return the total cost of repairing the whole asset
	 */
	private long calculateCost() {
		double fixCost = 0;
		for (AssetContent content : assetContentCollection) {
			fixCost += content.multiplier()*health;
		}
		return (long) fixCost;
	}
	
	/**
	 * checks how much material is needed for the repair of the asset
	 * @param testedMaterial - do we need this material
	 * @return amount of material needed
	 */
	protected int isNeeded(RepairMaterial testedMaterial){
		int needed = 0;
		for (AssetContent content : assetContentCollection) {
			if  (content.returnCopyMaterials().containsKey(testedMaterial.toString()))
				needed += content.returnCopyMaterials().get(testedMaterial.toString());	
		}
		return needed;
	}
	
	/**
	 * checks how much tool is needed for the repair of the asset
	 * @param testedTool - do we need this tool
	 * @return amount of tool needed
	 */
	protected int isNeeded(RepairTool testedTool){
		int needed = 0;
		for (AssetContent content : assetContentCollection) {
			if (content.returnCopyTools().containsKey(testedTool.toString())){
				HashMap<String, Integer> copyOfTools = content.returnCopyTools();
				String toolNme = testedTool.toString();
				int amount = copyOfTools.get(toolNme).intValue();
				needed = Math.max(needed, amount);
			}
		}
		return needed;
	}
	
	/**adds new Content and repairMultiplier to the AssetContentCollection
	 */
	protected void addNewContent(AssetContent content){
		if (content == null)
			throw new RuntimeException("You cannot add null content to asset");
		if (!assetContentCollection.contains(content)){
			assetContentCollection.add(content);
		}
	}
	
	/**
	 * switch the fixing status (is this asset being fixed)
	 */
	protected void changeFixingStatus() {
		fixing = !fixing;
	}
	
	/**
	 * checks if the asset is empty and waiting for further handling before 
	 * getting back to the market
	 * @return asset where a group just ended staying and no maintains man attached
	 */
	protected boolean canTheMaintenceManCome() {
		return (!fixing && status == "UNAVAILABLE");
	}
	
	/**
	 * Compare this.name and String.
	 * @param id, name of Content to compare.
	 * @return true, The same name.
	 * @return false,not The same name.
	 */
	public boolean equals(int id){
		return ID==id;
	}
	
	/**
	 * @Override toString
	 */
	@Override											
	public String toString(){
		StringBuilder printOut = new StringBuilder();
		printOut.append("Asset: ");
		printOut.append("[ID: ").append(ID).append("type: ").append(TYPE).append("][location: ").append(LOCATION.getX()).append(",").append(LOCATION.getX());
		printOut.append("][Status: ").append(status).append("][Cost: ").append(COST).append("][Size: ").append(SIZE);
		printOut.append("][Health: ").append(health).append("][Fixing: ").append(fixing);
		printOut.append("] Contents : { ");
		for(int i = 0;i< assetContentCollection.size();i++){
			printOut.append("[").append(assetContentCollection.get(i).toString()).append("]");
		}
		printOut.append("} ");
		return printOut.toString();
	}
	
	/**
	 * @Override compareTo
	 * compare between this.size and other's size.
	 */
	@Override
	public int compareTo(Asset other) {
		return this.SIZE - other.SIZE;
	}
	
	/**
	 * get Content By Name
	 * @param assetContentName
	 * @return AssetContent , that equals to the name
	 */
	public AssetContent getContentByName(String assetContentName) {
		int i;
		for(i = 0;i< assetContentCollection.size() || assetContentCollection.get(i).equals(assetContentName);i++)
		{
		if(assetContentCollection.get(i).equals(assetContentName))
			return assetContentCollection.get(i);
		}
		return null;
	}
	
	/**
	 * @return the asset address
	 */
	public Point2D.Double adress() {
		return LOCATION;
	}
	
	
	/**
	 * @return the status handling 
	 */
	public String statusAssest(){
		return status;
	}

	/**
	 * @return the cost per night
	 */
	public int cost() {
		return COST;
	}
}
