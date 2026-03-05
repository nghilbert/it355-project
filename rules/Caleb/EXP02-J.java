import java.util.Arrays;

/**
 * Demonstrates the CERT rule EXP02-J: Do not use Object.equals()
 * to compare arrays.
 *
 * This program shows how to correctly compare arrays using
 * Arrays.equals(), which compares the contents of the arrays
 * rather than their memory references.
 *
 * @author Caleb Appiah
 */
public class ArrayCompare {

    /**
     * Entry point of the program demonstrating correct array comparison.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {

        int[] a = {1,2,3};
        int[] b = {1,2,3};

        if(Arrays.equals(a, b)) {
            System.out.println("Arrays are equal");
        }

    }

}
