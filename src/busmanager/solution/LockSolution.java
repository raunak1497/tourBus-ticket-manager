package busmanager.solution;

import busmanager.Lock;

public class LockSolution extends Lock {

    private boolean lock = false;
    @Override
    public void lock() {
        while(lock == true);

        lock = true;
    }

    @Override
    public boolean tryLock() {
        if(lock == true)
            return false;

        lock = true;
        return true;
    }

    @Override
    public void unlock() {
        if(lock == false )
            throw  new IllegalMonitorStateException();
        lock = false;
    }

    @Override
    public boolean isReentered() {
        throw new Error("Not implemented yet");
    }
}
