package com.bsren.javaStd.blockingQueue;

import com.bsren.javaStd.collections.skipListTest;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyArrayBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, Serializable {

    private static final long serialVersionUID = -817911632652898426L;

    final Object[] items;

    int takeIndex;

    int putIndex;

    int count;

    final ReentrantLock lock;

    private final Condition notEmpty;

    private final Condition notFull;

    static final int inc(int i,int modulus){
        if(++i>=modulus){
            i = 0;
        }
        return i;
    }

    static final int dec(int i,int modulus){
        if(--i<0){
            i = modulus-1;
        }
        return i;
    }

    final E itemAt(int i){
        return (E)items[i];
    }

    static <E> E itemAt(Object[] items,int i){
        return (E) items[i];
    }

    /**
     * must hold lock, and lockCount = 1,and items[putIndex]=null
     */
    private void enqueue(E e){
        final Object[] items = this.items;
        items[putIndex] = e;
        if(++putIndex==items.length){
            putIndex=0;
        }
        count++;
        notEmpty.signal();
    }


    /**
     * must hold lock and lockCount = 1 and items[takeIndex]!=null
     */
    private E dequeue(){
        final Object[] items = this.items;
        E e = (E) items[takeIndex];
        items[takeIndex]=null;
        if(++takeIndex==items.length){
            takeIndex=0;
        }
        count--;
        notFull.signal();
        return e;
    }

    /**
     * assert lock.isHeldByCurrentThread();
     * assert lock.getHoldCount() == 1;
     * assert items[removeIndex] != null;
     * assert removeIndex >= 0 && removeIndex < items.length;
     */
    void removeAt(final int removeIndex){
        final Object[] items = this.items;
        if(removeIndex==takeIndex){
            items[takeIndex] = null;
            if(++takeIndex==items.length){
                takeIndex=0;
            }
            count--;
        }else {
            for (int i=removeIndex,putIndex = this.putIndex;;){
                int pre = i;
                if(++i==items.length){
                    i=0;
                }
                if(i==putIndex){
                    items[pre]=null;
                    this.putIndex = pre;
                    break;
                }
                items[pre] = items[i];
            }
        }
        notFull.signal();
    }

    public boolean remove(Object o){
        if(o==null){
            return false;
        }
        final Object[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if(count>0){
                final int putIndex = this.putIndex;
                int i = takeIndex;
                do {
                    if(o.equals(items[i])){
                        removeAt(i);
                        return true;
                    }
                    if(++i==items.length){
                        i=0;
                    }
                }while (i!=putIndex);
            }
            return false;
        }finally {
            lock.unlock();
        }
    }

    public boolean contains(Object o){
        if(o==null){
            throw new NullPointerException();
        }
        final ReentrantLock lock = this.lock;
        final Object[] items = this.items;
        lock.lock();
        try {
            if(count>0){
                final int putIndex = this.putIndex;
                int i = takeIndex;
                do {
                    if(items[i].equals(o)){
                        return true;
                    }
                    if(++i==items.length){
                        i=0;
                    }
                }while (i!=putIndex);
            }
            return false;
        }finally {
            lock.unlock();
        }
    }

    public MyArrayBlockingQueue(int capacity){
        this(capacity,false);
    }

    public MyArrayBlockingQueue(int capacity,boolean fair){
        if(capacity<=0){
            throw new IllegalArgumentException();
        }
        this.items = new Object[capacity];
        lock = new ReentrantLock(fair);
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
    }

    public MyArrayBlockingQueue(int capacity,boolean fair,Collection<? extends E>c){
        this(capacity,fair);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            final Object[] items = this.items;
            int i=0;
            try {
                for (E e:c){
                    if(e==null){
                        throw new NullPointerException();
                    }
                    items[i++] = e;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                throw new IllegalArgumentException();
            }
            count = i;
            putIndex = (i==capacity)?0:i;
        }finally {
            lock.unlock();
        }
    }


    /**
     * add本质还是调用的offer
     */
    public boolean add(E e){
        return super.add(e);
    }

    public boolean offer(E e){
        if(e==null){
            throw new NullPointerException();
        }
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if(count==items.length){
                return false;
            }else {
                enqueue(e);
                return true;
            }
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void put(E e) throws InterruptedException{
        if(e==null){
            throw new NullPointerException();
        }
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count==items.length){
                notFull.await();
            }
            enqueue(e);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        if(e==null){
            throw new NullPointerException();
        }
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count==items.length){
                if(nanos<0){
                    return false;
                }
                nanos = notFull.awaitNanos(nanos);
            }
            enqueue(e);
            return true;
        }finally {
            lock.unlock();
        }
    }

    public E poll(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if(count==0){
                return null;
            }
            return dequeue();
        }finally {
            lock.unlock();
        }
    }


    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count==0){
                if(nanos<0){
                    return null;
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            return dequeue();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public E peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return itemAt(takeIndex);
        }finally {
            lock.unlock();
        }
    }

    public int size(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return count;
        }finally {
            lock.unlock();
        }
    }

    public int remainingCapacity(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return items.length-count;
        }finally {
            lock.unlock();
        }
    }

    public Object[] toArray(){
        Object[] a;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            final int count = this.count;
            a = new Object[count];
            int n = items.length-takeIndex;
            if(count<=n){
                System.arraycopy(items,takeIndex,a,0,count);
            }else {
                System.arraycopy(items,takeIndex,a,0,n);
                System.arraycopy(items,0,a,n,count-n);
            }
            return a;
        }finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }





    @Override
    public E take() throws InterruptedException {
        return null;
    }


    @Override
    public int drainTo(Collection<? super E> c) {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        return 0;
    }



}
