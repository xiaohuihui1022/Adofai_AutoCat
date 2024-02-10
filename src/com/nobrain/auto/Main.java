package com.nobrain.auto;

import com.nobrain.auto.lib.Adofai;
import com.nobrain.auto.manager.KeyDetect;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main.fxml")));

            primaryStage.setTitle("Auto Cat");
            Scene scene = new Scene(root, 270, 100);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.getIcons().addAll(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("resources/icon.png"))));

            LogManager.getLogManager().reset();
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);

            GlobalScreen.registerNativeHook();

//            GlobalScreen.addNativeKeyListener(new KeyDetector());
            GlobalScreen.addNativeKeyListener(new KeyDetect());
            primaryStage.setOnCloseRequest(we -> {
                try {
                    GlobalScreen.unregisterNativeHook();
                    if (Adofai.thread != null) if (!Adofai.thread.isInterrupted()) Adofai.thread.interrupt();
                } catch (NativeHookException e) {
                    e.printStackTrace();
                }
            });
            primaryStage.show();
        } catch (Exception e){
            e.printStackTrace();
        }



    }

    public static String getPrintStackTrace(Exception e) {

        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));

        return errors.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


