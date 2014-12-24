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
	
	public void updateStatus(String newStatus) {
		this.status = newStatus;
	}
	
	public void LinkAsset(Asset assetFound) {
		this.assetLinked = assetFound;
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
