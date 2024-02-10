package com.nobrain.auto.lib;

import com.nobrain.auto.clasz.PressInfo;
import com.nobrain.auto.manager.KeyDetect;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Adofai {
    public boolean isCancel = false;
    public static Thread thread;
    private final List<PressInfo> delayList;
    private final TextField lag;
    private final Label isOn;
    private long original = 0 ;


    public Adofai(String path,TextField lag,TextField name, Label isOn,CheckBox workshop) throws ParseException {
        isCancel = false;
        this.lag = lag;
        this.isOn = isOn;

        if (!workshop.isSelected() && path != null) {
            delayList = new LoadMap(path).delays;
        } else {
            SearchMap.DistanceClass map = new SearchMap(name.getText()).result;
            delayList = new LoadMap(map.path.getAbsolutePath()).delays;
            name.setText(map.title);
        }
    }

    public void cancel(){
        isCancel = true;
        isOn.setVisible(false);

        if(thread!=null) if (!thread.isInterrupted()) thread.interrupt();
    }

    public void start(Boolean isFirst) throws AWTException {
        isCancel = false;
        isOn.setVisible(true);

        final Robot robot = new Robot();
        robot.keyPress('P');
        robot.keyRelease('P');

        if(original!=0) {
            PressInfo pressInfo = new PressInfo(original);
            delayList.set(0,pressInfo);
        }

        if(thread!=null) if(!thread.isInterrupted()) thread.interrupt();

        List<PressInfo> delays = delayList;
        long originalNum = delays.get(0).delay;
        if(original==0) original = originalNum;
        String text = lag.getText();
        double lagDelay = 0;

        try {
            double num = Double.parseDouble(text);
            if (!Double.isNaN(num)) {
                lagDelay = num;
            }
        } catch (Exception ignored) {
        }

        if(isFirst)
            lagDelay -= 120;

        PressInfo pressInfo = new PressInfo(originalNum+(long)lagDelay*1000000);

        delays.set(0,pressInfo);
        thread = new Thread(() -> {
            Iterator<PressInfo> pressIntegrator = delays.iterator();
            long nowTime, prevTime = System.nanoTime();
            PressInfo press = pressIntegrator.next();

            while (true) {
                if(isCancel) break;
                nowTime = System.nanoTime();
                if (nowTime - prevTime >= press.delay) {
                    prevTime += press.delay;
                    if (pressIntegrator.hasNext()) {
                        press = pressIntegrator.next();
                    } else {
                        isOn.setVisible(false);
                        break;
                    }

                    PressInfo finalPress = press;

                    robot.keyPress(finalPress.key);
                    robot.keyRelease(finalPress.key);

                }
            }
            thread.interrupt();
            KeyDetect.canCancel = false;
        });
        thread.start();
    }

}
