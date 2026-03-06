package edu.ilstu.recordvault.store;

import java.nio.file.Path;
/**
 * Defines storage operations for loading and saving records.
 *
 * Implementations must safely handle file errors and return a boolean
 * indicating success or failure (per EXP00-J).
 */
public interface Storage {
    /**
     * Loads records from a file into the RecordStore.
     *
     * Implementations should handle missing files safely and not
     * expose raw exceptions (per FIO02-J and ERR01-J).
     *
     * @param store the RecordStore to populate
     * @param file the file to read from
     * @return true if loading succeeds or the file does not exist,
     *         false if a read error occurs
     */
    boolean load(RecordStore store, Path file);
     /**
     * Saves records from the RecordStore to a file.
     *
     * Callers must check the returned value to ensure the save
     * operation succeeded (per EXP00-J).
     *
     * @param store the RecordStore containing records
     * @param file the file to write to
     * @return true if the save succeeds, false if an error occurs
     */
    boolean save(RecordStore store, Path file);

}
