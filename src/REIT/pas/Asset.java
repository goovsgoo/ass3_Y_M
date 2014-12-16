package REIT.pas;

import java.util.HashMap;

/*
 * 2.1 Dish

Found in: Menu

This object will hold information of a single dish. This object can be stored in the restaurant's

menu and can be used in different dish and dish order-related operations.

A Dish object will hold the following information: 
	(1) Dish Name 
	(2) Dish Cook Time 
	(3) Collection of Dish Ingredients
	(4) Collection of Required Kitchen Tools 
	(5) Difficulty Rating 
	(6) Reward.

Each dish has different difficulty rating which will affect the distribution of the different OrderOfDish to the different chefs.
Detailed explanation below. A dish also requires a collection of exhaustible ingredients and different kitchen tools,
in order to successfully simulate the cooking procedure.

2.1 Asset

Found in: Management
This object will hold information of a single asset.
An asset object must hold the following elds: (1) Name (2) Type (3) Location (4) Collection of
AssetContent (5) Status (6) Cost per night (7) Size
1
1. Type: The type will be one of a list of types provided as an input le.
2. Location: Coordinates are to be stored as (x,y) coordinates in two dimensional space. The
distance is to be calculated as the Euclidean distance between two dierent coordinates.
3. Status: One of three dierent options: (i) AVAILABLE (ii) BOOKED (iii) OCCUPIED (iv)
UNAVAILABLE. Where the asset is vacant if it is not currently being used. Booked, where the
asset has been booked by a customer group but not occupied yet. Occupied, if the properly is
currently being used by a customer, and unavailable if the asset is not t for renting, as long it is
not repaired. Repairing a asset requires a collection of exhaustible repair materials, and dierent
repair tools, and will be detailed later on.
4. Size: How many people the asset can t in.
 */

/**
 * 
 * @author Itai Sanders
 * this class simulates a dish.
 *
 */
public class Asset {
	final private String NAME;
	final private int COOK_TIME;
	final private int DIFFICULTY;
	final private int REWARD;
	private HashMap<String, Integer> ingredients;
	private HashMap<String, Integer> tools;
	
	/**
	 * the Dish constructor creates a Dish object with a given name, cooking time, difficulty and reward.
	 * the constructor also creates two empty ArrayLists, one for Ingredients and one for KitchenTools.
	 * @param name of dish.
	 * @param cookTime - time to cook the dish.
	 * @param difficulty of dish.
	 * @param reward of dish.
	 */
	public Asset(String name, int cookTime, int difficulty, int reward){
		this.NAME = name;
		this.COOK_TIME = cookTime;
		this.DIFFICULTY = difficulty;
		this.REWARD = reward;
		
		ingredients = new HashMap<String, Integer>();
		tools = new HashMap<String, Integer>();
	}
	
	/**
	 * if the new ingredient exists in the dish - adds it's quantity.
	 * else adds the Ingredient object to the list.
	 * @param newIngredient to add.
	 */
	protected void addIngredient(String name, int quantity){
		if (!ingredients.containsKey(name))
			ingredients.put(name, new Integer(quantity));
		else 
			ingredients.put(name, new Integer (ingredients.get(name)+quantity));
	}
	
	/**
	 * if the new tool exists in the dish - adds it's quantity.
	 * else adds the KitchenTool object to the list.
	 * @param newTool
	 */
	protected void addTool(String name, int quantity){
		if (!tools.containsKey(name))
			tools.put(name, new Integer(quantity));
		else 
			tools.put(name, new Integer (tools.get(name)+quantity));
	}
	
	/**
	 * check's whether a certain Ingredient is required to make the Dish.
	 * @param checkedIngredient - Ingredient object to be checked.
	 * @return true - if required.
	 * @return false - if not required.
	 */
	protected int isRequired(RepairMaterial checkedIngredient){
		if  (ingredients.containsKey(checkedIngredient.toString()))
			return ingredients.get(checkedIngredient.toString());
			
		return 0;
	}
	
	/**
	 * check's whether a certain KitchenTool is required to make the Dish.
	 * @param checkedTool - KitchenTool object to be checked.
	 * @return true - if required.
	 * @return false - if not required.
	 */	
	protected int isRequired(RepairTool checkedTool){
		if (tools.containsKey(checkedTool.toString()))
				return tools.get(checkedTool.toString());
		return 0;
	}
	
	/**
	 * overrides toString method  
	 */
	public String toString(){
		return this.NAME;
	}
	
	/**
	 * sends the difficulty of this Dish to Order class (replace getter).
	 * @param order
	 */
	protected void sendDifficulty(Order order){
		order.addDifficulty(DIFFICULTY);
	}
	
	/**
	 * sends the cook time of this Dish to Order class (replace getter).
	 * @param order
	 * @return cookTime
	 */
	protected int sendCookTime(Order order){
		return COOK_TIME;
	}
	
	/**
	 * sends the total reward of this Dish to Order class (replace getter).
	 * multiply it by the quantity of the dish in this order.
	 * @param order
	 * @param quantity
	 */
	protected void sendReward(Order order, int quantity){
		order.sumReward(REWARD*quantity);
	}
	
	/**
	 * simulates the preparation of the dish.
	 * Thread "goes to sleep" while preparing the dish.
	 * @param factor- chef's efficiency factor.
	 * @throws InterruptedException
	 */
	public void prepare(double factor) throws InterruptedException{
		Thread.sleep(Math.round(COOK_TIME*factor));
	}
	
	/**
	 * compares between this.name and a String.
	 * @param dishName- name of the dish to compare.
	 * @return true if the names match.
	 * @return false if the names don't match.
	 */
	public boolean equals(String dishName){
		return this.NAME.equals(dishName);
	}
}
