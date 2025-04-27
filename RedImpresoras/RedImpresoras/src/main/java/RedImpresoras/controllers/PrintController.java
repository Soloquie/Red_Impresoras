package RedImpresoras.controllers;

import RedImpresoras.model.PriorityQueue;
import org.springframework.web.bind.annotation.*;

import RedImpresoras.model.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // para que el frontend pueda acceder
public class PrintController {

    private final PriorityQueue<Document> printQueue;
    private final Printer[] printers;
    private final Dispatcher dispatcher;
    private final Thread dispatcherThread;
    private final List<Document> printedDocuments = Collections.synchronizedList(new ArrayList<>());

    public List<Document> getPrintedDocuments() {
        return printedDocuments;
    }


    public PrintController() {
        this.printQueue = new PriorityQueue<>();
        this.printers = new Printer[] {
                new Printer("Printer-1"),
                new Printer("Printer-2"),
                new Printer("Printer-3")
        };

        for (Printer printer : printers) {
            printer.setOnDocumentPrinted(doc -> printedDocuments.add(doc));
            new Thread(printer).start();
        }


        // Aquí pre-cargamos documentos
        this.printQueue.enqueue(new Document("Payroll", Priority.High));
        this.printQueue.enqueue(new Document("Internal Memo", Priority.Low));
        this.printQueue.enqueue(new Document("Client Report", Priority.Medium));
        this.printQueue.enqueue(new Document("Invoice", Priority.High));
        this.printQueue.enqueue(new Document("Weekly Summary", Priority.Medium));

        this.dispatcher = new Dispatcher(printQueue, printers);
        this.dispatcherThread = new Thread(dispatcher);
        this.dispatcherThread.start();
    }


    @GetMapping("/queue")
    public List<Map<String, String>> getQueue() {
        List<Map<String, String>> documents = new ArrayList<>();
        synchronized (printQueue) {
            Node<Document> current = printQueue.getHead();
            while (current != null) {
                Map<String, String> docInfo = new HashMap<>();
                docInfo.put("name", current.getData().getName());
                docInfo.put("priority", current.getData().getPriority().name());
                documents.add(docInfo);
                current = current.getNext();
            }
        }
        return documents;
    }

    @GetMapping("/printers")
    public List<Map<String, String>> getPrinters() {
        List<Map<String, String>> printerStatus = new ArrayList<>();
        for (Printer printer : printers) {
            Map<String, String> info = new HashMap<>();
            info.put("id", printer.getId());
            info.put("status", printer.isBusy() ? "Printing" : "Idle");
            info.put("document", printer.getCurrentDocumentName());
            printerStatus.add(info);
        }
        return printerStatus;
    }


    @PostMapping("/send")
    public String sendDocument(@RequestParam String printerId,
                               @RequestParam String documentName,
                               @RequestParam Priority priority) {
        Document doc = new Document(documentName, priority);
        printQueue.enqueue(doc);
        return "Document added to print queue.";
    }

    @GetMapping("/printed")
    public List<Map<String, String>> getPrinted() {
        List<Map<String, String>> documents = new ArrayList<>();
        synchronized (printedDocuments) {
            for (Document doc : printedDocuments) {
                Map<String, String> docInfo = new HashMap<>();
                docInfo.put("name", doc.getName());
                docInfo.put("priority", doc.getPriority().name());
                documents.add(docInfo);
            }
        }
        return documents;
    }

}