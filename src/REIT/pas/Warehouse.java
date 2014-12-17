package REIT.pas;

import java.util.ArrayList;

/**
 * 
 * @author Meni & Yoed
 * Warehouse class implement the REIT storage
 * 
 * it holds two databases:
 * 		ArrayList<RepairTool> - list of tools being implemented with semaphores.
 * 		ArrayList<RepairMaterial> - list of materials
 *  
 * the class offers two public methods:
 * 		acquire(RepairRequirements repairRequirements) - for all tools and materials in the repairRequirements, calls for the tools' acquire() method and subtracts the material' quantity
 * 		release(RepairRequirements repairRequirements) - for all tools and materials in Dish, calls for the tools' release()
 * 
 */


public class Warehouse implements WarehouseInterface {
	
	private ArrayList<RepairTool> tools;
	private ArrayList<RepairMaterial> RepairMaterials;
	
	private static Warehouse SAMPLE = null;

	
	private Warehouse(){
		tools = new ArrayList<RepairTool>();
		RepairMaterials = new ArrayList<RepairMaterial>();
	}
	
	static public Warehouse sample(){
		if (SAMPLE == null)
			SAMPLE = new Warehouse();
		return SAMPLE;
	}
	
	
	/**
	 * this method simulates a maintenance man asking to take what needed for a certain asset repair.
	 * the method calls for the proper tools and do acquire() method.
	 * @param repairRequirements of the tools and materials needed to be acquired.
	 */
	public void acquire(RepairRequirements repairRequirements) {
		// acquire RepairTool
		for (int i = 0 ; i < tools.size() ; i++){
			RepairTool tool = tools.get(i);
			int quantity = repairRequirements.isRequired(tool);
			if (quantity > 0)
				tool.acquire(quantity);
		}																// Yoed stop here///////////////////////////////////////
		// acquire materials
		for (RepairMaterial ingredient : ingredients){
			int quantity = RepairRequirements.isRequired(ingredient);
			if (quantity > 0){
				ingredient.acquire(quantity);
			}
		}
	}

	/**
	 * this method simulates a chef returning the tools used to prepare a dish.
	 * the method calls for the appropriate tools' release() method.
	 * @param dish which order was finished and it's tools are released.
	 */
	public void release(Dish dish) {
		for (int i = tools.size()-1 ; i >= 0 ; i--){
			KitchenTool tool = tools.get(i);
			int quantity = dish.isRequired(tool);
			if (quantity > 0)
				tool.release(quantity);
		}
	}

	/**
	 * this method adds a new KitchenTool to the Warehouse.
	 * @param tool to be added.
	 */
	protected void addTool(KitchenTool newTool){
		for (KitchenTool tool : tools){
			if (tool.equals(newTool)){
				tool.release(newTool.quantity());
				return;
			}
		}
		tools.add(newTool);
	}
	
	/**
	 * this method adds a new ingredient to the Warehouse.
	 * @param ingredient to be added.
	 */
	protected void addIngredient(Ingredient newIngredient){
		for (Ingredient ingredient : ingredients){
			if (ingredient.equals(newIngredient)){
				ingredient.add(newIngredient);
				return;
			}
		}
		ingredients.add(newIngredient);
	}


}






	/**
	 * this method simulates a maintenance man asking to take what needed for a certain asset repair.
	 * the method calls for the appropriate tools' acquire() method.
	 * @param repairRequirements of the tools and materials needed to be acquired.
	 */
	public void acquire(RepairRequirements repairRequirements) {
		// TODO Auto-generated method stub

	}

	/**
	 * this method simulates a maintenance man returning the tools used to fix an asset.
	 * the method calls for the appropriate tools' release() method.
	 * @param repairRequirements which was used and its tools are released.
	 */
	public void release(RepairRequirements repairRequirements) {
		// TODO Auto-generated method stub

	}

	/**
	 * this method adds a new repair tool to the Warehouse.
	 * @param tool to be added.
	 */
	protected void addTool(RepairTool tool){
		// TODO
	}
	
	/**
	 * this method adds a new material to the Warehouse.
	 * @param material to be added.
	 */
	protected void addMaterial(RepairMaterial material){
		// TODO
	}


}
