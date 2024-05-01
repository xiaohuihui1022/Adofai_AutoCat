package com.nobrain.auto.manager;

import com.nobrain.auto.lib.Adofai;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;



public class KeyDetect implements NativeKeyListener {
    public static boolean canCancel = false;


    public void nativeKeyPressed(NativeKeyEvent e) {
        String key = NativeKeyEvent.getKeyText(e.getKeyCode());

        if (key.equalsIgnoreCase("Insert")) {
            if(Controller.adofai==null) return;
            try {
                if(canCancel) {
                    Controller.adofai.cancel();
                    canCancel = false;
                } else {
                    Controller.adofai.start();
                    canCancel = true;
                }
            } catch (AWTException awtException) {
                awtException.printStackTrace();
            }
        }
        // 左箭头
        if (e.getKeyCode() == 57419){
            if (Controller.adofai==null) return;
            if (canCancel){
                // -5ms
                Controller.adofai.time -= 5000000;
            }
            else {
                return;
            }
        }

        // 右箭头
        if (e.getKeyCode() == 57421){
            if (Controller.adofai==null) return;
            if (canCancel){
                // +5ms
                Controller.adofai.time += 5000000;
            }
            else {
                return;
            }
        }


    }
    public void nativeKeyReleased(NativeKeyEvent e) {
    }
    public void nativeKeyTyped(NativeKeyEvent e) {

    }

}



