package listas.example;

public class Main {
    public static void main(String[] args) {
        PriorityQueue<Document> printQueue = new PriorityQueue<>();

        Printer[] printers = {
                new Printer("Printer-1"),
                new Printer("Printer-2")
        };

        Thread[] printerThreads = new Thread[printers.length];
        for (int i = 0; i < printers.length; i++) {
            printerThreads[i] = new Thread(printers[i]);
            printerThreads[i].start();
        }

        printQueue.enqueue(new Document("Payroll", Priority.High));
        printQueue.enqueue(new Document("Internal Memo", Priority.Low));
        printQueue.enqueue(new Document("Client Report", Priority.Medium));
        printQueue.enqueue(new Document("Invoice", Priority.High));
        printQueue.enqueue(new Document("Weekly Summary", Priority.Medium));

        Dispatcher dispatcher = new Dispatcher(printQueue, printers);
        Thread dispatcherThread = new Thread(dispatcher);
        dispatcherThread.start();

        printQueue.shutdown();

        try {
            dispatcherThread.join();
            for (Thread t : printerThreads) {
                t.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("[Main] All jobs processed. Exiting.");
    }
}






