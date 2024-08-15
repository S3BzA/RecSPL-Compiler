import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SourceReader {
    private List<String> sourceCode;

    public void readSourceCode(String filePath) throws IOException {
        sourceCode = Files.readAllLines(Paths.get(filePath));
    }

    public List<String> getSourceCode() {
        return sourceCode;
    }

    public String getLine(int lineNumber) {
        if (lineNumber < 0 || lineNumber >= sourceCode.size()) {
            throw new IndexOutOfBoundsException("Invalid line number: " + lineNumber);
        }
        return sourceCode.get(lineNumber);
    }

    public char getCharacter(int lineNumber, int charIndex) {
        String line = getLine(lineNumber);
        if (charIndex < 0 || charIndex >= line.length()) {
            throw new IndexOutOfBoundsException("Invalid character index: " + charIndex);
        }
        return line.charAt(charIndex);
    }
}