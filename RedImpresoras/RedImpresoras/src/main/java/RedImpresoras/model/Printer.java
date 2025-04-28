package RedImpresoras.model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class Printer implements Runnable {
    private final String id;
    private final Queue<Document> documentQueue = new LinkedList<>();
    private Document currentDocument;
    private final Object lock = new Object();
    private boolean running = true;
    private Consumer<Document> onDocumentPrinted;

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
            documentQueue.offer(doc); // ENCOLAR documento
            lock.notify(); // Despertar el hilo si estaba esperando
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
                while (documentQueue.isEmpty() && running) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }

                if (!running && documentQueue.isEmpty()) {
                    break;
                }

                currentDocument = documentQueue.poll(); // Tomar siguiente documento
            }

            if (currentDocument != null) {
                System.out.println("[" + id + "] Printing: " + currentDocument);
                try {
                    Thread.sleep(8000); // Simula impresión de 8 segundos
                } catch (InterruptedException e) {
                    break;
                }

                if (onDocumentPrinted != null) {
                    onDocumentPrinted.accept(currentDocument);
                }

                synchronized (lock) {
                    currentDocument = null;
                }
            }
        }

        System.out.println("[" + id + "] Shutdown complete.");
    }

    public String getCurrentDocumentName() {
        synchronized (lock) {
            if (currentDocument == null) {
                return "Idle";
            } else {
                return currentDocument.getName();
            }
        }
    }

    public boolean isBusy() {
        synchronized (lock) {
            return currentDocument != null;
        }
    }
}
