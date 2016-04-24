package eu.miko.myoid;

import android.content.Context;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Hub;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyoHubManager implements IMyoHubManager {
    @Inject
    public MyoHubManager(MyoListener listener){
        this.listener = listener;
    }

    private Hub hub = Hub.getInstance();
    private AbstractDeviceListener listener;
    private Boolean hubInitialized = false;

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

    public boolean getIfHubInitialized() {
        return hubInitialized;
    }
}
