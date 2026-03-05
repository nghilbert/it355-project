/**
 * Demonstrates the CERT rule LCK01-J: Do not synchronize on objects
 * that may be reused.
 *
 * This class uses a private lock object for synchronization instead
 * of shared objects like Strings or boxed primitives. This prevents
 * unintended interference from other parts of the program that may
 * also synchronize on those objects.
 *
 * @author Caleb Appiah
 */
public class SafeSync {

    /** Private lock object used for synchronization */
    private final Object lock = new Object();

    /**
     * Performs a thread-safe operation using the private lock.
     */
    public void safeMethod() {

        synchronized(lock) {
            System.out.println("Thread-safe operation");
        }

    }

}
