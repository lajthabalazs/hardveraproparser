package hu.droidium.hardverapro.price_master;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Type {
	COOLER(Constants.COOLER_PREFIX),
	CPU (Constants.CPU_PREFIX),
	MOTHERBOARD(Constants.MOTHERBOARD_PREFIX),
	RAM(Constants.RAM_PREFIX),
	VGA(Constants.VGA_PREFIX),
	CASE(Constants.CASE_PREFIX),
	PSU(Constants.PSU_PREFIX),
	HDD(Constants.HDD_PREFIX),
	SSD(Constants.SSD_PREFIX),
	DISPLAY(Constants.DISPLAY_PREFIX),
	UNKNOWN(new String[]{});
	;
	
	private String[] prefixes;
	private Pattern pattern;
	
	Type(String[] prefixes) {
		this.prefixes = prefixes;
		String regex = "(";
		for (String prefix : prefixes) {
			if (regex.length() > 1) {
				regex = regex + "|";
			}
			regex = regex + prefix;
		}
		regex = regex + ")\\s*:\\s*(.*)";
		pattern = Pattern.compile(regex);
	}
	
	public static Type getType(String line) {
		for (Type t : Type.values()) {
			if (t != UNKNOWN) {
				if (t.pattern.matcher(line).matches()) {
					return t;
				}
			}
		}
		return UNKNOWN;
	}
	
	public String getRemaining(String modified) {
		Matcher matcher = pattern.matcher(modified);
		if(matcher.matches()) {
			return matcher.group(2);
		}
		return modified;
	}

	private static class Constants {
		private static final String[] CPU_PREFIX = {"cpu", "processzor", "proci"};
		private static final String[] MOTHERBOARD_PREFIX = {"mb", "alaplap", "lap"};
		private static final String[] RAM_PREFIX = {"ram", "memória", "memoria"};
		private static final String[] VGA_PREFIX = {"vga", "videokártya", "videókártya", "videokartya","videokari","videókari"};
		private static final String[] CASE_PREFIX = {"ház", "haz", "gépház", "gephaz"};
		private static final String[] PSU_PREFIX = {"psu","tap", "táp", "tapegyseg", "tápegység"};
		private static final String[] HDD_PREFIX = {"hdd","merevlemez", "vinyo", "vinyó", "winchester", "vincseszter"};
		private static final String[] SSD_PREFIX = {"ssd"};
		private static final String[] COOLER_PREFIX = {"huto","hűtő","cooler","cpu\\s*huto","cpu\\s*hűtő","cpu\\s*cooler",
																"processzor\\s*huto","processzor\\s*hűtő","processzor\\s*cooler",
																"proci\\s*huto","proci\\s*hűtő","proci\\s*cooler"};
		private static final String[] DISPLAY_PREFIX = {"monitor","képernyő","kepernyo"};
	}
}