package com.queuedesk.model;

import com.queuedesk.exception.InvalidStatusTransitionException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TicketTest {

    private BugTicket ticket;

    @BeforeEach
    void setUp(){
        //create a fresh ticket before every test
        ticket = new BugTicket("Crash",
                "App crashes on start",
                "Alice",
                Priority.HIGH,
                Severity.HIGH,
                "Open the app");
    }

    @Test
    void transitionTo_throwsWhenOpenGoesStraightToClosed(){
        assertThrows(InvalidStatusTransitionException.class,()->ticket.transitionTo(Status.CLOSED));
    }

    @Test
    void transitionTo_throwsWhenTciketIsAlreadyClosed(){
        ticket.transitionTo(Status.IN_PROGRESS);
        ticket.transitionTo(Status.CLOSED);

        assertThrows(
                InvalidStatusTransitionException.class,()-> ticket.transitionTo(Status.IN_PROGRESS)
        );
    }

    @Test
    void transitionTo_changesStatusWhenMoveIsValid(){
        ticket.transitionTo(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS,ticket.getStatus());
    }

    @Test
    void compareTo_putsHigherPriorityFirst(){
        BugTicket critical = new BugTicket(
                "Server down",
                "Production server is unavailable",
                "Bob",
                Priority.CRITICAL,
                Severity.HIGH,
                "Check server logs"
        );

        assertTrue(critical.compareTo(ticket)<0);

    }


    @Test
    void compareTo_putsOlderTicketFirstWhenPrioritiesAreEqual() throws InterruptedException{
        BugTicket older = new BugTicket("First ticket", "Description", "Alice",
                Priority.HIGH, Severity.HIGH, "Steps");

        // gives timestamps a better chance of being different
        Thread.sleep(5);

        BugTicket newer = new BugTicket(
                "Second ticket", "Description", "Bob",
                Priority.HIGH, Severity.HIGH, "Steps"
        );

        assertTrue(older.compareTo(newer)<0);

    }

}
