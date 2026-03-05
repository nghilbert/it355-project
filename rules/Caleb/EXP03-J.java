/**
 * Demonstrates the CERT rule EXP03-J: Do not use equality operators
 * when comparing boxed primitives.
 *
 * This program compares Integer objects using the equals() method
 * rather than the == operator. This ensures that the comparison
 * checks the actual numeric value instead of the object reference.
 *
 * @author Caleb Appiah
 */
public class BoxedCompare {

    /**
     * Entry point of the program demonstrating proper boxed
     * primitive comparison.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {

        Integer a = 1000;
        Integer b = 1000;

        if(a.equals(b)) {
            System.out.println("Values are equal");
        }

    }

}
