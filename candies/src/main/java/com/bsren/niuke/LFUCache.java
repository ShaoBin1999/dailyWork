package com.bsren.niuke;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class Nod{
    int key;
    int value;
    int cont;
    int time;

    public Nod(int key, int value,int time) {
        this.key = key;
        this.value = value;
        this.cont = 1;
        this.time = time;
    }

}
public class LFUCache {

    Map<Integer,Nod> map = new HashMap<>();
    PriorityQueue<Nod> p = new PriorityQueue<>(new Comparator<Nod>() {
        @Override
        public int compare(Nod o1, Nod o2) {
            if(o1.cont==o2.cont){
                return o1.time-o2.time;
            }else {
                return o1.cont-o2.cont;
            }
        }
    });
    int capacity;
    int time;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        time = 0;
    }

    public int get(int key) {
        Nod nod = map.get(key);
        if(nod==null){
            return -1;
        }
        p.remove(nod);
        nod.cont++;
        nod.time = time++;
        p.add(nod);
        return nod.value;
    }

    public void put(int key, int value) {
        Nod nod = map.get(key);
        if(nod!=null){
            p.remove(nod);
            nod.value = value;
            nod.cont++;
            nod.time = time++;
            p.add(nod);
            return;
        }
        if(map.size()>=this.capacity){
            Nod poll = p.poll();
            if(poll!=null) map.remove(poll.key);
        }
        if(map.size()<this.capacity){
            nod = new Nod(key,value,time++);
            map.put(key,nod);
            p.add(nod);
        }

    }
}
