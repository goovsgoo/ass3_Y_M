package REIT.pas;

import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.*;
//import java.lang.Math;
//import java.awt.geom.Point2D;
//import java.util.HashMap;

/*
 * 2.3 Assets
Found in: Management, RunnableClerk
This object will hold a collection of Asset. And contain methods that are related to assets.
There are two objects to tasks for this class:
1. Retrieve the list of damaged assets.
2. A find method which returns to the clerk an asset which fits the type and size requirements.
 */

/**  @author Meni & Yoed
 * this class simulates an Assets.
 */
public class Assets{
	private Vector<Asset> AssetsCollection;	 //by size
	//private Management management = Management.instance();
	private static Assets SAMPLE = null;
	
	/** this method is a factury method.
	 *  cares that only once will be initialized an object
	 */
	static public Assets sample(){
		if (SAMPLE == null)
			SAMPLE = new Assets();
		return SAMPLE;
	}
	
	/**
	 * constructs a new Asset object by name.
	 */
	private Assets(){
		AssetsCollection = new Vector<Asset>();
	}
																		
	/**adds new Content and repairMultiplier to the AssetContentCollection
	 */
	protected void addNewAsset(Asset NewAsset){
		if (NewAsset == null)
			throw new RuntimeException("You cannot add null asset to assets");
		AssetsCollection.add(NewAsset);	
	}
	
	/**
	 * 
	 */
	public void FindFitRequest(int sizeOfAsset){
		    Iterator<Asset> i = AssetsCollection.iterator();
		    while (i.hasNext()) {
		      i.
		}
	}

}
