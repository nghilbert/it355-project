package recommendations;

/**
 * ERR51-J: Prefer user-defined exceptions over more general
 * exception types.
 *
 * @author Nathan Hilbert
 */
public class ERR51 {

	/** Thrown when an item is out of stock. */
	static class ItemOutOfStockException extends Exception {
		public ItemOutOfStockException() {
			super("Sorry, this item is out of stock.");
		}
	}

	/** Throws and prints exception */
	public static void main(String[] args) {
		try {
			throw new ItemOutOfStockException();
		} catch (ItemOutOfStockException e) {
			System.out.println(e.getMessage());
		}
	}
}