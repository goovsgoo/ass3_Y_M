package REIT.pas;

import java.util.concurrent.*;
import java.lang.Math;
import java.awt.geom.Point2D;
import java.util.HashMap;


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
	private HashMap<AssetContent,Integer> AssetContentCollection;	
	private String Status;
	final private int COST;
	final private int SIZE;
	
	private Management management = Management.sample();
	
	
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
		AssetContentCollection = new HashMap<AssetContent, Integer>();
	}
		
	/**
	 * return the asset size.
	 * @return asset size.
	 */
	protected int assetSize() {
		return SIZE;
	}
	
	/**adds new Content and repairMultiplier to the AssetContentCollection
	 */
	protected void addNewContent(AssetContent content,int repairMultiplier){
		if (content == null)
			throw new RuntimeException("You cannot add null content to asset");
		if (!AssetContentCollection.containsKey(content))
			AssetContentCollection.put(content,new Integer(repairMultiplier));	
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

}
