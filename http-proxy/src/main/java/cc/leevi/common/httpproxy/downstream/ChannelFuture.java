package cc.leevi.common.httpproxy.downstream;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChannelFuture<T> {

    private Lock lock = new ReentrantLock();

    private Condition done = lock.newCondition();

    private volatile T response;

    public void received(T response){
        lock.lock();
        this.response = response;
        done.signal();
        lock.unlock();
    }

    public boolean isDone() {
        return response != null;
    }

    public T get() throws InterruptedException, ExecutionException {
        lock.lock();
        if(!isDone()){
            done.await();
        }
        lock.unlock();
        return response;
    }
}
