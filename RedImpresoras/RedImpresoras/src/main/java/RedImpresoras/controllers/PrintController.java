package RedImpresoras.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import RedImpresoras.model.Dispatcher;
import RedImpresoras.model.Document;
import RedImpresoras.model.Node;
import RedImpresoras.model.Printer;
import RedImpresoras.model.Priority;
import RedImpresoras.model.PriorityQueue;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
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
        boolean busy = printer.isBusy();
        info.put("id", printer.getId());
        info.put("status", busy ? "Printing" : "Idle");
        info.put("document", busy ? printer.getCurrentDocumentName() : "-");
        printerStatus.add(info);
    }
    return printerStatus;
}



@PostMapping("/send")
public String sendDocument(@RequestParam String printerId,
                            @RequestParam String documentName,
                            @RequestParam Priority priority) {
    Document doc = new Document(documentName, priority);

    for (Printer printer : printers) {
        if (printer.getId().equals(printerId)) {
            printer.assignDocument(doc);
            break;
        }
    }

    return "Document sent to printer.";
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