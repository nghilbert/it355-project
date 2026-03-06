package edu.ilstu.recordvault.cli;

import edu.ilstu.recordvault.store.CsvStorage;
import edu.ilstu.recordvault.model.Record;
import edu.ilstu.recordvault.store.RecordStore;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * Provides a command line menu for RecordVault.
 *
 * Lets the user create records list records save records
 * and exit the program
 */
public class Menu {

	private final RecordStore store;
	private final CsvStorage csvStorage;
	private final Path csvFilepath;
	private final Scanner scanner;

	// OBJ11-J (Clayton)
	// constructor does not do any risky work
	// TSM01-J (Clayton)
	// object built first then used later
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

		/**
		 * Returns the menu option in the format shown to the user.
		 *
		 * @return formatted menu option text
		 */
		@Override
		public String toString() {
			return "[" + option_id + "]: " + label;
		}
	}

	/**
	 * IDS00-J (Nate)
	 * Menu input is checked before it is used.
	 *
	 * FIO14-J (Lucas)
	 * When the loop ends shutdown can continue cleanly.
	 */
	public void start() {
		System.out.println("===== RecordVault =====");

		boolean isRunning = true;

		while (isRunning) {
			printMenu();
			String rawInput = scanner.nextLine().trim();

			int choice;
			try {
				choice = Integer.parseInt(rawInput);
			} catch (NumberFormatException e) {
				System.out.println("Error: Invalid choice.");
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
				default:
					System.out.println("[ERROR] Invalid choice.");
			}
		}

		System.out.println("Exiting Record Vault...");
	}

	/**
	 * EXP00-J (Lucas)
	 * save returns boolean so check it.
	 *
	 * ERR01-J (Driss)
	 * Only a safe error message shown.
	 */
	private void doSave() {
		boolean ok = csvStorage.save(store, csvFilepath);

		if (ok) {
			System.out.println("[OK] Saved to " + csvFilepath);
		} else {
			System.out.println("[ERROR] Save failed. Check the log.");
		}
	}

	/**
	 * IDS00-J (Nate)
	 * Name and value input must pass validation.
	 *
	 * IDS01-J (Driss)
	 * Normalize text first before checking it.
	 */
	private void doCreate() {
		System.out.print("Enter record name: ");
		String rawName = scanner.nextLine();

		String name = rawName.trim();

		if (name.isEmpty()) {
			System.out.println("[ERROR] Invalid name.");
			return;
		}

		System.out.print("Enter record value: ");
		String rawValue = scanner.nextLine();

		String value = rawValue.trim();

		if (value.isEmpty()) {
			System.out.println("[ERROR] Invalid value.");
			return;
		}

		// OBJ11-J (Clayton)
		// record created through factory
		// TSM03-J (Clayton)
		// object ready before returning
		try {
			Record record = Record.of(name, value);
			store.addRecord(record);
			System.out.println("[OK] Created record: " + record.getId());
		} catch (Exception e) {
			System.out.println("[ERROR] Failed to create record.");
		}
	}

	/**
	 * OBJ05-J (Clayton)
	 * getAllRecords returns copy not internal list
	 *
	 * EXP01-J (Lucas)
	 * Simple loop used here
	 */
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

	/**
	 * Prints the menu options for the user.
	 */
	private void printMenu() {
		System.out.println();
		for (Option option : Option.values()) {
			System.out.println("  " + option);
		}
		System.out.print("Choose an option: ");
	}
}
