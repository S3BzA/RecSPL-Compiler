import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
	private int id;
	private TokenType tokenClass;
	private String word;

	public Token(int id, String tokenClass, String word) {
		this.id = id;
		this.tokenClass = TokenType.fromString(tokenClass);
		this.word = word;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTokenClass() {
		return tokenClass.toString();
	}

	public void setTokenClass(String tokenClass) {
		this.tokenClass = TokenType.fromString(tokenClass);
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String toXML() {
		return "<TOK>\n" +
				"  <ID>" + id + "</ID>\n" +
				"  <CLASS>" + tokenClass + "</CLASS>\n" +
				"  <WORD>" + word + "</WORD>\n" +
				"</TOK>";
	}

	public static Token fromXML(String xml) {
		Pattern idPattern = Pattern.compile("<ID>(\\d+)</ID>");
		Pattern classPattern = Pattern.compile("<CLASS>([^<]+)</CLASS>");
		Pattern wordPattern = Pattern.compile("<WORD>([^<]+)</WORD>");

		Matcher idMatcher = idPattern.matcher(xml);
		Matcher classMatcher = classPattern.matcher(xml);
		Matcher wordMatcher = wordPattern.matcher(xml);

		if (idMatcher.find() && classMatcher.find() && wordMatcher.find()) {
			int id = Integer.parseInt(idMatcher.group(1));
			String tokenClass = classMatcher.group(1);
			String word = wordMatcher.group(1);
			return new Token(id, tokenClass, word);
		} else {
			throw new IllegalArgumentException("Invalid XML format");
		}
	}

	@Override
	public String toString() {
		return "TOKEN(" + id + " " + tokenClass + " " + word + ")";
	}
}