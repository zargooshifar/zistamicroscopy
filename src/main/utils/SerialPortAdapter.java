package main.utils;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.SerialPort;
import org.openmuc.jrxtx.SerialPortBuilder;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

public class SerialPortAdapter {

    private BufferedReader input;
    private OutputStream output;
    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 9600;

    private SerialPort serialPort;


    public SerialPortAdapter(String port){
        try {
            initialize(port);
        } catch (IOException e) {
            System.out.println("exeption!");
        } catch (NoSuchPortException e) {
            System.out.println("no such port");
        } catch (PortInUseException e) {
            System.out.println("port in use");
        }
    }


    public void initialize(String portName) throws IOException, NoSuchPortException, PortInUseException {

        serialPort = SerialPortBuilder.newBuilder(portName).setBaudRate(9600).setParity(Parity.NONE).build();

        input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        output = serialPort.getOutputStream();



    }

    public OutputStream getOutput() {
        return output;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
    }

    public BufferedReader getInput() {
        return input;
    }

    public void setInput(BufferedReader input) {
        this.input = input;
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }


//    public synchronized void close() {
//        if (serialPort != null) {
//            serialPort.removeEventListener();
//            serialPort.close();
//        }
//    }



}
