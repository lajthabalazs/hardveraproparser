package hu.droidium.hardverapro.price_master;

import hu.droidium.hardverapro.Line;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PriceMasterLine extends Line {
	private static final String[] FORBIDDEN = {"szia", "üdv","kösz","kérni","árazás","szeret","mennyit","kerül", "érhet", "szerint"};
	private static final String pricedLinePattern = "huf|ft|k|kft|k ft|e|eft|e ft";

	String line;
	String deviceClass;
	String model;
	String modelParams;
	String brand;
	int price;
	private Type type;
	private String name;	

	public static PriceMasterLine process(final String s) {
		String modified = s.replaceAll("<[^>]*>"," ");
		PriceMasterLine line = new PriceMasterLine();
		line.line = modified;
		modified = modified.toLowerCase().trim();
		if (modified.length() == 0) {
			return null;
		}
		for (String forbidden : FORBIDDEN) {
			if (modified.contains(forbidden)) {
				return null;
			}
		}
		// Check prefix
		Type type = Type.getType(modified);
		type.getRemaining(modified);
		// Grab price
		modified = type.getRemaining(modified);
		 Pattern pattern = Pattern.compile("(.*)\\s+(\\d+\\.?,?-?\\d*)\\s*(" + pricedLinePattern +")\\s*(\\(.*\\))?");
		 Matcher m = pattern.matcher(modified);
		if (m.matches()) {
			String item = m.group(1).trim();
			if (item.endsWith("~")) {
				item = item.substring(0, item.length() - 1).trim();
			}
			float price = -1;
			String priceString = m.group(2);
			if (priceString.contains("-")) {
				priceString = priceString.substring(priceString.lastIndexOf("-") + 1);
			}
			String unit = m.group(3);
			if (!(unit.equals("huf")||unit.equals("ft"))) {
				priceString = priceString.replaceAll("\\,", ".");
				try {
					price = Float.parseFloat(priceString);
				} catch (NumberFormatException e) {				
					System.err.println(modified + " > " + e.getMessage());
				}
				price = price * 1000;
			} else {
				priceString = priceString.replaceAll("\\,","").replaceAll("\\.", "");
				try {
					price = Float.parseFloat(priceString);
				} catch (NumberFormatException e) {
					System.err.println(modified + " > " + e.getMessage());
				}
			}
			line.price = (int) price;
			line.type = type;
			line.name = item;
		} else {
			return null;
		}
		return line;
	}
	
	@Override
	public String toString() {
		return type + " : " + name + " ~ " + price + "Ft";
	}
	public static void main(String[] args) {
		String[] lines = {"Alaplap: Asus M4A785TD-M Evo ~  8.000 Ft",
		"Processzor: QuadCore AMD Phenom II X4 Black Edition 965 ~ 14 eFt",
		"Ram: DDR3 4 Gb, 1600 MHz ~ 4-5 eFt (márka, késleltetés, 1/2 modul...)",
		"Videókártya: ATI Radeon HD 5770 1GB ~ 9 eFt",
		"Táp: M Tech 600 W ~ 2-3 eFt (?)",
		"Gépház: Codegen E6100-A11 ~ 6 eFt",
		"HDD: Samsung HD 7200 RPM, 1TB ~ 8 eFt",
		"SSD: Kingstone 64GB ~  6 eFt",
		"Monitor: Philips 190 CW ~ 8 eFt",
		"Billentyűzet:  Microsoft Wireless Keyboard 3000 ~ 2-3 eFt"};
		for (String line: lines) {
			PriceMasterLine processedLine = PriceMasterLine.process(line);
			if (processedLine != null) {
				System.out.println("" + processedLine);
			}
		}
	}
}