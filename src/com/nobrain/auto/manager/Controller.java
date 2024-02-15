package com.nobrain.auto.manager;

import com.nobrain.auto.lib.Adofai;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public static Adofai adofai = null;
    private Stage primaryStage;
    private String defaultPath = null;



    @FXML
    private Label label;
    @FXML
    private Button button;
    @FXML
    private Button any;
    @FXML
    private TextField lag;
    @FXML
    private TextField name;
    @FXML
    private Label on;

    @FXML public void onButtonClick(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("冰与火谱面文件", "*.adofai","*.ADOFAI"));
        fileChooser.setTitle("选择文件");
        if(defaultPath!=null) {
            fileChooser.setInitialDirectory(new File(defaultPath));
        }
        File file = fileChooser.showOpenDialog(primaryStage);

        if(file!=null) {
            label.setText("已选择文件 - " + file.getName());
            defaultPath = file.getAbsolutePath().replace(file.getName(),"");

            try {
                adofai = new Adofai(file.getAbsolutePath(), lag, on);
            } catch (ParseException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("错误");
                e.printStackTrace();
                alert.setHeaderText(e.toString());
                alert.setContentText("无法解析谱面");
                alert.showAndWait();
                adofai = null;
                label.setText("解析错误 - " + file.getName());
            }
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        label.setText("未选择文件");
    }

}
