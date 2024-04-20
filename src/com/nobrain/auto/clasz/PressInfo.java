package com.nobrain.auto.clasz;

public class PressInfo {
    public int key = 74;
    public long delay;
    private int HoldDelay = 0;
    private boolean isAuto = false;

    public void  setPressDelay(long delay){
        this.delay = delay;
    }

    public void setHoldDelay(int holdDelay) {
        this.HoldDelay = holdDelay;
    }

    public int getHoldDelay(){
        return this.HoldDelay;
    }

    public void setIsAuto(boolean value) { this.isAuto = value; }

    public boolean getIsAuto(){
        return isAuto;
    }
}
