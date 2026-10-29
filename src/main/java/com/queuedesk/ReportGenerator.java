package com.queuedesk;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ReportGenerator {
    public static String generate(List<Ticket>tickets,int overduehours){
        StringBuilder sb=new StringBuilder();
        sb.append("\n===End of the report===");
        sb.append("Total tickets: ").append(tickets.size()).append("\n\n");

        sb.append("By status : \n");
        for(Status status : Status.values()){
            int count=0;
            for(Ticket ticket : tickets){
                if(ticket.getStatus()==status){
                    count++;
                }
            }
            sb.append(" ").append(status).append(": ").append(count).append("\n");
        }

        sb.append("\nBy Priority:\n");
        for(Priority priority : Priority.values()){
            int count=0;
            for(Ticket ticket : tickets){
                if(ticket.getPriority()==priority){
                    count++;
                }
            }
            sb.append(" ").append(priority).append(": ").append(count).append("\n");
        }

        sb.append("\n Oldest open ticket:");
        List<Ticket> openTickets=new ArrayList<>();
        for(Ticket ticket: tickets){
            if(ticket.getStatus()==Status.OPEN){
                openTickets.add(ticket);
            }
        }
        if(openTickets.isEmpty()){
            sb.append("none");
        }else{
            Ticket oldest=openTickets.get(0);
            for(Ticket ticket:openTickets){
                if(ticket.getCreatedAt().isBefore(oldest.getCreatedAt())){
                    oldest=ticket;
                }
            }
            sb.append(oldest);
        }

        sb.append("\n Overdue critical tickets: ");
        List<Ticket> overdue = new ArrayList<>();
        for(Ticket ticket: tickets){
            if(ticket.getPriority()==Priority.CRITICAL && ticket.getStatus()!=Status.CLOSED && ticket.getStatus()!=Status.RESOLVED && ticket.ageInHours() >= overduehours){
                overdue.add(ticket);
            }
        }
        if(overdue.isEmpty()){
            sb.append("none");
        }else{
            for(Ticket ticket: overdue){
                sb.append(" ").append(ticket).append("\n");
            }
        }
        return sb.toString();
    }

    //save to a file
    public static void writeToFile(String report , Path file) throws IOException {
        try(BufferedWriter writer = Files.newBufferedWriter(file)){
            writer.write(report);
        }
    }
}
