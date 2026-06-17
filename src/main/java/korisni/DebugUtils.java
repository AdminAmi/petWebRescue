package korisni;

import java.util.Collection;

/**
 * Utility class for debugging purposes.
 * This class provides a method to print the contents of various objects, including collections, to the console for debugging.
 * It can handle null values and collections, providing useful information about their contents.
 * Usage:
 * <pre>{@code
 * DebugUtils.debugPrint("Ana", 25, List.of("a", "b"), true);
 * }</pre>
 * 
 * @author Amel Džanić
 * @version 1.0
 * @since 2026-05-01
 */
public class DebugUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws an {@link AssertionError} if instantiation is attempted.
     *
     * @throws AssertionError if this constructor is called
     */
    private DebugUtils() {
        throw new AssertionError("Instantiation of utility class is not allowed.");
    }

    /**
     * Prints the contents of the provided items to the console for debugging purposes.
     * If an item is null, it prints "argX: null".
     * If an item is a collection, it prints the size of the collection and its contents.
     * Otherwise, it prints the item's string representation.
     * 
     * @param <T> the type of items to print
     * @param items the items to print
     */
    @SafeVarargs
    public static <T> void debugPrint(T... items) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                System.out.println("arg" + i + ": null");
            } else if (items[i] instanceof Collection<?>) {
                Collection<?> collection = (Collection<?>) items[i];
                System.out.println("arg" + i + " (Collection size=" + collection.size() + "): " + collection);
            } else {
                System.out.println("arg" + i + ": " + items[i]);
            }
        }
    }
}
