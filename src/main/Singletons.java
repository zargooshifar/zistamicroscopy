package main;

import main.Controllers.*;
import mmcorej.CMMCore;

public class Singletons {


    private static CMMCore coreInstance;
    private static StageController stageInstance;
    private static CameraController cameraControllerInstance;
    private static AcquisitionController acquisitionController;
    private static StatusbarController statusbarController;
    private static StageStateController stageStateController;
    private static ToolbarController toolbarController;

    public static CMMCore getCoreInstance() {
        if (coreInstance == null)
            coreInstance = new CMMCore();
        return coreInstance;
    }


    public static StageController getStageControllerInstance() {
        if (stageInstance == null)
            stageInstance = new StageController();
        return stageInstance;
    }

    public static CameraController getCameraControllerInstance() {
        if (cameraControllerInstance == null)
            cameraControllerInstance = new CameraController();
        return cameraControllerInstance;
    }

    public static AcquisitionController getAcquisitionControllerInstance() {
        if (acquisitionController == null)
            acquisitionController = new AcquisitionController();
        return acquisitionController;
    }

    public static StatusbarController getStatusbarControllerInstance() {
        if (statusbarController == null)
            statusbarController = new StatusbarController();
        return statusbarController;
    }

    public static StageStateController getStageStateInstance() {
        if (stageStateController == null)
            stageStateController = new StageStateController();
        return stageStateController;
    }

    public static ToolbarController getToolbarController() {
        if (toolbarController == null)
            toolbarController = new ToolbarController();
        return toolbarController;
    }


}
