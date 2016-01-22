package eu.miko.myoid;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Hub;

public class MyoHubManager implements IMyoHubManager {
    private static MyoHubManager instance;
    public static MyoHubManager getInstance() {
        if (instance == null) instance = new MyoHubManager();
        return instance;
    }

    private Hub hub = Hub.getInstance();
    private AbstractDeviceListener listener = null;

    public Boolean hubInitialized = false;

    private MyoHubManager(){}

    @Override
    public void initializeHub(MyoidAccessibilityService mas, String packageName) {
        if (!hubInitialized) {
            hubInitialized = hub.init(mas, packageName);
            hub.setLockingPolicy(Hub.LockingPolicy.STANDARD);
            if (listener == null) {
                listener = new MyoListener(mas);
            }
            hub.addListener(listener);
        }
    }

    @Override
    public Hub getHub() {
        return hub;
    }
}
