/**
 * MET50-J: Avoid ambiguous or confusing uses of overloading.
 *
 * <p>Description: Avoid overloads that can be misunderstood or selected unexpectedly
 * (for example, due to autoboxing or similar parameter types).
 *
 * <p>Why: Ambiguous overloads can call the wrong method without errors, leading to
 * incorrect behavior and hard-to-find bugs.
 */
public final class MET50J {

  private MET50J() { }

  /**
   * Returns a record by its index (position).
   *
   * @param index record position
   * @return record value at that index
   */
  public static int getByIndex(int index) {
    int[] records = {111990000, 222990000, 333990000};
    return records[index];
  }

  /**
   * Returns whether a record value exists.
   *
   * @param value record value to check
   * @return true if the value exists
   */
  public static boolean existsByValue(Integer value) {
    int[] records = {111990000, 222990000, 333990000};
    for (int v : records) {
      if (value != null && value == v) return true;
    }
    return false;
  }

  /** Simple demo. */
  public static void main(String[] args) {
    System.out.println(getByIndex(2));                 // 333990000
    System.out.println(existsByValue(111990000));      // true (autoboxed to Integer)
  }
}