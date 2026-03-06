package edu.ilstu.recordvault.cli;

import edu.ilstu.recordvault.store.CsvStorage;
import edu.ilstu.recordvault.model.Record;
import edu.ilstu.recordvault.store.RecordStore;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Menu {
	private final RecordStore store;
	private final CsvStorage csvStorage;
	private final Path csvFilepath;
	private final Scanner scanner;

	public Menu(RecordStore store, CsvStorage csvStorage, Path csvFilepath) {
		this.store = store;
		this.csvStorage = csvStorage;
		this.csvFilepath = csvFilepath;
		this.scanner = new Scanner(System.in);
	}

	private enum Option {
		CREATE(1, "Create record"),
		LIST(2, "List records"),
		SAVE(3, "Save"),
		EXIT(4, "Exit");

		final int option_id;
		final String label;

		Option(int option_id, String label) {
			this.option_id = option_id;
			this.label = label;
		}

		@Override
		public String toString() {
			return "[" + option_id + "]: " + label;
		}
	}

	public void start() {
		System.out.println("===== RecordVault =====");

		boolean isRunning = true;
		while (isRunning) {
			printMenu();
			int choice;
			try {
				choice = Integer.parseInt(scanner.nextLine().trim());
			} catch (NumberFormatException e) {
				System.out.println("[ERROR] Invalid choice.");
				continue;
			}
			if (choice < 1 || choice > Option.values().length) {
				System.out.println("[ERROR] Invalid choice.");
				continue;
			}
			switch (choice) {
				case 1:
					doCreate();
					break;
				case 2:
					doList();
					break;
				case 3:
					doSave();
					break;
				case 4:
					isRunning = false;
					break;
			}
		}

		System.out.println("Exiting Record Vault...");
	}

	private void doSave() {

	}

	private void doCreate() {

	}

	private void doList() {
		List<Record> records = store.getAllRecords();
		if (records.isEmpty()) {
			System.out.println("(no records)");
			return;
		}
		System.out.println("--- Records ---");
		for (Record record : records) {
			System.out.println(record);
		}
		System.out.println("--- End ---");
	}

	private void printMenu() {
		System.out.println();
		for (Option option : Option.values()) {
			System.out.println("  " + option);
		}
		System.out.print("Choose an option: ");
	}
}