package com.nobrain.auto.lib;

import com.nobrain.auto.clasz.PressInfo;
import com.nobrain.auto.manager.KeyDetect;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.awt.*;
import java.util.List;

import static com.nobrain.auto.Main.isFirst;
import static com.nobrain.auto.Main.isSecond;

public class Adofai {
    public boolean isCancel = false;
    public static Thread thread;
    private List<PressInfo> delayList;
    private Label isOn;


    public Adofai(String path, Label isOn) throws ParseException {
        isCancel = false;
        this.isOn = isOn;
        delayList = new LoadMap(path).delays;

    }

    public void cancel(){
        isCancel = true;
        isOn.setVisible(false);

        if(thread!=null) if (!thread.isInterrupted()) thread.interrupt();
    }

    public void start() throws AWTException {
        isCancel = false;
        isOn.setVisible(true);
        final Robot robot = new Robot();

        thread = new Thread(() -> {
            Iterator<PressInfo> pressInterator = delayList.iterator();

            long nowTime, prevTime = System.nanoTime();
            int prev = 0, now, delay = 55;
            PressInfo press = pressInterator.next();

            if (isFirst){
                press.delay -= 150000000;
                isFirst = false;
                isSecond = true;
            }
            else if (isSecond) {
                press.delay += 150000000;
                isSecond = false;
            }


            while (true) {
                if(isCancel) break;
                nowTime = System.nanoTime();
                if (nowTime - prevTime >= press.delay) {
                    prevTime += press.delay;
                    // 按键松开的延迟，所以可以设置一个默认值
                    delay = 55;

                    if (pressInterator.hasNext()) {
                        press = pressInterator.next();
                    } else {
                        isOn.setVisible(false);
                        break;
                    }

                    PressInfo finalPress = press;
                    now = (int)(finalPress.delay/1000000);

                    robot.keyPress(finalPress.key);

                    if(now<55&&prev<55) {
                        delay = now-5;
                        if(delay<0) delay = 0;
                    }


                    // 计算按键松开的延迟

                    try {
                        Timer timer = new Timer();
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                robot.keyRelease(finalPress.key);
                                timer.cancel();
                                timer.purge();
                            }
                        };
                        timer.schedule(timerTask, delay);
                    } catch (Exception ignored) {
                    }


                    prev = (int)(finalPress.delay/1000000);
                }
            }
            thread.interrupt();
            KeyDetect.canCancel = false;
        });
        thread.start();
    }

}