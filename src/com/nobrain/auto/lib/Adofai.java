package com.nobrain.auto.lib;

import com.nobrain.auto.clasz.PressInfo;
import com.nobrain.auto.manager.KeyDetect;
import javafx.scene.control.Label;
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
            int prev = 0, now, delay;
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
                // 如果玩家按下insert退出了宏
                if (isCancel) break;
                // 获取时间用来循环
                nowTime = System.nanoTime();
                // 如果该按按键了
                if (nowTime - prevTime >= press.delay) {
                    // 算下一个按键需要的延迟
                    prevTime += press.delay;

                    // 按键松开的延迟，所以可以设置一个默认值
                    delay = 55;

                    // 如果上一个按键对应的floor不是最后一个floor
                    if (pressInterator.hasNext()) {
                        // 切换到下一个floor对应的按键
                        press = pressInterator.next();
                    } else {
                        // 设定文字“运行中”为不可见状态
                        isOn.setVisible(false);
                        // 结束循环
                        break;
                    }

                    // 设定一个变量（虽然不知道有啥用）
                    PressInfo finalPress = press;
                    // 帮助计算按键松开延迟
                    now = (int) (finalPress.delay / 1000000);

                    // 检测是否是自动播放格子
                    if (finalPress.getIsAuto()){
                        // 算一遍底下的代码，防止出bug
                        // 如果有长按，就
                        if (press.getHoldDelay() != 0){
                            delay = press.getHoldDelay();
                        }
                        // 默认情况的按键延迟
                        else {
                            if (now < 55 && prev < 55) {
                                delay = now - 5;
                                if (delay < 0) delay = 0;
                            }
                        }

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

                        prev = (int) (finalPress.delay / 1000000);
                        // continue，进行下一轮循环
                        continue;
                    }




                    // 按下按键
                    robot.keyPress(finalPress.key);

                    // 计算按键松开的延迟

                    // 如果有长按，就
                    if (press.getHoldDelay() != 0){
                        delay = press.getHoldDelay();
                    }
                    // 默认情况的按键延迟
                    else {
                        if (now < 55 && prev < 55) {
                            delay = now - 5;
                            if (delay < 0) delay = 0;
                        }
                    }

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


                    prev = (int) (finalPress.delay / 1000000);
                }
            }
            thread.interrupt();
            KeyDetect.canCancel = false;
        });
        thread.start();
    }

}