package edu.ilstu.recordvault.store;

import edu.ilstu.recordvault.model.Record;
import edu.ilstu.recordvault.util.SafeLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Thread-safe storage for Record objects.
 *
 * Uses a private ReentrantLock to protect access to the internal
 * record map (per LCK01-J and LCK08-J).
 */
public class RecordStore {

    // LCK01-J (Caleb): using a private ReentrantLock, not synchronized on shared objects
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<Integer, Record> records = new LinkedHashMap<>();
    private final SafeLogger logger = SafeLogger.getInstance();
    /**
 * Creates an empty RecordStore.
 *
 * The constructor performs no risky work and starts no threads
 * (per TSM01-J).
 */
    public RecordStore() {} // TSM01-J (Clayton) — construtcor starts no threads (it just builds the object)


    // LCK01-J (Caleb): lock is a private ReentrantLock field (not synchronized(this), not a String, etc.)
    // LCK08-J (Caleb): unlock always happens in finally so we dont accidentally keep the lock if something breaks
    /**
 * Adds a record to the store.
 *
 * A private ReentrantLock is used for synchronization instead of
 * synchronizing on shared objects (per LCK01-J). The lock is always
 * released in a finally block (per LCK08-J).
 *
 * @param record the record to add
 */
    public void addRecord(Record record) {
        if (record == null) {
            logger.log("WARN: addRecord called with null");
            return;
        }


        lock.lock();
        try {
            records.put(record.getId(), record);
        } finally {
            lock.unlock();
        }
    }

    // OBJ05-J -( Clayton) — return a snapshot view so callers can’t modify our internal map
    // LCK08-J (Caleb): lock released in finally.
    /**
 * Returns all records currently stored.
 *
 * A defensive snapshot is returned so callers cannot modify the
 * internal map (per OBJ05-J). Locking ensures thread-safe access
 * (per LCK01-J / LCK08-J).
 *
 * @return an unmodifiable list of records
 */
    public List<Record> getAllRecords() {
        lock.lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(records.values()));
        } finally {
            lock.unlock();
        }
    }

    // LCK01-J / LCK08-J — normal lock usage (lock + try/finally unlock)
    /**
 * Finds a record by its ID.
 *
 * Thread-safe access is enforced using a private lock
 * (per LCK01-J / LCK08-J).
 *
 * @param id the record ID
 * @return the matching record, or null if not found
 */
    public Record findById(int id) {
        lock.lock();
        try {
            return records.get(id);
        } finally {
            lock.unlock();
        }
    }

    // LCK01-J / LCK08-J
    /**
 * Updates the value of a record.
 *
 * Uses proper lock handling with try/finally to ensure the lock
 * is always released (per LCK08-J).
 *
 * @param id the record ID
 * @param newValue the new value to store
 * @return true if the record was updated, false if not found
 */
    public boolean updateRecord(int id, String newValue) {
        lock.lock();
        try {
            Record r = records.get(id);
            if (r == null) {
                return false;
            }
            r.setValue(newValue);
            return true;
        } finally {
            lock.unlock();
        }
    }

    // LCK01-J / LCK08-J
    /**
 * Removes a record from the store.
 *
 * Lock usage follows LCK01-J and LCK08-J to ensure thread-safe
 * modification of the record map.
 *
 * @param id the record ID
 * @return true if the record was removed
 */
    public boolean removeRecord(int id) {
        lock.lock();
        try {
            Record removed = records.remove(id);
            return (removed != null);
        } finally {
            lock.unlock();
        }
    }

    // LCK01-J / LCK08-J
    /**
 * Replaces all records in the store with a new list.
 *
 * The internal map is cleared and rebuilt while holding the lock
 * to ensure thread-safe updates (per LCK01-J / LCK08-J).
 *
 * @param incoming the new list of records
 */
    public void replaceAll(List<Record> incoming) {
        if (incoming == null) {
            logger.log("WARN: replaceAll called with null list ");
            return;
        }

        lock.lock();
        try {
            records.clear();
            for (Record r : incoming) {
                if (r != null) {
                    records.put(r.getId(), r);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    // LCK01-J / LCK08-J
    /**
 * Returns the number of records in the store.
 *
 * Access is protected by a lock to ensure thread safety
 * (per LCK01-J / LCK08-J).
 *
 * @return the number of stored records
 */
    public int size() {
        lock.lock();
        try {
            return records.size();
        } finally {
            lock.unlock();
        }
    }

    // IDS07-J (Caleb): not used here. Left as a stub.
    /**
 * Placeholder for future folder-opening functionality.
 *
 * This method is intentionally not implemented.
 *
 * @param folderPath the folder path
 * @throws UnsupportedOperationException always thrown
 */
    private static void openFolderStub(String folderPath) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
