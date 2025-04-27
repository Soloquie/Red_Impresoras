package listas.example;

public class Document implements Comparable<Document> {
    private final String name;
    private final Priority priority;

    private static final PriorityComparator comparator = new PriorityComparator();

    public Document(String name, Priority priority) {
        this.name = name;
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Document other) {
        return comparator.compare(this.priority, other.priority);
    }

    @Override
    public String toString() {
        return name + " - Priority: " + priority;
    }
}

