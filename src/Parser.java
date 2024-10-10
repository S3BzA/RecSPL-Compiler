
import java.util.List;

public class Parser {
	// Recursive Decent Parser for Grammar in LL(1)
	private List<Token> tokens;
	private int position;

	public Parser(List<Token> tokens) {
		this.tokens = tokens;
		this.position = 0;
	}
	
	// Match a specific token type and value (for literal matching)
	private void match(TokenType expectedType, String expectedValue) {
        if (position < tokens.size()) {
            Token currentToken = tokens.get(position);
            if (currentToken.getTokenClass().equals(expectedType.toString()) &&
                currentToken.getWord().equals(expectedValue)) {
                position++; // Advance the position if token matches
            } else {
                throw new RuntimeException("Syntax Error: Expected '" + expectedValue + "' at position " + position);
            }
        } else {
            throw new RuntimeException("Syntax Error: Unexpected end of input");
        }
    }

	// Each of the following methods implements a non-terminal symbol of the grammar (non-terminals are in uppercase)
	// PROG ::= main GLOBVARS ALGO FUNCTIONS
	// GLOBVARS ::= /* nullable */
	// GLOBVARS ::= VTYP VNAME , GLOBVARS
	// VTYP ::= num | text
	// VNAME ::= V-Token
	// ALGO ::= begin INSTRUC end
	// INSTRUC ::= /* nullable */
	// INSTRUC ::= COMMAND ; INSTRUC
	// COMMAND ::= skip | halt | print ATOMIC | ASSIGN | CALL | BRANCH
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
}