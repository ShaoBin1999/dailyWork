package com.bsren.javaStd.blockingQueue;

import java.util.*;

/**
 * @param <E>
 */
public class MyPriorityQueue<E> extends AbstractQueue<E> {

    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    transient Object[] queue;

    int size;

    transient int modCount;

    private final Comparator<? super E> comparator;

    public MyPriorityQueue() {
        this(DEFAULT_INITIAL_CAPACITY, null);
    }


    @Override
    public int size() {
        return size;
    }

    public MyPriorityQueue(int initialCapacity) {
        this(initialCapacity, null);
    }

    public MyPriorityQueue(Comparator<? super E> comparator) {
        this(DEFAULT_INITIAL_CAPACITY, comparator);
    }

    public MyPriorityQueue(int initialCapacity,
                         Comparator<? super E> comparator) {
        // Note: This restriction of at least one is not actually needed,
        // but continues for 1.5 compatibility
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        this.queue = new Object[initialCapacity];
        this.comparator = comparator;
    }

    private void removeAt(int i){
        modCount++;
        int s=--size;
        if(s==i){
            queue[i] = null;
        }else {
            E moved = (E) queue[s];
            queue[s] = null;
            siftDown(i,moved);
            if(queue[i]==moved){
                siftUp(i,moved);
            }
        }
    }


    private void siftUp(int k,E x){
        if(comparator!=null){
            siftUpUsingComparator(k, x, queue, comparator);
        }else {
            siftUpComparable(k, x, queue);
        }
    }

    private static <T> void siftUpComparable(int k, T x, Object[] es) {
        Comparable<? super T> key = (Comparable<? super T>) x;
        while (k>0){
            int parent = (k-1)>>>1;
            Object e = es[parent];
            if(key.compareTo((T)e)>=0){
                break;
            }
            es[k] = e;
            k = parent;
        }
        es[k] = x;
    }

    private static <T> void siftUpUsingComparator(int k, T x, Object[] es,Comparator<? super T> comparator){
        while (k>0){
            int parent = (k-1)>>>1;
            Object e = es[parent];
            if(comparator.compare(x,(T)e)>=0){
                break;
            }
            es[k] = e;
            k = parent;
        }
        es[k] = x;
    }

    private void siftDown(int k,E x){
        if (comparator != null)
            siftDownUsingComparator(k, x, queue, size, comparator);
        else
            siftDownComparable(k, x, queue, size);
    }

    private static <T> void siftDownComparable(int k,T x,Object[] es,int size){
        int half = size>>>1;
        Comparable<? super T> key = (Comparable<? super T>) x;
        while (k<half){
            int child = (k<<1)+1;
            Object c = es[child];
            int right = child+1;
            if(right<size && ((Comparable<? super T>)c).compareTo((T)es[right])>0){
                c = es[right];
                child = right;
            }
            if(key.compareTo((T)c)<=0){
                break;
            }
            es[k] = c;
            k = child;
        }
        es[k] = x;
    }

    private static <T> void siftDownUsingComparator(int k,T x,Object[] es,int size,Comparator<? super T> cmp){
        int half = size>>>1;
        while (k<half){
            int child = (k<<1)+1;
            Object c = es[child];
            int right = child+1;
            if(right<size && cmp.compare((T)c,(T)es[right])>0){
                c = es[right];
                child = right;
            }
            if(cmp.compare(x,(T)c)<=0){
                break;
            }
            es[k] = c;
            k = child;
        }
        es[k] = x;
    }

    private void heapify(){
        for (int i=(size>>>1)-1;i>=0;i--){
            siftDown(i,(E) queue[i]);
        }
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private void grow() throws Exception {
        int old = queue.length;
        int newCapacity = old+((old<64)?(old+2):(old>>1));
        if(newCapacity<0 || newCapacity>MAX_ARRAY_SIZE){
            throw new Exception("element more than max capacity");
        }
        queue = Arrays.copyOf(queue,newCapacity);
    }


    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public boolean add(E e) {
        return offer(e);
    }

    @Override
    public boolean offer(E e) {
        if(e==null){
            throw new NullPointerException();
        }
        modCount++;
        int i = size;
        if(i>=queue.length){
            try {
                grow();
            } catch (Exception ex) {
                return false;
            }
        }
        size=i+1;
        if(i==0){
            queue[0]=e;
        }else {
            siftUp(i,e);
        }
        return true;
    }

    @Override
    public E poll() {
        return null;
    }

    @Override
    public E peek() {
        if(size==0){
            return null;
        }
        return (E) queue[0];
    }

    private int indexOf(Object o){
        if(o!=null){
            for (int i=0;i<size;i++){
                if(queue[i].equals(o)){
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean remove(Object o) {
        int i = indexOf(o);
        if(i==-1){
            return false;
        }else {
            removeAt(i);
            return true;
        }
    }

    boolean removeEq(Object o) {
        for (int i = 0; i < size; i++) {
            if (o == queue[i]) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o)!=-1;
    }

    public Object[] toArray() {
        return Arrays.copyOf(queue, size);
    }

    public <T> T[] toArray(T[] a) {
        final int size = this.size;
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(queue, size, a.getClass());
        System.arraycopy(queue, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    public void clear() {
        modCount++;
        for (int i = 0; i < size; i++)
            queue[i] = null;
        size = 0;
    }



     @Override
     public Iterator<E> iterator() {
         return null;
     }

}
