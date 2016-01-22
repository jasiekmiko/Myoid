package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.thalmic.myo.Hub;

public class MyoidAccessibilityService extends AccessibilityService {
    private final String TAG = "Myoid service";
    private IMyoHubManager myoHubManager = MyoHubManager.getInstance();
    private static MyoidAccessibilityService me;

    public static MyoidAccessibilityService getMyoidService() {
        if(me == null) throw new Error("Myoid service not created.");
        return me;
    }

    @Override
    public void onCreate() {
        myoHubManager.initializeHub(getPackageName());
        Log.i(TAG, "Service created.");
        me = this;
    }

    @Override
    protected void onServiceConnected() {
        Toast.makeText(this, "service connected", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Service connected.");

        Hub hub = myoHubManager.getHub();
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

    @Override
    public void onDestroy() {
        me = null;
    }
}
