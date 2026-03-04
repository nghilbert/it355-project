import java.io.FileInputStream;
import java.io.IOException;

/**
 * Demonstrates compliant handling of exceptions.
 *
 * <p>Rule: ERR01-J. Do not allow exceptions to expose sensitive information.
 * Sensitive details such as file paths or stack traces should not be
 * revealed to the user.</p>
 *
 * @author Driss El Andlosy
 */
public class ERR01J {

    /**
     * Opens a file without exposing sensitive exception details.
     *
     * @param fileName the name of the file to open
     */
    public static void openFile(String fileName) {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            System.out.println("File opened successfully.");
            fis.close();

        } catch (IOException e) {
            // Do NOT print stack trace or sensitive details
            System.out.println("Error: Unable to open file.");
        }
    }

    /**
     * Main method for testing the rule.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        openFile("data.txt");
    }
}