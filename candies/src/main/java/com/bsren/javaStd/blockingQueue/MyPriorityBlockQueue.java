package com.bsren.javaStd.blockingQueue;
import com.jayway.jsonpath.spi.json.TapestryJsonProvider;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 实现目标：
 * 1. 所添加对象实现comparable接口
 * 2. 根据所添加对象调整小顶堆的结构，将小的放在最上面
 * 3. 实现正常的添加删除，以及阻塞的take方法，需要实现扩容，必须线程安全，不得越界
 * 4. 需实现iterator
 * 5. 需实现drain to方法
 * @param <E>
 */
public class MyPriorityBlockQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> {

    private Object[] queue;

    private int size;

    private final ReentrantLock lock;

    private final Condition notEmpty;

    public MyPriorityBlockQueue(int initialCapacity){
        if(initialCapacity<1){
            throw new IllegalArgumentException();
        }
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.queue = new Object[initialCapacity];
    }

    public static void main(String[] args) throws InterruptedException {
        MyPriorityBlockQueue<Integer> myPriorityBlockQueue = new MyPriorityBlockQueue<>(10);
        myPriorityBlockQueue.offer(1);
        myPriorityBlockQueue.offer(2);
        myPriorityBlockQueue.offer(0);
        System.out.println(myPriorityBlockQueue.poll());
        System.out.println(myPriorityBlockQueue.poll());
        System.out.println(myPriorityBlockQueue.poll());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("not need to wait 10s and get the return: "+myPriorityBlockQueue.poll(10,TimeUnit.SECONDS));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                myPriorityBlockQueue.offer(2);
                myPriorityBlockQueue.offer(4);
            }
        }).start();
        Thread.sleep(1000);
        System.out.println(myPriorityBlockQueue.poll());
    }

    private static <T> void siftUp(int k,T x,Object[] array){
        Comparable<? super T> key = (Comparable<? super T>) x;
        while (k>0){
            int parent = (k-1)>>>1;
            Object o = array[parent];
            if(key.compareTo((T)o)>=0){
                break;
            }
            array[k] = o;
            k = parent;
        }
        array[k] = key;
    }

    private static <T> void siftDown(int k,T x,Object[] array,int n){
        if(n<=0){
            return;
        }
        Comparable<? super T> key =  (Comparable<? super T>)x;
        int half = n>>>1;
        while (k<half){
            int child = (k<<1)+1;
            Object c = array[child];
            int right = child+1;
            if(right<n && ((Comparable<? super T>)c).compareTo((T)array[right])>0){
                child = right;
                c = array[child];
            }
            if (key.compareTo((T) c) <= 0)
                break;
            array[k] = c;
            k = child;
        }
        array[k] = key;
    }

    private void heapify(){
        Object[] array = this.queue;
        int n = size;
        int half = (size>>>1)-1;
        for (int i=half;i>=0;i--){
            siftDown(i,array[i],array,n);
        }
    }

    /**
     * 1.首先获取锁
     * 2.判断是否扩容
     * 3.添加到小顶堆中
     * 4.后续处理：size++，notEmpty的signal方法
     */
    @Override
    public boolean offer(E e) {
        if(e==null){
            throw new NullPointerException();
        }
        ReentrantLock lock = this.lock;
        lock.lock();
        int n, cap;
        Object[] array;
        //这里需要用循环，因为tryGrow可能会失败
        while ((n = size) >= (cap = (array = queue).length))
            tryGrow(array, cap);
        try {
            siftUp(n,e,array);
            size=n+1;
            notEmpty.signal();
        }finally {
            lock.unlock();
        }
        return true;
    }

    private void tryGrow(Object[] array, int oldCap) {
        int newCap;
        Object[] newArray = null;
        if(oldCap<64){
            newCap = oldCap<<1+2;
        }else {
            newCap = oldCap+oldCap>>1;
        }
        if(newCap<0){
            newCap = Integer.MAX_VALUE;
        }
        newArray = new Object[newCap];
        queue = newArray;
        System.arraycopy(array,0,newArray,0,oldCap);
    }

    /**
     * called while holding lock
     */
    private E dequeue(){
        int n = size-1;
        if(n<0){
            return null;
        }else {
            Object[] array = this.queue;
            E result = (E) array[0];
            E x = (E)array[n];
            array[n] = null;
            siftDown(0,x,array,n);
            size = n;
            return result;
        }
    }

    /**
     * 利用equals方法去寻找object，如果找不到则返回-1
     */
    private int indexOf(Object o){
        if(o!=null){
            Object[] array = queue;
            int n = size;
            for (int i = 0; i < size; i++) {
                if(array[i].equals(o)){
                    return i;
                }
            }
        }
        return -1;
    }

    private void removeAt(int i){
        Object[] array = this.queue;
        int n = size-1;
        if(n==i){
            array[n]=null;
        }else {
            E moved = (E)array[n];
            array[n] = null;
            siftDown(i,moved,array,n);
            if(array[i]==moved){
                siftUp(i,moved,array);
            }
        }
        size = n;
    }

    public boolean remove(Object o){
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int i = indexOf(o);
            if(i!=-1){
                removeAt(i);
                return true;
            }
            return false;
        }finally {
            lock.unlock();
        }
    }

    void removeEQ(Object o){
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] array = this.queue;
            for (int i = 0; i < array.length; i++) {
                if(array[i]==o){
                    removeAt(i);
                    break;
                }
            }
        }finally {
            lock.unlock();
        }
    }

    public boolean contains(Object o){
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return indexOf(o)!=-1;
        }finally {
            lock.unlock();
        }
    }

    public Object[] toArray(){
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return Arrays.copyOf(queue,size);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(E e) throws InterruptedException {
        offer(e);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return offer(e);
    }

    @Override
    public E take() throws InterruptedException {
        ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        E result;
        try {
            while ((result=dequeue())==null){
                notEmpty.await();
            }
        }finally {
            lock.unlock();
        }
        return result;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        ReentrantLock lock = this.lock;
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

    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return drainTo(c,Integer.MAX_VALUE);
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        if(c==null){
            throw new NullPointerException();
        }
        if(c==this){
            throw new IllegalArgumentException();
        }
        if(maxElements<=0){
            return 0;
        }
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = Math.max(maxElements,size);
            for (int i=0;i<n;i++){
                c.add((E)queue[0]);
                dequeue();
            }
            return n;
        }finally {
            lock.unlock();
        }
    }

    @Override
    public E poll() {
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return dequeue();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public E peek() {
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return (size==0)?null:(E)queue[0];
        }finally {
            lock.unlock();
        }
    }


    @Override
    public Iterator<E> iterator() {
        return new Itr(toArray());
    }

    final class Itr implements Iterator<E>{

        final Object[] array;
        int cursor;
        int lastRet;

        Itr(Object[] array){
            this.array = array;
            lastRet = -1;
        }

        @Override
        public boolean hasNext() {
            return cursor<array.length;
        }

        @Override
        public E next() {
            if(cursor>=array.length){
                throw new NoSuchElementException();
            }
            lastRet = cursor;
            return (E)array[cursor++];
        }

        public void remove(){
            if(lastRet<0){
                throw new IllegalStateException();
            }
            removeEQ(array[lastRet]);
            lastRet = -1;
        }
    }
}
