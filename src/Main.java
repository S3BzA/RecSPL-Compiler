public class Main {

	public static void main(String[] args) {
		if (args.length == 0 || args[0].equals("--help") || args[0].equals("-h")) {
			printHelp();
			return;
		}

		String filePath = args[0];

		try {
			Ansi.printlnFormatted(Ansi.green("Starting Lexer..."));
			Ansi.printlnFormatted(Ansi.green("Tokenizing file: " + Ansi.bold(filePath)));
			Lexer lexer = new Lexer(filePath);
			lexer.tokenize();
			Ansi.printlnFormatted(Ansi.blue(lexer.toString()));
		} catch (Exception e) {
			Ansi.printlnFormatted(Ansi.red("Error: " + e.getMessage()));
		}
	}

	private static void printHelp() {
		System.out.println("Usage: make all ARGS=<file-path>");
		System.out.println("ARGS Options:");
		System.out.println("  --help, -h    Display this help menu.");
		System.out.println("  path/to/file.txt   The path to the file you want to compile.");
	}
}