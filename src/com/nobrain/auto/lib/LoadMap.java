package com.nobrain.auto.lib;

import com.nobrain.auto.clasz.PressInfo;
import com.nobrain.auto.clasz.Key;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class LoadMap {
    public ArrayList<PressInfo> delays = new ArrayList<>();

    private String[] pathData;

    private String[] angleData;

    public LoadMap(String path) throws ParseException {
         // 设置json
         JSONParser parser = new JSONParser();
         // 读取json
         JSONObject map = (JSONObject) parser.parse(read(path).replaceAll("\uFEFF", ""));
         // 读取settings
         JSONObject setting = (JSONObject) map.get("settings");
         // 读取actions
         JSONArray actions = (JSONArray) map.get("actions");
         // 如果是pathData
         if (map.containsKey("pathData")){
             pathData = map.get("pathData").toString().split("");
         }
         // 如果是angleData
         else if (map.containsKey("angleData")){
             angleData = map.get("angleData")
                     .toString()
                     .replace("[", "")
                     .replace("]", "")
                     .split(",");
         }

         // 建立两个表，用来标注BPM和旋转变化。
         HashMap<Integer, Double> changeBPM = new HashMap<>();
         HashMap<Integer, Boolean> changeTwirl = new HashMap<>();

         // 获取谱面初始BPM
         double currentBPM = toDouble(setting.get("bpm"));
         // 初始旋转为false
         boolean isTwirl = false;

         // 定义keys
         String[] key2 ="jf".split("");
         String[] key4 ="jkfd".split("");
         String[] key6 ="jkfdsl".split("");
         String[] key8 ="jkfdsla;".split("");

         // 计算音高（倍速）
         double pitch = toDouble(setting.get("pitch"))/100;
         // 读取谱面延时
         double tick = toDouble(setting.get("countdownTicks"));
         // 计算延时
         double loadDelay = ((0.5 + ((0.45*tick)/((currentBPM*pitch)/100)))*1000);
         int offset = toInt(setting.get("offset"));
         double result = ((60000/(currentBPM*pitch)+offset+loadDelay)*1000000);

         // 添加进数组
         delays.add(new PressInfo((long)result));

         // 循环读取谱面
         for(Object o : actions) {
             JSONObject action = (JSONObject)o;
             // 如果查到兔子/乌龟
             if(action.get("eventType").equals("SetSpeed")) {
                 // Avoid NPE
                 if(action.get("speedType") == null) {
                     currentBPM = toDouble(action.get("beatsPerMinute"));
                     changeBPM.put(toInt(action.get("floor"))-1, currentBPM);
                     continue;
                 }
                 // 如果是直接修改BPM
                 if(action.get("speedType").equals("Bpm")) {
                     currentBPM = toDouble(action.get("beatsPerMinute"));
                     changeBPM.put(toInt(action.get("floor"))-1, currentBPM);
                 // 如果是BPM倍频器
                 } else {
                     changeBPM.put(toInt(action.get("floor"))-1, currentBPM*toDouble(action.get("bpmMultiplier")));
                     currentBPM = currentBPM*toDouble(action.get("bpmMultiplier"));
                 }
             }
             // 如果有旋转
             if(action.get("eventType").equals("Twirl")) {
                 changeTwirl.put(toInt(action.get("floor"))-1,true);
             }
         }

         // 计算BPM
         currentBPM = toDouble(setting.get("bpm"))*pitch;
         int i = 0;
         // 如果原谱是pathdata
         if (pathData != null){
             for (int n = 0; n < pathData.length; n++) {
                 String now = pathData[n];
                 String next = getValue(pathData,n+1);
                 if(changeBPM.get(n)!=null) currentBPM = changeBPM.get(n);
                 if(changeTwirl.get(n)!=null) isTwirl = !isTwirl;
                 // 中旋
                 boolean isMidspin = next.equals("!");
                 if(now.equals("!")) continue;
                 if(isMidspin) {
                     n++;
                     next = getValue(pathData,n+1);
                 }


                 int angle = AngleUtill.getCurrentAngle(now,next,isTwirl,isMidspin);
                 double tempBPM = ((double)angle/180)*(60/(currentBPM*pitch));

                 PressInfo pressInfo = new PressInfo((long)(tempBPM*1000000000));


                 switch (Key.getKey((int) (tempBPM * 1000))) {
                     case Key.KEY8 -> {
                         if (i >= key8.length) i = 0;
                         pressInfo.key = convert(key8[i]);
                     }
                     case Key.KEY6 -> {
                         if (i >= key6.length) i = 0;
                         pressInfo.key = convert(key6[i]);
                     }
                     case Key.KEY4 -> {
                         if (i >= key4.length) i = 0;
                         pressInfo.key = convert(key4[i]);
                     }
                     case Key.KEY2 -> {
                         if (i >= key2.length) i = 0;
                         pressInfo.key = convert(key2[i]);
                     }
                     case Key.KEY1 -> {
                         if (i != 0) i = 0;
                         pressInfo.key = convert(key2[i]);
                     }
                 }
                 i++;
                 delays.add(pressInfo);
             }

         }
         // 如果原谱子是angleData
         else if (angleData != null){
             for (int n = 0; n < angleData.length; n++) {
                 int now = strToInt(angleData[n]);

                 int next = strToInt(getValue(angleData,n + 1));
                 boolean isMidspin = next == 999;

                 if(changeBPM.get(n)!=null) currentBPM = changeBPM.get(n);
                 if(changeTwirl.get(n)!=null) isTwirl = !isTwirl;

                 if(now == 999) continue;
                 if(isMidspin) {
                     n++;
                     next = strToInt(getValue(angleData,n + 1));
                 }


                 int angle = AngleUtill.getCurrentAngleData(now,next,isTwirl,isMidspin);
                 double tempBPM = ((double)angle / 180) * (60 / (currentBPM*pitch));

                 PressInfo pressInfo = new PressInfo((long)(tempBPM * 1000000000));

                 switch (Key.getKey((int) (tempBPM * 1000))) {
                     case Key.KEY8 -> {
                         if (i >= key8.length) i = 0;
                         pressInfo.key = convert(key8[i]);
                     }
                     case Key.KEY6 -> {
                         if (i >= key6.length) i = 0;
                         pressInfo.key = convert(key6[i]);
                     }
                     case Key.KEY4 -> {
                         if (i >= key4.length) i = 0;
                         pressInfo.key = convert(key4[i]);
                     }
                     case Key.KEY2 -> {
                         if (i >= key2.length) i = 0;
                         pressInfo.key = convert(key2[i]);
                     }
                     case Key.KEY1 -> {
                         if (i != 0) i = 0;
                         pressInfo.key = convert(key2[i]);
                     }
                 }

                 i++;
                 delays.add(pressInfo);
             }
         }
    }

    private String getValue(String[] array, int index) {
        if(index>=array.length-1) return array[array.length-1];
        return array[index];
    }

    private int toInt(Object o) {
        try {
            return Integer.parseInt(String.valueOf(o));
        } catch (Exception e) {
            return -1;
        }
    }




    private double toDouble(Object o) {
        try {
            return Double.parseDouble(String.valueOf(o));
        } catch (Exception e) {
            return -1;
        }
    }

    private int convert(String key) {
        return Integer.parseInt(key
                .replaceAll("j","74")
                .replaceAll("a","65")
                .replaceAll("s","83")
                .replaceAll("d","68")
                .replaceAll("k","75")
                .replaceAll("l","76")
                .replaceAll(";","59")
                .replaceAll("f","70"));
    }

    private String read(String path) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.contains("floor")&&!line.contains("SetSpeed")&&!line.contains("Twirl")) continue;

                result.append(line).append("\n");
            }
            return result.toString();
        } catch (IOException e) {
            return "";
        }
    }

    // 将string转为int
    private int strToInt(String str) {
        int result;
        try {
            result = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            double temp = Double.parseDouble(str);
            result = (int) temp;
        }
        return result;

    }
}
