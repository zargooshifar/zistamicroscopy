package main;

import main.Controllers.AcquisitionController;
import main.Controllers.CameraController;
import main.Controllers.StageController;
import mmcorej.CMMCore;

public class Singletons {


    private static CMMCore coreInstance;
    private static StageController stageInstance;
    private static CameraController cameraControllerInstance;
    private static AcquisitionController acquisitionController;

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


}
