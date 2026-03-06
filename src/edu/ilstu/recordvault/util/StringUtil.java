package edu.ilstu.recordvault.util;

public class StringUtil {

    private StringUtil() {}

    // IDS01-J (Driss)
    // Clean up whitespace first so the string is predictable before validation or storage.
    // ERR08-J (Driss)
    // Null input is handled up front instead of throwing a NullPointerException.
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
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
