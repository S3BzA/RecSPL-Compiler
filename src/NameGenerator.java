public class NameGenerator {
    private int vNames = 0;
    private int fNames = 0;
    private static NameGenerator instance;
    
    private NameGenerator() {}

    public static NameGenerator getNameGenerator() {
        if (instance == null) {
            instance = new NameGenerator();
        }
        return instance;
    }

    public String getVarName() {
        return "v" + vNames++;
    }

    public String getFunName() {
        return "f" + fNames++;
    }
}
