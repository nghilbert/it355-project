/**
 * Demonstrates IDS16-J: Prevent XML Injection.
 * 
 * @author Nathan Hilbert
 */
public class Product {
	/**
	 * Updates the quantity of a product in the shopping cart with an XML string.
	 * 
	 * @param quantity is a string and may contain xml syntax. Parses as an unsigned
	 *                 integer to prevent injection attempts.
	 * @return the resulting XML
	 * @throws IllegalArgumentException when quantity is not an unsigned int
	 */
	private String updateQuantity(String quantity) throws IllegalArgumentException {
		int numericalQuantity;
		try {
			numericalQuantity = Integer.parseUnsignedInt(quantity);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Quantity must be a positive integer", e);
		}

		return """
				<item>
					<description>Widget</description>
					<price>500.00</price>
					<quantity>%s</quantity>
				</item>
				""".formatted(String.valueOf(numericalQuantity));
	}

	/**
	 * Runs test cases.
	 * 
	 * @param args each argument will be tested against updateQuantity
	 */
	public static void main(String[] args) {
		Product product = new Product();
		try {
			String xmlInjection = "1</quantity><price>1.0</price><quantity>1";
			System.out.println("Try injecting XML: " + xmlInjection);
			System.out.println("SUCCESS! Updated XML:\n" + product.updateQuantity(xmlInjection));
		} catch (Exception e) {
			System.out.println("FAILED: " + e.getMessage());
		}

		try {
			String quantity = "3";
			System.out.println("\nTry positive integer: " + quantity);
			System.out.println("SUCCESS! Updated XML:\n" + product.updateQuantity(quantity));
		} catch (Exception e) {
			System.out.println("FAILED: " + e.getMessage());
		}

		for (String arg : args) {
			try {
				System.out.println("\nTry user input: " + arg);
				System.out.println("SUCCESS! Updated XML:\n" + product.updateQuantity(arg));
			} catch (Exception e) {
				System.out.println("FAILURE: " + e.getMessage());
			}
		}
	}
}