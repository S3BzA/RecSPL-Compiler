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
			Ansi.printlnFormatted(Ansi.green("Starting Parser..."));
			Parser parser = new Parser(lexer.getTokens());
			parser.parse();
			parser.printSyntaxTree();
			ScopeAnalyser scopeAnalyser = new ScopeAnalyser(parser.getRoot());
			Ansi.printlnFormatted(Ansi.green("Building Symbol Table..."));
			scopeAnalyser.ScopeTest();
			Ansi.printlnFormatted(Ansi.green("Analysing..."));
		} catch (Exception e) {
			Ansi.printlnFormatted(Ansi.red("Error: " + e.getMessage()));
			e.printStackTrace();
		}
	}

	private static void printHelp() {
		Ansi.printlnFormatted(Ansi.yellow("Usage: make all ARGS="+Ansi.italic("<file-path>")));
		Ansi.printlnFormatted(Ansi.yellow("ARGS Options:"));
		Ansi.printlnFormatted(Ansi.yellow("  --help, -h    Display this help menu."));
		Ansi.printlnFormatted(Ansi.yellow("  path/to/file.txt   The path to the file you want to compile."));
	}
}