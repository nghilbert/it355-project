import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Demonstrates FIO14-J: Perform proper cleanup at program termination.
 *
 * If the JVM is terminated (System.exit / Runtime.exit), ensure critical cleanup occurs:
 * flush buffers, close files, release locks. Use try-with-resources and/or a shutdown hook.
 */
public final class Fio14J_CleanupOnTermination 
{

    private Fio14J_CleanupOnTermination() { }

    /**
     * Runs a program that writes to a file and installs a shutdown hook to ensure cleanup.
     *
     * @param args command-line args
     * @throws IOException If an I/O error occurs
     */
    public static void main(String[] args) throws IOException 
    {
        Path out = Path.of("app.log");

        BufferedWriter writer = Files.newBufferedWriter(out, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        // Shutdown hook ensures we still attempt to flush/close if System.exit() is called.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try 
            {
                writer.flush();
                writer.close();
            } 
            catch (IOException e) 
            {
                // In real apps: log to a fallback sink if possible.
                System.err.println("Shutdown cleanup failed: " + e.getMessage());
            }
        }));

        try 
        {
            writer.write("Application started\n");
            writer.flush();

            // Simulate serious failure condition:
            if (args.length > 0 && args[0].equalsIgnoreCase("fatal")) 
            {
                writer.write("Fatal error encountered, terminating safely...\n");
                writer.flush();
                System.exit(1); // Hook runs before JVM exits.
            }

            writer.write("Application finished normally\n");
            writer.flush();
        } 
        finally 
        {
            // Normal path cleanup (also good practice even with a shutdown hook).
            writer.close();
        }
    }
}