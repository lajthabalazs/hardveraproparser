package price_grabber;

import java.util.ArrayList;

public class Post {
	
	private static final String[] FORBIDDEN = {"szia", "üdv","kösz","kérni","árazás","szeret","mennyit","kerül", "érhet", "szerint"};
	private static final String[] PRICE_MASTER = {"MurdR", "itsmybestgun","lajthabalazs"};
	private boolean off;
	private String nextLine;
	private int messageIndex;
	private String userName;
	private boolean priceMasterPost;
	private String[] originalLines;
	private String[] processedLines;

	public void setOff(boolean b) {
		off = b;
	}

	public void setHasNext(String line) {
		nextLine = line;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean hasNext () {
		return nextLine != null;
	}

	public String getNextLine() {
		return nextLine;
	}

	public void setMessageIndex(int messageIndex) {
		this.messageIndex = messageIndex;
	}

	public void setPriceMasterPost(boolean priceMasterPost) {
		this.priceMasterPost = priceMasterPost;
	}
	
	public boolean isPriceMasterPost() {
		for (String s : PRICE_MASTER) {
			if (userName.equals(s)) {
				return true;
			}
		}
		return priceMasterPost;
	}
	
	public void setLines(String[] lines) {
		this.originalLines = new String[lines.length];
		System.arraycopy(lines, 0, originalLines, 0, lines.length);
		processLines();
	}

	private void processLines() {
		ArrayList<String> processed = new ArrayList<String>();
		lines:for (String s : originalLines) {
			s = s.toLowerCase();
			s = s.replaceAll("<[^>]*>"," ");
			if (s.trim().length() == 0) {
				continue lines;
			}
			for (String forbidden : FORBIDDEN) {
				if (s.contains(forbidden)) {
					continue lines;
				}
			}
			processed.add(s);
		}
		processedLines = processed.toArray(new String[processed.size()]);
	}

	public boolean isOff() {
		return off;
	}

	public int getMessageIndex() {
		return messageIndex;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String[] getOriginalLines() {
		return originalLines;
	}

	public String[] getProcessedLines() {
		return processedLines;
	}
}
