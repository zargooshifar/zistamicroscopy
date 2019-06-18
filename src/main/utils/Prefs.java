package main.utils;

import java.util.prefs.Preferences;

public class Prefs {

    private static Prefs instance;
    private Prefs(){}

    Preferences preferences = Preferences.userRoot().node(this.getClass().getName());

    public static Prefs getInstance(){
        if(instance == null)
            instance = new Prefs();
        return instance;
    }




    public static class Window{

        public static boolean isStageState() {
            return Prefs.getInstance().preferences.getBoolean("stageState", true);
        }

        public static void setStageState(boolean value) {
            Prefs.getInstance().preferences.putBoolean("stageState", value);

        }

        public static boolean isHistogram() {
            return Prefs.getInstance().preferences.getBoolean("histogram", true);
        }

        public static void setHistogram(boolean value) {
            Prefs.getInstance().preferences.putBoolean("histogram", value);
        }

        public static boolean isDeviceProperty() {
            return Prefs.getInstance().preferences.getBoolean("deviceProperty", true);
        }

        public static void setDeviceProperty(boolean value) {
            Prefs.getInstance().preferences.putBoolean("deviceProperty", value);
        }
    }


}
