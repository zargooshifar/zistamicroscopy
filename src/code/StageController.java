package code;

import code.ImageUtils.ImageCanvas;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import ij.CompositeImage;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.plugin.ContrastEnhancer;
import ij.process.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mmcorej.CMMCore;
import mmcorej.MMCoreJ;
import mmcorej.StrVector;
import mmcorej.TaggedImage;
import org.json.JSONException;
import org.json.JSONObject;
import org.micromanager.acquisition.*;
import org.micromanager.utils.ImageUtils;
import org.micromanager.utils.MDUtils;
import org.micromanager.utils.MMScriptException;
import org.micromanager.utils.PropertyItem;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StageController implements Initializable {

    private static CMMCore core;
    private static AcquisitionWrapperEngine engine;
    private final TimerController timerController_ = new TimerController();
    HamburgerBackArrowBasicTransition openMenuTask;
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
    private JFXButton viewScale;
    @FXML
    private JFXButton btn_capture;
    @FXML
    private JFXButton btn_capture_save;
    @FXML
    private JFXButton btn_startlive;
    @FXML
    private ChoiceBox<String> pixelTypePicker;
    @FXML
    private ChoiceBox<String> bitDepthPicker;
    @FXML
    private JFXToggleButton autoContrastToggle;
    @FXML
    private Pane viewPane;
    @FXML
    private ImageView cameraView;
    @FXML
    private Spinner<Integer> exposure;
    @FXML
    private Spinner<Integer> gain;
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
    @FXML
    private Label leftStatusText;
    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private Label rightStatusText;
    private Stage stage;
    private MMCoreJ studio;
    private String configAddress;
    private double smallStep = 10;
    private double mediumStep = 100;
    private double largeStep = 1000;
    private double xPos = 0;
    private double yPos = 0;
    private double xStartPoint = 0;
    private double yStartPoint = 0;
    private double xEndPoint = 1000;
    private double yEndPoint = 1000;
    private double horizentalSteps = 100;
    private double verticalSteps = 100;
    private StageArrowStates currentState = StageArrowStates.SMALL;
    private long fpsInterval_ = 5000L;
    private long fpsTimer_;
    private long fpsCounter_;
    private long imageNumber_;
    private long oldImageNumber_;
    private LinkedBlockingQueue<TaggedImage> imageQueue_;
    private boolean cameraViewRotated = false;
    private MMAcquisition mmAcquisition;
    private boolean autoContrast;
    private boolean isLive;
    private boolean histogramDrawed;
    private ImageCanvas imageCanvas;
    private int remainingCaptures;
    private float capturingProgress;
    private float capturingProgressStep;
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
    private double lastTimeStamp = 0;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        core = new CMMCore();
        engine = new AcquisitionWrapperEngine(new AcquisitionManager());
        loadConfig();


        imageCanvas = ImageCanvas.getInstance();
        imageCanvas.setSizeListener(viewPane);


        ImageProcessor imageProcessor = new ByteProcessor((int) core.getImageWidth(), (int) core.getImageHeight());
        try {
            core.setExposure(Double.valueOf(50));
            core.snapImage();
            imageProcessor.setPixels(core.getImage());
            imageProcessor.autoThreshold();

//            imageWindow = new ImageWindow(getImagePlus(core.getTaggedImage()));


        } catch (Exception e) {
            e.printStackTrace();
        }

        viewPane.getChildren().add(imageCanvas);

        try {
            imageCanvas.updateImage(getImagePlus(core.getTaggedImage()));
        } catch (Exception e) {
            e.printStackTrace();
        }


//        viewPane.widthProperty().addListener((observable, oldValue, newValue) -> {
//            swingNode.prefWidth(newValue.doubleValue());
//
////            jPanel.setSize(newValue.intValue(), jPanel.getHeight());
////            imageCanvas.setSize(newValue.intValue(),imageCanvas.getHeight());
////                imageCanvas.screenXD((double)newValue);
////            if(!cameraViewRotated){
////                cameraView.fitWidthProperty().setValue(newValue);
////            }else {
////                cameraView.fitHeightProperty().setValue(newValue);
////
////            }
//    });
//        viewPane.heightProperty().addListener((observable, oldValue, newValue) -> {
//            swingNode.prefHeight(newValue.doubleValue());
////            jPanel.setSize(jPanel.getWidth(), newValue.intValue());
////            imageCanvas.setSize(imageCanvas.getWidth(), newValue.intValue());
//
////                imageCanvas.screenYD((double) newValue);
////            if(!cameraViewRotated) {
////                cameraView.fitHeightProperty().setValue(newValue);
////
////
////            }else {
////                cameraView.fitWidthProperty().setValue(newValue);
////            }
//        });


        PropertyItem exposure_prop = new PropertyItem();
        exposure_prop.readFromCore(core, core.getCameraDevice(), "Exposure", false);


        exposure.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory((int) exposure_prop.lowerLimit, (int) exposure_prop.upperLimit, Integer.valueOf(exposure_prop.value), 10));
        exposure.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                core.setExposure(Double.valueOf(newValue));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        autoContrastToggle.selectedProperty().addListener((observable, oldValue, newValue) -> autoContrast = newValue);


        pixelTypePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                core.setProperty(core.getCameraDevice(), "PixelType", newValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        bitDepthPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                core.setProperty(core.getCameraDevice(), "BitDepth", newValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

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


//        viewPane.widthProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                imageCanvas.refreshSize();
//            }
//        });
//
//        viewPane.heightProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                imageCanvas.refreshSize();
//            }
//        });

    }

    private void invalidInput() {
        JFXSnackbar bar = new JFXSnackbar(viewPane);


        JFXButton button = new JFXButton();
        button.setText("Invalid Input!");
        button.setStyle("-fx-background-color: #fff;");
        button.setPrefHeight(50);
        button.setPrefWidth(200);
        Pane pane = new Pane();
        pane.setPadding(new Insets(16, 16, 16, 16));
        pane.getChildren().add(button);
        bar.enqueue(new JFXSnackbar.SnackbarEvent(pane));
    }

    private void updateHistogram(int[] histogram) {
//        Platform.runLater(() -> {
//            if(!histogramDrawed){
//                histogramChart.getData().clear();
//                histogramDrawed = true;
//                XYChart.Series dataSeries1 = new XYChart.Series();
//                for(int i = 0; i< histogram.length ; i++){
//                    dataSeries1.getData().add(new XYChart.Data(String.valueOf(i), histogram[i]));
//
//                }
//                histogramChart.getData().add(dataSeries1);
//
//            }
//        });

    }

    public ArrayList<PropertyItem> getProps() {
        ArrayList<PropertyItem> propList_ = new ArrayList<>();
        try {
            StrVector devices = core.getLoadedDevices();

            propList_.clear();

            for (int i = 0; i < devices.size(); i++) {
                StrVector properties = core.getDevicePropertyNames(devices.get(i));
                for (int j = 0; j < properties.size(); j++) {
                    PropertyItem item = new PropertyItem();
                    item.readFromCore(core, devices.get(i), properties.get(j), false);

                    if ((!item.readOnly) && !item.preInit) {
                        propList_.add(item);
                    }
                }

            }
        } catch (Exception e) {

        }
        return propList_;

    }

    public void loadConfig() {
        configAddress = "src/configs/MMConfig_demo.cfg";


        try {
//            core.loadDevice("Camera", "TwainCamera", "TwainCam");
//            core.initializeDevice("Camera");

            core.loadSystemConfiguration(configAddress);
            core.initializeAllDevices();
            loadCameraConfig();

//            String propBinning = core.getProperty("Camera", "Binning");
//            String propPixelType = core.getProperty("Camera", "PixelType");
//            String exp = core.getProperty("Camera", "Exposure");
//            System.out.println(propBinning);
//            System.out.println(propPixelType);
//            System.out.println(exp);
//            System.out.println("xxxx");

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void loadCameraConfig() throws Exception {
        StrVector pixelTypeValues = core.getAllowedPropertyValues("Camera", "PixelType");
        for (int i = 0; i < pixelTypeValues.size(); i++) {
            String val = pixelTypeValues.get(i);
            pixelTypePicker.getItems().add(val);
        }
        String defaultPixelType = pixelTypeValues.get((int) pixelTypeValues.size() - 1);
        pixelTypePicker.valueProperty().setValue(defaultPixelType);
        core.setProperty(core.getCameraDevice(), "PixelType", defaultPixelType);


        StrVector bitDepthValues = core.getAllowedPropertyValues("Camera", "BitDepth");
        for (int i = 0; i < bitDepthValues.size(); i++) {
            String val = bitDepthValues.get(i);
            bitDepthPicker.getItems().add(val);
        }
        String defaultBitDepth = bitDepthValues.get((int) bitDepthValues.size() - 1);
        bitDepthPicker.valueProperty().setValue(defaultBitDepth);
        core.setProperty(core.getCameraDevice(), "BitDepth", defaultBitDepth);


    }

    @FXML
    void toggleStageButtons(ActionEvent event) {

        switch (currentState) {
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
                currentState = StageArrowStates.MEDIUM;
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
                currentState = StageArrowStates.LARGE;
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
                currentState = StageArrowStates.SMALL;
                break;

        }

    }

    @FXML
    void changeViewScale(ActionEvent event) {
        imageCanvas.setKeepAspectRatio(!imageCanvas.getKeepAspectRatio());
    }

    @FXML
    void rotateLeft(ActionEvent event) {
        imageCanvas.rotate(-90);

    }

    @FXML
    void rotateRight(ActionEvent event) {
        imageCanvas.rotate(90);

    }

    @FXML
    void moveBottomLarge(ActionEvent event) {
        try {
            core.setRelativeXYPosition(0, -largeStep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPosition();
    }

    @FXML
    void moveBottomMedium(ActionEvent event) {
        try {

            core.setRelativeXYPosition(0, -mediumStep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPosition();
    }

    @FXML
    void moveBottomSmall(ActionEvent event) {
        try {
            core.setRelativeXYPosition(0, -smallStep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPosition();
    }

    @FXML
    void moveLeftLarge(ActionEvent event) {
        try {
            core.setRelativeXYPosition(-largeStep, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPosition();
    }

    @FXML
    void moveLeftMedium(ActionEvent event) {
        try {
            core.setRelativeXYPosition(-mediumStep, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPosition();
    }

    @FXML
    void moveLeftSmall(ActionEvent event) {
        try {
            core.setRelativeXYPosition(-smallStep, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPosition();
    }

    @FXML
    void moveRightLarge(ActionEvent event) {
        try {
            core.setRelativeXYPosition(largeStep, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPosition();
    }

    @FXML
    void moveRightMedium(ActionEvent event) {
        try {
            core.setRelativeXYPosition(mediumStep, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPosition();
    }

    @FXML
    void moveRightSmall(ActionEvent event) {
        try {
            core.setRelativeXYPosition(smallStep, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPosition();
    }

    @FXML
    void moveUpLarge(ActionEvent event) {
        try {
            core.setRelativeXYPosition(0, largeStep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPosition();
    }

    @FXML
    void moveUpMedium(ActionEvent event) {
        try {
            core.setRelativeXYPosition(0, mediumStep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPosition();
    }

    @FXML
    void moveUpSmall(ActionEvent event) {
        try {
            core.setRelativeXYPosition(0, smallStep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPosition();
    }

    private void move(Direction direction) {
        switch (direction) {
            case UP:
                switch (currentState) {
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
                switch (currentState) {
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
                switch (currentState) {
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
                switch (currentState) {
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
        refreshPosition();
    }

    private void refreshPosition() {
        try {
            xPos = core.getXPosition();
            yPos = core.getYPosition();
            Platform.runLater(() -> {
//                xlabel.textProperty().setValue(String.valueOf(xPos));
//                ylabel.textProperty().setValue(String.valueOf(yPos));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void stopLivestream() {
//        stop.setDisable(true);
//        liveButton.setDisable(false);

        if (core.isSequenceRunning()) {
            try {
                core.stopSequenceAcquisition(core.getCameraDevice());
                histogramDrawed = false;

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
        saveImagePlus(getImagePlus(image), image.tags, path, tiffFileName);

    }

    private ImagePlus getImagePlus(TaggedImage image) {

        int width = (int) core.getImageWidth();
        int height = (int) core.getImageHeight();
        ImageProcessor ip;
        String pixelType = null;
        Object img = image.pix;

        ImagePlus imagePlus = null;

        try {
            pixelType = MDUtils.getPixelType(image.tags);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MMScriptException e) {
            e.printStackTrace();
        }

        if (pixelType.equals("GRAY8")) {
            ip = new ByteProcessor(width, height);
            ip.setPixels(img);
            if (autoContrast) {
                new ContrastEnhancer().stretchHistogram(ip, 0.5);
            }
            imagePlus = new ImagePlus("", ip);
        } else if (pixelType.equals("GRAY16")) {
            ip = new ShortProcessor(width, height);
            ip.setPixels(img);
            if (autoContrast) {
                new ContrastEnhancer().stretchHistogram(ip, 0.5);
            }
            imagePlus = new ImagePlus("", ip);

        } else if (pixelType.equals("GRAY32")) {
            ip = new FloatProcessor(width, height);
            ip.setPixels(img);
            if (autoContrast) {
                new ContrastEnhancer().stretchHistogram(ip, 0.5);
            }
            imagePlus = new ImagePlus("", ip);

        } else if (pixelType.equals("RGB32")) {
            byte[][] planes = ImageUtils.getColorPlanesFromRGB32((byte[]) img);
            ColorProcessor cp = new ColorProcessor(width, height);
            cp.setRGB(planes[0], planes[1], planes[2]);
            if (autoContrast) {
                new ContrastEnhancer().stretchHistogram(cp, 0.5);
            }
            imagePlus = new ImagePlus("", cp);

        } else if (pixelType.equals("RGB64")) {
            short[][] planes = ImageUtils.getColorPlanesFromRGB64((short[]) img);
            ImageStack stack = new ImageStack(width, height);
            stack.addSlice("Red", planes[0]);
            stack.addSlice("Green", planes[1]);
            stack.addSlice("Blue", planes[2]);
            ImagePlus imp = new ImagePlus("", stack);
            imp.setDimensions(3, 1, 1);
            imp = new CompositeImage(imp, CompositeImage.COLOR);
            if (autoContrast) {
                new ContrastEnhancer().stretchHistogram(imp, 0.5);
            }
            imagePlus = imp;

        }


        return imagePlus;

    }

    @FXML
    void doCapture(ActionEvent event) {
        if (!core.isSequenceRunning()) {
            try {
                core.snapImage();
                TaggedImage image = core.getTaggedImage();

                imageCanvas.updateImage(getImagePlus(image));
//                displayImageRoutine(image);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


//        ImagePlus imagePlus = new ImagePlus();
//        imagePlus.setImage(imageProcessor.getBufferedImage());
//        imagePlus.show();

//        ImageCanvas imageCanvas = new ImageCanvas(imagePlus);
    }

    @FXML
    void doCaptureSave(ActionEvent event) {
        captureAndSave(new Date().toString(), 0, 0, true);

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
                throw new Exception("Camera did not send image within " + timeout + "ms");
            }

            TaggedImage timg = core.getLastTaggedImage();

            // With first image acquired, create the display
            displayImageRoutine(timg);

            long firstImageSequenceNumber = MDUtils.getSequenceNumber(timg.tags);

            synchronized (this) {
                fpsCounter_ = 0;
                fpsTimer_ = System.nanoTime();
                imageNumber_ = firstImageSequenceNumber;
                oldImageNumber_ = firstImageSequenceNumber;
            }
            imageQueue_ = new LinkedBlockingQueue<TaggedImage>(10);
            // XXX The logic here is very weird. We add this first image only if we
            // are using a single camera, because the single camera timer code checks
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

        this.fpsInterval_ = (long) (20.0D * interval);
        if (this.fpsInterval_ < 1000L) {
            this.fpsInterval_ = 1000L;
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

        Platform.runLater(() -> imageCanvas.updateImage(getImagePlus(image)));

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

        stopLivestream();
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


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int xStep = 0;
                int yStep = 0;
                changeStageControlsDisable(true);
                while (yStartPoint + yStep * verticalSteps < yEndPoint) {
                    while (xStartPoint + xStep * horizentalSteps < xEndPoint) {
                        while (yPos != yStartPoint + yStep * verticalSteps || xPos != xStartPoint + xStep * horizentalSteps) {
                            try {
                                core.setXYPosition(xStartPoint + xStep * horizentalSteps, yStartPoint + yStep * verticalSteps);
                                refreshPosition();
                                core.waitForDevice(core.getXYStageDevice());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        captureAndSave(date, xStep, yStep, false);
                        getEstimatedTimeRemaining();
                        setProgress();
                        try {
                            core.waitForDevice(core.getCameraDevice());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        xStep++;
                    }
                    xStep = 0;
                    yStep++;
                }
                changeStageControlsDisable(false);


            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

    }

    private void changeStageControlsDisable(boolean mode) {
        upButton.setDisable(mode);
        leftButton.setDisable(mode);
        rightButton.setDisable(mode);
        bottomButton.setDisable(mode);
        dupButton.setDisable(mode);
        dleftButton.setDisable(mode);
        drightButton.setDisable(mode);
        dbottomButton.setDisable(mode);
        tupButton.setDisable(mode);
        tleftButton.setDisable(mode);
        trightButton.setDisable(mode);
        tbottomButton.setDisable(mode);
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
            Platform.runLater(() -> leftStatusText.setText("calculating estimated remaining time..."));
        } else {
            int finalHour = hour;
            int finalMin = min;
            int finalSec = sec;
            Platform.runLater(() -> leftStatusText.setText(String.format("%d hours %d minutes %d seconds remaining...", finalHour, finalMin, finalSec)));
        }

        lastTimeStamp = now;
    }

    private void setProgress() {
        capturingProgress += capturingProgressStep;
        Platform.runLater(() -> {
            progressBar.setVisible(true);
            progressBar.setProgress(capturingProgress / 100);
            System.out.println(capturingProgress);
            rightStatusText.setText(String.format("%.2f %% ", capturingProgress));
        });

    }

    private enum StageArrowStates {SMALL, MEDIUM, LARGE}

    private enum Direction {UP, LEFT, RIGHT, BOTTOM}

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
