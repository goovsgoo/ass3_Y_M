package REIT.pas;
import REIT.act.*;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

/**  @author Meni & Yoed
 * this class Parse the XML files.
 */
public class Parser {
	
	private static Management management = Management.sample();
	private static Warehouse warehouse = Warehouse.sample();
	private static RepairTool repairTool;
	private static RepairMaterial repairMaterial;
	private static RunnableClerk runnableClerk;


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
		else if (rootName.equals("AssetContentsRepairDetails")){
			parseAssetContentsRepairDetails(doc);
		}
		else if (rootName.equals("Assets")){
			parseAssets(doc);
		}
		else if (rootName.equals("CustomersGroups")){
			parseCustomersGroups(doc);
		}
			
	}
	
	
	private static void parseInitialData(Document initialDataDoc){

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
	 				warehouse.addMaterials(repairMaterial);
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
		 		NamedNodeMap location = clerkElement.getElementsByTagName("Location").item(0).getAttributes();
		 		String x = location.getNamedItem("x").getNodeValue();
		 		String y = location.getNamedItem("y").getNodeValue();		
	 			Point2D.Double clerkLocation = new Point2D.Double(Double.parseDouble(x), Double.parseDouble(y));
	 			runnableClerk = new RunnableClerk(clerkName, clerkLocation);
 				management.addClerk(runnableClerk);
			 }
		 }
		
		//parse MaintenancePersons
	 	int numberOfMaintenancePersons = Integer.parseInt(staffElement.getElementsByTagName("NumberOfMaintenancePersons").item(0).getTextContent());
	 	int totalNumberOfRentalRequests = Integer.parseInt(staffElement.getElementsByTagName("TotalNumberOfRentalRequests").item(0).getTextContent());
		management.setNumberOfMaintenanceMen(numberOfMaintenancePersons);
	 	management.setCount(totalNumberOfRentalRequests);
		//Maybe start here - RunnableMaintaineceRequest? ~ Yoed
	}
			
	private static void parseAssetContentsRepairDetails(Document assetContentsDoc){

		// parse AssetContent
		for (int i = 0; i < assetContentsDoc.getElementsByTagName("AssetContent").getLength(); i++) {
			Node assetContentNode = assetContentsDoc.getElementsByTagName("AssetContent").item(i);
		 	if (assetContentNode.getNodeType() == Node.ELEMENT_NODE) {
		 		Element assetContentElement = (Element) assetContentNode;
		 		String assetContentName = assetContentElement.getElementsByTagName("Name").item(0).getTextContent();
		 				
		 		AssetContent newAssetContent = new AssetContent(assetContentName);
		 				 		
		 		// parse tools
		 		Node toolListNode = assetContentElement.getElementsByTagName("Tools").item(0);
		 		Element toolListElement = (Element) toolListNode;
		 		for (int j = 0 ; j < toolListElement.getElementsByTagName("Tool").getLength() ; j++){
		 			Node toolNode = toolListElement.getElementsByTagName("Tool").item(j);
		 			if (toolNode.getNodeType() == Node.ELEMENT_NODE){
		 				Element toolElement = (Element) toolNode;
		 				String toolName = toolElement.getElementsByTagName("Name").item(0).getTextContent();
		 				int quantity = Integer.parseInt(toolElement.getElementsByTagName("Quantity").item(0).getTextContent());
		 				management.addToolNameToColl(toolName);
		 				newAssetContent.addTool(toolName, quantity);
		 			}
		 		}
				 		
		 		// parse Materials
		 		Node materialsListNode = assetContentElement.getElementsByTagName("Materials").item(0);
		 		Element materialsListElement = (Element) materialsListNode;
		 		for (int j = 0 ; j < materialsListElement.getElementsByTagName("Material").getLength() ; j++){
		 			Node materialNode = materialsListElement.getElementsByTagName("Material").item(j);
		 			if (materialNode.getNodeType() == Node.ELEMENT_NODE){
		 				Element materialElement = (Element) materialNode;
		 				String materialName = materialElement.getElementsByTagName("Name").item(0).getTextContent();
		 				int quantity = Integer.parseInt(materialElement.getElementsByTagName("Quantity").item(0).getTextContent());
		 				management.addMaterialNameToColl(materialName);
		 				newAssetContent.addMaterial(materialName, quantity);
		 			}
		 		}
		 		
				// add AssetContent to asset in management???????? ~ Yoed
			 	//management.addAssetContent(newAssetContent);
			}
		}
	}
	
	private static void parseAssets(Document assetsDoc){

		// parse Asset
		for (int i = 0; i < assetsDoc.getElementsByTagName("Asset").getLength(); i++) {
			Node assetNode = assetsDoc.getElementsByTagName("Asset").item(i);
		 	if (assetNode.getNodeType() == Node.ELEMENT_NODE) {
		 		Element assetElement = (Element) assetNode;
		 		String assetType = assetElement.getElementsByTagName("Type").item(0).getTextContent();
 				int assetSize = Integer.parseInt(assetElement.getElementsByTagName("Size").item(0).getTextContent());
 				//String location = assetElement.getElementsByTagName("Location").item(0).getNodeValue();					//I dont know how to take the Location ~ Yoed 
	 			//need to be Point2D//String location = new StringBuilder("Location #").append(i).toString();	
 				Point2D.Double location = new Point2D.Double();
 				int assetCostPerNight = Integer.parseInt(assetElement.getElementsByTagName("CostPerNight").item(0).getTextContent());

		 		Asset newAsset = new Asset(i,assetType,assetSize,location,assetCostPerNight);
		 		
		 	//parse assetContentsList
			Node assetContentsListNode = assetElement.getElementsByTagName("AssetContents").item(0);
			Element assetContentsElement = (Element) assetContentsListNode;
		 		
		 	// parse AssetContent
				for (int j = 0; j < assetContentsElement.getElementsByTagName("AssetContent").getLength(); j++) {
					Node assetContentNode = assetContentsElement.getElementsByTagName("AssetContent").item(j);
				 	if (assetContentNode.getNodeType() == Node.ELEMENT_NODE) {
				 		Element assetContentElement = (Element) assetContentNode;
				 		String assetContentName = assetContentElement.getElementsByTagName("Name").item(0).getTextContent();
		 				float repairMultiplier = Float.parseFloat(assetContentElement.getElementsByTagName("RepairMultiplier").item(0).getTextContent());
		 				
		 				AssetContent tmp = new AssetContent(assetContentName);
		 				// AssetContent tmp = management.getContentByName(assetContentName);
		 				tmp.updateMultiplier(repairMultiplier);
		 				newAsset.addNewContent(tmp);
				 	}
				 }
				// add asset to assets in management???????? ~ Yoed
			 	management.addAsset(newAsset);
			}
		}
	}
	
	
	private static void parseCustomersGroups(Document customersGroupsDoc){
		
		// parse CustomerGroupDetailsList
		Node customerGroupsListNode = customersGroupsDoc.getElementsByTagName("CustomerGroups").item(0);
		Element customerGroupsElement = (Element) customerGroupsListNode;
		
		// parse CustomerGroupDetails
		for (int j = 0; j < customerGroupsElement.getElementsByTagName("CustomerGroupDetails").getLength(); j++) {
			Node customerGroupNode = customerGroupsElement.getElementsByTagName("CustomerGroupDetails").item(j);
		 	if (customerGroupNode.getNodeType() == Node.ELEMENT_NODE) {
		 		Element customerGroupElement = (Element) customerGroupNode;
		 		String groupManagerName = customerGroupElement.getElementsByTagName("GroupManagerName").item(0).getTextContent();
		 		
		 		CustomerGroupDetails newCustomerGroup = new CustomerGroupDetails(groupManagerName);
		 		
		 		// parse CustomersList
		 		Node customersListNode = customerGroupElement.getElementsByTagName("Customers").item(0);
				Element customersElement = (Element) customersListNode;
				
				// parse Customer
				for (int n = 0; n < customersElement.getElementsByTagName("Customer").getLength(); n++) {
					Node customerNode = customersElement.getElementsByTagName("Customer").item(n);
				 	if (customerNode.getNodeType() == Node.ELEMENT_NODE) {
				 		Element customerElement = (Element) customerNode;
				 		String customerName = customerElement.getElementsByTagName("Name").item(0).getTextContent();
				 		String Vandalism = customerElement.getElementsByTagName("Vandalism").item(0).getTextContent();
		 				int MinimumDamage = Integer.parseInt(customerElement.getElementsByTagName("MinimumDamage").item(0).getTextContent());
		 				int MaximumDamage = Integer.parseInt(customerElement.getElementsByTagName("MaximumDamage").item(0).getTextContent());
		 				Customer newCustomer = new Customer(customerName, Vandalism, MinimumDamage, MaximumDamage);
		 				newCustomerGroup.addCustomer(newCustomer);

				 	}
				}
		
				// parse RentalRequestsList
		 		Node rentalRequestsListNode = customerGroupElement.getElementsByTagName("RentalRequests").item(0);
				Element rentalRequestsElement = (Element) rentalRequestsListNode;
				
				// parse Request
				for (int n = 0; n < rentalRequestsElement.getElementsByTagName("Request").getLength(); n++) {
					Node requestNode = rentalRequestsElement.getElementsByTagName("Request").item(n);
				 	if (requestNode.getNodeType() == Node.ELEMENT_NODE) {
				 		Element requestElement = (Element) requestNode;
				 		String requestID = new StringBuilder("Request #").append(n).toString();
				 		String Type = requestElement.getElementsByTagName("Type").item(0).getTextContent();
		 				int Size = Integer.parseInt(requestElement.getElementsByTagName("Size").item(0).getTextContent());
		 				int Duration = Integer.parseInt(requestElement.getElementsByTagName("Duration").item(0).getTextContent());
		 				RentalRequest newRequest = new RentalRequest(requestID, Type, Size, Duration);
		 				newRequest.assignGroupManager(newCustomerGroup);
		 				newCustomerGroup.addRequest(newRequest);
		 				// management.incrementCounter();
				 	}
				}
			management.addGroup(newCustomerGroup);	
		 	}
		}
	}
	
}


 



