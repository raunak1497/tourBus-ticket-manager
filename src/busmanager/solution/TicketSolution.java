package busmanager.solution;

import busmanager.Ticket;

public class TicketSolution implements Ticket {

    Status status = Status.ISSUED;
    @Override
    public Status getStatus() {
        return status;
    }
}
