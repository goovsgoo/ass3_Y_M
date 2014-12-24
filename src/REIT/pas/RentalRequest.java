package REIT.pas;

public class RentalRequest implements Comparable<RentalRequest>{
	private final String ID;
	private final String assetType;
	private final int assetSize;
	private final int stay;
	private Asset assetLinked;
	private String status;
	private String groupManager;
	
	public RentalRequest(String id, String type, int size, int duration, String manager) {
		this.ID = id;
		this.assetType = type;
		this.assetSize = size;
		this.stay = duration;
		this.assetLinked = null;
		this.status = "incomplete";
		this.groupManager = manager;
	}
	
	/**
	 * updates the request's status
	 * @param newStatus, the up to date status of the request.
	 */
	public void updateStatus(String newStatus) {
		this.status = newStatus;
	}

	/**
	 * assign an asset to request upon finding one
	 * @param assetFound, the appropriate asset to fulfill the request.
	 */
	public void LinkAsset(Asset assetFound) {
		this.assetLinked = assetFound;
	}
	
	/**
	 * the minimum asset size to fulfill the request.
	 * @return minimum number of rooms in the requested asset 
	 */
	public int minSizeRequested() {
		return assetSize;
	}
	
	// Overriding the compareTo method
	public int compareTo(RentalRequest other){
		return this.assetSize - other.assetSize;
	}
	   	   
	/**
	 * for overrides toString method  
	 */
	public String toString(){
		return this.ID;
	}
}
