package busmanager.solution;

import busmanager.Lock;

import java.util.concurrent.atomic.AtomicBoolean;

public class LockSolution extends Lock {

    private AtomicBoolean lock = new AtomicBoolean(false);
    private volatile Thread owner = null;
    private int count = 0;

    @Override
    public void lock() {
        if(lock.get() && owner == Thread.currentThread()){
            count+=1;
            return;
        }
        while(lock.compareAndExchange(false, true) != false);

        count = 1;

        owner = Thread.currentThread();
    }

    @Override
    public boolean tryLock() {
        if(lock.get() && owner == Thread.currentThread()){
            count+=1;
            return true;
        }
        if(lock.compareAndExchange(false, true) != false)
            return false;

        owner = Thread.currentThread();
        return true;
    }

    @Override
    public void unlock() {
        if(!lock.get())
            throw  new IllegalMonitorStateException();

        if(owner != Thread.currentThread())
            throw new IllegalMonitorStateException();

        count -= 1;

        if(count == 0){
            owner = null;
            lock.set(false);
        }
    }

    @Override
    public boolean isReentered() {
        return count > 0;
    }
}
