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

All code is in `src/main/java/com/queuedesk`.

| File | What it does |
|---|---|
| `Ticket` | Abstract base class: shared fields, status rules, ordering |
| `BugTicket`, `FeatureRequestTicket`, `AccessRequestTicket` | The three ticket types |
| `Escalatable` | Interface for tickets that can be escalated |
| `Priority`, `Severity`, `Status` | Enums |
| `Repository<T>` | Stores tickets, finds them by id, groups by assignee |
| `TicketComparators` | Ready-made sort orders |
| `TicketAnalytics` | Stream-based statistics |
| `TicketFileLoader` | Reads the old pipe-delimited `tickets.txt` |
| `TicketJsonStore` | Saves and loads tickets as JSON |
| `ReportGenerator` | Builds and writes the end-of-day report |
| `*Exception` | Custom errors for bad data, missing tickets and invalid status changes |
| `Main` | Demo that runs everything |

Tests are in `src/test/java/com/queuedesk`.
