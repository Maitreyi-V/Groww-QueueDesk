package com.queuedesk;

import com.queuedesk.exception.InvalidStatusTransitionException;
import com.queuedesk.exception.InvalidTicketDataException;
import com.queuedesk.exception.TicketNotFoundException;
import com.queuedesk.io.ReportGenerator;
import com.queuedesk.io.TicketFileLoader;
import com.queuedesk.io.TicketJsonStore;
import com.queuedesk.model.AccessRequestTicket;
import com.queuedesk.model.BugTicket;
import com.queuedesk.model.Escalatable;
import com.queuedesk.model.FeatureRequestTicket;
import com.queuedesk.model.Priority;
import com.queuedesk.model.Severity;
import com.queuedesk.model.Status;
import com.queuedesk.model.Ticket;
import com.queuedesk.model.TicketComparators;
import com.queuedesk.repository.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Main {
    private static final Path TICKETS_TXT = Path.of("tickets.txt");
    private static final Path TICKETS_JSON = Path.of("tickets.json");
    private static final Path REPORT_TXT = Path.of("report.txt");
    private static final int OVERDUE_AFTER_HOURS = 0;

    public static void main(String[] args) throws IOException {
        BugTicket bug = sampleBug();
        FeatureRequestTicket feature = sampleFeature();
        AccessRequestTicket access = sampleAccess();

        bug.escalate();
        feature.transitionTo(Status.IN_PROGRESS);
        bug.transitionTo(Status.RESOLVED);
        // access stays OPEN
        bug.setAssignee("Alice");
        feature.setAssignee("Bob");
        access.setAssignee("Alice");

        List<Ticket> tickets = List.of(bug, feature, access);

        // A missing ticket is caught once, here at the top, for a clean message.
        try {
            printTriageBoard(tickets);
            demoTriageQueues(tickets);
            demoRepository(tickets);
            demoStatusRule(access);
            migrateAndReport();
        } catch (TicketNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---- sample data ----

    private static BugTicket sampleBug() {
        return new BugTicket("Database Timeout", "connection drops under load", "Alice",
                Priority.HIGH, Severity.CRITICAL, "1. Run 100 queries\n 2. Observe timeout");
    }

    private static FeatureRequestTicket sampleFeature() {
        return new FeatureRequestTicket("Dark Mode", "Add night mode theme", "Bob",
                Priority.MEDIUM, 12, "Improves user retention");
    }

    private static AccessRequestTicket sampleAccess() {
        return new AccessRequestTicket("Prod DB Access", "Need read-only access for debugging", "Charlie",
                Priority.LOW, "Production Postgres", "Read-Only");
    }

    // ---- demos ----

    public static void printTriageBoard(List<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            System.out.println(ticket);
            System.out.println("  " + ticket.shortDescription(30));
            System.out.println("Estimated effort: " + ticket.estimateEffortHours());

            if (ticket instanceof Escalatable e && e.isEscalated()) {
                System.out.println(e.escalationBadge());
            }
            System.out.println("-----------------------------------");
        }
    }

    private static void demoTriageQueues(List<Ticket> tickets) {
        PriorityQueue<Ticket> natural = new PriorityQueue<>(tickets);
        serveAll("By natural order", natural);

        PriorityQueue<Ticket> byStatus = new PriorityQueue<>(TicketComparators.BY_STATUS_THEN_AGE);
        byStatus.addAll(tickets);
        serveAll("By status then age", byStatus);
    }

    private static void serveAll(String heading, PriorityQueue<Ticket> queue) {
        System.out.println(heading);
        while (!queue.isEmpty()) {
            Ticket next = queue.poll();
            System.out.println("Serving " + next.getTitle() + " [" + next.getPriority() + "]");
        }
    }

    private static void demoRepository(List<Ticket> tickets) {
        Repository<Ticket> repo = new Repository<>();
        for (Ticket ticket : tickets) {
            repo.add(ticket);
        }

        System.out.println("-----Grouped by assignee-----");
        Map<String, List<Ticket>> grouped = repo.groupByAssignee();
        grouped.forEach((assignee, list) ->
                System.out.println(assignee + " has " + list.size() + " ticket(s)"));

        demoLookups(repo, tickets.get(0).getID());
    }

    private static void demoLookups(Repository<Ticket> repo, int knownId) {
        // findById: a missing ticket is an error (throws)
        try {
            repo.findById(999);
        } catch (TicketNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // findByIdSafe: a missing ticket is a normal answer (Optional)
        repo.findByIdSafe(999).ifPresentOrElse(
                ticket -> System.out.println("Found: " + ticket),
                () -> System.out.println("No ticket with ID 999"));

        String title = repo.findByIdSafe(knownId)
                .map(Ticket::getTitle)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
        System.out.println("Title: " + title);
    }

    private static void demoStatusRule(Ticket openTicket) {
        try {
            openTicket.transitionTo(Status.CLOSED);
        } catch (InvalidStatusTransitionException e) {
            System.out.println(e.getMessage());
        }
    }

    // ---- real work: migrate the old file once, then load JSON and write the report ----

    private static void migrateAndReport() throws IOException {
        try {
            if (!Files.exists(TICKETS_JSON)) {
                List<Ticket> old = TicketFileLoader.load(TICKETS_TXT);
                TicketJsonStore.save(old, TICKETS_JSON);
                System.out.println("Migrated " + old.size() + " tickets to " + TICKETS_JSON);
            }

            List<Ticket> loaded = TicketJsonStore.load(TICKETS_JSON);
            System.out.println("Loaded " + loaded.size() + " tickets");

            String report = ReportGenerator.generate(loaded, OVERDUE_AFTER_HOURS);
            System.out.println(report);
            ReportGenerator.writeToFile(report, REPORT_TXT);
        } catch (InvalidTicketDataException e) {
            System.out.println("Could not load tickets: " + e.getMessage());
        }
    }

    public static <T extends Ticket> T oldest(List<T> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            return null;
        }
        T oldest = tickets.get(0);
        for (T ticket : tickets) {
            if (ticket.getCreatedAt().isBefore(oldest.getCreatedAt())) {
                oldest = ticket;
            }
        }
        return oldest;
    }
}
