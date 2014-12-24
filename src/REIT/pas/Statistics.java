package REIT.pas;

import java.util.ArrayList;
import java.util.HashMap;

/*3.3 Statistics
Found in: Management
This object contains the following fields::
(1) Money gained 
(2) Rentals 
(3) Collection of repair tools 
(4) Collection of repair materials.
1. Money gained: The income from rentals.
2. Rental requests: Collection of rental requests, including their information.
3. Repair tools: Tools used in the process.
4. Repair materials: Materials consumed and their quantity.
Updating this object may be done from anywhere in the program, however it is advised to do so in
minimal amount of places. */

public class Statistics {
	
	private static Statistics SIMPLE = null;
	
	private int rewards;
	private ArrayList<RentalRequest> requests;
	private ArrayList<RepairTool> usedRepairTools;
	private HashMap<String, int[]> usedRepairMaterials;
		
	private Statistics(){
		rewards = 0;
		requests = new ArrayList<RentalRequest>();
		usedRepairTools = new ArrayList<RepairTool>();
		usedRepairMaterials = new HashMap<String, int[]>();
	}
	
	/**
	 * 
	 * @return instance of Statistics.
	 */
	static public Statistics instance(){
		if (SIMPLE == null)
			SIMPLE = new Statistics();
		return SIMPLE;
	}
	
	/**
	 * @param reward - reward of rental request to sum up.
	 */
	public void getReward(int reward){
		rewards += reward;
	}
	
	/**
	 * @param request - a rental request we provided
	 */
	protected void addRequest(RentalRequest request){
		if (!requests.contains(request))
			requests.add(request);
	}
	
	/**
	 * adds consumed materials - name, how much left and how much consumed.
	 * @param material
	 * @param quantity
	 */
	protected void consumeMaterial(RepairMaterial material, int quantity){
		if (!usedRepairMaterials.containsKey(material.toString())){
			int stats[] = {material.quantity(), quantity};
			usedRepairMaterials.put(material.toString(), stats);

		} else {
			int stats[] = {material.quantity(), usedRepairMaterials.get(material.toString())[1] + quantity};
			usedRepairMaterials.put(material.toString(), stats);
		}
	}
	
	/**
	 * adds a tool that we used to fix an asset
	 * @param tool
	 */
	protected void addUsedTool(RepairTool tool) {
		if (!usedRepairTools.contains(tool))
			usedRepairTools.add(tool);
	}
	
	/**
	 * overrides toString method.
	 */
	public String toString(){
		StringBuilder printOut = new StringBuilder("\n\n\n===================\nS T A T I S T I C S \n===================\n\n");
		printOut.append("Money Gained: ").append(rewards).append("\n\n");
		
		printOut.append("Requsets done\n");
		for (RentalRequest request : requests){
			printOut.append(request).append("\n");
		}
		printOut.append("\n");
		printOut.append("Tools used\n");
		for (RepairTool tool : usedRepairTools){
			printOut.append(tool).append("\n");
		}
		printOut.append("\n");
		printOut.append("Consumed material:\n");
		for (String material : usedRepairMaterials.keySet()){
			printOut.append(material).append("[left: ").append(usedRepairMaterials.get(material)[0]).append("][consumed: ").append(usedRepairMaterials.get(material)[1]).append("]\n");
		}
		return printOut.toString();
	}
	
}
