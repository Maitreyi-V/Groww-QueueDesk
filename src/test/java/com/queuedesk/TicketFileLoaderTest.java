package com.queuedesk;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TicketFileLoaderTest {

    @Test
    void load_throwsWhenLineHasWrongNumberOfFields(){
        InvalidTicketDataException e= assertThrows(InvalidTicketDataException.class,()->TicketFileLoader.load(Path.of("tickets-bad.txt")));
        assertTrue(e.getMessage().contains("Line 1"));
    }

    @Test
    void load_returnsAllTicketsFromValidFile() throws Exception{
        List<Ticket> tickets=TicketFileLoader.load(Path.of("tickets.txt"));
        assertEquals(8,tickets.size());
        assertInstanceOf(BugTicket.class,tickets.get(0));
    }
}
