public class Main {
	public static void main(String[] args) {
		System.out.println("Hello, World!");
		Lexer lexer = new Lexer("SPLProgrammes/Main.txt");
		// lexer.tokenize();
		lexer.importTokens("tokens.xml");
		System.out.println(lexer.toString());
	}
}