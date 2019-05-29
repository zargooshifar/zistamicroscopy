package main.utils;

import mmcorej.CMMCore;
import mmcorej.StrVector;
import org.micromanager.utils.PropertyItem;

import java.util.ArrayList;

public class deprecatedsMethods {


    //    private void updateHistogram(ImagePlus imagePlus) {
//
//        HistogramWindow histogramWindow = new HistogramWindow(imagePlus);
//        histogramWindow.showHistogram(imagePlus, 0);
//
//        JPanel jPanel = new JPanel();
//        jPanel.add(histogramWindow.getCanvas());
//        SwingNode swingNode = new SwingNode();
//        swingNode.setContent(jPanel);
//
//        histogramPanel.getChildren().add(swingNode);
//
//    }

    public ArrayList<PropertyItem> getProps() {
        CMMCore core = new CMMCore();
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

}
