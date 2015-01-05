package REIT.pas;

/*
2.8 RepairMaterial

Found in: Warehouse

fields: (1) Name (2) Quantity.

This object is destined to be in the warehouse. It will hold the name of the material, as well as the
current quantity found in the warehouse. Ensure thread safety in this object.
 */

/**
 * 
 * @author Yoed & Meni
 * this class implement an RepairMaterial.
 * 
 */
public class RepairMaterial {
	
	private String name;
	private int quantity;
	
	/**
	 * constructs new RepairMaterial object by name and quantity.
	 * @param name
	 * @param quantity
	 */
	public RepairMaterial(String name, int quantity){
		this.name = name;
		this.quantity = quantity;
	}
	
	/**
	 * compare between RepairMaterial object by name.
	 * @param other RepairMaterial for compare.
	 * @return true - name match.
	 * @return false - name not match.
	 */
	protected boolean equals(RepairMaterial other){
		return this.name.equals(other.name);
	}
	
	/**
	 * compare between this.name and a String.
	 * @param otherNameString for compare this.name.
	 * @return true - name match.
	 * @return false - name not match.
	 */
	protected boolean equals(String otherNameString){
		return this.name.equals(otherNameString);
	}
	
	/**
	 * if other.name equal this.name then add other.quantity to this.quantity 
	 * @param other RepairMaterial object for add to this.quantity
	 */
	protected void add(RepairMaterial other){
		if (this.equals(other))
			this.quantity = this.quantity + other.quantity;
	}
	
	/**
	 * add to this.quantity
	 * @param i - Number of additions
	 */
	protected void add(int i){
		this.quantity = this.quantity + i;
	}
	
	/**
	 * simulates acquiring of one RepairMaterial by Decrease this.quantity by 1.
	 */
	protected synchronized void acquire(){
		if (quantity > 0){
			this.quantity--;
		} else {
			throw new RuntimeException("You can not get more "+this); 
		}
	}
	
	/**
	 * Simulates taking plenty of RepairMaterial by Decrease this.quantity by i. 
	 * @param i Material to acquire.
	 */
	protected synchronized void acquire(int i){
		if (quantity >= i){
			this.quantity = this.quantity -i;
			Statistics.instance().consumeMaterial(this, i);		
		} else {
			throw new RuntimeException( "You can not get "+ i +" "+ this); 
		}
	}
	
	
	/**													
	 * checks the available quantity. used for testing.
	 * @return this.quantity.
	 */
	protected int quantity(){  						
		return this.quantity;
	}
	
	/**
	 * for overrides toString method  
	 */
	public String toString(){
		return this.name;
	}
}
