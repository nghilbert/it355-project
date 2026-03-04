package recommendations;

/**
 * MET54-J: Always provide feedback about the resulting value of a method.
 *
 * @author Nathan Hilbert
 */
public class MET54 {
	private String name = "Nathan";

	/**
	 * Return a boolean value that signals a success or failure
	 *
	 * @param newName the potential new name.
	 * @return false if you are already using that name, else true.
	 */
	public boolean rename(String newName) {
		if (name.equals(newName)) {
			return false;
		}
		this.name = newName;
		return true;
	}

	/** Runs test cases. */
	public static void main(String[] args) {
		MET54 met54 = new MET54();

		System.out.println("Try renaming to the same name.");
		System.out.println(met54.rename("Nathan") ? "SUCCESS" : "FAILED: You are already named that.");
		System.out.println();
		System.out.println("Try renaming to a new name.");
		System.out.println(met54.rename("Nate") ? "SUCCESS" : "FAILED: You are already named that.");
	}
}