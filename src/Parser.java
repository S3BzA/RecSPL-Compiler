
import java.util.List;

public class Parser {
	private final List<Token> tokens;
	private int position;
	private TreeNode<Token> root;

	public Parser(List<Token> tokens) {
		this.tokens = tokens;
		this.position = 0;
		this.root = null; // Root will be initialized when parsing starts
	}

	public void printSyntaxTree() {
		if (root != null) {
			System.out.println("Syntax Tree:");
			root.printTree("", true);
		} else {
			System.out.println("Syntax Tree is empty");
		}
	}

	// Utility method to get the current token
	private Token peek() {
		if (position < tokens.size()) {
			return tokens.get(position);
		}
		return null;
	}

	// Utility method to consume the current token and advance
	private Token match(TokenType expectedType) {
		Token currentToken = peek();
		if (currentToken != null && currentToken.getTokenClass().equals(expectedType.name())) {
			position++;
			return currentToken;
		} else {
			int errorPosition = position;
			this.printSyntaxTree();
			throw new RuntimeException("Syntax Error at position " + errorPosition + ": Expected " + expectedType.name() + " but found "
					+ (currentToken != null ? currentToken.getTokenClass() : "EOF"));
		}
	}

	// Method to handle syntax errors
	private void error(String message) {
		this.printSyntaxTree();
		throw new RuntimeException("Syntax Error: " + message);
	}

	// Check if there are remaining tokens to parse
	private boolean hasMoreTokens() {
		return position < tokens.size();
	}

	// PROG ::= main GLOBVARS ALGO FUNCTIONS
	public TreeNode<Token> parse() {
		root = new TreeNode<>(new Token(-1, TokenType.V, "PROG")); // Non-terminal node
		
		root.addChild(new TreeNode<>(match(TokenType.MAIN))); 

		// Parse the non-terminals in the order defined by the grammar
		root.addChild(parseGlobVars());
		root.addChild(parseAlgo());
		root.addChild(parseFunctions());

		// Check if there are any remaining tokens
		if (hasMoreTokens()) {
			error("Unexpected tokens at the end of the program, position: " + position);
		}

		return root;
	}

	// GLOBVARS ::= /* nullable */
	// GLOBVARS ::= VTYP VNAME , GLOBVARS
	private TreeNode<Token> parseGlobVars() {
		TreeNode<Token> globVarsNode = new TreeNode<>(new Token(-1, TokenType.V, "GLOBVARS")); // Non-terminal node

		while (peek() != null && (peek().getWord().equals("num") || peek().getWord().equals("text"))) {
			TreeNode<Token> vtypNode = new TreeNode<>(match(TokenType.fromString(peek().getWord()))); // Match VTYP (num | text)
			globVarsNode.addChild(vtypNode);
			TreeNode<Token> vnameNode = new TreeNode<>(match(TokenType.V)); // Match VNAME (V-Token)
			globVarsNode.addChild(vnameNode);

			if (peek() != null && peek().getTokenClass().equals("COMMA")) {
				globVarsNode.addChild(new TreeNode<>(match(TokenType.COMMA))); // Match comma
			}
		}

		return globVarsNode;
	}

	// ALGO ::= begin INSTRUC end
	private TreeNode<Token> parseAlgo() {
		TreeNode<Token> algoNode = new TreeNode<>(new Token(-1, TokenType.V, "ALGO")); // Match 'begin'
		algoNode.addChild(new TreeNode<>(match(TokenType.BEGIN))); // Match 'begin'

		// Parse instructions (nullable)
		algoNode.addChild(parseInstruc());

		algoNode.addChild(new TreeNode<>(match(TokenType.END))); // Match 'end'
		return algoNode;
	}

	// INSTRUC ::= /* nullable */
	// INSTRUC ::= COMMAND ; INSTRUC
	private TreeNode<Token> parseInstruc() {
		TreeNode<Token> instrucNode = new TreeNode<>(new Token(-1, TokenType.V, "INSTRUC")); // Non-terminal node

		while (peek() != null && !peek().getWord().equals("end")) {
			instrucNode.addChild(parseCommand()); // Parse a command
			instrucNode.addChild(new TreeNode<>(match(TokenType.SCOLON))); // Match ';'
		}

		return instrucNode;
	}

	// COMMAND ::= skip | halt | print ATOMIC | ASSIGN | CALL | BRANCH | return ATOMIC
	private TreeNode<Token> parseCommand() {
		TokenType current = TokenType.fromTokenType(peek().getTokenClass());
		TreeNode<Token> commandNode;
		switch (current) {
			case SKIP -> commandNode = new TreeNode<>(match(TokenType.SKIP)); // Match 'skip'
			case HALT -> commandNode = new TreeNode<>(match(TokenType.HALT)); // Match 'halt'
			case PRINT -> {
				commandNode = new TreeNode<>(new Token(-1, TokenType.V, "COMMAND")); // Match 'print'
				commandNode.addChild(new TreeNode<>(match(TokenType.PRINT))); // Match 'print'
				commandNode.addChild(parseAtomic()); // Match atomic after 'print'
			}
			case RETURN -> {
				commandNode = new TreeNode<>(new Token(-1, TokenType.V, "COMMAND")); // Match 'return'
				commandNode.addChild(new TreeNode<>(match(TokenType.RETURN))); // Match 'return'
				commandNode.addChild(parseAtomic()); // Match atomic after 'return'
			}
			case V -> commandNode = parseAssign(); // Parse assignment if V-Token (variable)
			case F -> commandNode = parseCall(); // Parse function call
			case IF -> commandNode = parseBranch(); // Parse branch (if-then-else)
			default -> {
				error("Unrecognized command: "+peek().getTokenClass());
				return null;
			}
		}

		return commandNode;
	}

	// ATOMIC ::= VNAME | CONST
	private TreeNode<Token> parseAtomic() {
		TokenType current = TokenType.fromString(peek().getTokenClass());
		if (null == current) {
			error("Expected atomic expression");
			return null;
		} else
			switch (current) {
				case V -> {
					return new TreeNode<>(match(TokenType.V)); // Match V-Token (variable name)
				}
				case N, T -> {
					return new TreeNode<>(match(current)); // Match constant (N-Token or T-Token)
				}
				default -> {
					error("Expected atomic expression");
					return null;
				}
			}
	}

	// ASSIGN ::= VNAME < input | VNAME = TERM
	private TreeNode<Token> parseAssign() {
		TreeNode<Token> assignNode = new TreeNode<>(new Token(-1, TokenType.V, "ASSIGN")); // Non-terminal node
		assignNode.addChild(new TreeNode<>(match(TokenType.V))); // Match variable (VNAME)
		switch (peek().getWord()) {
			case "< input" -> assignNode.addChild(new TreeNode<>(match(TokenType.INPUT))); // Match '< input'
			case "=" -> {
				match(TokenType.EQUALS); // Match '='
				assignNode.addChild(parseTerm()); // Parse the term on the right-hand side
			}
			default -> error("Invalid assignment syntax");
		}

		return assignNode;
	}

	// CALL ::= FNAME(ATOMIC, ATOMIC, ATOMIC)
	private TreeNode<Token> parseCall() {
		TreeNode<Token> callNode = new TreeNode<>(new Token(-1, TokenType.V, "CALL")); // Non-terminal node
		callNode.addChild(new TreeNode<>(match(TokenType.F))); // Match function name (F-Token)

		callNode.addChild(new TreeNode<>(match(TokenType.LPAREN))); // Match '('

		callNode.addChild(parseAtomic()); // Match first argument

		callNode.addChild(new TreeNode<>(match(TokenType.COMMA)));

		callNode.addChild(parseAtomic()); // Match second argument

		callNode.addChild(new TreeNode<>(match(TokenType.COMMA)));

		callNode.addChild(parseAtomic()); // Match third argument

		callNode.addChild(new TreeNode<>(match(TokenType.RPAREN))); // Match ')'

		return callNode;
	}

	// BRANCH ::= if COND then ALGO else ALGO
	private TreeNode<Token> parseBranch() {
		TreeNode<Token> branchNode = new TreeNode<>(new Token(-1, TokenType.V, "BRANCH")); // Non-terminal node
		branchNode.addChild(new TreeNode<>(match(TokenType.IF))); // Match 'if'

		branchNode.addChild(parseCond()); // Parse condition

		branchNode.addChild(new TreeNode<>(match(TokenType.THEN))); // Match 'then'
		branchNode.addChild(parseAlgo()); // Parse then-algo

		branchNode.addChild(new TreeNode<>(match(TokenType.ELSE))); // Match 'else'
		branchNode.addChild(parseAlgo()); // Parse else-algo

		return branchNode;
	}

	// TERM ::= ATOMIC | CALL | OP
	private TreeNode<Token> parseTerm() {
		TokenType current = TokenType.fromString(peek().getTokenClass());
		switch (current) {
			case V, N, T -> {
				return parseAtomic(); // If it's a variable or constant, parse it as atomic
			}
			case F -> {
				return parseCall(); // If it's a function call, parse as a call
			}
			default -> {
				return parseOp(); // Otherwise, it's an operator, parse as an operation
			}
		}
	}

	// OP ::= UNOP(ARG) | BINOP(ARG,ARG)
	private TreeNode<Token> parseOp() {
		TokenType current = TokenType.fromTokenType(peek().getTokenClass());
		TreeNode<Token> opNode;

		// UNOP handling (unary operations)
		if (current == TokenType.NOT || current == TokenType.SQRT) {
			opNode = new TreeNode<>(match(current)); // Match 'not' or 'sqrt'
			match(TokenType.LPAREN); // Match '('
			opNode.addChild(parseArg()); // Parse argument inside parentheses
			match(TokenType.RPAREN); // Match ')'
		}
		// BINOP handling (binary operations)
		else if (current == TokenType.ADD || current == TokenType.SUB || current == TokenType.MUL ||
				current == TokenType.DIV || current == TokenType.OR || current == TokenType.AND ||
				current == TokenType.EQ || current == TokenType.GRT) {
			opNode = new TreeNode<>(match(current)); // Match binary operator
			match(TokenType.LPAREN); // Match '('
			opNode.addChild(parseArg()); // Parse first argument
			match(TokenType.COMMA); // Match ','
			opNode.addChild(parseArg()); // Parse second argument
			match(TokenType.RPAREN); // Match ')'
		} else {
			error("Expected operator");
			return null;
		}

		return opNode;
	}

	// ARG ::= ATOMIC | OP
	private TreeNode<Token> parseArg() {
		TokenType current = TokenType.fromString(peek().getTokenClass());
		if (current == TokenType.V || current == TokenType.N || current == TokenType.T) {
			return parseAtomic(); // If it's an atomic value, parse it
		} else {
			return parseOp(); // Otherwise, it's an operation
		}
	}

	// COND ::= UCOND | BCOND
	private TreeNode<Token> parseCond() {
		TokenType current = TokenType.fromTokenType(peek().getTokenClass());
		if (current == TokenType.NOT || current == TokenType.SQRT) {
			return parseUCond(); // Parse unary condition
		} else if (current == TokenType.OR || current == TokenType.AND || current == TokenType.EQ
				|| current == TokenType.GRT) {
			return parseBCond(); // Parse binary condition
		} else {
			error("Expected condition");
			return null;
		}
	}

	// UCOND ::= UNOP(SIMPLE)
	private TreeNode<Token> parseUCond() {
		TreeNode<Token> uCondNode = new TreeNode<>(match(TokenType.fromString(peek().getTokenClass()))); // Match 'not'
																											// or 'sqrt'
		match(TokenType.LPAREN); // Match '('
		uCondNode.addChild(parseSimple()); // Parse simple condition inside parentheses
		match(TokenType.RPAREN); // Match ')'
		return uCondNode;
	}

	// BCOND ::= BINOP(BPARAM)
	private TreeNode<Token> parseBCond() {
		TreeNode<Token> bCondNode = new TreeNode<>(match(TokenType.fromTokenType(peek().getTokenClass()))); // Match binary
																											// operator
		match(TokenType.LPAREN); // Match '('
		bCondNode.addChild(parseBParam()); // Parse the binary parameters
		match(TokenType.RPAREN); // Match ')'
		return bCondNode;
	}

	// BPARAM ::= SIMPLE,SIMPLE | ATOMIC,ATOMIC
	private TreeNode<Token> parseBParam() {
		TreeNode<Token> bParamNode = new TreeNode<>(new Token(-1, TokenType.V, "BPARAM")); // Non-terminal node
		TokenType firstArg = TokenType.fromTokenType(peek().getTokenClass());

		if (firstArg == TokenType.V || firstArg == TokenType.N || firstArg == TokenType.T) {
			bParamNode.addChild(parseAtomic()); // Parse first atomic argument
		} else {
			bParamNode.addChild(parseSimple()); // Parse first simple expression
		}

		match(TokenType.COMMA); // Match ','

		TokenType secondArg = TokenType.fromTokenType(peek().getTokenClass());
		if (secondArg == TokenType.V || secondArg == TokenType.N || secondArg == TokenType.T) {
			bParamNode.addChild(parseAtomic()); // Parse second atomic argument
		} else {
			bParamNode.addChild(parseSimple()); // Parse second simple expression
		}

		return bParamNode;
	}

	// SIMPLE ::= BINOP(ATOMIC,ATOMIC)
	private TreeNode<Token> parseSimple() {
		TreeNode<Token> simpleNode = new TreeNode<>(match(TokenType.fromTokenType(peek().getTokenClass()))); // Match
																											// binary
																											// operator
		match(TokenType.LPAREN); // Match '('
		simpleNode.addChild(parseAtomic()); // Parse first atomic argument
		match(TokenType.COMMA); // Match ','
		simpleNode.addChild(parseAtomic()); // Parse second atomic argument
		match(TokenType.RPAREN); // Match ')'
		return simpleNode;
	}

	// FUNCTIONS ::= /* nullable */ | DECL FUNCTIONS
	private TreeNode<Token> parseFunctions() {
		TreeNode<Token> functionsNode = new TreeNode<>(new Token(-1, TokenType.V, "FUNCTIONS")); // Non-terminal node

		while (peek() != null && (peek().getWord().equals("void") || peek().getWord().equals("num"))) {
			functionsNode.addChild(parseDecl()); // Parse function declaration
		}

		return functionsNode;
	}

	// DECL ::= HEADER BODY
	private TreeNode<Token> parseDecl() {
		TreeNode<Token> declNode = new TreeNode<>(new Token(-1, TokenType.V, "DECL")); // Non-terminal node
		declNode.addChild(parseHeader()); // Parse header
		declNode.addChild(parseBody()); // Parse body
		return declNode;
	}

	// HEADER ::= FTYP FNAME(VNAME, VNAME, VNAME)
	private TreeNode<Token> parseHeader() {
		TreeNode<Token> headerNode = new TreeNode<>(new Token(-1, TokenType.V, "HEADER")); // Non-terminal node
		headerNode.addChild(new TreeNode<>(match(TokenType.fromString(peek().getWord())))); // Match function type (num | void)
		headerNode.addChild(new TreeNode<>(match(TokenType.F))); // Match function name (FNAME)
		match(TokenType.LPAREN); // Match '('
		headerNode.addChild(new TreeNode<>(match(TokenType.V))); // Match first variable name (VNAME)
		match(TokenType.COMMA);
		headerNode.addChild(new TreeNode<>(match(TokenType.V))); // Match second variable name (VNAME)
		match(TokenType.COMMA);
		headerNode.addChild(new TreeNode<>(match(TokenType.V))); // Match third variable name (VNAME)
		match(TokenType.RPAREN); // Match ')'
		return headerNode;
	}

	// BODY ::= PROLOG LOCVARS ALGO EPILOG SUBFUNCS end
	private TreeNode<Token> parseBody() {
		TreeNode<Token> bodyNode = new TreeNode<>(new Token(-1, TokenType.V, "BODY")); // Non-terminal node
		bodyNode.addChild(parseProlog()); // Parse prolog
		bodyNode.addChild(parseLocVars()); // Parse local variables
		bodyNode.addChild(parseAlgo()); // Parse algorithm
		bodyNode.addChild(parseEpilog()); // Parse epilog
		bodyNode.addChild(parseSubFuncs()); // Parse sub-functions
		bodyNode.addChild(new TreeNode<>(match(TokenType.END))); // Match 'end'
		return bodyNode;
	}

	// PROLOG ::= {
	private TreeNode<Token> parseProlog() {
		return new TreeNode<>(match(TokenType.LBRACE)); // Match '{'
	}

	// EPILOG ::= }
	private TreeNode<Token> parseEpilog() {
		return new TreeNode<>(match(TokenType.RBRACE)); // Match '}'
	}

	// LOCVARS ::= VTYP VNAME , VTYP VNAME , VTYP VNAME ,
	private TreeNode<Token> parseLocVars() {
		TreeNode<Token> locVarsNode = new TreeNode<>(new Token(-1, TokenType.V, "LOCVARS")); // Non-terminal node

		while (peek() != null && (peek().getWord().equals("num") || peek().getWord().equals("text"))) {
			locVarsNode.addChild(new TreeNode<>(match(TokenType.fromTokenType(peek().getTokenClass())))); // Match variable
																										// type (VTYP)
			locVarsNode.addChild(new TreeNode<>(match(TokenType.V))); // Match variable name (VNAME)
			locVarsNode.addChild(new TreeNode<>(match(TokenType.COMMA))); // Match ','
		}

		return locVarsNode;
	}

	// SUBFUNCS ::= FUNCTIONS
	private TreeNode<Token> parseSubFuncs() {
		return parseFunctions(); // Functions are sub-functions
	}
}

// PROG ::= main GLOBVARS ALGO FUNCTIONS
// GLOBVARS ::= /* nullable */
// GLOBVARS ::= VTYP VNAME , GLOBVARS
// VTYP ::= num | text
// VNAME ::= V-Token
// ALGO ::= begin INSTRUC end
// INSTRUC ::= /* nullable */
// INSTRUC ::= COMMAND ; INSTRUC
// COMMAND ::= skip | halt | print ATOMIC | ASSIGN | CALL | BRANCH | return
// ATOMIC
// ATOMIC ::= VNAME | CONST
// CONST ::= N-Token | T-Token
// ASSIGN ::= VNAME < input | VNAME = TERM
// CALL ::= FNAME(ATOMIC, ATOMIC, ATOMIC)
// BRANCH ::= if COND then ALGO else ALGO

// TERM ::= ATOMIC | CALL | OP
// OP ::= UNOP(ARG) | BINOP(ARG,ARG)
// ARG ::= ATOMIC | OP
// COND ::= UCOND | BCOND
// UCOND ::= UNOP(SIMPLE)
// BCOND ::= BINOP(BPARAM)
// BPARAM ::= SIMPLE,SIMPLE | ATOMIC,ATOMIC
// SIMPLE ::= BINOP(ATOMIC,ATOMIC)
// UNOP ::= not | sqrt
// BINOP ::= or | and | eq | grt | add | sub | mul | div
// FNAME ::= F-Token
// FUNCTIONS ::= /* nullable */ | DECL FUNCTIONS
// DECL ::= HEADER BODY
// HEADER ::= FTYP FNAME(VNAME, VNAME, VNAME)
// FTYP ::= num | void
// BODY ::= PROLOG LOCVARS ALGO EPILOG SUBFUNCS end
// PROLOG ::= {
// EPILOG ::= }
// LOCVARS ::= VTYP VNAME , VTYP VNAME , VTYP VNAME ,
// SUBFUNCS ::= FUNCTIONS