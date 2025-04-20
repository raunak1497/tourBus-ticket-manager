package busmanager.solution;

import busmanager.Depot;
import busmanager.Ticket;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DepotSolution extends Depot<BusSolution, TicketSolution> {
    private List<BusSolution> buses = new LinkedList<>();
    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public BusSolution createBus(int capacity) {
        BusSolution ret =  new BusSolution(capacity,counter.incrementAndGet());
        buses.add(ret);
        return ret;
    }

    @Override
    public TicketSolution issueTicket(int id) {
        return new TicketSolution(this);
    }

    @Override
    public boolean boardBus(BusSolution bus, Set<TicketSolution> tickets) {
        bus.busLock.writeLock().lock();
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
            bus.busLock.writeLock().unlock();
        }

    }

    @Override
    public boolean transferTickets(BusSolution from, BusSolution to, Set<TicketSolution> tickets) {
        BusSolution first,second;

        if(from.id < to.id){
            first = from;
            second = to;
        }else if(from.id > to.id){
            first = to;
            second = from;
        }else{
            return true;
        }

        first.busLock.writeLock().lock();
        second.busLock.writeLock().lock();
        try{
            if(!from.contents.containsAll(tickets))
                return false;

            if(tickets.size() + to.contents.size() > to.capacity)
                return false;

            from.contents.removeAll(tickets);
            to.contents.addAll(tickets);

            return true;
        }finally {
            from.busLock.writeLock().unlock();
            to.busLock.writeLock().unlock();
        }

    }

    @Override
    public boolean useTickets(BusSolution bus, Set<TicketSolution> tickets) {
        bus.busLock.writeLock().lock();
        try{
            if(!bus.contents.containsAll(tickets))
                return false;

            for(TicketSolution ticket : tickets) {
                ticket.status = Ticket.Status.USED;
            }
            bus.contents.removeAll(tickets);

            return true;
        }finally {
            bus.busLock.writeLock().unlock();
        }

    }

    @Override
    public boolean expireTickets(BusSolution bus, Set<TicketSolution> tickets) {
        bus.busLock.writeLock().lock();
        try{
            if(!bus.contents.containsAll(tickets))
                return false;

            for(TicketSolution ticket : tickets) {
                ticket.status = Ticket.Status.EXPIRED;
            }
            bus.contents.removeAll(tickets);

            return true;
        }finally {
            bus.busLock.writeLock().unlock();
        }

    }

    @Override
    public Set<TicketSolution> getTickets() {
        return  getTickets(buses);
    }

    @Override
    public Set<TicketSolution> getTickets(BusSolution bus) {
        bus.busLock.readLock().lock();
        try{
            return new HashSet<>(bus.contents);
        }finally{
            bus.busLock.readLock().unlock();
        }

    }

    @Override
    public Set<TicketSolution> getTickets(List<BusSolution> buses) {
        Set<TicketSolution> ret =  new HashSet<>();

        //to avoid deadlock we must sort at to have some order
        LinkedList<BusSolution> sorted = new LinkedList<>(buses);
        Collections.sort(sorted,(BusSolution bus1, BusSolution bus2) -> {
            if(bus1.id < bus2.id){
                return -1;
            }else if(bus1.id > bus2.id){
               return 1;
            }else{
                return 0;
            }
        });

        //lock all buses
        for(BusSolution bus : sorted)
            bus.busLock.readLock().lock();

        try{
                //get all tickets
            for(BusSolution bus : buses)
                ret.addAll(bus.contents);
        }finally {
            //unlock all buses
            for(BusSolution bus : buses)
                bus.busLock.readLock().unlock();
       }

        return ret;
    }
}