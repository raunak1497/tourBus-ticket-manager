package busmanager.solution;
import busmanager.Bus;

import java.util.HashSet;
import java.util.Set;

public class BusSolution implements Bus{
    int capacity;
    Set<TicketSolution> contents = new HashSet<>();

    public BusSolution(int capacity) {
        this.capacity = capacity;
    }
}
