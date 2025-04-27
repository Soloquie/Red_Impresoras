package RedImpresoras.model;

public class Dispatcher implements Runnable {
    private final PriorityQueue<Document> globalQueue;
    private final Printer[] printers;
    private int nextPrinter = 0;

    public Dispatcher(PriorityQueue<Document> queue, Printer[] printers) {
        this.globalQueue = queue;
        this.printers = printers;
    }

    @Override
    public void run() {
        while (!globalQueue.isShutdown() || !globalQueue.isEmpty()) {
            Document doc = globalQueue.dequeue();
            System.out.println("[Dispatcher] Assigned to " + printers[nextPrinter].getId() + ": " + doc);


            if (doc != null) {
                Printer target = printers[nextPrinter];
                target.assignDocument(doc);
                nextPrinter = (nextPrinter + 1) % printers.length;
            }
        }

        for (Printer p : printers) {
            p.stop();
        }

        System.out.println("[Dispatcher] Shutdown complete.");
    }
}
