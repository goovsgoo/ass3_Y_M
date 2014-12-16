package REIT.testing;

import java.util.ArrayList;

import REIT.passives.RepairMaterial;
import REIT.passives.RepairTool;

public class WarehouseTester extends Warehouse {
	
	private ArrayList<RepairTool> tools;
	private ArrayList<RepairMaterial> materials;
	
	public WarehouseTester(){
		tools = new ArrayList<RepairTool>();
		materials = new ArrayList<RepairMaterial>();
	}
	
	protected int toolTypeCount(){
		return tools.size();
	}
	
	protected int materialsTypeCount(){
		return materials.size();
	}
	
	protected RepairTool getTool(int i){
		return tools.get(i);
	}
	
	protected RepairMaterial getMaterial(int i){
		return materials.get(i);
	}
	
	protected int toolAvailability(int i){
		return tools.get(i).quantity();
	}
	
	protected int materialQantity(int i){
		return materials.get(i).quantity();
	}
	
	protected void clear(){
		tools.clear();
		materials.clear();
	}
	
}
