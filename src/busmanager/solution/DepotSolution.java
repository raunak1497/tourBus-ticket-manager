package busmanager.solution;

import busmanager.Depot;
import busmanager.Ticket;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DepotSolution extends Depot<BusSolution, TicketSolution> {
    private List<BusSolution> buses = new LinkedList<>();

    @Override
    public BusSolution createBus(int capacity) {
        BusSolution ret =  new BusSolution(capacity);
        buses.add(ret);
        return ret;
    }

    @Override
    public TicketSolution issueTicket(int id) {
        return new TicketSolution(this);
    }

    @Override
    public boolean boardBus(BusSolution bus, Set<TicketSolution> tickets) {
        bus.busLock.lock();
        try{
            if(tickets.size() + bus.contents.size() > bus.capacity)
                return false;

            for(TicketSolution ticket : tickets) {
                if(ticket.status != Ticket.Status.ISSUED)
                    return false;
            }

            bus.contents.addAll(tickets);

            for(TicketSolution ticket : tickets) {
                ticket.status = Ticket.Status.IN_CIRCULATION;
            }
            return true;
        }finally{
            bus.busLock.unlock();
        }

    }

    @Override
    public boolean transferTickets(BusSolution from, BusSolution to, Set<TicketSolution> tickets) {
        from.busLock.lock();
        to.busLock.lock();
        try{
            if(!from.contents.containsAll(tickets))
                return false;

            if(tickets.size() + to.contents.size() > to.capacity)
                return false;

            from.contents.removeAll(tickets);
            to.contents.addAll(tickets);

            return true;
        }finally {
            from.busLock.unlock();
            to.busLock.unlock();
        }

    }

    @Override
    public boolean useTickets(BusSolution bus, Set<TicketSolution> tickets) {
        bus.busLock.lock();
        try{
            if(!bus.contents.containsAll(tickets))
                return false;

            for(TicketSolution ticket : tickets) {
                ticket.status = Ticket.Status.USED;
            }
            bus.contents.removeAll(tickets);

            return true;
        }finally {
            bus.busLock.unlock();
        }

    }

    @Override
    public boolean expireTickets(BusSolution bus, Set<TicketSolution> tickets) {
        bus.busLock.lock();
        try{
            if(!bus.contents.containsAll(tickets))
                return false;

            for(TicketSolution ticket : tickets) {
                ticket.status = Ticket.Status.EXPIRED;
            }
            bus.contents.removeAll(tickets);

            return true;
        }finally {
            bus.busLock.unlock();
        }

    }

    @Override
    public Set<TicketSolution> getTickets() {
        return  getTickets(buses);
    }

    @Override
    public Set<TicketSolution> getTickets(BusSolution bus) {
        bus.busLock.lock();
        try{
            return new HashSet<>(bus.contents);
        }finally{
            bus.busLock.unlock();
        }

    }

    @Override
    public Set<TicketSolution> getTickets(List<BusSolution> buses) {
        Set<TicketSolution> ret =  new HashSet<>();

        for(BusSolution bus : buses){
            bus.busLock.lock();
            try{
                ret.addAll(bus.contents); //INCORRECT !!!
            }finally {
                bus.busLock.unlock();
            }

        }
        return ret;
    }
}