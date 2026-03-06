package util;

// RuleMap:
//   IDS01-J (Driss) — normalize strings: trim whitespace and collapse multiple spaces
//   ERR08-J (Driss) — null-check every parameter; never catch NullPointerException
//   EXP01-J (Lucas) — keep string operations simple and readable

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

        // ERR08-J (Driss)
        if (input == null) {
            return "";
        }

        if (maxLength < 4) {
            maxLength = 4;
        }

        // EXP01-J (Lucas)
        if (input.length() <= maxLength) {
            return input;
        }

        return input.substring(0, maxLength - 3) + "...";
    }

    // ERR08-J (Driss)
    // Explicit null check avoids relying on exceptions.
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
