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

    public void tokenize() {
        List<String> sourceCodeLines = sourceReader.getSourceCode();
        String sourceCode = String.join("\n", sourceCodeLines);
        System.out.println("Source code:\n" + sourceCode);

        String regex = "\\b(main|begin|end|skip|halt|print|input|num|text|void|if|then|else|not|sqrt|or|and|eq|grt|add|sub|mul|div)\\b|V_[a-z]([a-z]|[0-9])*|F_[a-z]([a-z]|[0-9])*|\"[A-Z][a-z]{0,7}\"|[=(),{}]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sourceCode);
        int tokenCounter = 1;
        int lastMatchEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastMatchEnd) {
                String unmatched = sourceCode.substring(lastMatchEnd, matcher.start()).trim();
                if (!unmatched.isEmpty()) {
                    handleSyntaxError(unmatched);
                }
            }

            String match = matcher.group();
            TokenType type;

            if (TokenType.isKeyword(match)) {
                type = TokenType.fromString(match);
            } else if (isVariableName(match)) {
                type = TokenType.V;
            } else if (isFunctionName(match)) {
                type = TokenType.F;
            } else if (isStringLiteral(match)) {
                type = TokenType.STRING_LITERAL;
            } else {
                continue;
            }

            tokens.add(new Token(tokenCounter++, type, match));
            lastMatchEnd = matcher.end();
        }

        if (lastMatchEnd < sourceCode.length()) {
            String unmatched = sourceCode.substring(lastMatchEnd).trim();
            if (!unmatched.isEmpty()) {
                handleSyntaxError(unmatched);
            }
        }

        exportTokens();
    }

    private boolean isVariableName(String word) {
        return word.matches("V_[a-z]([a-z]|[0-9])*");
    }

    private boolean isFunctionName(String word) {
        return word.matches("F_[a-z]([a-z]|[0-9])*");
    }

    private boolean isStringLiteral(String word) {
        return word.matches("\"[A-Z][a-z]{0,7}\"");
    }

    private void handleSyntaxError(String unmatched) {
        if (unmatched.matches("\"[A-Z][a-z]{8,}\"")) {
            throw new RuntimeException("Syntax Error: String literal invalid length: " + unmatched);
        } else {
            throw new RuntimeException("Syntax Error: Unrecognized token: " + unmatched);
        }
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