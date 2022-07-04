package com.bsren.demo;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class Producer_Sync {
    public static void main(String[] args) throws Exception {
        //创建生产者组
        DefaultMQProducer producer = new DefaultMQProducer("ProducerGroup01");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.start();

        //topic消息将要发往的地址
        //body是真正的消息体
        int i=0;
        while (i++<=6){
            Message msg = new Message("myTopic001", ("RocketMQ 第"+i+"条消息").getBytes());

            //同步阻塞式发消息，也就是发送完消息，必须等broker返回一个发送成功的消息，才会做其他事
            //这种方式消息发送的安全性可以得到保证，但是同步阻塞必定慢
            SendResult sendResult = producer.send(msg);
            System.out.println(i+" "+sendResult);
        }
//
//
//        //关闭消费者
//        Thread.sleep(3000);
//        producer.shutdown();
    }
}