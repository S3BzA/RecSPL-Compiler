import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SourceReader {
    private List<String> sourceCode;
	private int currentLine = 0;
	private int currentChar = 0;

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

	public String nextLine() {
		if (currentLine >= sourceCode.size()) {
			System.out.println("End of file reached.");
			return null;
		}
		return sourceCode.get(currentLine++);
	}

	public void reset() {
		currentLine = 0;
	}

	public char nextChar() {
		return getCharacter(currentLine, currentChar++);
	}
}