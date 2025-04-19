package busmanager.solution;

import busmanager.Lock;

public class LockSolution extends Lock {
    @Override
    public void lock() {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public void unlock() {

    }

    @Override
    public boolean isReentered() {
        return false;
    }
}
