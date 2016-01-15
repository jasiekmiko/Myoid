package eu.miko.myoid;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Hub;

public class SharedServiceResources {
    private static Hub hub = Hub.getInstance();
    private static AbstractDeviceListener listener = null;

    public static Boolean hubInitialized = false;

    public static boolean serviceConnected = false;
    public static MyoidAccessibilityService mas;

    public static void registerMyoidAccessibilityService(MyoidAccessibilityService service) {
        mas = service;
    }

    public static void initializeHub(String packageName) {
        if (!hubInitialized) {
            hubInitialized = hub.init(mas, packageName);
            hub.setLockingPolicy(Hub.LockingPolicy.NONE);
            if (listener == null) {
                listener = new MyoListener(mas);
            }
            hub.addListener(listener);
        }
    }

    public static Hub getHub() {
        return hub;
    }
}
