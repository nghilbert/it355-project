/**
 * Demonstrates the CERT rule IDS07-J: Sanitize untrusted data passed
 * to the Runtime.exec() method.
 *
 * This program shows how to validate user input before passing it
 * to a system command. Only commands from a predefined whitelist
 * are allowed to execute. This prevents command injection attacks
 * where malicious input could cause unintended system operations.
 *
 * @author Caleb Appiah
 */
import java.io.IOException;

public class SafeExec {

    /**
     * Entry point of the program.
     * Validates a command before executing it through Runtime.exec().
     *
     * @param args command line arguments (not used)
     * @throws IOException if the system command fails to execute
     */
    public static void main(String[] args) throws IOException {

        String userInput = "dir";

        if(userInput.equals("dir") || userInput.equals("ls")) {
            Runtime.getRuntime().exec(userInput);
        } else {
            System.out.println("Invalid command");
        }

    }
}
