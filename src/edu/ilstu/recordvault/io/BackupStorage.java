package edu.ilstu.recordvault.io;


import edu.ilstu.recordvault.model.Record;
import edu.ilstu.recordvault.store.RecordStore;
import edu.ilstu.recordvault.util.SafeLogger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BackupStorage {

    // RULE: SER12-J (Clayton)
    private static final int MAGIC_NUMBER = 0x52564C54; // "RVLT"
    private static final int FILE_VERSION = 1;
    private static final int MAX_RECORD_COUNT = 100_000;

    private final SafeLogger logger = SafeLogger.getInstance();

    // RULE: OBJ11-J (Clayton)
    public BackupStorage() {
    }

    // RULE: SER05-J (Nate) — writes the magic number and version header, then serializes the record list.
    // RULE: ERR08-J (Driss) — null arguments are rejected before any work begins.
    public boolean exportBackup(RecordStore store, Path file) {
        if (store == null || file == null) {
            logger.log("WARN: exportBackup called with null argument.");
            return false;
        }

        Path parent = file.getParent();
        if (parent != null && !Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                // RULE: FIO02-J (Driss) — handle directory creation failures safely.
                // RULE: ERR01-J (Driss) — keep the message safe and avoid exposing internal details.
                logger.log("ERROR: Cannot create backup directory.");
                return false;
            }
        }

        List<Record> snapshot = store.getAllRecords();

        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(file));
             DataOutputStream dos = new DataOutputStream(bos)) {

            // SER12-J (Clayton)
            dos.writeInt(MAGIC_NUMBER);
            dos.writeInt(FILE_VERSION);
            dos.flush();

            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(snapshot);
                oos.flush();
            }
        } catch (IOException e) {
            //  FIO02-J (Driss) — handle backup write failures safely.
            //  ERR01-J (Driss) — keep the message safe and avoid exposing internal details.
            logger.log("ERROR: Unable to export backup.");
            return false;
        }

        logger.log("INFO: Exported " + snapshot.size() + " records to backup: " + file);
        return true;
    }

    //  SER12-J (Clayton) — magic number and version are validated before any deserialization occurs.
    //  SER08-J (Nate) — ObjectInputFilter allowlist is the primary deserialization defense.
    //  ERR08-J (Driss) — null arguments are rejected before any work begins.
    public boolean importBackup(RecordStore store, Path file) {
        if (store == null || file == null) {
            logger.log("WARN: importBackup called with null argument.");
            return false;
        }

        if (!Files.exists(file)) {
            //  FIO02-J (Driss) — handle missing files safely instead of assuming the file exists.
            logger.log("WARN: Backup file not found: " + file);
            return false;
        }

        List<Record> imported;

        try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file));
             DataInputStream dis = new DataInputStream(bis)) {

            int magic;
            try {
                //  FIO08-J (Driss) — detect incomplete binary input before using header data.
                magic = dis.readInt();
            } catch (EOFException e) {
                logger.log("ERROR: Backup file too short; magic number missing.");
                return false;
            }

            if (magic != MAGIC_NUMBER) {
                logger.log("ERROR: Wrong magic number: 0x" + Integer.toHexString(magic));
                return false;
            }

            int version;
            try {
                //  FIO08-J (Driss) — detect incomplete binary input before using header data.
                version = dis.readInt();
            } catch (EOFException e) {
                logger.log("ERROR: Backup file truncated; version missing.");
                return false;
            }

            if (version != FILE_VERSION) {
                logger.log("ERROR: Version mismatch. Expected " + FILE_VERSION + ", got " + version);
                return false;
            }

            try (ObjectInputStream ois = new FilteredObjectInputStream(bis)) {
                Object obj = ois.readObject();

                //  ERR08-J (Driss) — validate the object before using or casting it.
                if (obj == null || !(obj instanceof List)) {
                    logger.log("ERROR: Deserialized object is null or unexpected type.");
                    return false;
                }

                @SuppressWarnings("unchecked")
                List<?> rawList = (List<?>) obj;

                if (rawList.size() > MAX_RECORD_COUNT) {
                    logger.log("ERROR: Backup has too many records: " + rawList.size());
                    return false;
                }

                imported = new ArrayList<>();
                for (Object item : rawList) {
                    if (!(item instanceof Record)) {
                        logger.log("WARN: Skipping non-Record item in backup list.");
                        continue;
                    }
                    imported.add((Record) item);
                }

            } catch (ClassNotFoundException e) {
                //  ERR01-J (Driss) — keep the message safe and avoid exposing internal details.
                logger.log("ERROR: Unknown class during deserialization.");
                return false;
            } catch (InvalidClassException e) {
                //  SER01-J (Nate)
                logger.log("ERROR: serialVersionUID mismatch; backup is incompatible.");
                return false;
            } catch (StreamCorruptedException e) {
                logger.log("ERROR: Backup stream is corrupt.");
                return false;
            }

        } catch (FileNotFoundException | NoSuchFileException e) {
            //  FIO02-J (Driss) — handle file disappearance safely during read.
            logger.log("WARN: Backup file disappeared before read: " + file);
            return false;
        } catch (IOException e) {
            //  FIO02-J (Driss) — handle I/O failures safely.
            //  ERR01-J (Driss) — keep the message safe and avoid exposing internal details.
            logger.log("ERROR: Unable to import backup.");
            return false;
        }

        store.replaceAll(imported);
        logger.log("INFO: Imported " + imported.size() + " records from " + file);
        return true;
    }

    //  SER12-J (Clayton) — allowlist permits only ArrayList, Record, and String; all other classes are rejected.
    //  SER08-J (Nate) — this filter replaces SecurityManager as the deserialization defense.
    private static class FilteredObjectInputStream extends ObjectInputStream {

        FilteredObjectInputStream(java.io.InputStream in) throws IOException {
            super(in);
            this.setObjectInputFilter(buildFilter());
        }

        private static ObjectInputFilter buildFilter() {
            return filterInfo -> {
                Class<?> cls = filterInfo.serialClass();
                if (cls == null) return ObjectInputFilter.Status.UNDECIDED;
                if (cls == java.util.ArrayList.class) return ObjectInputFilter.Status.ALLOWED;
                if (cls == edu.ilstu.recordvault.model.Record.class) return ObjectInputFilter.Status.ALLOWED;
                if (cls == java.lang.String.class) return ObjectInputFilter.Status.ALLOWED;
                return ObjectInputFilter.Status.REJECTED;
            };
        }
    }
}