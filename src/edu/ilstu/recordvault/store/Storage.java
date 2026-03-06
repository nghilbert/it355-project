package edu.ilstu.recordvault.io;

import store.RecordStore;
import java.nio.file.Path;

public interface Storage {

    boolean load(RecordStore store, Path file);
    boolean save(RecordStore store, Path file);

}
