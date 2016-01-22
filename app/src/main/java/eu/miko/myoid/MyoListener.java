package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

public class MyoListener extends AbstractDeviceListener {
    private GestureResolver gestureResolver = GestureResolver.getInstance();
    private Performer performer = Performer.getInstance();

    @Override
    public void onConnect(Myo myo, long timestamp) {
        performer.shortToast("Myo Connected");
    }

    @Override
    public void onDisconnect(Myo myo, long timestamp) {
        performer.shortToast("Myo Disconnected");
    }

    @Override
    public void onPose(Myo myo, long timestamp, Pose pose) {
        gestureResolver.resolvePose(pose);
    }

}
