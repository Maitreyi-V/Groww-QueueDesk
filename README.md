# QueueDesk

An internal ticketing tool for a fictional IT helpdesk, built in plain Java.
Staff log bugs, feature requests and access requests. The tool triages them by priority and age.

Built as Month 1 of Groww's "Grow before you Groww" pre-internship curriculum.

## What it does

- Three ticket types: bug, feature request, access request
- Status rules: `OPEN → IN_PROGRESS → RESOLVED → CLOSED` (no jumping from `OPEN` straight to `CLOSED`, no changes after `CLOSED`)
- Triage queue: highest priority first, oldest first when priorities tie
- Escalation for bug and access tickets
- Analytics with streams: count by priority, group by assignee, average and total effort, oldest open ticket, tag line
- Safe lookup: `findById` throws if missing, `findByIdSafe` returns an `Optional`
- Saves and loads tickets as JSON
- Imports the old pipe-delimited `tickets.txt` once, then uses `tickets.json`
- Writes an end-of-day report to `report.txt`

## Tech

- Java 21
- Maven
- JUnit 5 and Mockito (tests)
- Jackson (JSON)

## How to run

1. Open the project in IntelliJ IDEA.
2. Run `Main` (`src/main/java/com/queuedesk/Main.java`).
3. Run it from the project folder, so it can find `tickets.txt` and `tickets.json`.

On the first run, `Main` converts `tickets.txt` into `tickets.json`. After that it loads from `tickets.json`.
To migrate again, delete `tickets.json`.

## How to run the tests

```
mvn test
```

## Project layout

```
src/main/java/com/queuedesk
├── Main.java                       demo that runs everything
├── model/                          the things in the app, and their rules
│   ├── Ticket                      abstract base: shared fields, status rules, ordering
│   ├── BugTicket, FeatureRequestTicket, AccessRequestTicket
│   ├── Escalatable                 interface for tickets that can be escalated
│   ├── Priority, Severity, Status  enums
│   └── TicketComparators           ready-made sort orders
├── exception/                      custom errors
│   ├── InvalidStatusTransitionException
│   ├── InvalidTicketDataException
│   └── TicketNotFoundException
├── repository/
│   └── Repository<T>               stores tickets, finds by id, groups by assignee
├── analytics/
│   └── TicketAnalytics             stream-based statistics
└── io/                             reading and writing files
    ├── TicketFileLoader            reads the old pipe-delimited tickets.txt
    ├── TicketJsonStore             saves and loads tickets as JSON
    └── ReportGenerator             builds and writes the end-of-day report
```

Tests mirror the same packages under `src/test/java/com/queuedesk`.

Data files in the project root: `tickets.txt` (old format), `tickets.json` (current format), `tickets-bad.txt` (invalid input used by a test).
