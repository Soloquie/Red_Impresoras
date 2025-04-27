package RedImpresoras.model;

public class PriorityQueue<T extends Comparable<T>> {

    private Node<T> head;
    private Node<T> tail;
    private int size;
    private boolean shutdown = false;

    public synchronized void enqueue(T data) {
        Node<T> newNode = new Node<>(data);

        if (head == null) {
            head = tail = newNode;
        } else if (data.compareTo(head.getData()) < 0) {
            newNode.setNext(head);
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.getNext() != null && data.compareTo(current.getNext().getData()) >= 0) {
                current = current.getNext();
            }

            newNode.setNext(current.getNext());
            current.setNext(newNode);

            if (newNode.getNext() == null) {
                tail = newNode;
            }
        }

        size++;
        notifyAll();
    }

    public synchronized T dequeue() {
        while (isEmpty() && !shutdown) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        if (isEmpty())
            return null;

        T data = head.getData();
        head = head.getNext();
        if (head == null)
            tail = null;
        size--;
        return data;
    }

    public synchronized boolean isEmpty() {
        return size == 0;
    }

    public synchronized void shutdown() {
        shutdown = true;
        notifyAll();
    }

    public synchronized boolean isShutdown() {
        return shutdown;
    }

    public synchronized Node<T> getHead() {
        return head;
    }

}
