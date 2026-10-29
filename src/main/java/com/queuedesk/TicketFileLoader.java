package com.queuedesk;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TicketFileLoader {
    public static List<Ticket> load (Path file) throws IOException,InvalidTicketDataException{
        List<Ticket> tickets = new ArrayList<>();
        int lineNumber=0;
        try (BufferedReader reader = Files.newBufferedReader(file)){
            String line;
            while((line =reader.readLine())!=null){
                lineNumber++;
                String [] parts=line.split("\\|");
                if(parts.length!=7){
                    throw new InvalidTicketDataException("Line " + lineNumber + ": expected 7 fields but found " + parts.length);
                }

                Ticket ticket;
                try{
                    ticket=switch(parts[0].trim()){
                        case "BUG" -> new BugTicket(parts[1].trim(),parts[2].trim(),parts[3].trim(),Priority.valueOf(parts[4].trim()),Severity.valueOf(parts[5].trim()),parts[6].trim());
                        case "FEATURE" -> new FeatureRequestTicket(parts[1].trim(),parts[2].trim(),parts[3].trim(),Priority.valueOf(parts[4].trim()),Integer.parseInt(parts[5].trim()),parts[6].trim());
                        case "ACCESS" -> new AccessRequestTicket(parts[1].trim(),parts[2].trim(),parts[3].trim(),Priority.valueOf(parts[4].trim()),parts[5].trim(),parts[6].trim());
                        default -> throw new InvalidTicketDataException("Line " + lineNumber + ": unknown type '" + parts[0] + "'");
                    };
                }catch(IllegalArgumentException e ){
                    throw new InvalidTicketDataException("Line " + lineNumber + ": " + e.getMessage(), e);
                }
                tickets.add(ticket);
            }
        }
        return tickets;
    }
}
