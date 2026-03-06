package edu.ilstu.recordvault;

import edu.ilstu.recordvault.cli.Menu;
import edu.ilstu.recordvault.store.CsvStorage;
import edu.ilstu.recordvault.store.RecordStore;
import edu.ilstu.recordvault.util.SafeLogger;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	private static final Path CSV_FILE = Paths.get("data", "records.csv");

	public static void main(String[] args) {
		SafeLogger logger = SafeLogger.getInstance();
		logger.log("RecordVault starting up...");

		RecordStore store = new RecordStore();
		CsvStorage csvStorage = new CsvStorage();

		try {
			csvStorage.load(store, CSV_FILE);
			logger.log("Loaded records from: " + CSV_FILE);
		} catch (Exception e) {
			System.err.println("[WARN] Could not load saved records. Starting fresh.");
			logger.log("WARN: CSV load failed (" + e.getClass().getSimpleName() + ")");
		}

		// FIO14-J (Lucas) cleanup after termination
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			// EXP00-J (Lucas) do not ignore return values
			boolean saved = csvStorage.save(store, CSV_FILE);
			if (!saved) {
				System.err.println("[ERROR] Final save failed. Data may be lost.");
				logger.log("ERROR: final save failed during shutdown.");
			} else {
				logger.log("Final save complete.");
			}
			logger.close();
		}, "shutdown-hook"));

		new Menu(store, csvStorage, CSV_FILE).start();
		System.exit(0);
	}
}