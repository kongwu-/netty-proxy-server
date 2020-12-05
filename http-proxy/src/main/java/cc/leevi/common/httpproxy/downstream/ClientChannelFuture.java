package cc.leevi.common.httpproxy.downstream;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientChannelFuture {
    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();
    private volatile long start;

    private volatile Object response;

    public Object get(int timeout){
        if(!isDone()){
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                done.await(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                lock.unlock();
            }
        }
        return response;

    }

    public void received(Object response){
        lock.lock();
        try {
            this.response = response;
            done.signal();
        }finally {
            lock.unlock();
        }
    }

    public boolean isDone() {
        return response != null;
    }
}
