package REIT.pas;

import java.util.concurrent.*;
import java.lang.Math;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;
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
	private Management management = Management.sample();
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
	}
	
	/**
	 * for maintains man use - take from warehouse all the stuff you need to repair the asset's contents
	 * calculate the time of the repair
	 * sleep that time
	 * release all the tools used back to the warehouse
	 * @throws InterruptedException 
	 */
	public void fixAsset() throws InterruptedException {
		warehouse.acquire(this);
		long cost = calculateCost(); 
		Thread.sleep(cost);
		warehouse.release(this);
		health = 100;
		updateStatus();
		changeFixingStatus();
		// management.endOfFixing();
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
			if (content.returnCopyTools().containsKey(testedTool.toString()))
				needed = Math.max(needed, content.returnCopyTools().get(testedTool).intValue());	
		}
		return needed;
	}
	
	/*
	public void fixAsset() {
		for (int i =0 ; i< assetContentCollection.size(); i++){
			warehouse.acquire(assetContentCollection.get(i));
		}

	}
	*/
	
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
	 * @Override
	 */
	@Override											
	public String toString(){
		StringBuilder printOut = new StringBuilder();
		printOut.append("Asset: ");
		printOut.append("[ID: ").append(ID).append("][location: ").append(LOCATION.getX()).append(",").append(LOCATION.getX());
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
	 * @Override
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
	 * Calculate tools and materials names for camper
	 */
	/*
	private void calculateToolsMaterials() {
		int i;
		//from all the name in the project
		Vector<String> copyMaterialsNames = management.returnCopyMaterial();
		Vector<String> copyToolsNames = management.returnCopyTools();
		
		for(i = 0;i< assetContentCollection.size();i++){
			
			//from this Content
			HashMap<String, Integer> copyTools = assetContentCollection.get(i).returnCopyTools();
			HashMap<String, Integer> copyMaterials = assetContentCollection.get(i).returnCopyMaterials();
			
			/*
			for (Entry<String, Integer> entry : copyTools.entrySet()) {
				if( !copyToolsNames.contains(entry.getKey()) )
					copyToolsNames.add(entry.getKey());
			}
			for (Entry<String, Integer> entry : copyMaterials.entrySet()) {
				if( !copyMaterialsNames.contains(entry.getKey()) )
					copyMaterialsNames.add(entry.getKey());
			}
			*//*
		}
	} 
	*/
	
}
