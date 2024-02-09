package com.nobrain.auto.clasz;

public class Key {
    public final static int KEY1 = 1;
    public final static int KEY2 = 2;
    public final static int KEY4 = 4;
    public final static int KEY6 = 6;
    public final static int KEY8 = 8;

    public static int getKey(int n) {
        if(n<=17) return KEY8;
        else if(n<=32) return KEY6;
        else if(n<=69) return KEY4;
        else if(n<=166) return KEY2;
        return KEY1;
    }

}
