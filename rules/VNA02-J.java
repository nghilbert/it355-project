import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates VNA02-J: Ensure compound operations on shared variables are atomic.
 *
 * Operations like ++, +=, and "check-then-act" must be synchronized or use atomic classes.
 */
public final class Vna02J_AtomicCompoundOps
 {

    private Vna02J_AtomicCompoundOps() { }

    private static final AtomicInteger requestCount = new AtomicInteger(0);

    /**
     * Atomically increments the request counter.
     *
     * @return the updated counter value
     */
    public static int incrementRequestCount()
    {
        // getAndIncrement / incrementAndGet are atomic (no race condition)
        return requestCount.incrementAndGet();
    }

    /**
     * Example of an atomic "check-then-act" using compare-and-set style.
     * This sets a max value safely without racing.
     *
     * @param max Maximum allowed value
     */
    public static void capCounter(int max) 
    {
        while (true) 
        {
            int current = requestCount.get();
            if (current <= max) 
            {
                return; // already within bounds
            }
            // Attempt to set it back to max only if no one changed it since we read.
            if (requestCount.compareAndSet(current, max)) 
            {
                return;
            }
            // Otherwise retry: another thread changed it.
        }
    }
}