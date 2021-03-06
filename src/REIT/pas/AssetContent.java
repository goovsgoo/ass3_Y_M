package REIT.pas;

import java.util.HashMap;

/*
 *
 * 
 * 
 * 2.2 AssetContent
Found in: Asset
Asset content is an object which holds the details of one item found in the asset. An asset typically
has collection of these items.
This object will hold the following fields: (1) Name (2) Health (3) Repair Cost multiplier
1. The health of the asset will deteriorate as customers use the asset. This variable will reflect the
health of a single item found in the asset.
2. Repair cost multiplier: Calculating the time it will take the Maintenance Person to repair an
item found in the asset equals to (100-Health)*Repair Cost Multiplier. The higher the multiplier
the higher the time it takes to repair. Health begins at 100, can be fractions, and not below 0.

/**
 * 
 * @author Meni & Yoed
 * Asset content is an object which holds the details of one item found in the asset.
 */

public class AssetContent {
	final private String NAME;
	private float repairMultiplier;
	private HashMap<String, Integer> materials;
	private HashMap<String, Integer> tools;
	
	/**
	 * the constructor creates a AssetContent object by name.
	 * the constructor also creates two empty HashMap for tools and for materials.
	 * @param name of assetContent.
	 */
	public AssetContent(String name){
		repairMultiplier=1;
		this.NAME = name;
		tools = new HashMap<String, Integer>();
		materials = new HashMap<String, Integer>();
	}
	
	/**
	 * if the new material exists in the assetContent material list add only it's quantity.
	 * else adds the new material object to the list.
	 * @param name of material to add.
	 * @param quantity of material to add.
	 */
	protected void addMaterial(String name, int quantity){
		if (!materials.containsKey(name))
			materials.put(name, new Integer(quantity));
		else 
			materials.put(name, new Integer (materials.get(name)+quantity));
	}
	
	/**
	 * if the new tool exists in the assetContent tools list add only it's quantity.
	 * else adds the new tool object to the list.
	 * @param name of tool to add.
	 * @param quantity of tool to add.
	 */
	protected void addTool(String name, int quantity){
		if (!tools.containsKey(name))
			tools.put(name, new Integer(quantity));
		else 
			tools.put(name, new Integer (tools.get(name)+quantity));
	}
	
	/** Look up the materials list, do I need this material for fix the content?
	 * @param testedMaterial , Search Is there such material in the list.
	 * @return true , is need.
	 * @return false , is not need.
	 */
	protected int isNeeded(RepairMaterial testedMaterial){
		if  (materials.containsKey(testedMaterial.toString()))
			return materials.get(testedMaterial.toString());
		return 0;
	}
	
	/**Look up the tool list, do I need this tool for fix the content?
	 * @param testedTool , Search Is there such tool in the list.
	 * @return true , is need.
	 * @return false , is not need.
	 */	
	protected int isNeeded(RepairTool testedTool){
		if (tools.containsKey(testedTool.toString()))
				return tools.get(testedTool.toString());
		return 0;
	}
	
	/**
	 * overrides toString method  
	 * @Override
	 */
	@Override
	public String toString(){
		StringBuilder printOut = new StringBuilder();
		printOut.append("Asset Content: ");
		printOut.append("[Name: ").append(NAME).append("][Repair Multiplier: ").append(repairMultiplier);
		for(Object objname:tools.keySet()) {
			printOut.append("][Tool: ").append(objname);
			printOut.append("][Tool quantity: ").append(tools.get(objname));
		}
		for(Object objname:materials.keySet()) {
			printOut.append("][Material: ").append(objname);
			printOut.append("][Material quantity: ").append(materials.get(objname)).append("]");
		}
		return printOut.toString();
	}
	
	/**
	 * Compare this.name and String.
	 * @param AssetContentName, name of Content to compare.
	 * @return true, The same name.
	 * @return false,not The same name.
	 */
	public boolean equals(String AssetContentName){
		return this.NAME.equals(AssetContentName);
	}

	/**
	 * @return the repairMultiplier
	 */
	public double multiplier() {
		return repairMultiplier;
	}
	
	/**
	 * update repairMultiplier of content
	 * @param repairMultiplier2
	 */
	public void updateMultiplier(float repairMultiplier2) {
		repairMultiplier = repairMultiplier2;
	}
	
	/**
	 * get copy of list of materials
	 * @return copyMaterials, copy of HashMap for materials
	 */
	public HashMap<String, Integer> returnCopyMaterials() {
		HashMap<String, Integer> copyMaterials = new HashMap<String, Integer>(materials);
		return copyMaterials;
	}
	
	/**
	 * get copy of list of tools
	 * @return copyTools, copy of HashMap for tools
	 */
	public HashMap<String, Integer> returnCopyTools() {
		HashMap<String, Integer> copyTools = new HashMap<String, Integer>(tools);
		return copyTools;
	}
}
