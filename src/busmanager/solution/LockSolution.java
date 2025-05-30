package busmanager.solution;

import busmanager.Lock;

import java.util.concurrent.atomic.AtomicBoolean;

public class LockSolution extends Lock {

    private AtomicBoolean lock = new AtomicBoolean(false);
    private volatile Thread owner = null; //volatile to ensure no data-race
    private volatile int count = 0; //volatile to ensure no data-race
    private static  void highPrecisionSleep(long millis){
        long currentTime = System.currentTimeMillis();

        while(System.currentTimeMillis() - currentTime < millis);
    }

    @Override
    public void lock() {
        if(lock.get() && owner == Thread.currentThread()){
            count+=1;
            return;
        }
        long backoff = 10;

        while(true){
            while(lock.get());

            if(lock.compareAndExchange(false, true) == false)
                break;

            highPrecisionSleep(backoff);
            backoff *= 1.5;
        }


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
        return count > 1; //only need to re-enter when we have accquired lock more than once
    }
}
