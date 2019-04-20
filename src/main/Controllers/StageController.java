package main.Controllers;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import main.Singletons;
import mmcorej.CMMCore;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;


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

    public EventHandler<KeyEvent> keyListener = event -> {
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
    AtomicBoolean hold = new AtomicBoolean(false);


    public StageController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/controlBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StackPane.setAlignment(this, Pos.BOTTOM_CENTER);
        StackPane.setMargin(this, new Insets(0, 0, 24, 0));



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


//        upButton.setOnMouseClicked((event -> move(Direction.UP)));
//        leftButton.setOnMouseClicked((event -> move(Direction.LEFT)));
//        rightButton.setOnMouseClicked((event -> move(Direction.RIGHT)));
//        bottomButton.setOnMouseClicked(event -> move(Direction.BOTTOM));
        dupButton.setOnMouseClicked((event -> move(Direction.UP)));
        dleftButton.setOnMouseClicked((event -> move(Direction.LEFT)));
        drightButton.setOnMouseClicked((event -> move(Direction.RIGHT)));
        dbottomButton.setOnMouseClicked(event -> move(Direction.BOTTOM));
        tupButton.setOnMouseClicked((event -> move(Direction.UP)));
        tleftButton.setOnMouseClicked((event -> move(Direction.LEFT)));
        trightButton.setOnMouseClicked((event -> move(Direction.RIGHT)));
        tbottomButton.setOnMouseClicked(event -> move(Direction.BOTTOM));


        upButton.setOnMousePressed(event -> {
            hold.set(true);
            smoothmove(Direction.UP);
        });

        leftButton.setOnMousePressed(event -> {
            hold.set(true);
            smoothmove(Direction.LEFT);
        });

        rightButton.setOnMousePressed(event -> {
            hold.set(true);
            smoothmove(Direction.RIGHT);
        });

        bottomButton.setOnMousePressed(event -> {
            hold.set(true);
            smoothmove(Direction.BOTTOM);
        });

        upButton.setOnMouseReleased(event -> hold.set(false));
        leftButton.setOnMouseReleased(event -> hold.set(false));
        rightButton.setOnMouseReleased(event -> hold.set(false));
        bottomButton.setOnMouseReleased(event -> hold.set(false));
    }

    public void smoothmove(Direction direction) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (hold.get())
                    move(direction);
            }
        });
        thread.start();

    }




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

    void moveBottomLarge() {
        try {
            core.setRelativeXYPosition(0, -getStepSizes().getLargeStep());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    void moveBottomMedium() {
        try {

            core.setRelativeXYPosition(0, -getStepSizes().getMediumStep());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    void moveBottomSmall() {
        try {
            core.setRelativeXYPosition(0, -getStepSizes().getSmallStep());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    void moveLeftLarge() {
        try {
            core.setRelativeXYPosition(-getStepSizes().getLargeStep(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    void moveLeftMedium() {
        try {
            core.setRelativeXYPosition(-getStepSizes().getMediumStep(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    void moveLeftSmall() {
        try {
            core.setRelativeXYPosition(-getStepSizes().getSmallStep(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    void moveRightLarge() {
        try {
            core.setRelativeXYPosition(getStepSizes().getLargeStep(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    void moveRightMedium() {
        try {
            core.setRelativeXYPosition(getStepSizes().getMediumStep(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    void moveRightSmall() {
        try {
            core.setRelativeXYPosition(getStepSizes().getSmallStep(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    void moveUpLarge() {
        try {
            core.setRelativeXYPosition(0, getStepSizes().getLargeStep());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    void moveUpMedium() {
        try {
            core.setRelativeXYPosition(0, getStepSizes().getMediumStep());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updatePosition();
    }

    void moveUpSmall() {
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
                        moveUpLarge();
                        break;
                    case MEDIUM:
                        moveUpMedium();
                        break;
                    case SMALL:
                        moveUpSmall();
                        break;
                }
                break;
            case LEFT:
                switch (currentMoveSize) {
                    case LARGE:
                        moveLeftLarge();
                        break;
                    case MEDIUM:
                        moveLeftMedium();
                        break;
                    case SMALL:
                        moveLeftSmall();
                        break;
                }
                break;
            case RIGHT:
                switch (currentMoveSize) {
                    case LARGE:
                        moveRightLarge();
                        break;
                    case MEDIUM:
                        moveRightMedium();
                        break;
                    case SMALL:
                        moveRightSmall();
                        break;
                }
                break;
            case BOTTOM:
                switch (currentMoveSize) {
                    case LARGE:
                        moveBottomLarge();
                        break;
                    case MEDIUM:
                        moveBottomMedium();
                        break;
                    case SMALL:
                        moveBottomSmall();
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
            Singletons.getStageStateInstance().updatePosition(core.getXPosition(), core.getYPosition(), 0);

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
