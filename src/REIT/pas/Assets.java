package REIT.pas;

import java.util.Collections;

import java.util.Vector;



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
	private Vector<Asset> AssetsCollection;	
	private static Assets SAMPLEAsset = null;
	
	/** this method is a factury method.
	 *  cares that only once will be initialized an object
	 * @return sample of Assets
	 */
	static public Assets sampleAsset(){
		if (SAMPLEAsset == null)
			SAMPLEAsset = new Assets();
		return SAMPLEAsset;
	}
	
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
	 * find a min size asset for the requests size.
	 * @param sizeOfAsset, size of asset by the amount of people 
	 * @return assset , for rent if find
	 * @return null , if not find
	 */
	public Asset FindFitRequest(int sizeOfAsset){
		int index;
		for(index=0;index < AssetsCollection.size();index++){
			if ( (AssetsCollection.get(index).assetSize()>=sizeOfAsset )
					&& ( AssetsCollection.get(index).statusAssest() == "AVAILABLE" ) ){
				return AssetsCollection.get(index);
			}	
		}
			return null;
	}
	
	protected int size(){
		return this.AssetsCollection.size();
	}
	
	/**
	 * finds an asset that we need to fix
	 * @return Asset
	 */
	public Asset findAssetToFix(){
		synchronized(this.getClass()){
			boolean found = false;
			int i = 0;
			Asset matchingAsset = null;
			while (!found && i < size()) {
				if (AssetsCollection.get(i).assetHealth() <= 65 && AssetsCollection.get(i).canTheMaintenceManCome()) {
					matchingAsset = AssetsCollection.get(i);
					found = true;
					matchingAsset.changeFixingStatus();
				}
				
				i++;
			}
			return matchingAsset;
		}
		
	}
	
	/**
	 * sort the asset by size
	 * @param sizeOfAsset, size of asset by the amount of people 
	 */
	public void sort(){
		Collections.sort(AssetsCollection);
	}

}
