
package korisni;

import java.util.Collection;

public class DebugUtils {
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

// Korištenje:
// debugPrint("Ana", 25, List.of("a", "b"), true);
// Ispis:
// arg0: Ana
// arg1: 25
// arg2 (Collection size=2): [a, b]
// arg3: true