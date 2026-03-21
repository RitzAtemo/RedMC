package red.aviora.redmc.moderation.managers;

import red.aviora.redmc.moderation.models.Ticket;
import red.aviora.redmc.moderation.models.TicketReply;
import red.aviora.redmc.moderation.models.TicketStatus;
import red.aviora.redmc.moderation.utils.ModerationDataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TicketManager {

    private final ModerationDataStorage storage;
    private List<Ticket> tickets = new ArrayList<>();

    public TicketManager(ModerationDataStorage storage) {
        this.storage = storage;
    }

    public void load() {
        tickets = storage.loadTickets();
    }

    public Ticket createTicket(UUID authorUuid, String authorName, String message) {
        String id = UUID.randomUUID().toString();
        Ticket ticket = new Ticket(id, authorUuid, authorName, message, TicketStatus.OPEN, System.currentTimeMillis(), new ArrayList<>());
        tickets.add(ticket);
        storage.saveTickets(tickets);
        return ticket;
    }

    public boolean closeTicket(String ticketId) {
        Ticket ticket = getById(ticketId);
        if (ticket == null) return false;
        if (ticket.getStatus() == TicketStatus.CLOSED) return false;
        ticket.setStatus(TicketStatus.CLOSED);
        storage.saveTickets(tickets);
        return true;
    }

    public boolean addReply(String ticketId, UUID staffUuid, String staffName, String message) {
        Ticket ticket = getById(ticketId);
        if (ticket == null) return false;
        TicketReply reply = new TicketReply(staffUuid, staffName, message, System.currentTimeMillis());
        ticket.getReplies().add(reply);
        storage.saveTickets(tickets);
        return true;
    }

    public Ticket getById(String id) {
        for (Ticket ticket : tickets) {
            if (ticket.getId().equals(id) || ticket.getShortId().equals(id)) return ticket;
        }
        return null;
    }

    public List<Ticket> getAll() {
        return tickets;
    }

    public List<Ticket> getByAuthor(UUID authorUuid) {
        return tickets.stream()
            .filter(t -> t.getAuthorUuid().equals(authorUuid))
            .collect(Collectors.toList());
    }

    public List<Ticket> getOpen() {
        return tickets.stream()
            .filter(t -> t.getStatus() == TicketStatus.OPEN)
            .collect(Collectors.toList());
    }
}
