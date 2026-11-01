package com.queuedesk.analytics;

import com.queuedesk.model.AccessRequestTicket;
import com.queuedesk.model.BugTicket;
import com.queuedesk.model.FeatureRequestTicket;
import com.queuedesk.model.Priority;
import com.queuedesk.model.Severity;
import com.queuedesk.model.Status;
import com.queuedesk.model.Ticket;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TicketAnalyticsTest {
    private BugTicket bugWithPriority(Priority priority) {
        return new BugTicket("Title", "Description", "Alice", priority, Severity.LOW, "Steps");
    }

    @Test
    void  countByPriority_countsEachPriorityCorrectly(){
        List<Ticket> tickets = List.of(
                bugWithPriority(Priority.HIGH),
                bugWithPriority(Priority.LOW),
                bugWithPriority(Priority.HIGH)
        );

        Map<Priority,Long> result =TicketAnalytics.countByPriority(tickets);
        assertEquals(2L,result.get(Priority.HIGH));
        assertEquals(1L,result.get(Priority.LOW));
        assertNull(result.get(Priority.CRITICAL));
    }


    @Test
    void averageEffortHours_returnsAverageOfAllTickets(){
        List<Ticket> tickets = List.of(
                new BugTicket("T1", "Desc", "Alice", Priority.HIGH, Severity.LOW, "Steps"),
                new BugTicket("T2", "Desc", "Bob", Priority.HIGH, Severity.HIGH, "Steps")
        );
        double result= TicketAnalytics.averageEffortHours(tickets);
        assertEquals(4.0,result,0.001);
    }

    @Test
    void averageEffortHours_returnsZeroForEmptyList(){
            double result= TicketAnalytics.averageEffortHours(List.of());
            assertEquals(0.0,result,0.001);
    }

    @Test
    void totalEffortHours_addsEffortOfAllTicketTypes(){
        List<Ticket> tickets = List.of(
                new BugTicket("Bug", "Desc", "Alice", Priority.HIGH, Severity.LOW, "Steps"),
                new FeatureRequestTicket("Feature", "Desc", "Bob", Priority.MEDIUM, 4, "Reason"),
                new AccessRequestTicket("Access", "Desc", "Charlie", Priority.LOW, "Grafana", "Viewer")
        );
        assertEquals(15.0,TicketAnalytics.totalEffortHours(tickets),0.001);

    }

    @Test
    void totalEffortHours_returnsZeroForEmptyList(){
        assertEquals(0.0, TicketAnalytics.totalEffortHours(List.of()),0.001);
    }


    @Test
    void tagLine_mergesDistinctSortedTagsFromAllTickets(){
        BugTicket first = bugWithPriority(Priority.HIGH);
        BugTicket second = bugWithPriority(Priority.LOW);
        first.addTag("login");
        first.addTag("api");
        second.addTag("login");
        second.addTag("urgent");

        String result = TicketAnalytics.tagLine(List.of(first,second));
        assertEquals("api,login,urgent",result);
    }

    @Test
    void tagLine_returnsEmptyStringWhenNoTicketsHaveTags(){
        assertEquals("",TicketAnalytics.tagLine(List.of()));
    }

    @Test
    void oldestOpenTicket_returnsEarliestOpenTicketAndIgnoresClosedOnes(){
        Ticket oldestOpen = mock(Ticket.class);
        Ticket newerOpen = mock(Ticket.class);
        Ticket olderClosed = mock(Ticket.class);

        //configure the statuses
        when(oldestOpen.getStatus()).thenReturn(Status.OPEN);
        when(newerOpen.getStatus()).thenReturn(Status.OPEN);
        when(olderClosed.getStatus()).thenReturn(Status.CLOSED);

        //configure their creation dates
        when(oldestOpen.getCreatedAt()).thenReturn(LocalDateTime.of(2026,10,1,9,0));
        when(newerOpen.getCreatedAt())
                .thenReturn(LocalDateTime.of(2026, 10, 2, 9, 0));
        when(olderClosed.getCreatedAt())
                .thenReturn(LocalDateTime.of(2026, 9, 1, 9, 0));

        //call
        Optional<Ticket>result=TicketAnalytics.oldestOpenTicket(List.of(oldestOpen,newerOpen,olderClosed));

        // verify
        assertTrue(result.isPresent());
        assertSame(result.get(),oldestOpen);
    }

    @Test
    void oldestOpenTicket_returnsEmptyWhenNothingIsOpen(){
        Ticket closedTicket = mock(Ticket.class);
        when(closedTicket.getStatus()).thenReturn(Status.CLOSED);
        Optional<Ticket> result = TicketAnalytics.oldestOpenTicket(List.of(closedTicket));
        assertTrue(result.isEmpty());

    }
}
