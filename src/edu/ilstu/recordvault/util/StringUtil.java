package edu.ilstu.recordvault.util;
/**
 * Utility methods for safe string handling.
 *
 * Provides normalization, sanitization, and validation helpers
 * used throughout RecordVault.
 */
public class StringUtil {
    /**
 * Prevents instantiation of the utility class.
 */
    private StringUtil() {}

    // IDS01-J (Driss)
    // Clean up whitespace first so the string is predictable before validation or storage.
    // ERR08-J (Driss)
    // Null input is handled up front instead of throwing a NullPointerException.
    /**
 * Normalizes whitespace in a string.
 *
 * Input is trimmed and repeated whitespace is collapsed into
 * a single space (per IDS01-J). Null input is handled safely
 * without throwing an exception (per ERR08-J).
 *
 * @param input the string to normalize
 * @return the normalized string
 */
    public static String normalize(String input) {

        // ERR08-J (Driss)
        if (input == null) {
            return "";
        }

        // IDS01-J (Driss)
        String trimmed = input.trim();

        // EXP01-J (Lucas)
        // Keep the transformation simple and readable.
        return trimmed.replaceAll("\\s+", " ");
    }

    // IDS01-J (Driss)
    // Strip newline characters so a crafted message cannot fake extra log entries.
    // ERR08-J (Driss)
    // Null input returns a safe placeholder instead of crashing.
    /**
 * Sanitizes a message before writing it to logs.
 *
 * Removes newline and control characters to prevent log
 * injection or forged log entries (per IDS01-J). Null input
 * is replaced with a safe placeholder (per ERR08-J).
 *
 * @param message the message to sanitize
 * @return the sanitized log-safe message
 */
    public static String sanitizeForLog(String message) {

        // ERR08-J (Driss)
        if (message == null) {
            return "(null)";
        }

        // IDS01-J (Driss)
        return message.replace('\n', ' ').replace('\r', ' ').replace('\0', ' ');
    }

    // EXP01-J (Lucas)
    // Straightforward length check without any tricky math.
    // ERR08-J (Driss)
    // Null input handled explicitly.
    /**
 * Truncates a string to a maximum length.
 *
 * Uses a simple length check and appends "..." when the string
 * exceeds the allowed length (per EXP01-J). Null input is
 * handled safely (per ERR08-J).
 *
 * @param input the string to truncate
 * @param maxLength the maximum allowed length
 * @return the truncated string
 */
    public static String truncate(String input, int maxLength) {
    // RULE: ERR08-J (Driss)
    if (input == null) {
        return "";
    }

    // RULE: EXP01-J (Lucas)
    int safeLength = Math.max(maxLength, 4);

    if (input.length() <= safeLength) {
        return input;
    }

    return input.substring(0, safeLength - 3) + "...";
}

    // ERR08-J (Driss)
    // Explicit null check avoids relying on exceptions.
    /**
 * Checks if a string is null or contains only whitespace.
 *
 * @param s the string to check
 * @return true if the string is null or blank
 */
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
