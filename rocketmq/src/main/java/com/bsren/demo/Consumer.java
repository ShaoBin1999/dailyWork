package com.bsren.demo;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class Consumer {
    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ConsumerGroup01");

        //设置nameserver地址
        consumer.setNamesrvAddr("127.0.0.1:9876");

        //每个consumer只能关注一个topic
        //topic 关注的topic名
        //subExpression 根据Tag过滤 表示我需要过滤掉当前topic里的什么信息, *代表不过滤
        String topic = "myTopic001";
        String subExpression = "*";
        consumer.subscribe(topic, subExpression);

        //使用监听的方式接受信息，防止消费者本身一直在这阻塞等着消息
        //list就是收到的所有消息
        consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            for(MessageExt msg : list){
                System.out.println(new String(msg.getBody()));
            }

            //给broker返回消费成功的信息,broker就会将这条消息状态改为success，这样这条消息之后就不会被其他consumer消费
            //相当于给broker返回了一个ack信息
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        //启动消费者
        consumer.start();
        System.out.println("consumer start....");
    }
}
