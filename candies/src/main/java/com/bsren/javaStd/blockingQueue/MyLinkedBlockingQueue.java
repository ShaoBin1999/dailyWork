package com.bsren.javaStd.blockingQueue;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyLinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> , Serializable {

    private static final long serialVersionUID = -6903933977591709194L;

    static class Node<E>{
        E item;

        Node<E> next;

        Node(E e){
            item = e;
        }
    }

    private final int capacity;

    private final AtomicInteger count = new AtomicInteger(0);

    //head 的 item总是为null
    transient Node<E> head;

    private transient Node<E> last;

    private final ReentrantLock takeLock = new ReentrantLock();

    private final Condition notEmpty = takeLock.newCondition();

    private final ReentrantLock putLock = new ReentrantLock();

    private final Condition notFull = putLock.newCondition();

    private void signalNotEmpty(){
        ReentrantLock lock = this.takeLock;
        lock.lock();
        try {
            notEmpty.signal();
        }finally {
            lock.unlock();
        }
    }

    private void signalNotFull(){
        ReentrantLock lock = this.putLock;
        lock.lock();
        try {
            notFull.signal();
        }finally {
            lock.unlock();
        }
    }

    /**
     * must hold lock and notFull
     */
    private void enqueue(Node<E> node){
        last.next = node;
        last = node;
    }

    /**
     * must hold lock and notEmpty
     * 头节点始终在变，每插入一次，头节点就往后移动
     */
    private E dequeue(){
        Node<E> h = this.head;
        Node<E> first = h.next;
        h.next = null;
        head = first;
        E e = first.item;
        first.item = null;
        return e;
    }

    void fullyLock(){
        putLock.lock();
        takeLock.lock();
    }

    void fullyUnlock(){
        putLock.unlock();
        takeLock.unlock();
    }

    public MyLinkedBlockingQueue(){
        this(Integer.MAX_VALUE);
    }

    public MyLinkedBlockingQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
        last = head = new Node<E>(null);
    }

    /**
     * c中的元素不能为null，否则抛出异常
     * c中元素数量不能超过容量，否则抛出异常
     */
    public MyLinkedBlockingQueue(Collection<? extends E> c){
        this(Integer.MAX_VALUE);
        final ReentrantLock lock = this.putLock;
        lock.lock();
        try {
            int n = 0;
            for (E e:c){
                if(e==null){
                    throw new NullPointerException();
                }
                if(n==capacity){
                    throw new IllegalStateException();
                }
                enqueue(new Node<>(e));
                n++;
            }
            count.set(n);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        return count.get();
    }

    public int remainingCapacity(){
        return this.capacity-count.get();
    }

    @Override
    public void put(E e) throws InterruptedException {
        if(e==null){
            throw new NullPointerException();
        }
        int c = -1;
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            while (count.get()==this.capacity){
                notFull.await();
            }
            enqueue(new Node<>(e));
            c = count.getAndIncrement();
            if(c+1<this.capacity){
                notFull.signal();
            }
        }finally {
            putLock.unlock();
        }
        if(c==0){
            signalNotEmpty();
        }
    }


    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        if(e==null){
            throw new NullPointerException();
        }
        long nanos = unit.toNanos(timeout);
        int c = -1;
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            while (count.get()==capacity){
                if(nanos<=0){
                    return false;
                }
                nanos = notFull.awaitNanos(nanos);
            }
            enqueue(new Node<>(e));
            c = count.getAndIncrement();
            if(c+1<this.capacity){
                notFull.signal();
            }
        }finally {
            putLock.unlock();
        }
        if(c==0){
            signalNotEmpty();
        }
        return true;
    }

    public boolean offer(E e){
        if(e==null){
            throw new NullPointerException();
        }
        final AtomicInteger count = this.count;
        if(count.get()==capacity){
            return false;
        }
        int c = -1;
        Node<E> node = new Node<>(e);
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            if(count.get()<capacity){
                enqueue(node);
                c = count.getAndIncrement();
                if(c+1<capacity){
                    notFull.signal();
                }
            }
        }finally {
            putLock.unlock();
        }
        if(c==0){
            signalNotEmpty();
        }
        return c>=0;
    }

    @Override
    public E take() throws InterruptedException {
        E x;
        int c = -1;
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            while (count.get()==0){
                notEmpty.await();
            }
            x = dequeue();
            c = count.getAndDecrement();
            if(c>1){
                notEmpty.signal();
            }
        }finally {
            takeLock.unlock();
        }
        if(c==capacity){
            signalNotFull();
        }
        return x;
    }


    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E x;
        int c = -1;
        long nanos = unit.toNanos(timeout);
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            while (count.get()==0){
                if(nanos<=0){
                    return null;
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            x = dequeue();
            c = count.getAndIncrement();
            if(c>1){
                notEmpty.signal();
            }
        }finally {
            takeLock.unlock();
        }
        if(c==capacity){
            signalNotFull();
        }
        return x;
    }


    @Override
    public E poll() {
        final AtomicInteger count = this.count;
        if(count.get()==0){
            return null;
        }
        E x = null;
        int c = -1;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            if(count.get()>0){  //double check
                x = dequeue();
                c = count.getAndDecrement();
                if(c>1){
                    notEmpty.signal();
                }
            }
        }finally {
            takeLock.unlock();
        }
        if(c==capacity){
            signalNotFull();
        }
        return x;
    }


    @Override
    public E peek() {
        if(count.get()==0){
            return null;
        }
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            Node<E> next = head.next;
            if(next==null){
                return null;
            }
            else return next.item;
        }finally {
            takeLock.unlock();
        }
    }

    /**
     * assert fully locked
     * unlink p with predecessor trail
     */
    void unlink(Node<E> p,Node<E> trail){
        p.item = null;
        trail.next = p.next;
        if(last==p){
            last = trail;
        }
        if(count.getAndDecrement()==capacity){
            notFull.signal();
        }
    }

    public boolean contains(Object o){
        if(o==null){
            throw new NullPointerException();
        }
        fullyLock();
        try {
            for (Node<E> p = head.next;p!=null;p = p.next){
                if(o.equals(p.item)){
                    return true;
                }
            }
            return false;
        }finally {
            fullyUnlock();
        }
    }

    public boolean remove(Object o){
        if(o==null){
            return false;
        }
        fullyLock();
        try {
            for (Node<E> trail = head,p = trail.next;
                 p!=null;
                 trail=p,p = p.next){
                if(o.equals(p)){
                    unlink(p,trail);
                    return true;
                }
            }
            return false;
        }finally {
            fullyUnlock();
        }
    }

    public Object[] toArray(){
        fullyLock();
        try {
            int size = count.get();
            Object[] a = new Object[size];
            int k = 0;
            for (Node<E> p = head.next;p !=null;p = p.next){
                a[k++] = p.item;
            }
            return a;
        }finally {
            fullyUnlock();
        }
    }

    public <T> T[] toArray(T[] a) {
        fullyLock();
        try {
            int size = count.get();
            if (a.length < size)
                a = (T[])java.lang.reflect.Array.newInstance
                        (a.getClass().getComponentType(), size);

            int k = 0;
            for (Node<E> p = head.next; p != null; p = p.next)
                a[k++] = (T)p.item;
            if (a.length > k)
                a[k] = null;
            return a;
        } finally {
            fullyUnlock();
        }
    }

    public String toString() {
        fullyLock();
        try {
            Node<E> p = head.next;
            if (p == null)
                return "[]";

            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (;;) {
                E e = p.item;
                sb.append(e == this ? "(this Collection)" : e);
                p = p.next;
                if (p == null)
                    return sb.append(']').toString();
                sb.append(',').append(' ');
            }
        } finally {
            fullyUnlock();
        }
    }

    public void clear(){
        fullyLock();
        try {
            for (Node<E> p, h = head; (p = h.next) != null; h = p) {
                h.next = h;
                p.item = null;
            }
            head = last;
            if (count.getAndSet(0) == capacity)
                notFull.signal();
        }finally {
            fullyUnlock();
        }
    }


    @Override
    public Iterator<E> iterator() {
        return null;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        fullyLock();
        try {
            s.defaultWriteObject();
            for (Node<E> p = head.next;p!=null;p=p.next){
                s.writeObject(p.item);
            }
            s.writeObject(null);
        }finally {
            fullyUnlock();
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        count.set(0);
        last = head = new Node<>(null);
        for (;;){
            E item = (E)s.readObject();
            if(item==null){
                return;
            }
            add(item);
        }
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
