/**
 * Demonstrates VNA01-J: Ensure visibility of shared references to immutable objects.
 *
 * Even if an object is immutable, a shared reference must be safely published
 * (e.g., via volatile, final with safe publication, or proper synchronization)
 * so other threads reliably see the latest reference.
 */
public final class Vna01J_VisibilityForImmutableReference 
{

    /**
     * Immutable data type (fields are final, no setters).
     */
    public static final class Config 
    {
        private final String endpoint;
        private final int timeoutMs;

        /**
         * Creates an immutable configuration.
         *
         * @param endpoint API endpoint
         * @param timeoutMs timeout in milliseconds
         */
        public Config(String endpoint, int timeoutMs)
        {
            this.endpoint = endpoint;
            this.timeoutMs = timeoutMs;
        }

        /** @return endpoint string */
        public String getEndpoint() 
        { 
            return endpoint; 
        }

        /** @return timeout in milliseconds */
        public int getTimeoutMs() 
        { 
            return timeoutMs; 
        }
    }

    /**
     * Volatile ensures changes to this reference are visible across threads.
     */
    private static volatile Config config = new Config("https://api.example.com", 1000);

    private Vna01J_VisibilityForImmutableReference() { }

    /**
     * Updates the shared config reference safely (visible to all threads).
     *
     * @param newConfig the new immutable configuration
     */
    public static void updateConfig(Config newConfig) 
    {
        if (newConfig == null) 
        {
            throw new IllegalArgumentException("newConfig must not be null");
        }
        config = newConfig; // volatile write => safely published
    }

    /**
     * Reads the current config reference safely.
     *
     * @return the latest visible Config reference
     */
    public static Config getConfig()
    {
        return config; // volatile read => sees latest write
    }
}