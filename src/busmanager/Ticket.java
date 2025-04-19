package busmanager;

public interface Ticket {
    enum Status {ISSUED, IN_CIRCULATION, USED, EXPIRED}

    Status getStatus();
}
