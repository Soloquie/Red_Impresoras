package RedImpresoras.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class PriorityComparator implements Comparator<Priority> {
    private static final Map<Priority, Integer> priorityMap = new HashMap<>();

    static {
        priorityMap.put(Priority.High, 1);
        priorityMap.put(Priority.Medium, 2);
        priorityMap.put(Priority.Low, 3);
    }

    @Override
    public int compare(Priority p1, Priority p2) {
        return Integer.compare(priorityMap.get(p1), priorityMap.get(p2));
    }
}
