package REIT.pas;

import java.util.concurrent.*;
import java.util.Comparator;
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
public class Requests{
	private PriorityBlockingQueue<RentalRequest> rentalRequestCollection;	 //by size
	//private Management management = Management.instance();
	private static Requests SAMPLE = null;
	 Comparator<RentalRequest> comparatorSize = new SizeComparator();
	
	/**
	 * constructs a new Asset object by name.
	 */
	private Requests(){
		rentalRequestCollection = new PriorityBlockingQueue<RentalRequest>(32767,comparatorSize);
	}
	
	/** this method is a factury method.
	 *  cares that only once will be initialized an object
	 */
	static public Requests sample(){
		if (SAMPLE == null)
			SAMPLE = new Requests();
		return SAMPLE;
	}
	
	/**adds new Request
	 */
	protected void addNewAsset(RentalRequest NewRequest){
		if (NewRequest == null)
			throw new RuntimeException("You cannot add null Request to Requests");
		rentalRequestCollection.put(NewRequest);	
	}
	
	/**
	 * 
	 */
	//public void FindFitRequest(int sizeOfAsset){
	//	for(int i = 0;  && i< rentalRequestCollection.size();i++){
	//		
	//	}
	//}
	


}


