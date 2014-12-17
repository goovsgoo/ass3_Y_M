package REIT.pas;
import REIT.active.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;


public class Parser {
	
	private static Management management = Management.instance();
	private static Warehouse warehouse = Warehouse.instance();
	private static RepairTool repairTool;
	private static RepairMaterial repairMaterial;
	private static RunnableChef runnableChef;
	private static RunnableDeliveryPerson runnableDeliverer;

	/**
	 * parse the files that gets from Driver class.		
	 * @param fileName- xml file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void parseFile(String fileName) throws ParserConfigurationException, SAXException, IOException{

		File xmlFile = new File(fileName);
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(xmlFile);
		
		String rootName = doc.getDocumentElement().getNodeName();
		
		if (rootName.equals("REIT")){
			parseInitialData(doc);
		}
		else if (rootName.equals("Menu")){
			parseMenu(doc);
		}
		else if (rootName.equals("OrderList")){
			parseOrdersList(doc);
		}
			
	}
	
	
	private static void parseInitialData(Document initialDataDoc){
		
		//parse address
		/*
		Node addressList = initialDataDoc.getElementsByTagName("Address").item(0);
	 	if (addressList.getNodeType() == Node.ELEMENT_NODE) {
			Element addressElement = (Element) addressList;
			double x = Double.parseDouble(addressElement.getElementsByTagName("x").item(0).getTextContent());
			double y = Double.parseDouble(addressElement.getElementsByTagName("y").item(0).getTextContent());
			
			management.coordinatesSetter(x, y);
		}
		*/
		
		//parse warehouse
		Node warehouseListNode = initialDataDoc.getElementsByTagName("Warehouse").item(0);
		Element warehouseElement = (Element) warehouseListNode;
		
		//parse tools
		Node toolListNode = warehouseElement.getElementsByTagName("Tools").item(0);
		Element toolListElement = (Element) toolListNode;
		for (int i = 0; i < toolListElement.getElementsByTagName("Tool").getLength(); i++) {
			Node toolNode = toolListElement.getElementsByTagName("Tool").item(i);
			if (toolNode.getNodeType() == Node.ELEMENT_NODE) {
		 		Element toolElement = (Element) toolNode;
		 		String toolName = toolElement.getElementsByTagName("Name").item(0).getTextContent();
 				int quantity = Integer.parseInt(toolElement.getElementsByTagName("Quantity").item(0).getTextContent());
 				
 				repairTool = new RepairTool(toolName, quantity);
 				warehouse.addTool(repairTool);
			}
		 }
		
 		// parse RepairMaterial
		Node MaterialListNode = warehouseElement.getElementsByTagName("Materials").item(0);
		Element MaterialListElement = (Element) MaterialListNode;
		for (int i = 0; i < MaterialListElement.getElementsByTagName("Material").getLength(); i++) {
			Node materialNode = MaterialListElement.getElementsByTagName("Material").item(i);
			if (materialNode.getNodeType() == Node.ELEMENT_NODE) {
			 		Element materialElement = (Element) materialNode;
			 		String MaterialName = materialElement.getElementsByTagName("Name").item(0).getTextContent();
	 				int quantity = Integer.parseInt(materialElement.getElementsByTagName("Quantity").item(0).getTextContent());
	 				
	 				repairMaterial = new RepairMaterial(MaterialName, quantity);
	 				warehouse.addMaterial(repairMaterial);
			}
		}
		
		//parse staff
		Node staffListNode = initialDataDoc.getElementsByTagName("Staff").item(0);
		Element staffElement = (Element) staffListNode;
		
		//parse clerks
		Node clerksListNode = staffElement.getElementsByTagName("Clerks").item(0);
		Element clerksListElement = (Element) clerksListNode;
		for (int i = 0; i < clerksListElement.getElementsByTagName("Clerk").getLength(); i++) {
			Node clerkNode = clerksListElement.getElementsByTagName("Clerk").item(i);
			if (clerkNode.getNodeType() == Node.ELEMENT_NODE) {
		 		Element clerkElement = (Element) clerkNode;
		 		String clerkName = clerkElement.getElementsByTagName("Name").item(0).getTextContent();
	 			//double efficiency = Double.parseDouble(clerkElement.getElementsByTagName("efficiencyRating").item(0).getTextContent());
 				//int endurance = Integer.parseInt(clerkElement.getElementsByTagName("enduranceRating").item(0).getTextContent());
		 		String orderID = new StringBuilder("Order #").append(i).toString();							//Yoed stop Here!!////////////
		 		
 				runnableChef = new RunnableChef(chefName, efficiency, endurance);
 				management.addChef(runnableChef);
			 }
		 }
		
		//parse delivery persons
		Node deliverersListNode = staffElement.getElementsByTagName("DeliveryPersonals").item(0);
		Element delivererListElement = (Element) deliverersListNode;
		for (int i = 0; i < delivererListElement.getElementsByTagName("DeliveryPerson").getLength(); i++) {
			Node delivererNode = delivererListElement.getElementsByTagName("DeliveryPerson").item(i);
			if (delivererNode.getNodeType() == Node.ELEMENT_NODE) {
		 		Element delivererElement = (Element) delivererNode;
		 		String delivererName = delivererElement.getElementsByTagName("name").item(0).getTextContent();
 				double speed = Double.parseDouble(delivererElement.getElementsByTagName("speed").item(0).getTextContent());
 				
 				runnableDeliverer = new RunnableDeliveryPerson(delivererName, speed);
 				management.addDeliveryPerson(runnableDeliverer);
			}
		 }
	}
			
	private static void parseMenu(Document menuDoc){

		// parse dishes
		Node dishListNode = menuDoc.getElementsByTagName("Dishes").item(0);
		Element dishListElement = (Element) dishListNode;
		for (int i = 0; i < dishListElement.getElementsByTagName("Dish").getLength(); i++) {
			Node dishNode = dishListElement.getElementsByTagName("Dish").item(i);
		 	if (dishNode.getNodeType() == Node.ELEMENT_NODE) {
		 		Element dishElement = (Element) dishNode;
		 		String dishName = dishElement.getElementsByTagName("name").item(0).getTextContent();
		 		int cookTime = Integer.parseInt(dishElement.getElementsByTagName("expectedCookTime").item(0).getTextContent());
		 		int difficulty = Integer.parseInt(dishElement.getElementsByTagName("difficultyRating").item(0).getTextContent());
		 		int reward = Integer.parseInt(dishElement.getElementsByTagName("reward").item(0).getTextContent());
		 				
		 		Dish newDish = new Dish(dishName, cookTime, difficulty, reward);
		 				 		
		 		// parse tools
		 		Node toolListNode = dishElement.getElementsByTagName("KitchenTools").item(0);
		 		Element toolListElement = (Element) toolListNode;
		 		for (int j = 0 ; j < toolListElement.getElementsByTagName("KitchenTool").getLength() ; j++){
		 			Node toolNode = toolListElement.getElementsByTagName("KitchenTool").item(j);
		 			if (toolNode.getNodeType() == Node.ELEMENT_NODE){
		 				Element toolElement = (Element) toolNode;
		 				String toolName = toolElement.getElementsByTagName("name").item(0).getTextContent();
		 				int quantity = Integer.parseInt(toolElement.getElementsByTagName("quantity").item(0).getTextContent());
		 				
		 				newDish.addTool(toolName, quantity);
		 			}
		 		}
				 		
		 		// parse ingredients
		 		Node ingredientListNode = dishElement.getElementsByTagName("Ingredients").item(0);
		 		Element ingredientListElement = (Element) ingredientListNode;
		 		for (int j = 0 ; j < ingredientListElement.getElementsByTagName("Ingredient").getLength() ; j++){
		 			Node IngredientNode = ingredientListElement.getElementsByTagName("Ingredient").item(j);
		 			if (IngredientNode.getNodeType() == Node.ELEMENT_NODE){
		 				Element IngredientElement = (Element) IngredientNode;
		 				String ingredientName = IngredientElement.getElementsByTagName("name").item(0).getTextContent();
		 				int quantity = Integer.parseInt(IngredientElement.getElementsByTagName("quantity").item(0).getTextContent());
		 				
		 				newDish.addIngredient(ingredientName, quantity);
		 			}
		 		}
		 		
				// add dish to menu in management.
			 	management.addDish(newDish);
			}
		}
	}
	
	private static void parseOrdersList(Document oredersListDoc){
		
		// parse orders.
		Node orderListNode = oredersListDoc.getElementsByTagName("Orders").item(0);
		Element orderListElement = (Element) orderListNode;
		for (int i = 0; i < orderListElement.getElementsByTagName("Order").getLength(); i++) {
			Node orderNode = orderListElement.getElementsByTagName("Order").item(i);
		 	if (orderNode.getNodeType() == Node.ELEMENT_NODE) {
		 		Element orderElement = (Element) orderNode;
		 		String orderID = new StringBuilder("Order #").append(i).toString();
		 		Element address = (Element) orderElement.getElementsByTagName("DeliveryAddress").item(0);
		 		int x = Integer.parseInt(address.getElementsByTagName("x").item(0).getTextContent());
		 		int y = Integer.parseInt(address.getElementsByTagName("y").item(0).getTextContent());
		 		
		 		Order newOrder = new Order(orderID, x, y);
		 		
		 		Node dishListNode = orderElement.getElementsByTagName("Dishes").item(0);
		 		Element dishListElement = (Element) dishListNode;
		 		for (int j = 0 ; j < dishListElement.getElementsByTagName("Dish").getLength() ; j++){
		 			Node dishNode = dishListElement.getElementsByTagName("Dish").item(j);
		 			if (dishNode.getNodeType() == Node.ELEMENT_NODE){
		 				Element dishElement = (Element) dishNode;
		 				String dishName = dishElement.getElementsByTagName("name").item(0).getTextContent();
		 				int quantity = Integer.parseInt(dishElement.getElementsByTagName("quantity").item(0).getTextContent());
		 				
		 				newOrder.addDish(management.getDishByName(dishName), quantity);
		 			}
		 		}
		 		management.addOrder(newOrder);
		 	}
		}
	}
	
}


 



