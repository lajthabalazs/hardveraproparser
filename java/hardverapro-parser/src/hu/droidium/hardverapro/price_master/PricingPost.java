package hu.droidium.hardverapro.price_master;

import hu.droidium.hardverapro.Post;

import java.util.ArrayList;
import java.util.List;

public class PricingPost extends Post{
	
	private static final String[] PRICE_MASTER = {"MurdR", "itsmybestgun","lajthabalazs"};
	private ArrayList<PricingLine> processedLines;

	public boolean isPriceRequest() {
		for (String s : PRICE_MASTER) {
			if (userName.equals(s)) {
				return false;
			}
		}
		return !hasMonospace;
	}
	
	@Override
	protected void processLines(String[] originalLines) {
		ArrayList<PricingLine> processed = new ArrayList<PricingLine>();
		for (String s : originalLines) {
			PricingLine processedLine = PricingLine.process(s);
			if (processedLine != null) {
				processed.add(processedLine);
			}
		}
		processedLines = processed;
	}

	public List<PricingLine> getProcessedLines() {
		return processedLines;
	}
}
