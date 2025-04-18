package busmanager.solution;

import busmanager.Ticket;

public class BusTicket {
    BusSolution bus;
    TicketSolution ticket;

    public BusTicket(BusSolution bus, TicketSolution ticket) {
        this.bus = bus;
        this.ticket = ticket;
    }
}
