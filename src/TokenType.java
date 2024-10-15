
public enum TokenType {
	// procedures
	MAIN("main"),
	BEGIN("begin"),
	END("end"),
	SKIP("skip"),
	HALT("halt"),
	PRINT("print"),
	RETURN("return"),
	INPUT("< input"),
	// types
	NUM("num"),
	TEXT("text"),
	VOID("void"),
	// control flow
	IF("if"),
	THEN("then"),
	ELSE("else"),
	// operators
	NOT("not"),
	SQRT("sqrt"),
	OR("or"),
	AND("and"),
	EQ("eq"),
	GRT("grt"),
	ADD("add"),
	SUB("sub"),
	MUL("mul"),
	DIV("div"),
	// symbols
	EQUALS("="),
	LPAREN("("),
	RPAREN(")"),
	COMMA(","),
	LBRACE("{"),
	RBRACE("}"),
	SCOLON(";"),
	// user-defined
	V("V"),
	F("F"),
	T("T"),
	N("N");


	private final String keyword;

	TokenType(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public String toString() {
		return keyword;
	}

	public static TokenType fromString(String keyword) {
		for (TokenType k : TokenType.values()) {
			if (k.keyword.equals(keyword)) {
				return k;
			}
		}
		throw new IllegalArgumentException("No keyword found for: " + keyword);
	}

	public static boolean isKeyword(String word) {
		for (TokenType k : TokenType.values()) {
			if (k.keyword.equals(word)) {
				return true;
			}
		}
		return false;
	}
}
