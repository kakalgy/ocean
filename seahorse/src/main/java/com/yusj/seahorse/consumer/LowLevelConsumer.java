package com.yusj.seahorse.consumer;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @Description
 * @Author kakalgy
 * @Date 2018/10/24 21:14
 **/
public class LowLevelConsumer {
    public static void main(String[] args) throws UnsupportedEncodingException {
        final String topic = "topic1";
        String clientID = "lowlevelconsumer1";//low level consumer without group.id

        System.out.println("Start Low level consumer!");
        SimpleConsumer simpleConsumer = new SimpleConsumer("hmaster", 9092, 100000, 64 * 1000000, clientID);
        FetchRequest req = new FetchRequestBuilder().clientId(clientID).addFetch(topic, 0, 0L, 1000000).
                addFetch(topic, 1, 0L, 500).addFetch(topic, 2, 0L, 1000000).build();

        FetchResponse fetchResponse = simpleConsumer.fetch(req);
        ByteBufferMessageSet messageSet = (ByteBufferMessageSet) fetchResponse.messageSet(topic, 1);

        System.out.println("Message Size: " + messageSet.sizeInBytes());
        for (MessageAndOffset messageAndOffset : messageSet) {
            ByteBuffer payload = messageAndOffset.message().payload();
            long offset = messageAndOffset.offset();
            byte[] bytes = new byte[payload.limit()];
            payload.get(bytes);
            System.out.println("Offset: " + offset + ", Payload: " + new String(bytes, "UTF-8"));
        }
    }
}
