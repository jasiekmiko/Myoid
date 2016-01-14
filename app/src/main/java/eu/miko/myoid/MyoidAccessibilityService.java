package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.thalmic.myo.Hub;

import static eu.miko.myoid.SharedServiceResources.getHub;
import static eu.miko.myoid.SharedServiceResources.initializeHub;

public class MyoidAccessibilityService extends AccessibilityService {
    private final String TAG = "Myoid service";


    @Override
    public void onCreate() {
        initializeHub(this, getPackageName());
        Log.i(TAG, "Service created.");
    }

    @Override
    protected void onServiceConnected() {
        Toast.makeText(this, "service connected", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Service connected.");

        Hub hub = getHub();
        hub.attachToAdjacentMyo();
        if (hub.getConnectedDevices().isEmpty()) {
            startStatusActivity();
        }
    }

    private void startStatusActivity() {
        Intent intent = new Intent(this, StatusActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

}
