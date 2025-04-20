package busmanager.solution;


import busmanager.Ticket;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TicketSolution implements Ticket {

    Status status = Status.ISSUED;
    final int id;
    final Lock ticketLock = new ReentrantLock();

    public TicketSolution(int id) {
        this.id = id;
    }

    @Override
    public Status getStatus() {
        return status;
    }
}
