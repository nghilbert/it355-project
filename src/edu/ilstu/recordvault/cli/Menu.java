package edu.ilstu.recordvault.cli;

public class Menu {

	private enum Option {
		CREATE(1, "Create record"),
		EXIT(2, "Exit");

		final int index;
		final String label;

		Option(int index, String label) {
			this.index = index;
			this.label = label;
		}

		@Override
		public String toString() {
			return "[" + index + "]: " + label;
		}
	}

	public Menu() {
	}

	public void start() {
	}

	private void printMenu() {
		System.out.println("\n===== RecordVault =====");
		for (Option option : Option.values()) {
			System.out.println("  " + option);
		}
		System.out.print("Input option: ");
	}
}