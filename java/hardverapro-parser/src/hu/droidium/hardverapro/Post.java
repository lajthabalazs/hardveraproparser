package hu.droidium.hardverapro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {
	
	private boolean off;
	private String nextLine;
	private int messageIndex;
	protected String userName;
	private String[] originalLines;
	private ArrayList<Line> processedLines;
	protected boolean hasMonospace;
	private Date date;

	public void setOff(boolean b) {
		off = b;
	}

	public void setHasNext(String line) {
		nextLine = line;
	}
	
	public void setDate(Date date){
		this.date = date;
	}
	
	public Date getDate() {
		return date;
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
	
	public void setLines(String[] lines) {
		this.originalLines = new String[lines.length];
		System.arraycopy(lines, 0, originalLines, 0, lines.length);
		processLines(originalLines);
	}

	protected void processLines(String[] originalLines) {
		ArrayList<Line> processed = new ArrayList<Line>();
		for (String s : originalLines) {
			Line processedLine = Line.process(s);
			if (processedLine != null) {
				processed.add(processedLine);
			}
		}
		processedLines = processed;
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

	public List<? extends Line> getProcessedLines() {
		return processedLines;
	}

	public void setHasMonospace(boolean hasMonospace) {
		this.hasMonospace = true;
	}
}