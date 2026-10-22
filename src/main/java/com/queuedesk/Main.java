package com.queuedesk;

import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
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


        printTriageBoard(tickets);

//        PriorityQueue<Ticket> triageQueue = new PriorityQueue<>();
//        triageQueue.add(bug);
//        triageQueue.add(feature);
//        triageQueue.add(access);
//
//        while(!triageQueue.isEmpty()){
//            Ticket nextTicket=triageQueue.poll();
//            System.out.println("Serving"+ nextTicket.getTitle()+"["+nextTicket.getPriority()+"]");
//        }

        Repository<Ticket> repo=new Repository<>();
        bug.setAssignee("Alice");
        feature.setAssignee("Bob");
        access.setAssignee("Alice");

        repo.add(bug);
        repo.add(feature);
        repo.add(access);

        System.out.println("-----Grouped by assignee-----");
        Map<String,List<Ticket>> grouped =repo.groupByAssignee();
        grouped.forEach((assignee,list)->{
            System.out.println(assignee+" has "+list.size()+" ticket(s) ");
        });

    }


    public static void printTriageBoard(List<Ticket> tickets){
            for(Ticket ticket : tickets){
                System.out.println("ID: "+ticket.getID() + "| Title: " + ticket.getTitle() + "| Priority: "+ ticket.getPriority());
                System.out.println("Estimated effort: " + ticket.estimateEffortHours());

                if(ticket instanceof Escalatable ){
                    Escalatable e = (Escalatable) ticket;
                    if(e.isEscalated()){
                        System.out.println(e.escalationBadge());
                    }
                }
                System.out.println("-----------------------------------");
            }
    }

    public static <T extends Ticket> T oldest(List<T> tickets){
        if(tickets==null || tickets.isEmpty()){
            return null;
        }
        T oldest=tickets.get(0);
        for(T ticket : tickets){
            if(ticket.getCreatedAT().isBefore(oldest.getCreatedAT())){
                oldest=ticket;
            }
        }
        return oldest;
    }




}