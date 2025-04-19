package busmanager.solution;

import busmanager.Ticket;

public class TicketSolution implements Ticket {

    Status status = Status.ISSUED;
    DepotSolution depot;

    public TicketSolution(DepotSolution depot) {
        this.depot = depot;
    }

    @Override
    public Status getStatus() {
        return status;
    }
}
