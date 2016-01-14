package eu.miko.myoid;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Hub;

public class SharedServiceResources {
    private static Hub hub = Hub.getInstance();
    private static AbstractDeviceListener listener = null;

    public static Boolean hubInitialized = false;

    public static void initializeHub(MyoidAccessibilityService mas, String packageName) {
        if (!hubInitialized) {
            hubInitialized = hub.init(mas, packageName);
            hub.setLockingPolicy(Hub.LockingPolicy.STANDARD);
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
