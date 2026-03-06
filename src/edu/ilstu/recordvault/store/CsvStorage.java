package edu.ilstu.recordvault.store;

import model.Record;
import store.RecordStore;
import util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class CsvStorage implements Storage {

    private static final char SEPARATOR = ',';
    private static final String SEP_STR = String.valueOf(SEPARATOR);
    private static final int EXPECTED_COLUMNS = 3;

    private final SafeLogger logger = SafeLogger.getInstance();
    /**
 * Creates a CsvStorage instance.
 *
 * Constructor performs no risky operations (per OBJ11-J).
 */
    public CsvStorage() {
    }

    // FIO02-J (Driss)
    // Missing CSV just means nothing saved yet.
    // ERR01-J (Driss)
    // input output errors are caught and logged safely
/**
 * Loads records from a CSV file into the RecordStore.
 *
 * Missing files are treated as empty stores (per FIO02-J).
 * Input/output errors are caught and logged safely (per ERR01-J).
 * Null parameters are checked before use (per ERR08-J).
 *
 * @param store the RecordStore to populate
 * @param file the CSV file path
 * @return true if loading succeeds or file does not exist, false if an error occurs
 */
   @Override
   public boolean load(RecordStore store, Path file) {

    // ERR08-J (Driss)
    // Null guard so we don't throw a NullPointerException later.
    if (file == null || store == null) {
        logger.log("CsvStorage.load() called with null argument.");
        return false;
    }

    // FIO02-J (Driss)
    if (!Files.exists(file)) {
        logger.log("CSV file not found (first run?): " + file);
        return true;
    }

    List<Record> loaded = new ArrayList<>();

    try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            // IDS01-J (Driss)
            // normalize input before using it
            line = StringUtil.normalize(line);

            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split(SEP_STR, -1);

            if (parts.length != EXPECTED_COLUMNS) {
                // FIO02-J (Driss)
                logger.log("Skipping messsed up line " + lineNumber + " in " + file);
                continue;
            }

            int id;
            try {
                // EXP01-J (Lucas)
                id = Integer.parseInt(parts[0].trim()); // parts[0] = id
            } catch (NumberFormatException e) {
                // ERR01-J (Driss)
                logger.log("Invalid ID on line " + lineNumber + ", skipping..");
                continue;
            }

            // IDS01-J (Driss)
            String name = StringUtil.normalize(parts[1]);
            String value = StringUtil.normalize(parts[2]);

            if (name.isEmpty()) {
                logger.log("Empty name on line " + lineNumber + ", skipping..");
                continue;
            }

            loaded.add(Record.ofExisting(id, name, value, new ArrayList<>()));
        }

    } catch (NoSuchFileException e) {

        // FIO02-J (Driss)
        logger.log("CSV file disappeared before reading: " + file);
        return true;

    } catch (IOException e) {

        // ERR01-J (Driss)
        logger.log("Error IOException reading CSV: " + e.getClass().getSimpleName());
        return false;
    }

    store.replaceAll(loaded);
    logger.log("Loaded " + loaded.size() + " records from " + file);
    return true;
}

    // EXP00-J (Lucas)
    // atomicWrite returns a boolean so we check it.
    // ERR08-J (Driss)
    /**
 * Saves records from the RecordStore to a CSV file.
 *
 * The return value of atomicWrite() is checked (per EXP00-J).
 * Null parameters are validated before use (per ERR08-J).
 *
 * @param store the RecordStore containing records
 * @param file the destination CSV file
 * @return true if the save succeeds, false if the write fails
 */
    @Override
    public boolean save(RecordStore store, Path file) {

        // ERR08-J (Driss)
        if (store == null || file == null) {
            logger.log("CsvStorage.save() called with null argument.");
            return false;
        }

        String content = buildCsvContent(store);

        // EXP00-J (Lucas)
        boolean wrote = FileUtil.atomicWrite(file, content);

        if (!wrote) {
            // ERR01-J Driss
            logger.log("CsvStorage.save() — atomicWrite returned false for " + file);
            return false;
        }

        logger.log("Saved " + store.size() + " records to " + file);
        return true;
    }

    // OBJ05-J (Clayton)
    // getAllRecords returns a copy so callers can't modify the store.\
    /**
 * Builds the CSV content for the current records.
 *
 * getAllRecords() returns a defensive copy so internal state
 * cannot be modified externally (per OBJ05-J).
 *
 * @param store the RecordStore containing records
 * @return the CSV formatted string
 */
    private String buildCsvContent(RecordStore store) {

        StringBuilder sb = new StringBuilder();
        sb.append("# RecordVault CSV export\n");

        // OBJ05-J (Clayton)
        List<Record> records = store.getAllRecords();

        for (Record r : records) {
            sb.append(r.getId())
                    .append(SEPARATOR)
                    .append(escapeCsv(r.getName()))
                    .append(SEPARATOR)
                    .append(escapeCsv(r.getValue()))
                    .append('\n');
        }

        return sb.toString();
    }

    // ERR08-J (Driss)
    // Null fields become empty strings instead of throwing an error
    /**
 * Escapes a field for safe CSV output.
 *
 * Null fields are converted to empty strings to prevent
 * NullPointerException (per ERR08-J).
 *
 * @param field the field value
 * @return the escaped CSV field
 */
    private String escapeCsv(String field) {

        // ERR08-J (Driss)
        if (field == null) {
            return "";
        }

        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }

        return field;
    }
}
