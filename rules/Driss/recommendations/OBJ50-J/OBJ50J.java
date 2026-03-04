/**
 * Demonstrates OBJ50-J (Recommendation):
 * A {@code final} reference is not the same as an immutable object.
 *
 * <p>Compliant approach: make the object's state immutable using {@code final} fields
 * and provide "copy" behavior by returning a new instance instead of mutating state.</p>
 */
public final class OBJ50J {

    /**
     * Immutable point: state cannot change after construction.
     */
    private static final class ImmutablePoint {
        private final int x;
        private final int y;

        /**
         * Creates an immutable point.
         *
         * @param x x-coordinate
         * @param y y-coordinate
         */
        private ImmutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Returns a new point with updated coordinates (does not mutate this object).
         *
         * @param newX new x-coordinate
         * @param newY new y-coordinate
         * @return new {@code ImmutablePoint} instance
         */
        private ImmutablePoint with(int newX, int newY) {
            return new ImmutablePoint(newX, newY);
        }

        /**
         * Returns a readable form of the point.
         *
         * @return point as a string
         */
        private String asText() {
            return "(" + x + ", " + y + ")";
        }
    }

    /**
     * Runs a small demo showing that:
     * <ul>
     *   <li>{@code final} prevents reassignment</li>
     *   <li>immutability prevents state changes</li>
     * </ul>
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        final ImmutablePoint p1 = new ImmutablePoint(1, 2);
        System.out.println("p1 = " + p1.asText());

        // "Change" by making a NEW object (p1 itself never mutates)
        ImmutablePoint p2 = p1.with(5, 6);

        System.out.println("p1 (still) = " + p1.asText());
        System.out.println("p2 (new)   = " + p2.asText());
    }
}