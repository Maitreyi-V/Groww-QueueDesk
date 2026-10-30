package com.queuedesk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TicketJsonStoreTest {

    @TempDir
    Path tempDir;

    @Test
    void saveThenLoad_restoresTicketsExactly() throws Exception{
        BugTicket bug = new BugTicket("Crash", "App crashes", "Alice",
                Priority.HIGH, Severity.LOW, "Open app");
        bug.setAssignee("Zed");
        bug.addTag("api");
        bug.escalate();
        bug.transitionTo(Status.IN_PROGRESS);

        FeatureRequestTicket feature = new FeatureRequestTicket("Dark mode", "Night theme", "Bob",
                Priority.LOW, 3, "Retention");

        AccessRequestTicket access = new AccessRequestTicket( "Grafana", "View access", "Cy",
                Priority.MEDIUM, "Grafana", "Viewer");

        List<Ticket> original= List.of(bug,feature,access);
        Path file =tempDir.resolve("tickets.json");

        TicketJsonStore.save(original,file);

        List<Ticket> loaded = TicketJsonStore.load(file);

        assertEquals(3,loaded.size());

        assertInstanceOf(BugTicket.class,loaded.get(0));
        assertInstanceOf(FeatureRequestTicket.class,loaded.get(1));
        assertInstanceOf(AccessRequestTicket.class,loaded.get(2));

       //checking bugticket data
        Ticket loadedBug=loaded.get(0);
        assertEquals(bug.getID(),loadedBug.getID());
        assertEquals(bug.getTitle(),loadedBug.getTitle());
        assertEquals(bug.getAssignee(),loadedBug.getAssignee());
        assertEquals(Status.IN_PROGRESS,loadedBug.getStatus());
        assertEquals(Set.of("api"),loadedBug.getTags());
        assertEquals(bug.getCreatedAt(),loadedBug.getCreatedAt());

        BugTicket restoredBug= (BugTicket)loadedBug;
        assertEquals(1,restoredBug.escalationLevel());

        assertEquals(11.5,loaded.get(1).estimateEffortHours(),0.001);
    }

    @Test
    void load_movesIdCounterPastLoadedIds() throws Exception{
        Path file = tempDir.resolve("high-id.json");
        Files.writeString(file, """
        [ {
          "type" : "access",
          "id" : 500,
          "title" : "Old ticket"
        } ]
        """);

        TicketJsonStore.load(file);

        BugTicket created = new BugTicket(
                "New", "Desc", "Alice",
                Priority.LOW, Severity.LOW, "Steps"
        );

        assertTrue(created.getID()>500);

    }

}
