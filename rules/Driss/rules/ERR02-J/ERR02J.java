import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Demonstrates CERT rule ERR02-J: do not let logging failures hide real errors.
 *
 * <p>Logging is frequently performed inside {@code catch} blocks. If the logging operation throws a
 * runtime exception, it can obscure the original failure or interrupt recovery logic. This class
 * provides a small wrapper that attempts to log but never allows logging to terminate the program.</p>
 *
 * @author Driss El Andlosy
 * @version 1.0
 * @since 1.0
 */
public final class ERR02J {

    /**
     * Logger used by this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ERR02J.class.getName());

    /**
     * Prevent instantiation of this utility class.
     */
    private ERR02J() {
        // Utility class: no instances.
    }

    /**
     * Logs a message and throwable without allowing logging failures to propagate.
     *
     * <p>This method enforces ERR02-J by ensuring that any {@link RuntimeException} thrown by the
     * logging framework cannot hide the original exception or crash recovery code.</p>
     *
     * @param message the context message to log; may be {@code null}
     * @param cause   the original problem being logged; must not be {@code null}
     * @throws NullPointerException if {@code cause} is {@code null}
     */
    public static void safeLog(String message, Throwable cause) {
        if (cause == null) {
            throw new NullPointerException("cause must not be null");
        }
        try {
            LOGGER.log(Level.SEVERE, message, cause);
        } catch (RuntimeException loggingFailure) {
            // Deliberately ignored: logging must not interfere with normal control flow.
        }
    }

    /**
     * Runs a short demonstration showing that the program can recover even if an error occurs.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Program started.");

        try {
            throw new SecurityException("Access denied!");
        } catch (SecurityException se) {
            safeLog("Security problem occurred.", se);
            System.out.println("Program recovered safely.");
        }

        System.out.println("Program finished.");
    }
}