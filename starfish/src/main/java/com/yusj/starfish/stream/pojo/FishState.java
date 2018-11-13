package com.yusj.starfish.stream.pojo;

public class FishState {
    private String fishName;
    private Long count;
    private Long timestamp;

    public FishState() {
    }

    public FishState(String fishName, Long count, Long timestamp) {
        this.fishName = fishName;
        this.count = count;
        this.timestamp = timestamp;
    }

    public String getFishName() {
        return fishName;
    }

    public void setFishName(String fishName) {
        this.fishName = fishName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "name: " + this.fishName + ", count: " + this.getCount() + ", timestamp: " + this.getTimestamp();
    }
}
