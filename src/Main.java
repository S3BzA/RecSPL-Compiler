
public class Main {

	public static void main(String[] args) {
		if (args.length == 0 || args[0].equals("--help") || args[0].equals("-h")) {
			printHelp();
			return;
		}

		String filePath = args[0];
		String outputPath;

		if (args.length < 2) {
			Ansi.printlnFormatted(Ansi.red("Warning: Missing output path argument. Using default path."));
			outputPath = "output.txt"; // Set your default path here
		} else {
			outputPath = args[1];
		}

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
			Ansi.printlnFormatted(Ansi.green("Building Symbol Table and Analysing usage..."));
			ScopeTree scopes = scopeAnalyser.PopulateTree();
			Ansi.printlnFormatted(Ansi.green("\nChecking types..."));
			TypeCheck typeChecker = new TypeCheck(scopes, parser.getRoot());
			typeChecker.AnalyseTypes();
			System.out.println("OUtput path arg: "+outputPath);
		} catch (Exception e) {
			Ansi.printlnFormatted(Ansi.red("Error: " + e.getMessage()));
		}
	}

	private static void printHelp() {
        Ansi.printlnFormatted(Ansi.yellow("Usage: make all ARGS=" + Ansi.italic("<file-path> <output-path>")));
        Ansi.printlnFormatted(Ansi.yellow("ARGS Options:"));
        Ansi.printlnFormatted(Ansi.yellow("  --help, -h    Display this help menu."));
        Ansi.printlnFormatted(Ansi.yellow("  <file-path>   The path to the file you want to compile."));
        Ansi.printlnFormatted(Ansi.yellow("  <output-path> The path where the output should be saved."));
    }
}