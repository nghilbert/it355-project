/**
 * Demonstrates the CERT rule VNA00-J: Ensure visibility when
 * accessing shared variables.
 *
 * This example uses the volatile keyword to ensure that updates
 * to the shared variable are visible across multiple threads.
 * Without volatile, one thread might not see updates made by
 * another thread due to caching or instruction reordering.
 *
 * @author Caleb Appiah
 */
public class VisibilityExample {

    /** Shared variable visible across all threads */
    private static volatile boolean running = true;

    /**
     * Entry point of the program demonstrating thread visibility.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {

        new Thread(() -> {
            while(running) {
                System.out.println("Thread running...");
            }
        }).start();

        running = false;
    }

}
