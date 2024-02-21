package com.nobrain.auto.clasz;

public class PressInfo {
    public int key = 74;
    public long delay;
    private int HoldDelay = 0;

    public void  setPressDelay(long delay){
        this.delay = delay;
    }

    public void setHoldDelay(int holdDelay) {
        this.HoldDelay = holdDelay;
    }

    public int getHoldDelay(){
        return this.HoldDelay;
    }
}
