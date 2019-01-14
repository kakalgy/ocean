package com.yusj.starfish.checkpoints.state;

import java.io.Serializable;

/**
 * 该数据在算子制作快照时用于保存到目前为止算子记录的数据条数
 */
public class UDFState implements Serializable {
    private long count;

    public UDFState() {
        count = 0L;
    }

    public void setState(long count) {
        this.count = count;
    }

    public long getState() {
        return this.count;
    }
}
