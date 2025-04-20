package busmanager.solution;
import busmanager.Bus;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BusSolution implements Bus{
    int capacity;
    Set<TicketSolution> contents = new HashSet<>();
    ReadWriteLock busLock = new ReentrantReadWriteLock();
    final int id;
    public BusSolution(int capacity,int id) {

        this.capacity = capacity;
        this.id = id;
    }
}
