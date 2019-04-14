package main.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import main.Singletons;
import mmcorej.CMMCore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AcquisitionController extends HBox {


    @FXML
    private JFXTextField horizentalStepsInput;
    @FXML
    private JFXTextField verticalStepsInput;
    @FXML
    private JFXTextField XStartInput;
    @FXML
    private JFXTextField YStartInput;
    @FXML
    private JFXTextField XEndInput;
    @FXML
    private JFXTextField YEndInput;
    @FXML
    private JFXButton startCapturingButton;
    @FXML
    private JFXButton setStartPointButton;
    @FXML
    private JFXButton setEndPointButton;

    private CMMCore core;
    private StageController stage;
    private double xStartPoint = 0;
    private double yStartPoint = 0;
    private double xEndPoint = 1000;
    private double yEndPoint = 1000;
    private double horizentalSteps = 100;
    private double verticalSteps = 100;
    private int remainingCaptures;
    private float capturingProgress;
    private float capturingProgressStep;
    private double lastTimeStamp = 0;


    public AcquisitionController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/acquisition.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        core = Singletons.getCoreInstance();
        stage = Singletons.getStageControllerInstance();


        XStartInput.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                xStartPoint = Double.valueOf(newValue);
            } catch (NumberFormatException e) {
                XStartInput.setText(oldValue);
                invalidInput();
            }
        });

        YStartInput.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                yStartPoint = Double.valueOf(newValue);
            } catch (NumberFormatException e) {
                YStartInput.setText(oldValue);
                invalidInput();
            }
        });

        XEndInput.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                xEndPoint = Double.valueOf(newValue);
            } catch (NumberFormatException e) {
                XEndInput.setText(oldValue);
                invalidInput();
            }
        });

        YEndInput.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                yEndPoint = Double.valueOf(newValue);
            } catch (NumberFormatException e) {
                YEndInput.setText(oldValue);
                invalidInput();
            }
        });

        horizentalStepsInput.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                horizentalSteps = Double.valueOf(newValue);
            } catch (NumberFormatException e) {
                horizentalStepsInput.setText(oldValue);
                invalidInput();
            }
        });

        verticalStepsInput.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                verticalSteps = Double.valueOf(newValue);
            } catch (NumberFormatException e) {
                verticalStepsInput.setText(oldValue);
                invalidInput();
            }
        });

    }


    @FXML
    void setEndPoint(ActionEvent event) {
        try {
            xEndPoint = core.getXPosition();
            yEndPoint = core.getYPosition();

            XEndInput.setText(String.valueOf(xEndPoint));
            YEndInput.setText(String.valueOf(yEndPoint));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void setStartPoint(ActionEvent event) {
        try {
            xStartPoint = core.getXPosition();
            yStartPoint = core.getYPosition();

            XStartInput.setText(String.valueOf(xStartPoint));
            YStartInput.setText(String.valueOf(yStartPoint));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // scanning section

    @FXML
    void startCapturing(ActionEvent event) {

        int counts = 0;
        counts += ((xStartPoint - xEndPoint) / horizentalSteps) * ((yStartPoint - yEndPoint) / verticalSteps);
        remainingCaptures = Math.abs(counts);

        capturingProgress = 0;
        capturingProgressStep = 100 / counts;

        System.out.println(remainingCaptures);

//        stopLivestream();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm");
        String date = dateFormat.format(new Date());


        if (xStartPoint > xEndPoint) {
            double x = xEndPoint;
            xEndPoint = xStartPoint;
            xStartPoint = x;
        }
        if (yStartPoint > yEndPoint) {
            double y = yEndPoint;
            yEndPoint = yStartPoint;
            yStartPoint = y;
        }


        int xStep = 0;
        int yStep = 0;
        System.out.println(String.format("%d  -  %d  -  %d  -  %d  -  %d  -  %d", (int) xStartPoint, (int) yStartPoint, (int) xEndPoint, (int) yEndPoint, (int) horizentalSteps, (int) verticalSteps));


        Runnable runnable = () -> {
            int xStep1 = 0;
            int yStep1 = 0;
            changeStageControlsDisable(true);
            while (yStartPoint + yStep1 * verticalSteps < yEndPoint) {
                while (xStartPoint + xStep1 * horizentalSteps < xEndPoint) {
                    while (stage.getPosition().getY() != yStartPoint + yStep1 * verticalSteps || stage.getPosition().getX() != xStartPoint + xStep1 * horizentalSteps) {
                        try {
                            core.setXYPosition(xStartPoint + xStep1 * horizentalSteps, yStartPoint + yStep1 * verticalSteps);
                            stage.updatePosition();
                            core.waitForDevice(core.getXYStageDevice());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
//                    captureAndSave(date, xStep1, yStep1, false);
                    getEstimatedTimeRemaining();
                    setProgress();
                    try {
                        core.waitForDevice(core.getCameraDevice());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    xStep1++;
                }
                xStep1 = 0;
                yStep1++;
            }
            changeStageControlsDisable(false);


        };

        Thread thread = new Thread(runnable);
        thread.start();

    }

    private void changeStageControlsDisable(boolean mode) {
//        upButton.setDisable(mode);
//        leftButton.setDisable(mode);
//        rightButton.setDisable(mode);
//        bottomButton.setDisable(mode);
//        dupButton.setDisable(mode);
//        dleftButton.setDisable(mode);
//        drightButton.setDisable(mode);
//        dbottomButton.setDisable(mode);
//        tupButton.setDisable(mode);
//        tleftButton.setDisable(mode);
//        trightButton.setDisable(mode);
//        tbottomButton.setDisable(mode);
        horizentalStepsInput.setDisable(mode);
        verticalStepsInput.setDisable(mode);

        XStartInput.setDisable(mode);
        YStartInput.setDisable(mode);
        XEndInput.setDisable(mode);
        YEndInput.setDisable(mode);

        setStartPointButton.setDisable(mode);
        setEndPointButton.setDisable(mode);

        startCapturingButton.setDisable(mode);


    }

    private void getEstimatedTimeRemaining() {
        remainingCaptures--;
        boolean firstTime = false;
        firstTime = lastTimeStamp == 0;

        double now = System.currentTimeMillis();
        double diff = now - lastTimeStamp;

        double estimatedTime = remainingCaptures * diff;

        System.out.println(estimatedTime / 1000);

        int timeInSec = (int) (estimatedTime / 1000);

        int hour = 0;
        int min = 0;
        int sec = 0;


        hour = timeInSec / 3600;
        min = (timeInSec - hour * 3600) / 60;
        sec = (timeInSec - hour * 3600 - min * 60);

        if (firstTime) {
            Platform.runLater(() -> Singletons.getStatusbarControllerInstance().setLeftStatusText("calculating estimated remaining time..."));
        } else {
            int finalHour = hour;
            int finalMin = min;
            int finalSec = sec;
            Platform.runLater(() -> Singletons.getStatusbarControllerInstance().setLeftStatusText(String.format("%d hours %d minutes %d seconds remaining...", finalHour, finalMin, finalSec)));
        }

        lastTimeStamp = now;
    }

    private void setProgress() {
        capturingProgress += capturingProgressStep;
        Platform.runLater(() -> {
            Singletons.getStatusbarControllerInstance().setProgressBar(capturingProgress / 100);
            Singletons.getStatusbarControllerInstance().setRightStatusText(String.format("%.2f %% ", capturingProgress));
        });

    }


    private void invalidInput() {

    }

}
