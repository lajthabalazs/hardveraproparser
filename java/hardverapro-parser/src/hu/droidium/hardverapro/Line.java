package hu.droidium.hardverapro;

public class Line {
	String line;

	public static Line process(final String s) {
		Line line = new Line();
		line.line = s;
		return line;
	}
	
	@Override
	public String toString() {
		return line;
	}

}
