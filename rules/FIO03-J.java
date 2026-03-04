import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Demonstrates FIO03-J: Remove temporary files before termination.
 *
 * Use NIO.2 and ensure temp files are deleted when done. Prefer explicit cleanup
 * (try/finally) and/or DELETE_ON_CLOSE for stream-based APIs.
 */
public final class Fio03J_RemoveTempFiles 
{

    private Fio03J_RemoveTempFiles() { }

    /**
     * Creates a temp file, writes data, reads it, and ensures deletion when finished.
     *
     * @throws IOException If an I/O error occurs
     */
    public static void useTempFileSafely() throws IOException 
    {
        Path temp = Files.createTempFile("report-", ".tmp");
        try 
        {
            Files.writeString(
                    temp,
                    "temporary secret data\n",
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            String content = Files.readString(temp, StandardCharsets.UTF_8);
            System.out.println("Read: " + content.trim());
        } 
        finally 
        {
            // Guaranteed cleanup even if an exception happens above.
            try 
            {
                Files.deleteIfExists(temp);
            } 
            catch (IOException cleanupError)
            {
                // In real apps: log and/or alert; don't silently ignore.
                System.err.println("Failed to delete temp file: " + cleanupError.getMessage());
            }
        }
    }
}