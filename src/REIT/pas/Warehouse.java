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
 * 		acquire(AssetContent assetContent) - for all tools and materials in the assetContent, calls for the tools' acquire() method and subtracts the material' quantity
 * 		release(AssetContent assetContent) - for all tools and materials in Dish, calls for the tools' release()
 * 
 */


public class Warehouse implements WarehouseInterface {
	
	private ArrayList<RepairTool> tools;
	private ArrayList<RepairMaterial> RepairMaterials;
	private static Warehouse SAMPLE = null;
	private Statistics statistics = Statistics.instance();
	
	private Warehouse(){
		tools = new ArrayList<RepairTool>();
		RepairMaterials = new ArrayList<RepairMaterial>();
	}
	
	/** this method is a factury method.
	 *  cares that only once will be initialized an object
	 *  @return sample of Warehouse
	 */
	static public Warehouse sample(){
		if (SAMPLE == null)
			SAMPLE = new Warehouse();
		return SAMPLE;
	}
	
	
	/** this method simulates a maintenance man asking to take what needed for a certain asset repair.
	 * the method calls for the proper tools and do acquire() method.
	 * @param assetof the tools and materials needed to be acquired.
	 * @Override
	 */
	@Override
	public void acquire(Asset asset) {
		// acquire RepairTool
		for (int i = 0 ; i < tools.size() ; i++){
			RepairTool tool = tools.get(i);
			int quantity = asset.isNeeded(tool);
			if (quantity > 0) {
				tool.acquire(quantity);
				statistics.addUsedTool(tool);
			}
		}																
		// acquire materials
		for (int i = 0 ; i < RepairMaterials.size() ; i++){
			RepairMaterial material = RepairMaterials.get(i);
			int quantity = asset.isNeeded(material);
			if (quantity > 0){
				material.acquire(quantity);
				statistics.consumeMaterial(material, quantity);
			}
		}
	}													
	
	/** this method returning the tools used by the maintenance man.
	 * the method call the tool release() method.
	 * @param asset of the tools needed to be release.
	 * @Override
	 */
	@Override
	public void release(Asset asset) {
		for (int i = 0 ; i < tools.size()  ; i++){
			RepairTool tool = tools.get(i);
			int quantity = asset.isNeeded(tool);
			if (quantity > 0)
				tool.release(quantity);
		}
	}

	/** this method adds a new tool to the Warehouse.
	 * @param tool to add.
	 */
	protected void addTool(RepairTool newTool){
		for (int i = 0 ; i < tools.size(); i++){
			RepairTool tool = tools.get(i);
			if (tool.equals(newTool)){
				tool.release(newTool.quantity());
				return;
			}
		}
		tools.add(newTool);
	}
	
	/**
	 * this method adds new materials to the Warehouse.
	 * @param materials to add.
	 */
	protected void addMaterials(RepairMaterial newMaterials){
		for (int i = 0 ; i < RepairMaterials.size(); i++){
			RepairMaterial material = RepairMaterials.get(i);
			if (material.equals(newMaterials)){
				material.add(newMaterials);
				return;
			}
		}
		RepairMaterials.add(newMaterials);
	}
	
	/**
	 * this method return the tool by name,  for Parser use.
	 * @return RepairTool 
	 */
	//protected RepairTool findToolByName(String name){
	//	for (int i = 0 ; i < tools.size(); i++){
	///		RepairTool toolForComper = tools.get(i);
	//		if (toolForComper.toString() == name)
	//			return toolForComper;
	//	}
	//	return null;
	//}
	
	/**
	 * this method return the tool by name,  for Parser use.
	 * @return RepairMaterial 
	 */
	//protected RepairMaterial findMaterialByName(String name){
	//	for (int i = 0 ; i < tools.size(); i++){
	//		RepairMaterial materialForComp = RepairMaterials.get(i);
	//		if (materialForComp.toString() == name)
	//			return materialForComp;
	//	}
	//	return null;
	//}

}

