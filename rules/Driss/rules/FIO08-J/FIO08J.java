import java.io.FileInputStream;
import java.io.IOException;

/**
 * Demonstrates SEI CERT Oracle rule FIO08-J: Distinguish between bytes read from a stream and -1.
 *
 * <p>{@link FileInputStream#read()} returns an {@code int} in the range {@code 0..255} for a valid byte,
 * and {@code -1} to indicate end-of-stream (EOF). This program stores the return value in an {@code int}
 * and checks for {@code -1} before narrowing the value to a {@code byte}.</p>
 *
 * <p>Casting to {@code byte} before checking for {@code -1} is incorrect because a valid {@code 0xFF} byte
 * can become {@code -1} when narrowed, which can be mistaken for EOF.</p>
 *
 * @author Driss El Andlosy
 */
public class FIO08J {

    /**
     * Reads the file {@code FIO08J.java} byte-by-byte using a compliant EOF check.
     *
     * <p>The loop reads into an {@code int}, tests for {@code -1}, then safely casts to {@code byte}
     * for processing.</p>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try (FileInputStream in = new FileInputStream("FIO08J.java")) {

            int value;

            while ((value = in.read()) != -1) {
                byte data = (byte) value;
                System.out.print((data & 0xFF) + " ");
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}