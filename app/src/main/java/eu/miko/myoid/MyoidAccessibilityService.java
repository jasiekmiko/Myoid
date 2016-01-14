package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

public class MyoidAccessibilityService extends AccessibilityService {
    private final String TAG = "Myoid service";


    @Override
    public void onCreate() {
        SharedServiceResources.initializeHub(this, getPackageName());
        Log.i(TAG, "Service created.");
    }

    @Override
    protected void onServiceConnected() {
        Toast.makeText(this, "service connected", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Service connected.");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

}
