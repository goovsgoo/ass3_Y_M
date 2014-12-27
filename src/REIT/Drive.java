package REIT;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import REIT.pas.Management;
import REIT.pas.Parser;



public class Drive {

	/**
	 * @param args- InitialData.xml,AssetContentsRepairDetails.xml, Assets.xml, CustomersGroups.xml 
	 */
	public static void main(String[] args) {
		if (args.length != 4)
			throw new RuntimeException("command must include exactly three file pathes.");
		
		for (String arg : args){
			try {
				Parser.parseFile(arg);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Management.sample().start();	
	}
}
