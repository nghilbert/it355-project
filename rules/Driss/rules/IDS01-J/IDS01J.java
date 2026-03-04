import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Pattern;

/**
 * Demonstrates IDS01-J: normalize strings before validating them.
 *
 * <p>Some Unicode characters can be normalized into other characters (for example, "fullwidth"
 * angle brackets can normalize into {@code '<'} and {@code '>'}). If validation happens first,
 * malicious input may bypass filters.</p>
 *
 */
public final class IDS01J {

    /** Matches blacklisted characters often used in HTML tags. */
    private static final Pattern ANGLE_BRACKETS = Pattern.compile("[<>]");

    private IDS01J() {
        // Utility class; prevent instantiation.
    }

    /**
     * Validates user input after normalizing it to Unicode NFKC.
     *
     * @param input untrusted input string
     * @return normalized safe string
     * @throws IllegalArgumentException if the normalized input contains disallowed characters
     */
    public static String normalizeThenValidate(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input must not be null.");
        }

        // 1) Normalize FIRST (recommended form for validation: NFKC)
        String normalized = Normalizer.normalize(input, Form.NFKC);

        // 2) Validate AFTER normalization
        if (ANGLE_BRACKETS.matcher(normalized).find()) {
            throw new IllegalArgumentException("Input contains disallowed characters.");
        }

        return normalized;
    }

    /**
     * Runs a small demo showing why normalization must happen before validation.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        // \uFE64 and \uFE65 normalize to '<' and '>' under NFKC
        String attacker = "\uFE64" + "script" + "\uFE65";

        try {
            System.out.println(normalizeThenValidate(attacker));
        } catch (IllegalArgumentException ex) {
            System.out.println("Blocked: " + ex.getMessage());
        }
    }
}