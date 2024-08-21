import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
	private final SourceReader sourceReader;
	private final List<Token> tokens = new ArrayList<>();

	Lexer(String sourceFile) {
		sourceReader = new SourceReader();
		try {
			sourceReader.readSourceCode(sourceFile);
		} catch (Exception e) {
			System.err.println("Error reading source file: " + e.getMessage());
		}
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public void tokenize() { // * This function will use our regex's, @James, to tokenize the source code
		String line;
		int tokenCounter = 1;
		while ((line = sourceReader.nextLine()) != null) {
			String[] words = line.split("\s+");// split by whitespace
			for (String word : words) {
				if (word.equals("")) {
					continue;
				}

				if (TokenType.isKeyword(word)) {
					tokens.add(new Token(tokenCounter++, TokenType.fromString(word), word));
					continue;
				} else if (isVariableName(word)) {
					tokens.add(new Token(tokenCounter++, TokenType.V , word));
					continue;
				} else if (isFunctionName(word)) {
					tokens.add(new Token(tokenCounter++, TokenType.F, word));
					continue;
				} else if (isText(word)) {
					tokens.add(new Token(tokenCounter++, TokenType.T, word));
					continue;
				} else if (isNumber(word)){
					tokens.add(new Token(tokenCounter++, TokenType.N, word));
					continue;
				} else {
					//TODO: reject, procedure not applicable
				}
			}
		}
		exportTokens();
	}

	private boolean isVariableName(String word) {
		return word.matches("V_[a-z]([a-z]|[0-9])*");
	}

	private boolean isFunctionName(String word) {
		return word.matches("F_[a-z]([a-z]|[0-9])*\\(");
	}

	private boolean isText(String word){
		return word.matches("\"[A-Z][a-z]{0,7}\"");
	}

	private boolean isNumber(String word){
		return word.matches("0|-?0\\.([0-9])*[1-9]|-?[1-9]([0-9])*|-?[1-9]([0-9])*\\.([0-9])*[1-9]");
	}

	private void exportTokens() { // save tokens to a file.xml
		StringBuilder xml = new StringBuilder();
		xml.append("<TOKENS>\n");
		for (Token token : tokens) {
			xml.append(token.toXML()).append("\n");
		}
		xml.append("</TOKENS>");
		try {
			Files.write(Paths.get("tokens.xml"), xml.toString().getBytes());
		} catch (IOException e) {
			System.err.println("Error writing tokens to file: " + e.getMessage());
		}
	}

	public void importTokens(String file) {
		try {
			String content = new String(Files.readAllBytes(new File(file).toPath()));

			Pattern tokPattern = Pattern.compile("<TOK>(.*?)</TOK>", Pattern.DOTALL);
			Matcher matcher = tokPattern.matcher(content);

			while (matcher.find()) {
				String tokXML = matcher.group(0);
				Token token = Token.fromXML(tokXML);
				tokens.add(token);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (Token token : tokens) {
			str.append(token.toString()).append("\n");
		}
		return str.toString();
	}
}