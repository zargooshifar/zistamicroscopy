package main.Controllers;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import javafx.geometry.Point2D;
import main.ImageUtils.ImageCanvas;
import main.Singletons;
import main.tools.FxPath;
import main.utils.OverlayHelper;
import main.utils.SerialPortAdapter;
import mmcorej.CMMCore;
import org.openmuc.jrxtx.DataBits;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.SerialPort;
import org.openmuc.jrxtx.StopBits;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GalvoController {

    private static GalvoController instance;
    private static SerialPortAdapter serial;
    private GalvoController(){
        serial = new SerialPortAdapter("COM9");
    }




    public static GalvoController getInstance(){
        if(instance == null)
            instance = new GalvoController();
        return instance;
    }


    public static void run(){
//        CMMCore core = Singletons.getCoreInstance();
//        ImageCanvas imageCanvas = ImageCanvas.getInstance();
//
//
//        Point[] points = imageCanvas.getImage().getRoi().getContainedPoints();
//
//        int delay=0;
//        String lastCommand = "";
//        for(Point point: points){
//            int x = (int) ((point.x *(18.0/core.getImageWidth())) + 117);
//            int y = (int) ((point.y * (36.0/core.getImageHeight())) + 108);
//            String command = String.format(",%03d%03d",x, y);
//            if(command.equals(lastCommand))
//                continue;
//            lastCommand = command;
//            System.out.println(command);
//
//
//            Timer timer = new Timer();
//            int finalDelay = delay;
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    try {
//                        Singletons.getCoreInstance().setSerialPortCommand("COM9", command,",");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
////                    System.out.println(finalDelay);
//                    System.out.println(command);
//
//                }
//            },100*delay);
//
//            delay++;
//
        CMMCore core = Singletons.getCoreInstance();

        FxPath path = OverlayHelper.getInstance().getPath();
        if(path==null)
            return;


        System.out.println(path);
        List<String> commands = new ArrayList<>();
        String lastCommand ="";
        for (Point2D p: path.getPathOnImage()){
            int x = (int) p.getX();
            int y = (int) p.getY();

            System.out.println(String.format("%d %d",x,y));
            if(x<0 || y<0)
                continue;



//            int gx = (int) ((x *(-40.0/core.getImageWidth())) + 145);
//            int gy = (int) ((y * (-26.0/core.getImageHeight())) + 138);
            int gx = (int) ((x *(40.0/core.getImageWidth())) + 105);
            int gy = (int) ((y * (26.0/core.getImageHeight())) + 113);
            String command = String.format(",%03d%03d",gx, gy);
            if(command.equals(lastCommand)) {
                continue;
            }
            lastCommand = command;
            commands.add(command);

        }
        AtomicInteger i = new AtomicInteger();
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable = () -> {
            System.out.println(commands.get(i.get()));
            try {
//                core.setSerialPortCommand("COM9", commands.get(i.get()),",");
                serial.getSerialPort().getOutputStream().write(commands.get(i.get()).getBytes());
                
//                serial.getSerialPort().getOutputStream().flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
            i.getAndIncrement();
            if(i.get() >= commands.size()) {
                service.shutdown();
            }

        };
        service.scheduleAtFixedRate(runnable,0, 1000, TimeUnit.MILLISECONDS);






    }




}
