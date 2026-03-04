/**
 * Compliant example for CERT rule ERR08-J.
 *
 * <p>Do not catch {@link NullPointerException}. Instead, prevent null dereferences by validating
 * inputs (or by clearly documenting a non-null precondition).</p>
 *
 * @author Driss El Andlosy
 */
public final class Err08J {

    /**
     * Prevent instantiation of this utility class.
     */
    private Err08J() {
        // Utility class: no instances.
    }

    /**
     * Checks whether a string looks like a simple name in the form {@code "First Last"}.
     *
     * <p>This method is compliant with ERR08-J because it avoids catching
     * {@link NullPointerException} and instead performs an explicit null check before
     * dereferencing {@code s}.</p>
     *
     * @param s input string; may be {@code null}
     * @return {@code true} if the trimmed input contains exactly two whitespace-separated words;
     *         {@code false} otherwise
     */
    public static boolean isName(String s) {
        if (s == null) {
            return false;
        }
        String[] parts = s.trim().split("\\s+");
        return parts.length == 2;
    }

    /**
     * Demonstrates the compliant method with a {@code null} input.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        String input = null;
        System.out.println("Result: " + isName(input));
    }
}