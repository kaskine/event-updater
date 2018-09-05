import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static final String GUI_FLAG = "-g";

    public static void main(String[] args) {

        List<String> arguments = Arrays.asList(args);

        if (arguments.size() == 0 || arguments.contains(GUI_FLAG)) {
            new Displayable();
        }
        else if (arguments.size() == 2) {
            Converter.convert(arguments.get(0), arguments.get(1));
        }
        else {
            System.out.println("Invalid Parameters. Please try again.");
        }
    }
}
