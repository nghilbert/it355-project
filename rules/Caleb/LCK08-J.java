import java.util.concurrent.locks.ReentrantLock;

/**
 * Demonstrates the CERT rule LCK08-J: Ensure actively held locks
 * are released on exceptional conditions.
 *
 * This example shows how to correctly release a lock using a
 * finally block. Even if an exception occurs inside the critical
 * section, the lock will still be released, preventing deadlocks
 * and blocked threads.
 *
 * @author Caleb Appiah
 */
public class LockExample {

    /** Lock used to control access to the critical section */
    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * Entry point of the program demonstrating safe lock handling.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {

        lock.lock();

        try {
            System.out.println("Critical section running");
        } finally {
            lock.unlock();
        }

    }
}
