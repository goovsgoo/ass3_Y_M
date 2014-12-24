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
	private int repairMultiplier;
	//private int health;
	private HashMap<String, Integer> materials;
	private HashMap<String, Integer> tools;
	
	/**
	 * the constructor creates a AssetContent object by name.
	 * the constructor also creates two empty HashMap for tools and for materials.
	 * @param name of assetContent.
	 */
	public AssetContent(String name){
		repairMultiplier=1;
		//health=100;
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
		return this.NAME;
	}
	
	/**
	 * simulates the fix of the content.
	 * the Thread sleep while fix of the content.
	 * @param time , (100-health)*RepairMultiplier in milesec.
	 * @throws InterruptedException
	 */
	public void fix(double time) throws InterruptedException{
		Thread.sleep(Math.round(time));
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
	 * update repairMultiplier of content
	 * @param repairMultiplier2
	 */
	public void updateMultiplier(int repairMultiplier2) {
		repairMultiplier=repairMultiplier2;
	}
}
