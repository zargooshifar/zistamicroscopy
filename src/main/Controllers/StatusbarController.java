package main.Controllers;

import com.jfoenix.controls.JFXProgressBar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.IOException;

public class StatusbarController extends HBox {

    @FXML
    private Label leftStatusText;
    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private Label rightStatusText;

    public StatusbarController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/statusbar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setHgrow(this, Priority.ALWAYS);

    }


    public void setLeftStatusText(String text) {
        leftStatusText.setText(text);
    }

    public void setRightStatusText(String text) {
        rightStatusText.setText(text);

    }

    public void setProgressBar(float v) {
        progressBar.setVisible(true);
        progressBar.setProgress(v);

    }

}
