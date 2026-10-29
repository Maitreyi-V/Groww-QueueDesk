package com.queuedesk;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RepositoryTest {

    @Test
    void findById_returnsTheTicketThatWasAdded() {
        Repository<Ticket> repo = new Repository<>();
        Ticket bug = new BugTicket("Crash", "App crashes on start", "Alice",
                Priority.HIGH, Severity.HIGH, "Open the app");
        repo.add(bug);
        Ticket found=repo.findById(bug.getID());
        assertSame(bug,found);
    }
}
