package REIT.pas;

import java.util.concurrent.*;
import java.lang.Math;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

import REIT.act.Dish;
import REIT.act.RunnableCookOneDish;


//import restaurant.passives.Order;
//import restaurant.passives.Dish;
//import restaurant.actives.RunnableCookOneDish;


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
	protected void updateStatus() {
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
	 * 	
	 */
	protected void fixAsset() {
		for (int i =0 ; i< assetContentCollection.size(); i++){
			warehouse.acquire(assetContentCollection.get(i));
		}

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
	@Override												//////need to write again!!!!!!!1
	public String toString(){
		StringBuilder printOut = new StringBuilder();
		printOut.append(ID).append(":/tType: ").append(TYPE).append(",\tStatus: ").append(Status).append(",\tcost: ").append(COST).append(",\tsize: ").append(SIZE).append(",\tlocation: [").append(LOCATION.getX()).append(", ").append(LOCATION.getY()).append("],\t Content in Order: ");
		for (AssetContent assetContent : AssetContentCollection.keySet()){
			printOut.append(assetContent.toString()).append("(").append(AssetContentCollection.get(assetContent)).append(") ");
		}
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
		{}
		if(assetContentCollection.get(i).equals(assetContentName))
			return assetContentCollection.get(i);
		return null;
	}
	
	
}
