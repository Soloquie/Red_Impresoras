package RedImpresoras.model;

import java.util.function.Consumer;

public class Printer implements Runnable {
    private final String id;
    private Document assignedDocument;
    private Consumer<Document> onDocumentPrinted;
    private final Object lock = new Object();
    private boolean running = true;

    public Printer(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setOnDocumentPrinted(Consumer<Document> consumer) {
        this.onDocumentPrinted = consumer;
    }

    public void assignDocument(Document doc) {
        synchronized (lock) {
            assignedDocument = doc;
            lock.notify();
        }
    }

    public void stop() {
        synchronized (lock) {
            running = false;
            lock.notify();
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (lock) {
                while (assignedDocument == null && running) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }

                if (!running && assignedDocument == null) break;

                if (assignedDocument != null) {
                    System.out.println("[" + id + "] Printing: " + assignedDocument);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        break;
                    }
                    if (onDocumentPrinted != null) {
                        onDocumentPrinted.accept(assignedDocument);
                    }
                    assignedDocument = null;
                }
            }
        }

        System.out.println("[" + id + "] Shutdown complete.");
    }

    public String getCurrentDocumentName() {
        synchronized (lock) {
            if (assignedDocument == null) {
                return "Idle";
            } else {
                return "Printing: " + assignedDocument.getName();
            }
        }
    }

    public boolean isBusy() {
        synchronized (lock) {
            return assignedDocument != null;
        }
    }


}