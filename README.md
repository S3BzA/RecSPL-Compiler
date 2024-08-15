# RecSPL-Compiler

## Found Resources

- [Example Lexer](https://craftinginterpreters.com/scanning.html)
- [Switch-case Tokenizer in Java](https://github.com/oracle/coherence/blob/bf64fbe59832e1dca3361f0afc5b3cab300572ca/prj/coherence-core/src/main/java/com/tangosol/dev/compiler/java/Tokenizer.java#L497)
- [Java Scanner Delimeters](https://stackoverflow.com/questions/28766377/how-do-i-use-a-delimiter-with-scanner-usedelimiter-in-java)

## RecSPL Specification Breakdown

### Program Structure

A valid RecSPL 2024 program consists of four main sections:

1. **Global Variables (`GLOBVARS`)**: Optional.
2. **Algorithm (`ALGO`)**: The main logic block.
3. **Functions (`FUNCTIONS`)**: Optional; user-defined functions.
4. **Main Block (`PROG`)**: Entry point encapsulating the above sections.

### Grammar Overview

The RecSPL 2024 language is defined using a context-free grammar, and tokens are generated by a lexer.

```plaintext
PROG ::= main GLOBVARS ALGO FUNCTIONS
GLOBVARS ::= /* nullable */
GLOBVARS ::= VTYP VNAME , GLOBVARS
VTYP ::= num | text
VNAME ::= Token of Class V
ALGO ::= begin INSTRUC end
INSTRUC ::= /* nullable */
INSTRUC ::= COMMAND ; INSTRUC
COMMAND ::= skip | halt | print ATOMIC | ASSIGN | CALL | BRANCH
ATOMIC ::= VNAME | CONST
CONST ::= Token of Class N or T
ASSIGN ::= VNAME < input | VNAME = TERM
CALL ::= FNAME(ATOMIC, ATOMIC, ATOMIC)
BRANCH ::= if COND then ALGO else ALGO
TERM ::= ATOMIC | CALL | OP
OP ::= UNOP(ARG) | BINOP(ARG, ARG)
ARG ::= ATOMIC | OP
COND ::= SIMPLE | COMPOSIT
SIMPLE ::= BINOP(ATOMIC, ATOMIC)
COMPOSIT ::= BINOP(SIMPLE, SIMPLE) | UNOP(SIMPLE)
UNOP ::= not | sqrt
BINOP ::= or | and | eq | grt | add | sub | mul | div
FNAME ::= Token of Class F
FUNCTIONS ::= /* nullable */ | DECL FUNCTIONS
DECL ::= HEADER BODY
HEADER ::= FTYP FNAME(VNAME, VNAME, VNAME)
FTYP ::= num | void
BODY ::= PROLOG LOCVARS ALGO EPILOG SUBFUNCS end
PROLOG ::= {
EPILOG ::= }
LOCVARS ::= VTYP VNAME , VTYP VNAME , VTYP VNAME ,
SUBFUNCS ::= FUNCTIONS
```

### Lexical Tokens

- **VNAME**: Variable names, prefixed with `V_` for easy identification.
- **FNAME**: Function names, prefixed with `F_`.
- **CONST**: Constants, classified as numbers or text.
  - **Numbers (N)**: Integers or real numbers, optionally negative.
  - **Text (T)**: Strings up to 8 characters, starting with an uppercase letter.

### Key Language Constructs

#### Global Variables (`GLOBVARS`)

- **Syntax**: `VTYP VNAME , GLOBVARS`
- **Type**: `num` for numbers, `text` for text.
- **Example**: `num V_count, text V_name,`

#### Algorithm (`ALGO`)

- Defined between `begin` and `end` keywords.
- **Instructions (`INSTRUC`)**: Consist of a sequence of `COMMAND` statements, separated by semicolons.
  - **Commands**:
    - `skip`: No operation.
    - `halt`: Stops execution.
    - `print ATOMIC`: Outputs an atomic value (variable or constant).
    - `ASSIGN`: Assigns input or a computed value to a variable.
    - `CALL`: Invokes a void function.
    - `BRANCH`: Conditional execution (`if-then-else`).

#### Functions (`FUNCTIONS`)

- Can be nested.
- **Syntax**:
  - **Header**: `FTYP FNAME(VNAME, VNAME, VNAME)` where `FTYP` is `num` or `void`.
  - **Body**: Contains `PROLOG`, local variables, algorithm, `EPILOG`, and optional sub-functions.
  - **Example**: `num F_add(V_a, V_b, V_c) { num V_result, num V_temp, num V_accum, begin ... end }`

#### Operations (`OP`)

- **Unary Operations (`UNOP`)**: `not`, `sqrt`.
- **Binary Operations (`BINOP`)**: `or`, `and`, `eq`, `grt`, `add`, `sub`, `mul`, `div`.
- **Deep Nesting**: Allowed within terms for complex assignments and expressions.

#### Conditions (`COND`)

- **Simple Conditions (`SIMPLE`)**: Binary operations between atomic values.
- **Composite Conditions (`COMPOSIT`)**: Can nest one level deep using simple conditions and unary operators.

### Lexical Categories (Regular Expressions)

- **Variables (`V`)**: `V_[a-z]([a-z]|[0-9])*`
- **Functions (`F`)**: `F_[a-z]([a-z]|[0-9])*`
- **Text (`T`)**: Strings with up to 8 characters, starting with an uppercase letter.
- **Numbers (`N`)**: Integers and real numbers, optionally negative.

## RecSPL Hello World Example

```plaintext
main {
    text V_message,
    begin
        V_message = "Hello",
        print V_message,
        halt
    end
}
```
