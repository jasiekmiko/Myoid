package eu.miko.myoid;

import android.content.Context;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Hub;

public class MyoHubManager implements IMyoHubManager {
    private static MyoHubManager instance;
    private MyoHubManager(){}
    public static MyoHubManager getInstance() {
        if (instance == null) instance = new MyoHubManager();
        return instance;
    }

    private Hub hub = Hub.getInstance();
    private AbstractDeviceListener listener = new MyoListener();

    public Boolean hubInitialized = false;

    @Override
    public void initializeHub(String packageName) {
        if (!hubInitialized) {
            Context service = MyoidAccessibilityService.getMyoidService();
            hubInitialized = hub.init(service, packageName);
            hub.setLockingPolicy(Hub.LockingPolicy.STANDARD);
            hub.addListener(listener);
        }
    }

    @Override
    public Hub getHub() {
        return hub;
    }
}
