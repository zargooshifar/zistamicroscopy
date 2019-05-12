package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class Main extends Application {


    public static final ScheduledExecutorService scheduleThreadPool = Executors.newScheduledThreadPool(2);

    public static final int CORE_NUMBER = getCoreNumber() > 1 ? getCoreNumber() - 1 : getCoreNumber();

    private static final ExecutorService service = Executors.newFixedThreadPool(CORE_NUMBER);


    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("resources/Main.fxml"));
        primaryStage.setTitle("Zistagene Microscopy");
//        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });


    }

    @Override
    public void stop() throws Exception {
        super.stop();

    }


    public static ExecutorService getThreadPool() {
        return service;
    }

    private static ExecutorService threadQueue = Executors.newSingleThreadExecutor();

    public static ExecutorService getThreadQueue() {
        return threadQueue;
    }

    public static ScheduledExecutorService getScheduledThreadPool() {
        return scheduleThreadPool;
    }


    public static int getCoreNumber() {
        return Runtime.getRuntime().availableProcessors();
    }
}



