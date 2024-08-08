public enum Keyword {
	MAIN("main"),
	NUM("num"),
	TEXT("text"),
	BEGIN("begin"),
	END("end"),
	SKIP("skip"),
	HALT("halt"),
	PRINT("print"),
	INPUT("< input"),
	EQUALS("="),
	LPAREN("("),
	RPAREN(")"),
	COMMA(","),
	IF("if"),
	THEN("then"),
	ELSE("else"),
	NOT("not"),
	SQRT("sqrt"),
	OR("or"),
	AND("and"),
	EQ("eq"),
	GRT("grt"), // Assuming "qrt" was a typo in the original list
	ADD("add"),
	SUB("sub"),
	MUL("mul"),
	DIV("div"),
	VOID("void"),
	LBRACE("{"),
	RBRACE("}");

	private final String keyword;

	Keyword(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public String toString() {
		return keyword;
	}

	public static Keyword fromString(String keyword) {
		for (Keyword k : Keyword.values()) {
			if (k.keyword.equals(keyword)) {
				return k;
			}
		}
		throw new IllegalArgumentException("No keyword found for: " + keyword);
	}
}
