package edu.ilstu.recordvault.util;

import io.CsvStorage;
import store.RecordStore;
import util.SafeLogger;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * Background service that periodically saves records to disk.
 *
 * Uses a background thread to perform autosave operations
 * at a fixed interval.
 */
public class AutosaveService {

    private static final long INTERVAL_MS = 30_000L;
    private static final long STOP_TIMEOUT_MS = 5_000L;

    private final RecordStore store;
    private final CsvStorage csvStorage;
    private final Path csvFile;
    private final SafeLogger logger;

    // VNA00-J 
    // AtomicBoolean is used so both threads always see the latest running value
    private final AtomicBoolean running = new AtomicBoolean(false);

    private Thread autosaveThread;

    // TSM01-J (Clayton)
    // Constructor just assigns the fields. The background thread is started later
    // TSM03-J (Clayton)
    // Everything needed by this class is initialized before the object is used
    /**
 * Creates the AutosaveService.
 *
 * The constructor only initializes fields and does not start
 * any threads (per TSM01-J). All required dependencies are
 * initialized before the object is used (per TSM03-J).
 *
 * @param store the RecordStore containing records
 * @param csvStorage the CSV storage handler
 * @param csvFile the file where records will be saved
 */
    public AutosaveService(RecordStore store, CsvStorage csvStorage, Path csvFile) {
        this.store = store;
        this.csvStorage = csvStorage;
        this.csvFile = csvFile;
        this.logger = SafeLogger.getInstance();
    }

    //TSM01-J (Clayton)
     // start() runs after the object is fully constructed, not inside the constructor.
     //VNA00-J
    /**
 * Starts the autosave background thread.
 *
 * The thread is started after the object is fully constructed
 * (per TSM01-J). An AtomicBoolean is used so both threads
 * always see the latest running state (per VNA00-J).
 */
    public void start() {
        // VNA00-J 
        running.set(true);

        // TSM01-J (Clayton)
        autosaveThread = new Thread(this::run, "autosave-thread");
        autosaveThread.setDaemon(true);
        autosaveThread.start();

        logger.log("AutosaveService started (every " + INTERVAL_MS + "ms).");
    }

    
     //FIO14-J (Lucas)
     //stop() interrupts the thread and waits for it to finish.
     //VNA00-J Caleb
     /**
 * Stops the autosave background thread.
 *
 * The thread is interrupted and joined to ensure proper
 * shutdown behavior (per FIO14-J).
 */
    public void stop() {
        // VNA00-J 
        running.set(false);

        if (autosaveThread != null) {
            autosaveThread.interrupt();

            // FIO14-J (Lucas)
            try {
                autosaveThread.join(STOP_TIMEOUT_MS);
                if (autosaveThread.isAlive()) {
                    // ERR02-J (Driss)
                    logger.log("Autosave thread did not stop within " + STOP_TIMEOUT_MS + "ms.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.log("Interrupted while waiting for autosave thread to stop.");
            }
        }

        logger.log("AutosaveService stopped.");
    }

    
     //VNA00-J (Caleb/Lucas)
     //The loop checks the running flag each cycle so the thread sees updates right away.
     //ERR01-J (Driss)
     // Any error during autosave should be handled so the thread doesn't silently die.
     /**
 * Main autosave loop executed by the background thread.
 *
 * The running flag is checked each cycle so updates are
 * visible across threads (per VNA00-J). Errors are caught
 * so the autosave thread does not terminate unexpectedly
 * (per ERR01-J).
 */
    private void run() {
        // VNA00-J 
        while (running.get()) {
            try {
                Thread.sleep(INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            // VNA00-J (Caleb/Lucas)
            if (!running.get()) {
                break;
            }

            performSave();
        }

        logger.log("Autosave thread exiting.");
    }

     //EXP00-J (Lucas)
     //save() returns a boolean so we check the result.
     //ERR01-J (Driss)
     //
    /**
 * Performs a single autosave operation.
 *
 * The return value of save() is checked to confirm the
 * write succeeded (per EXP00-J). Errors are handled safely
 * and logged without crashing the thread (per ERR01-J).
 */
    private void performSave() {
        try {
            // EXP00-J (Lucas)
            boolean ok = csvStorage.save(store, csvFile);

            if (ok) {
                logger.log("Autosave OK — " + store.size() + " records saved.");
            } else {
                // ERR01-J (Driss)
                logger.log("Autosave save() returned false.");
            }
        } catch (Exception e) {
            // ERR01-J (Driss)
            // ERR02-J (Driss)
            logger.log("Autosave unexpected error: " + e.getClass().getSimpleName());
        }
    }

    // VNA00-J (Caleb/Lucas)
    /**
 * Returns whether the autosave service is currently running.
 *
 * @return true if the autosave thread is active
 */
    public boolean isRunning() {
        return running.get();
    }
}
