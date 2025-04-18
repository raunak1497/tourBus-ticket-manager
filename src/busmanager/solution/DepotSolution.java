package busmanager.solution;

import busmanager.Action;
import busmanager.Bus;
import busmanager.Depot;
import busmanager.Ticket;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DepotSolution extends Depot<BusSolution, TicketSolution> {
    private Set<BusSolution> buses = new HashSet<>();
    private LinkedList<Action<BusTicket>> auditLog = new LinkedList<>();

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
    public synchronized boolean boardBus(BusSolution bus, Set<TicketSolution> tickets) {
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
    }

    @Override
    public synchronized boolean transferTickets(BusSolution from, BusSolution to, Set<TicketSolution> tickets) {
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
    }

    @Override
    public synchronized boolean useTickets(BusSolution bus, Set<TicketSolution> tickets) {
        if(!bus.contents.containsAll(tickets))
            return false;

        for(TicketSolution ticket : tickets) {
            ticket.status = Ticket.Status.USED;
            BusTicket busTicket = new BusTicket(bus, ticket);
            auditLog.addLast(new Action<>(Action.Direction.USED, busTicket));
        }
        bus.contents.removeAll(tickets);

        return true;
    }

    @Override
    public synchronized boolean expireTickets(BusSolution bus, Set<TicketSolution> tickets) {
        if(!bus.contents.containsAll(tickets))
            return false;

        for(TicketSolution ticket : tickets) {
            ticket.status = Ticket.Status.EXPIRED;
            BusTicket busTicket = new BusTicket(bus, ticket);
            auditLog.addLast(new Action<>(Action.Direction.EXPIRED, busTicket));
        }
        bus.contents.removeAll(tickets);

        return true;
    }

    @Override
    public synchronized Set<TicketSolution> getTickets() {
        Set<TicketSolution> ret =  new HashSet<>();

        for(BusSolution bus : buses){
            ret.addAll(bus.contents);
        }
        return ret;
    }

    @Override
    public synchronized Set<TicketSolution> getTickets(BusSolution bus) {
        return new HashSet<>(bus.contents);
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
