package main.Controllers;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import main.Singletons;
import mmcorej.CMMCore;

import java.io.IOException;


public class StageController extends GridPane {

    private static Position position;
    private static StepSizes stepSizes;
    @FXML
    private JFXButton upButton;
    @FXML
    private JFXButton leftButton;
    @FXML
    private JFXButton rightButton;
    @FXML
    private JFXButton bottomButton;
    @FXML
    private JFXButton dupButton;
    @FXML
    private JFXButton dleftButton;
    @FXML
    private JFXButton drightButton;
    @FXML
    private JFXButton dbottomButton;
    @FXML
    private JFXButton tupButton;
    @FXML
    private JFXButton tleftButton;
    @FXML
    private JFXButton trightButton;
    @FXML
    private JFXButton tbottomButton;
    @FXML
    private JFXButton toggleButton;
    @FXML
    private GridPane stageControllBox;
    private CMMCore core;
    private MoveSizes currentMoveSize = MoveSizes.SMALL;

    public StageController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/controlBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        setAlignment(Pos.BOTTOM_CENTER);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        core = Singletons.getCoreInstance();

        toggleButton.setOnMouseDragged(event -> {
//            setManaged(false);
            stageControllBox.setManaged(false);
            stageControllBox.setTranslateX(event.getX() + stageControllBox.getTranslateX() - 30);
            stageControllBox.setTranslateY(event.getY() + stageControllBox.getTranslateY() - 30);
            event.consume();
        });

        toggleButton.setOnMouseClicked((event) -> toggleStageButtons());

        stageControllBox.setOnMouseEntered(event -> {

            FadeTransition ft = new FadeTransition(Duration.millis(300), stageControllBox);
            ft.setFromValue(0.1);
            ft.setToValue(1);
            ft.play();
        });

        stageControllBox.setOnMouseExited(event -> {
            FadeTransition ft = new FadeTransition(Duration.millis(300), stageControllBox);
            ft.setFromValue(1);
            ft.setToValue(0.1);
            ft.play();

        });

    }

    private EventHandler<KeyEvent> keyListener = event -> {
        switch (event.getCode()) {
            case UP:
                move(Direction.UP);
                break;
            case LEFT:
                move(Direction.LEFT);
                break;
            case RIGHT:
                move(Direction.RIGHT);
                break;
            case DOWN:
                move(Direction.BOTTOM);
                break;

        }
        event.consume();
    };




    public Position getPosition() {
        if (position == null) {
            position = new Position();
        }
        return position;
    }

    public StepSizes getStepSizes() {
        if (stepSizes == null) {
            stepSizes = new StepSizes();
        }
        return stepSizes;
    }

    @FXML
    void moveBottomLarge(ActionEvent event) {
        try {
            core.setRelativeXYPosition(0, -getStepSizes().getLargeStep());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    @FXML
    void moveBottomMedium(ActionEvent event) {
        try {

            core.setRelativeXYPosition(0, -getStepSizes().getMediumStep());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    @FXML
    void moveBottomSmall(ActionEvent event) {
        try {
            core.setRelativeXYPosition(0, -getStepSizes().getSmallStep());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    @FXML
    void moveLeftLarge(ActionEvent event) {
        try {
            core.setRelativeXYPosition(-getStepSizes().getLargeStep(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    @FXML
    void moveLeftMedium(ActionEvent event) {
        try {
            core.setRelativeXYPosition(-getStepSizes().getMediumStep(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    @FXML
    void moveLeftSmall(ActionEvent event) {
        try {
            core.setRelativeXYPosition(-getStepSizes().getSmallStep(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    @FXML
    void moveRightLarge(ActionEvent event) {
        try {
            core.setRelativeXYPosition(getStepSizes().getLargeStep(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    @FXML
    void moveRightMedium(ActionEvent event) {
        try {
            core.setRelativeXYPosition(getStepSizes().getMediumStep(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    @FXML
    void moveRightSmall(ActionEvent event) {
        try {
            core.setRelativeXYPosition(getStepSizes().getSmallStep(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    @FXML
    void moveUpLarge(ActionEvent event) {
        try {
            core.setRelativeXYPosition(0, getStepSizes().getLargeStep());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    @FXML
    void moveUpMedium(ActionEvent event) {
        try {
            core.setRelativeXYPosition(0, getStepSizes().getMediumStep());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    @FXML
    void moveUpSmall(ActionEvent event) {
        try {
            core.setRelativeXYPosition(0, getStepSizes().getSmallStep());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    private void move(Direction direction) {
        switch (direction) {
            case UP:
                switch (currentMoveSize) {
                    case LARGE:
                        moveUpLarge(null);
                        break;
                    case MEDIUM:
                        moveUpMedium(null);
                        break;
                    case SMALL:
                        moveUpSmall(null);
                        break;
                }
                break;
            case LEFT:
                switch (currentMoveSize) {
                    case LARGE:
                        moveLeftLarge(null);
                        break;
                    case MEDIUM:
                        moveLeftMedium(null);
                        break;
                    case SMALL:
                        moveLeftSmall(null);
                        break;
                }
                break;
            case RIGHT:
                switch (currentMoveSize) {
                    case LARGE:
                        moveRightLarge(null);
                        break;
                    case MEDIUM:
                        moveRightMedium(null);
                        break;
                    case SMALL:
                        moveRightSmall(null);
                        break;
                }
                break;
            case BOTTOM:
                switch (currentMoveSize) {
                    case LARGE:
                        moveBottomLarge(null);
                        break;
                    case MEDIUM:
                        moveBottomMedium(null);
                        break;
                    case SMALL:
                        moveBottomSmall(null);
                        break;
                }
                break;

        }
        updatePosition();
    }

    public void updatePosition() {
        try {
            getPosition().setX(core.getXPosition());
            getPosition().setY(core.getYPosition());
            Platform.runLater(() -> {

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void toggleStageButtons() {

        switch (currentMoveSize) {
            case SMALL:
                upButton.setVisible(false);
                leftButton.setVisible(false);
                rightButton.setVisible(false);
                bottomButton.setVisible(false);
                dupButton.setVisible(true);
                dleftButton.setVisible(true);
                drightButton.setVisible(true);
                dbottomButton.setVisible(true);
                tupButton.setVisible(false);
                tleftButton.setVisible(false);
                trightButton.setVisible(false);
                tbottomButton.setVisible(false);
                currentMoveSize = MoveSizes.MEDIUM;
                break;
            case MEDIUM:
                upButton.setVisible(false);
                leftButton.setVisible(false);
                rightButton.setVisible(false);
                bottomButton.setVisible(false);
                dupButton.setVisible(false);
                dleftButton.setVisible(false);
                drightButton.setVisible(false);
                dbottomButton.setVisible(false);
                tupButton.setVisible(true);
                tleftButton.setVisible(true);
                trightButton.setVisible(true);
                tbottomButton.setVisible(true);
                currentMoveSize = MoveSizes.LARGE;
                break;
            case LARGE:
                upButton.setVisible(true);
                leftButton.setVisible(true);
                rightButton.setVisible(true);
                bottomButton.setVisible(true);
                dupButton.setVisible(false);
                dleftButton.setVisible(false);
                drightButton.setVisible(false);
                dbottomButton.setVisible(false);
                tupButton.setVisible(false);
                tleftButton.setVisible(false);
                trightButton.setVisible(false);
                tbottomButton.setVisible(false);
                currentMoveSize = MoveSizes.SMALL;
                break;

        }

    }

    private enum MoveSizes {SMALL, MEDIUM, LARGE}


    private enum Direction {UP, LEFT, RIGHT, BOTTOM}

    public static class Position {
        private double x;
        private double y;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }

    public static class StepSizes {
        private double smallStep = 1;
        private double mediumStep = 100;
        private double largeStep = 1000;

        public double getSmallStep() {
            return smallStep;
        }

        public void setSmallStep(double smallStep) {
            this.smallStep = smallStep;
        }

        public double getMediumStep() {
            return mediumStep;
        }

        public void setMediumStep(double mediumStep) {
            this.mediumStep = mediumStep;
        }

        public double getLargeStep() {
            return largeStep;
        }

        public void setLargeStep(double largeStep) {
            this.largeStep = largeStep;
        }
    }


}
