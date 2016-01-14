package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

public class MyoListener extends AbstractDeviceListener {

    private final AccessibilityService mService;

    public MyoListener(AccessibilityService service) {
        mService = service;
    }

    @Override
    public void onConnect(Myo myo, long timestamp) {
        shortToast("Myo Connected");
    }

    @Override
    public void onDisconnect(Myo myo, long timestamp) {
        shortToast("Myo Disconnected");
    }

    @Override
    public void onPose(Myo myo, long timestamp, Pose pose) {
        switch (pose) {
            case FIST:
                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                break;
            case WAVE_IN:
                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case WAVE_OUT:
                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                break;
            case FINGERS_SPREAD:
                mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
        }
        shortToast("Pose: " + pose);
    }

    private void shortToast(String text) {
        Toast.makeText(mService, text, Toast.LENGTH_SHORT).show();
    }
}
