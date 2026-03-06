package edu.ilstu.recordvault.store;

import edu.ilstu.recordvault.model.Record;
import edu.ilstu.recordvault.util.SafeLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class RecordStore {

    // LCK01-J (Caleb): using a private ReentrantLock, not synchronized on shared objects
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<Integer, Record> records = new LinkedHashMap<>();
    private final SafeLogger logger = SafeLogger.getInstance();

    public RecordStore() {} // TSM01-J (Clayton) — construtcor starts no threads (it just builds the object)


    // LCK01-J (Caleb): lock is a private ReentrantLock field (not synchronized(this), not a String, etc.)
    // LCK08-J (Caleb): unlock always happens in finally so we dont accidentally keep the lock if something breaks
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
    public List<Record> getAllRecords() {
        lock.lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(records.values()));
        } finally {
            lock.unlock();
        }
    }

    // LCK01-J / LCK08-J — normal lock usage (lock + try/finally unlock)
    public Record findById(int id) {
        lock.lock();
        try {
            return records.get(id);
        } finally {
            lock.unlock();
        }
    }

    // LCK01-J / LCK08-J
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
    public int size() {
        lock.lock();
        try {
            return records.size();
        } finally {
            lock.unlock();
        }
    }

    // IDS07-J (Caleb): not used here. Left as a stub.
    private static void openFolderStub(String folderPath) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
