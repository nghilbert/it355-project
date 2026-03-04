import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Demonstrates EXP00-J: Do not ignore values returned by methods.
 *
 * Return values often communicate success/failure or useful results that must be handled.
 * Ignoring them can hide errors and lead to unsafe behavior.
 */
public final class Exp00J_DoNotIgnoreReturnValues {

    private Exp00J_DoNotIgnoreReturnValues() { }

    /**
     * Attempts to create a directory and handles the return value / outcome.
     *
     * @param dir Directory path to create
     * @throws IOException If an I/O error occurs
     */
    public static void createDirectorySafely(Path dir) throws IOException {
        // Files.createDirectories() returns the created directory path (or the existing one).
        // Even if you don't "need" the Path, you should still handle exceptions and validate outcomes.
        Path result = Files.createDirectories(dir);

        // Use the result: confirm it exists and is a directory.
        if (!Files.isDirectory(result)) {
            throw new IOException("Failed to create directory: " + result);
        }
    }

    /**
     * Removes a file and checks the boolean return value.
     *
     * @param file File path to delete
     * @throws IOException If deletion fails
     */
    public static void deleteFileSafely(Path file) throws IOException {
        boolean deleted = Files.deleteIfExists(file); // returns true if a file was deleted
        if (!deleted && Files.exists(file)) {
            throw new IOException("Delete reported false but file still exists: " + file);
        }
    }
}