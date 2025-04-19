package busmanager.solution;

import busmanager.Lock;
import busmanager.Ticket;

public class TicketSolution implements Ticket {

    Status status = Status.ISSUED;
    DepotSolution depot;
    Lock lock;

    public TicketSolution(Lock lock) {
        this.lock = lock;
    }

    @Override
    public Status getStatus() {
        lock.lock();
        try{
            return status;
        }finally {
            lock.unlock();
        }
    }
}
