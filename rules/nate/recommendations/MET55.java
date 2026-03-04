package recommendations;

import java.util.*;

/**
 * MET55-J: Return an empty array or collection instead of a null value for
 * methods that return an array or collection.
 *
 * @author Nathan Hilbert
 */
public class MET55 {
	private List<Integer> nums = new ArrayList<>();

	/**
	 * Guard against returning null when the list is empty.
	 *
	 * @return the list of integers or an empty list.
	 */
	public List<Integer> getNums() {
		if (nums.isEmpty()) {
			return Collections.emptyList();
		}
		return nums;
	}

	/** Runs a test. */
	public static void main(String[] args) {
		MET55 met55 = new MET55();
		System.out.println("Before adding numbers: " + met55.getNums());
		met55.nums.add(1);
		met55.nums.add(2);
		met55.nums.add(3);
		System.out.println("\nAfter adding numbers: " + met55.getNums());
	}
}