package com.queuedesk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        try {
            List<Ticket> tickets = new ArrayList<>();
            BugTicket bug = new BugTicket("Database Timeout", "connection drops under load", "Alice", Priority.HIGH, Severity.CRITICAL, "1. Run 100 queries\n 2. Observe timeout");
            FeatureRequestTicket feature = new FeatureRequestTicket("Dark Mode",
                    "Add night mode theme",
                    "Bob",
                    Priority.MEDIUM,
                    12,
                    "Improves user retention");
            AccessRequestTicket access = new AccessRequestTicket("Prod DB Access",
                    "Need read-only access for debugging",
                    "Charlie",
                    Priority.LOW,
                    "Production Postgres",
                    "Read-Only");
            bug.escalate();


            tickets.add(bug);
            tickets.add(feature);
            tickets.add(access);


            feature.transitionTo(Status.IN_PROGRESS);
            bug.transitionTo(Status.RESOLVED);
            // access stays OPEN
            printTriageBoard(tickets);


            PriorityQueue<Ticket> natural = new PriorityQueue<>();
            natural.offer(bug);
            natural.offer(feature);
            natural.offer(access);

            System.out.println("By natural order");
            while (!natural.isEmpty()) {
                Ticket nextTicket = natural.poll();
                System.out.println("Serving" + nextTicket.getTitle() + "[" + nextTicket.getPriority() + "]");
            }

            PriorityQueue<Ticket> byStatus = new PriorityQueue<>(TicketComparators.BY_STATUS_THEN_AGE);
            byStatus.offer(bug);
            byStatus.offer(feature);
            byStatus.offer(access);

            System.out.println("By status then age");
            while (!byStatus.isEmpty()) {
                Ticket nextTicket = byStatus.poll();
                System.out.println("Serving" + nextTicket.getTitle() + "[" + nextTicket.getPriority() + "]");
            }

            Repository<Ticket> repo = new Repository<>();
            bug.setAssignee("Alice");
            feature.setAssignee("Bob");
            access.setAssignee("Alice");

            repo.add(bug);
            repo.add(feature);
            repo.add(access);

            System.out.println("-----Grouped by assignee-----");
            Map<String, List<Ticket>> grouped = repo.groupByAssignee();
            grouped.forEach((assignee, list) -> {
                System.out.println(assignee + " has " + list.size() + " ticket(s) ");
            });

            try {
                Ticket missing = repo.findById(999);
            } catch (TicketNotFoundException e) {
                System.out.println(e.getMessage());
            }


            repo.findByIdSafe(999).ifPresentOrElse(
                    ticket -> System.out.println("Found: " + ticket),
                    () -> System.out.println("No ticket with ID 999")
            );

            String title = repo.findByIdSafe(bug.getID())
                    .map(Ticket::getTitle)
                    .orElseThrow(()->new TicketNotFoundException("Ticket not found"));
            System.out.println("Title :"+ title);



            try {
                access.transitionTo(Status.CLOSED);
            } catch (InvalidStatusTransitionException e) {
                System.out.println(e.getMessage());
            }


//            try {
//                List<Ticket> loaded = TicketFileLoader.load(Path.of("tickets.txt"));
//                System.out.println("Loaded " + loaded.size() + " tickets");
//                System.out.println(ReportGenerator.generate(loaded, 0));
//                String report = ReportGenerator.generate(loaded, 0);
//                ReportGenerator.writeToFile(report, Path.of("report.txt"));
//            } catch (InvalidTicketDataException e) {
//                System.out.println("Could not load tickets" + e.getMessage());
//            }

            try {
                Path jsonFile = Path.of("tickets.json");
                if (!Files.exists(jsonFile)) {
                    List<Ticket> old = TicketFileLoader.load(Path.of("tickets.txt"));
                    TicketJsonStore.save(old, jsonFile);
                    System.out.println("Migrated");
                }
                List<Ticket> loaded = TicketJsonStore.load(jsonFile);
            }catch (InvalidTicketDataException e){
                System.out.println("Could not load tickets" + e.getMessage());
            }

        }catch (TicketNotFoundException e){
            System.out.println("Error: "+e.getMessage());
        }


    }


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