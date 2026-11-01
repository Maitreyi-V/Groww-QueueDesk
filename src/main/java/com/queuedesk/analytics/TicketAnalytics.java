package com.queuedesk.analytics;

import com.queuedesk.model.Priority;
import com.queuedesk.model.Status;
import com.queuedesk.model.Ticket;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TicketAnalytics {
     public static Map<Priority,Long> countByPriority(List<Ticket> tickets){
         return tickets.stream()
                 .collect(Collectors.groupingBy(Ticket::getPriority,Collectors.counting()));

     }

     public static Map<String,List<Ticket>> groupByAssignee(List<Ticket> tickets){
         return tickets.stream()
                 //.collect(Collectors.groupingBy(Ticket::getAssignee)); // this is going to break bcs of null pointer exception
                 .collect(Collectors.groupingBy((t)-> t.getAssignee()==null ? "Unassigned" : t.getAssignee()));
     }

     public static double averageEffortHours(List<Ticket> tickets){
         return tickets.stream()
                 .mapToDouble(Ticket :: estimateEffortHours)
                 .average().orElse(0.0);
     }

    public static double totalEffortHours(List<Ticket> tickets){
        return tickets.stream()
                .mapToDouble(Ticket :: estimateEffortHours)
                .reduce(0.0,Double :: sum);
    }

    public static Optional<Ticket> oldestOpenTicket(List<Ticket> tickets){
         return tickets.stream()
                 .filter(t-> t.getStatus()==Status.OPEN)
                 .min(Comparator.comparing(Ticket::getCreatedAt));
    }

    public static String tagLine(List<Ticket> tickets){
         return tickets.stream()
                 .flatMap(t->t.getTags().stream())
                 .distinct()
                 .sorted()
                 .collect(Collectors.joining(","));

    }

}
