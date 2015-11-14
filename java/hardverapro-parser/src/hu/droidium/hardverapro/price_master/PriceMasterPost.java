package hu.droidium.hardverapro.price_master;

import hu.droidium.hardverapro.Post;

import java.util.ArrayList;
import java.util.List;

public class PriceMasterPost extends Post{
	
	private static final String[] PRICE_MASTER = {"MurdR", "itsmybestgun","lajthabalazs"};
	private ArrayList<PriceMasterLine> processedLines;

	public boolean isPriceMasterPost() {
		for (String s : PRICE_MASTER) {
			if (userName.equals(s)) {
				return hasMonospace && (!isOff());
			}
		}
		return false;
	}
	
	@Override
	protected void processLines(String[] originalLines) {
		ArrayList<PriceMasterLine> processed = new ArrayList<PriceMasterLine>();
		for (String s : originalLines) {
			PriceMasterLine processedLine = PriceMasterLine.process(s);
			if (processedLine != null) {
				processed.add(processedLine);
			}
		}
		processedLines = processed;
	}

	public List<PriceMasterLine> getProcessedLines() {
		return processedLines;
	}
}
