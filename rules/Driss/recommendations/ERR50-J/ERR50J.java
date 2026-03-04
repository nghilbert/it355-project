/**
 * Demonstrates ERR50-J: Use exceptions only for exceptional conditions.
 *
 * <p>Exceptions should not be used for normal control flow (like ending a loop).
 * Instead, use proper bounds checks and conditionals.</p>
 *
 * @author Driss El Andlosy
 */
public final class ERR50J {

    private ERR50J() {
        // Utility class; prevent instantiation.
    }

    /**
     * Joins all strings in the given array using normal control flow.
     *
     * @param strings array of strings to join
     * @return concatenated result (empty string if array is null or empty)
     */
    public static String joinStrings(String[] strings) {
        if (strings == null || strings.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (strings[i] != null) { // normal validation, not exceptions
                result.append(strings[i]);
            }
        }
        return result.toString();
    }

    /**
     * Simple test run.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        String[] data = {"Hi", " ", "Secure", " ", "Coding"};
        System.out.println(joinStrings(data));
    }
}