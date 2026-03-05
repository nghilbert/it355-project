import java.io.IOException;
import java.nio.file.*;

/**
 * FIO02-J: Detect and handle file-related errors
 *
 * Rule:
 * Always detect and handle file I/O failures.
 * Do NOT assume file operations succeed.
 *
 * Best practice:
 * Use java.nio.file.Files methods because they throw exceptions
 * when something goes wrong (instead of silently failing).
 */
public class FIO02J {

    public static void main(String[] args) {
        Path file = Paths.get("demo.txt");

        try {
            // Write to file (throws IOException if it fails)
            Files.writeString(file, "Hello Secure Coding\n");

            // Delete file (throws exception if it fails)
            boolean deleted = file.toFile().delete();   // returns true/false (can fail silently)
            if (!deleted) {
                System.err.println("Delete failed (checked return value).");
                return;
            }

            System.out.println("File operations succeeded.");

        } catch (NoSuchFileException e) {
            System.err.println("Error: File not found -> " + e.getFile());

        } catch (IOException e) {
            System.err.println("I/O error occurred -> " + e.getMessage());

        } catch (SecurityException e) {
            System.err.println("Permission denied -> " + e.getMessage());
        }
    }
}