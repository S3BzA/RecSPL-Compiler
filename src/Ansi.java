public class Ansi {
	public static final String RESET = "\u001B[0m";
	public static final String BOLD = "\u001B[1m";
	public static final String ITALIC = "\u001B[3m";
	public static final String UNDERLINE = "\u001B[4m";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String BLUE = "\u001B[34m";
	public static final String YELLOW = "\u001B[33m";
	public static final String CYAN = "\u001B[36m";
	public static final String PURPLE = "\u001B[35m";

	public static void printlnFormatted(String text) {
		System.out.println(text);
	}

	public static String bold(String text) {
		return BOLD + text + RESET;
	}

	public static String italic(String text) {
		return ITALIC + text + RESET;
	}

	public static String underline(String text) {
		return UNDERLINE + text + RESET;
	}

	public static String red(String text) {
		return RED + text + RESET;
	}

	public static String green(String text) {
		return GREEN + text + RESET;
	}

	public static String blue(String text) {
		return BLUE + text + RESET;
	}

	public static String yellow(String string) {
		return YELLOW + string + RESET;
	}

	public static String cyan(String string) {
		return CYAN + string + RESET;
	}

	public static String purple(String string) {
		return PURPLE + string + RESET;
	}
}