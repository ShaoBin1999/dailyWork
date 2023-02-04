package com.bsren.javaStd.condition;

import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionConsumerProducer {

    private int queueSize = 10;
    private PriorityQueue<Integer> queue = new PriorityQueue<>(queueSize);
    private Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    public static void main(String[] args) throws InterruptedException  {
        ConditionConsumerProducer test = new ConditionConsumerProducer();
        Producer producer = test.new Producer();
        Consumer consumer = test.new Consumer();
        consumer.start();
        producer.start();
    }

    class Consumer extends Thread{
        @Override
        public void run() {
            consume();
        }
        volatile boolean flag=true;
        private void consume() {
            while(flag){
                lock.lock();
                try {
                    while(queue.isEmpty()){
                        try {
                            System.out.println("队列空，等待数据");
                            notEmpty.await();
                        } catch (InterruptedException e) {flag =false;}
                    }
                    Thread.sleep(500);
                    queue.poll();                //每次移走队首元素
                    notFull.signal();
                    System.out.println("从队列取走一个元素，队列剩余"+queue.size()+"个元素");
                } catch (InterruptedException e) {e.printStackTrace();}
                finally{
                    lock.unlock();
                }
            }
        }
    }

    class Producer extends Thread{
        @Override
        public void run() {
            produce();
        }
        volatile boolean flag=true;
        private void produce() {
            while(flag){
                lock.lock();
                try {
                    while(queue.size() == queueSize){
                        try {
                            System.out.println("队列满，等待有空余空间");
                            notFull.await();
                        } catch (InterruptedException e) {flag =false;}
                    }
                    Thread.sleep(500);
                    queue.offer(1);        //每次插入一个元素
                    notEmpty.signal();
                    System.out.println("向队列取中插入一个元素，队列剩余空间："+(queueSize-queue.size()));
                } catch (InterruptedException e) {e.printStackTrace();}
                finally{
                    lock.unlock();
                }
            }
        }
    }
}