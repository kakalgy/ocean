package com.yusj.seahorse.producer;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * @Description
 * @Author kakalgy
 * @Date 2018/10/24 21:49
 **/
public class HashPartitioner implements Partitioner {

    public HashPartitioner(VerifiableProperties verifiableProperties) {
    }

    @Override
    public int partition(Object key, int numPartitions) {
        if ((key instanceof Integer)) {
            return Math.abs(Integer.parseInt(key.toString())) % numPartitions;
        }
        return Math.abs(key.hashCode() % numPartitions);
    }
}
