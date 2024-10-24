
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
			System.out.println("Output path arg: "+outputPath+"// ignore, we didn;t end up doing this part (5a, 5b)");
		} catch (Exception e) {
			Ansi.printlnFormatted(Ansi.red("Error: " + e.getMessage()));
		}
	}

	private static void printHelp() {
        Ansi.printlnFormatted(Ansi.yellow(".jar usage: java -jar " + Ansi.italic("path/to/compile.jar path/to/input.txt")));
        Ansi.printlnFormatted(Ansi.yellow("makefile usage: make all ARGS=" + Ansi.italic("path/to/input.txt")));
        Ansi.printlnFormatted(Ansi.yellow("Options:"));
        Ansi.printlnFormatted(Ansi.yellow("  --help, -h    Display this help menu."));
		Ansi.printlnFormatted(Ansi.yellow("  path/to/input.txt    Path to the input file."));
    }
}