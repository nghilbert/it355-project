package edu.ilstu.recordvault.util;

import edu.ilstu.recordvault.util.SafeLogger;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
/**
 * Utility methods for safe file operations.
 *
 * Provides helper functions that prevent partial file writes
 * and handle file errors safely.
 */
public class FileUtil {

    private static final SafeLogger logger = SafeLogger.getInstance();
    /**
 * Prevents instantiation of the utility class.
 */
    private FileUtil() {}

    // FIO02-J (Lucas)
    // Write to a temp file first so the real file does not get half-written.
    // FIO14-J (Lucas)
    // If something fails the temp file gets cleaned up.
    /**
 * Writes content to a file using an atomic write strategy.
 *
 * The file is first written to a temporary file and then moved
 * to the destination to avoid partially written files (per FIO02-J).
 * Temporary files are cleaned up if an error occurs (per FIO14-J).
 * Null parameters are checked before use (per ERR08-J).
 *
 * @param destination the destination file path
 * @param content the content to write
 * @return true if the write operation succeeds, false if an error occurs
 */
    public static boolean atomicWrite(Path destination, String content) {

        // ERR08-J (Driss)
        // Nulls are checked up front instead of letting the method crash later.
        if (destination == null || content == null) {
            logger.log("atomicWrite called with null argument.");
            return false;
        }

        Path tempPath = destination.resolveSibling(destination.getFileName() + ".tmp");

        try {
            Path parent = destination.getParent();
            if (parent != null && !Files.exists(parent)) {

                // EXP00-J (Lucas)
                // mkdirs style call has a result so we check it
                Files.createDirectories(parent);
            }

            // FIO02-J (Lucas)
            Files.writeString(tempPath, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);

            try {
                // EXP00-J (Lucas)
                Files.move(tempPath, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                logger.log("ATOMIC_MOVE not supported using regular move.");
                Files.move(tempPath, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            return true;

        } catch (IOException e) {

            // ERR01-J (Driss)
            // Catch the failure here and log a safe messag only.
            logger.log("atomicWrite failed: " + e.getClass().getSimpleName() + " for " + destination);

            // FIO14-J (Lucas)
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ignored) {
                logger.log("Could not delete temp file: " + tempPath);
            }

            return false;
        }
    }
}
