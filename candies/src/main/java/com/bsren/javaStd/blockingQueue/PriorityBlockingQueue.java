package com.bsren.javaStd.blockingQueue;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PriorityBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>{

    private static final long serialVersionUID = 5595510919245408276L;

    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE-8;

    private transient  Object[] queue;

    private transient int size;

    private transient Comparator<? super E> comparator;

    private final ReentrantLock lock;

    private final Condition notEmpty;

    private transient volatile int allocationSpinLock;

    public PriorityBlockingQueue(int initialCapacity,
                                 Comparator<? super E> comparator) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.comparator = comparator;
        this.queue = new Object[initialCapacity];
    }

    private void tryGrow(Object[] array,int oldCap){
        lock.unlock();  //也许计算新cap并不算得上一个需要锁的操作，所以这里释放掉锁，用一个cas自旋锁来代替
        Object[] newArray = null;
        if(allocationSpinLock==0 && UNSAFE.compareAndSwapInt(this,allocationSpinLockOffset,0,1)){
            try {
                int newCap;
                if(oldCap<64){
                    newCap = oldCap+oldCap+2;
                }else {
                    newCap = oldCap+oldCap>>1;
                }
                if(newCap-MAX_ARRAY_SIZE>0){
                    int minCap = oldCap+1;
                    if(minCap<0 || minCap>MAX_ARRAY_SIZE){
                        throw new OutOfMemoryError();
                    }
                    newCap = MAX_ARRAY_SIZE;
                }
                if(newCap>oldCap && queue==array){
                    newArray = new Object[newCap];
                }
            }finally {
                allocationSpinLock = 0;
            }
        }
        if(newArray==null){
            Thread.yield();
        }
        lock.lock();
        if(newArray!=null && queue==array){
            queue = newArray;
            System.arraycopy(array, 0, newArray, 0, oldCap);
        }
    }

    /**
     * hold lock, poll()的时候用
     */
    private E dequeue(){
        int n = size-1;
        if(n<0){
            return null;
        }
        Object[] array = queue;
        E result = (E) array[0];
        E x = (E) array[n];
        array[n]= null;
        Comparator<? super E> cmp = comparator;
        if(cmp==null){
            siftDownComparable(0,x,array,n);
        }else {
            siftDownUsingComparator(0,x,array,n,cmp);
        }
        size = n;
        return result;
    }

    /**
     * insert x at position k
     */
    private static <T> void siftUpComparable(int k,T x,Object[] array){
        Comparable<? super T> key = (Comparable<? super T>) x;
        while (k>0){
            int parent = (k-1)>>>1;
            Object e = array[parent];
            if(key.compareTo((T)e)>=0){
                break;
            }
            array[k] = e;
            k = parent;
        }
        array[k] = key;
    }

    private static <T> void siftUpUsingComparator(int k,T x,Object[] array, Comparator<? super T> comparator){
        while (k>0){
            int parent = (k-1)>>>1;
            Object e = array[parent];
            if(comparator.compare(x,(T)e)>=0){
                break;
            }
            array[k] = e;
            k = parent;
        }
        array[k] = x;
    }

    /**
     * insert x at k, move down until it is a leaf or no more than its children
     * n: heap size
     */
    private static <T> void siftDownComparable(int k,T x,Object[] array,int n){
        if(n>0){
            Comparable<? super T> key = (Comparable<? super T>) x;
            int half = n>>>1;
            while (k<half){
                int child = (k<<1)+1;
                Object c = array[child];
                int right = child+1;
                if(right<n && ((Comparable<? super T>) c).compareTo((T) array[right]) > 0){
                    c = array[right];
                    child = right;
                }
                if(key.compareTo((T)c)<=0){
                    break;
                }
                array[k] = c;
                k = child;
            }
            array[k] = key;
        }
    }

    private static <T> void siftDownUsingComparator(int k,T x,Object[] array,int n,
                                                    Comparator<? super T> cmp){
        if(n>0){
            int half = n>>>1;
            while (k<half){
                int child = k>>1+1;
                Object c = array[child];
                int right = child+1;
                if(right<n && cmp.compare((T)c,(T)array[right])>0){
                    child = right;
                    c = array[right];
                }
                if(cmp.compare(x,(T)c)<=0){
                    break;
                }
                array[k] = c;
                k = child;
            }
            array[k] = x;
        }
    }

    /**
     * 从array的一半处截取前半部分，并从后往前依次插入
     */
    private void heapify(){
        Object[] array = queue;
        int n = size;
        int half = (n>>>1)-1;
        Comparator<? super E> cmp = comparator;
        if(cmp==null){
            for (int i=half;i>=0;i--){
                siftDownComparable(i, (E) array[i], array, n);
            }
        }
        else {
            for (int i = half; i >= 0; i--)
                siftDownUsingComparator(i, (E) array[i], array, n, cmp);
        }
    }

    public boolean add(E e) {
        return offer(e);
    }


    @Override
    public boolean offer(E e) {
        if(e==null){
            throw new NullPointerException();
        }
        final ReentrantLock lock = this.lock;
        lock.lock();
        Object[] array;
        int n, cap;
        while ((n = size) >= (cap = (array = queue).length))
            tryGrow(array, cap);
        try {
            Comparator<? super E> cmp = comparator;
            if(cmp==null){
                siftUpComparable(n,e,array);
            }else {
                siftUpUsingComparator(n,e,array,cmp);
            }
            size=n+1;
            notEmpty.signal();
        }finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public void put(E e) throws InterruptedException {
        offer(e);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return offer(e);
    }

    public E poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        E result;
        try {
            while ( (result = dequeue()) == null)
                notEmpty.await();
        } finally {
            lock.unlock();
        }
        return result;
    }

    public E poll(long timeout,TimeUnit unit) throws InterruptedException{
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        E result;
        try {
            while ((result=dequeue())==null && nanos>0){
                nanos = notEmpty.awaitNanos(nanos);
            }
        }finally {
            lock.unlock();
        }
        return result;
    }

    public E peek(){
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return (size==0? null:(E)queue[0]);
        }finally {
            lock.unlock();
        }
    }

    public Comparator<? super E> comparator(){
        return comparator;
    }

    public int size(){
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return size;
        }finally {
            lock.unlock();
        }
    }

    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    private int indexOf(Object o) {
        if (o != null) {
            Object[] array = queue;
            int n = size;
            for (int i = 0; i < n; i++)
                if (o.equals(array[i]))
                    return i;
        }
        return -1;
    }

    private void removeAt(int i){
        Object[] array = queue;
        int n = size-1;
        if(n==i){
            array[i]=null;
        }else {
            E moved = (E) array[n];
            array[n] = null;
            Comparator<? super E> cmp = comparator;
            if(cmp==null){
                siftDownComparable(i,moved,array,n);
            }else {
                siftDownUsingComparator(i,moved,array,n,cmp);
            }
            if(array[i]==moved){
                if(cmp==null){
                    siftUpComparable(i,moved,array);
                }else {
                    siftUpUsingComparator(i, moved, array, cmp);
                }
            }
        }
        size = n;
    }

    public boolean remove(Object o) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int i = indexOf(o);
            if (i == -1)
                return false;
            removeAt(i);
            return true;
        } finally {
            lock.unlock();
        }
    }


    public boolean contains(Object o) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return indexOf(o) != -1;
        } finally {
            lock.unlock();
        }
    }


    @Override
    public Iterator<E> iterator() {
        return null;
    }

    public Object[] toArray() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return Arrays.copyOf(queue, size);
        } finally {
            lock.unlock();
        }
    }

    public String toString() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = size;
            if (n == 0)
                return "[]";
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (int i = 0; i < n; ++i) {
                Object e = queue[i];
                sb.append(e == this ? "(this Collection)" : e);
                if (i != n - 1)
                    sb.append(',').append(' ');
            }
            return sb.append(']').toString();
        } finally {
            lock.unlock();
        }
    }



    public int drainTo(Collection<? super E> c) {
        return drainTo(c, Integer.MAX_VALUE);
    }

    public int drainTo(Collection<? super E> c, int maxElements) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        if (maxElements <= 0)
            return 0;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = Math.min(size, maxElements);
            for (int i = 0; i < n; i++) {
                c.add((E) queue[0]); // In this order, in case add() throws.
                dequeue();
            }
            return n;
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] array = queue;
            int n = size;
            size = 0;
            for (int i = 0; i < n; i++)
                array[i] = null;
        } finally {
            lock.unlock();
        }
    }

    public <T> T[] toArray(T[] a) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = size;
            if (a.length < n)
                // Make a new array of a's runtime type, but my contents:
                return (T[]) Arrays.copyOf(queue, size, a.getClass());
            System.arraycopy(queue, 0, a, 0, n);
            if (a.length > n)
                a[n] = null;
            return a;
        } finally {
            lock.unlock();
        }
    }




    // Unsafe mechanics
    private static final sun.misc.Unsafe UNSAFE;
    private static final long allocationSpinLockOffset;
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> k = java.util.concurrent.PriorityBlockingQueue.class;
            allocationSpinLockOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("allocationSpinLock"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
