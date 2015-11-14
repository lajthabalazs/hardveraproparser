package hu.droidium.hardverapro.price_master;

import hu.droidium.hardverapro.Line;

public class PricingLine extends Line {
	private static final String[] FORBIDDEN = {"szia", "üdv","kösz","kérni","árazás","szeret","mennyit","kerül", "érhet", "szerint"};
	String line;
	String deviceClass;
	String model;
	String modelParams;
	String brand;

	public static PricingLine process(final String s) {
		String modified = s.replaceAll("<[^>]*>"," ");
		PricingLine line = new PricingLine();
		line.line = modified;
		modified = modified.toLowerCase();
		if (modified.trim().length() == 0) {
			return null;
		}
		for (String forbidden : FORBIDDEN) {
			if (modified.contains(forbidden)) {
				return null;
			}
		}
		return line;
	}
	
	@Override
	public String toString() {
		return line;
	}

}
