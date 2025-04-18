package busmanager.solution;

import busmanager.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class DepotSolution extends Depot<BusSolution, TicketSolution> {
    private Set<BusSolution> buses = new HashSet<>();
    private LinkedList<Action<BusTicket>> auditLog = new LinkedList<>();
    private Lock lock = new LockSolution();
    @Override
    public BusSolution createBus(int capacity) {
        BusSolution ret =  new BusSolution(capacity);
        buses.add(ret);
        return ret;
    }

    @Override
    public TicketSolution issueTicket(int id) {
        return new TicketSolution(this.lock);
    }

    @Override
    public boolean boardBus(BusSolution bus, Set<TicketSolution> tickets) {
        lock.lock();
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
                BusTicket busTicket = new BusTicket(bus, ticket);
                auditLog.addLast(new Action<>(Action.Direction.IN_CIRCULATION, busTicket));
            }
            return true;
        }finally {
            lock.unlock();
        }

    }

    @Override
    public boolean transferTickets(BusSolution from, BusSolution to, Set<TicketSolution> tickets) {
        lock.lock();
        try{
            if(!from.contents.containsAll(tickets))
                return false;

            if(tickets.size() + to.contents.size() > to.capacity)
                return false;

            from.contents.removeAll(tickets);
            to.contents.addAll(tickets);

            for(TicketSolution ticket : tickets) {
                BusTicket busTicket = new BusTicket( from, ticket);
                auditLog.addLast(new Action<>(Action.Direction.MOVED_OUT, busTicket));
            }

            for(TicketSolution ticket : tickets) {
                BusTicket busTicket = new BusTicket( to, ticket);
                auditLog.addLast(new Action<>(Action.Direction.MOVED_IN, busTicket));
            }
            return true;
        }finally {
            lock.unlock();
        }

    }

    @Override
    public boolean useTickets(BusSolution bus, Set<TicketSolution> tickets) {
        lock.lock();
        try{
            if(!bus.contents.containsAll(tickets))
                return false;

            for(TicketSolution ticket : tickets) {
                ticket.status = Ticket.Status.USED;
                BusTicket busTicket = new BusTicket(bus, ticket);
                auditLog.addLast(new Action<>(Action.Direction.USED, busTicket));
            }
            bus.contents.removeAll(tickets);

            return true;
        }finally {
            lock.unlock();
        }

    }

    @Override
    public boolean expireTickets(BusSolution bus, Set<TicketSolution> tickets) {
        lock.lock();
        try{
            if(!bus.contents.containsAll(tickets))
                return false;

            for(TicketSolution ticket : tickets) {
                ticket.status = Ticket.Status.EXPIRED;
                BusTicket busTicket = new BusTicket(bus, ticket);
                auditLog.addLast(new Action<>(Action.Direction.EXPIRED, busTicket));
            }
            bus.contents.removeAll(tickets);

            return true;
        }finally {
            lock.unlock();
        }

    }

    @Override
    public Set<TicketSolution> getTickets() {
        lock.lock();
        try {
            Set<TicketSolution> ret =  new HashSet<>();

            for(BusSolution bus : buses){
                ret.addAll(bus.contents);
            }
            return ret;
        }finally {
            lock.unlock();
        }

    }

    @Override
    public Set<TicketSolution> getTickets(BusSolution bus) {
        lock.lock();
        try{
            return new HashSet<>(bus.contents);
        }finally {
            lock.unlock();
        }

    }

    @Override
    public List<Action<TicketSolution>> audit(BusSolution bus) {
        LinkedList<Action<TicketSolution>> ret = new LinkedList<>();

        for(Action<BusTicket> action : auditLog) {
            if(action.get().bus == bus){
                ret.addLast(new Action<>(action.getDirection(), action.get().ticket));
            }
        }

        return  ret;
    }

    @Override
    public List<Action<BusSolution>> audit(TicketSolution ticket) {
        LinkedList<Action<BusSolution>> ret = new LinkedList<>();

        for(Action<BusTicket> action : auditLog) {
            if(action.get().ticket == ticket){
                ret.addLast(new Action<>(action.getDirection(), action.get().bus));
            }
        }
        return  ret;
    }
}
