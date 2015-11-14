package hu.droidium.hardverapro;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ad {
	
	//private static final String BEGINING = ".*";
	//<h5><a href="apro/gamer_klaviatura"  >2 SZÍNBEN világító Ozone. Blade BILLENTY?ZET</a></h5>
	//private static final String URL_AND_TITLE_GROUPS = "<h5[^>]*>\\s*<\\s*a\\s*href=\"([^\"]*).*";
	// <span class="day">ma 17:42</span>
	//private static final String DATE_PART = "\\s*<\\s*span\\s*class\\s*=\\s*\"day\"\\s*>\\s*(.*)\\s*</\\s*span\\s*>\\s*";
	//private static final String END = ".*";
	//private static final String PATTERN = BEGINING + URL_AND_TITLE_GROUPS + DATE_PART + END;

	private Ad() {
	}
	
	public static Ad parse(String line) {
		Pattern p = Pattern.compile(".*<h5[^>]*>\\s*<\\s*a\\s*href=\"([^\"]*)\"\\s*>(.*)</a></h5></div>(.*)");
		Matcher m = p.matcher(line);
		if (m.matches()) {
		    //System.out.println(m.group(1) + " > " + m.group(2));
			System.out.println(m.group(1));
			System.out.println();
			System.out.println(m.group(2));
			System.out.println();
			System.out.println(m.group(3));
			System.out.println();
			return new Ad();
		} else {
			return null;
		}
	}
}