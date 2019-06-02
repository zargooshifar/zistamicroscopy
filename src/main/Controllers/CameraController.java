package main.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import ij.ImagePlus;
import ij.io.FileSaver;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import main.ImageUtils.ImageCanvas;
import main.Singletons;
import mmcorej.CMMCore;
import mmcorej.StrVector;
import mmcorej.TaggedImage;
import org.json.JSONException;
import org.json.JSONObject;
import org.micromanager.acquisition.AcquisitionManager;
import org.micromanager.acquisition.AcquisitionWrapperEngine;
import org.micromanager.acquisition.ProcessorStack;
import org.micromanager.acquisition.TaggedImageQueue;
import org.micromanager.utils.ImageUtils;
import org.micromanager.utils.MDUtils;
import org.micromanager.utils.PropertyItem;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CameraController extends HBox {


    private static AcquisitionWrapperEngine engine;
    private final TimerController timerController_ = new TimerController();
    @FXML
    private Spinner<Integer> exposure;
    @FXML
    private ChoiceBox<String> pixelTypePicker;
    @FXML
    private ChoiceBox<String> bitDepthPicker;
    @FXML
    private JFXToggleButton autoContrastToggle;
    @FXML
    private JFXButton btn_capture;
    @FXML
    private JFXButton btn_capture_save;
    @FXML
    private JFXButton btn_startlive;
    private CMMCore core;
    private boolean autoContrast = true;
    private boolean isLive;
    private long imageNumber_;
    private LinkedBlockingQueue<TaggedImage> imageQueue_;
    private ImageCanvas imageCanvas;

    public CameraController() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/camera.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        core = Singletons.getCoreInstance();
        engine = new AcquisitionWrapperEngine(new AcquisitionManager());
        imageCanvas = ImageCanvas.getInstance();

    }

    public boolean isLive() {
        return isLive;
    }

    public static void saveImagePlus(ImagePlus imp, JSONObject md, String path, String tiffFileName) {
        try {
            imp.setProperty("Info", md.toString(2));
        } catch (JSONException var6) {
            var6.printStackTrace();
        }

        FileSaver fs = new FileSaver(imp);
        File output = new File(path);
        if (!output.exists()) {
            output.mkdirs();
        }
        System.out.println(tiffFileName);
        fs.saveAsTiff(path + "/" + tiffFileName);
    }

    public void loadCameraConfig() throws Exception {
        String camera = core.getCameraDevice();
        if (camera != "") {
            StrVector pixelTypeValues = core.getAllowedPropertyValues(camera, "PixelType");
            for (int i = 0; i < pixelTypeValues.size(); i++) {
                String val = pixelTypeValues.get(i);
                pixelTypePicker.getItems().add(val);
            }
            String defaultPixelType = pixelTypeValues.get((int) pixelTypeValues.size() - 1);
            pixelTypePicker.valueProperty().setValue(defaultPixelType);
            core.setProperty(camera, "PixelType", defaultPixelType);

//            core.setProperty(camera,"Mode","Color Test Pattern");

            StrVector bitDepthValues = core.getAllowedPropertyValues(camera, "BitDepth");
            for (int i = 0; i < bitDepthValues.size(); i++) {
                String val = bitDepthValues.get(i);
                bitDepthPicker.getItems().add(val);
            }
            String defaultBitDepth = bitDepthValues.get((int) bitDepthValues.size() - 1);
            bitDepthPicker.valueProperty().setValue(defaultBitDepth);
            core.setProperty(camera, "BitDepth", defaultBitDepth);


            PropertyItem exposure_prop = new PropertyItem();
            exposure_prop.readFromCore(core, camera, "Exposure", false);

            int initialExposure = 100;
            core.setProperty(camera, "Exposure", initialExposure);
            exposure.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory((int) exposure_prop.lowerLimit, (int) exposure_prop.upperLimit, initialExposure, 10));
            exposure.valueProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    core.setExposure(Double.valueOf(newValue));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

//        autoContrastToggle.selectedProperty().addListener((observable, oldValue, newValue) -> autoContrast = newValue);


            pixelTypePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    core.setProperty(camera, "PixelType", newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            bitDepthPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    core.setProperty(camera, "BitDepth", newValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }

    }

    private void setCameraConfigDisable(boolean disabe) {
        pixelTypePicker.setDisable(disabe);
        bitDepthPicker.setDisable(disabe);
        btn_capture.setDisable(disabe);
        btn_capture_save.setDisable(disabe);
    }

    @FXML
    void takeLiveStream(MouseEvent event) throws Exception {


        if (!isLive) {
            setCameraConfigDisable(true);
            btn_startlive.setText("Stop Live Stream");
            core.clearCircularBuffer();
            try {
                core.startContinuousSequenceAcquisition(0);
            } catch (Exception e) {
                e.printStackTrace();
                throw (e);
            }

            long period = getInterval();

            // Wait for first image to create ImageWindow, so that we can be sure about image size
            long start = System.currentTimeMillis();
            long now = start;
            // Give 10s extra for the camera to transfer the image to us.
            long timeout = period + 10000;
            while (core.getRemainingImageCount() == 0 && (now - start < timeout)) {
                now = System.currentTimeMillis();
                Thread.sleep(5);
            }
            if (now - start >= timeout) {
                throw new Exception("CameraController did not send image within " + timeout + "ms");
            }

            TaggedImage timg = core.getLastTaggedImage();

            // With first image acquired, create the display
            displayImageRoutine(timg);

            long firstImageSequenceNumber = MDUtils.getSequenceNumber(timg.tags);

            synchronized (this) {
                long fpsCounter_ = 0;
                long fpsTimer_ = System.nanoTime();
                imageNumber_ = firstImageSequenceNumber;
                long oldImageNumber_ = firstImageSequenceNumber;
            }
            imageQueue_ = new LinkedBlockingQueue<TaggedImage>(10);
            // XXX The logic here is very weird. We add this first image only if we
            // are using a single camera, because the single camera timer main checks
            // and eliminates duplicates of the same frame. For multi camera, we do
            // not add the image, since no checks for duplicates are performed
            // (which is a bug that needs to be fixed).

            imageQueue_.put(timg);
            runDisplayThread(imageQueue_);
            Runnable task = singleCameraLiveTask();
            timerController_.start(task, period);


        } else {
            setCameraConfigDisable(false);

            btn_startlive.setText("Start Live Stream");
            stopLivestream();
        }


        isLive = !isLive;

    }

    private Runnable singleCameraLiveTask() {
        return () -> {
            if (core.getRemainingImageCount() != 0) {

                try {
                    TaggedImage ti = core.getLastTaggedImage();
                    long imageNumber = MDUtils.getSequenceNumber(ti.tags);
                    if (setImageNumber(imageNumber)) {
                        imageQueue_.put(ti);
                    }
                } catch (final Exception var4) {
                    var4.printStackTrace();

                }

            }
        };
    }

    private synchronized boolean setImageNumber(long imageNumber) {
        if (imageNumber > this.imageNumber_) {
            this.imageNumber_ = imageNumber;
            return true;
        } else {
            return false;
        }
    }

    private long getInterval() {
        double interval = 20.0D;

        try {
            interval = Math.max(core.getExposure(), interval);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        long fpsInterval_ = (long) (20.0D * interval);
        if (fpsInterval_ < 1000L) {
            fpsInterval_ = 1000L;
        }

        return (long) ((int) interval);
    }

    public Thread runDisplayThread(BlockingQueue<TaggedImage> rawImageQueue) {
        final BlockingQueue<TaggedImage> processedImageQueue = ProcessorStack.run(rawImageQueue, engine.getImageProcessors());
        BlockingQueue<TaggedImage> q = ProcessorStack.run(rawImageQueue, engine.getImageProcessors());
        Thread displayThread = new Thread("Display thread") {
            public void run() {
                while (true) {
                    try {
                        TaggedImage image = processedImageQueue.take();
                        if (image != TaggedImageQueue.POISON) {
                            displayImageRoutine(image);
//                            System.out.println(System.currentTimeMillis());
                        }

                        if (image != TaggedImageQueue.POISON) {
                            continue;
                        }
                    } catch (InterruptedException var2) {
                        var2.printStackTrace();
                    }

                    return;
                }
            }
        };
        displayThread.start();
        return displayThread;
    }

    private void displayImageRoutine(TaggedImage image) {

        Platform.runLater(() -> {
            imageCanvas.updateImage(image);
        });

    }

    public void stopLivestream() {
//        stop.setDisable(true);
//        liveButton.setDisable(false);

        if (core.isSequenceRunning()) {
            try {
                core.stopSequenceAcquisition(core.getCameraDevice());
//                histogramDrawed = false;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void captureAndSave(String date, int x, int y, boolean singleCapture) {
        if (!core.isSequenceRunning()) {
            try {
                core.snapImage();
                TaggedImage image = core.getTaggedImage();
                displayImageRoutine(image);
                String path = "";
                String filename = "";
                if (singleCapture) {
                    path = "samples/";
                    filename = String.format("%d.tiff", System.currentTimeMillis());
                } else {
                    path = String.format("samples/%s/", date);
                    filename = String.format("%d-%d.tiff", x, y);

                }
                saveTaggedImageFile(image, path, filename);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void saveTaggedImageFile(TaggedImage image, String path, String tiffFileName) {
        ImagePlus imp = new ImagePlus("", ImageUtils.makeProcessor(image));
        saveImagePlus(imp, image.tags, path, tiffFileName);

    }



    @FXML
    void doCapture(ActionEvent event) {
        if (!core.isSequenceRunning()) {
            try {
                core.snapImage();
                TaggedImage image = core.getTaggedImage();

                imageCanvas.updateImage(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    void doCaptureSave(ActionEvent event) {
        captureAndSave(new Date().toString(), 0, 0, true);

    }

    private class TimerController {
        private final Object timerLock_;
        private java.util.Timer timer_;
        private boolean timerTaskShouldStop_;
        private boolean timerTaskIsBusy_;

        private TimerController() {
            this.timerLock_ = new Object();
            this.timerTaskShouldStop_ = true;
            this.timerTaskIsBusy_ = false;
        }

        public void start(final Runnable task, long interval) {
            synchronized (this.timerLock_) {
                if (this.timer_ == null) {
                    this.timer_ = new Timer("Live mode timer");
                    this.timerTaskShouldStop_ = false;
                    TimerTask timerTask = new TimerTask() {
                        public void run() {
                            synchronized (TimerController.this.timerLock_) {
                                if (TimerController.this.timerTaskShouldStop_) {
                                    return;
                                }

                                TimerController.this.timerTaskIsBusy_ = true;
                            }

                            boolean var11 = false;

                            try {
                                var11 = true;
                                task.run();
                                var11 = false;
                            } finally {
                                if (var11) {
                                    synchronized (TimerController.this.timerLock_) {
                                        TimerController.this.timerTaskIsBusy_ = false;
                                        TimerController.this.timerLock_.notifyAll();
                                    }
                                }
                            }

                            synchronized (TimerController.this.timerLock_) {
                                TimerController.this.timerTaskIsBusy_ = false;
                                TimerController.this.timerLock_.notifyAll();
                            }
                        }
                    };
                    this.timer_.schedule(timerTask, 0L, interval);
                }
            }
        }

        public void stop() {
            synchronized (this.timerLock_) {
                if (this.timer_ != null && !this.timerTaskShouldStop_) {
                    this.timerTaskShouldStop_ = true;
                    this.timer_.cancel();
                }
            }
        }

        public void waitForCompletion() {
            synchronized (this.timerLock_) {
                while (this.timerTaskIsBusy_) {
                    try {
                        this.timerLock_.wait();
                    } catch (InterruptedException var4) {
                        Thread.currentThread().interrupt();
                    }
                }

                this.timer_ = null;
            }
        }
    }


}
