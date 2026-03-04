import java.util.Objects;

/**
 * Demonstrates EXP01-J: Do not use null when an object is required.
 *
 * Enforce non-null inputs and fail fast with clear error messages instead of
 * allowing NullPointerException later.
 */
public final class Exp01J_NoNullWhereObjectRequired 
{

    private Exp01J_NoNullWhereObjectRequired() { }

    /**
     * Prints a username safely by enforcing non-null.
     *
     * @param username A required username (must not be null)
     */
    public static void greetUser(String username) 
    {
        // Fail fast and clearly if the contract is violated
        String safeUsername = Objects.requireNonNull(username, "username must not be null");
        System.out.println("Hello, " + safeUsername + "!");
    }

    /**
     * Example usage.
     *
     * @param args Command-line args
     */
    public static void main(String[] args) 
    {
        greetUser("Lucas");
        // greetUser(null); // would throw a clear NullPointerException with your message
    }
}