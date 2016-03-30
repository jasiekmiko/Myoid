package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.thalmic.myo.Hub;

import javax.inject.Inject;

import dagger.ObjectGraph;

public class MyoidAccessibilityService extends AccessibilityService {
    private static MyoidAccessibilityService me;
    private ObjectGraph objectGraph;

    public static MyoidAccessibilityService getMyoidService() {
        if (me == null) throw new Error ("Myoid service not created.");
        return me;
    }

    private final String TAG = "Myoid service";

    @Inject IMyoHubManager myoHubManager;
    private WindowManager windowManager;

    protected boolean serviceConnected = false;

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public static boolean isServiceConnected() {
        return me != null && me.serviceConnected;
    }

    @Override
    public void onCreate() {
        me = this;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        objectGraph = ObjectGraph.create(new DaggerModule(this, windowManager));
        objectGraph.inject(this);

        myoHubManager.initializeHub(getPackageName());
        Log.i(TAG, "Service created.");
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
        serviceConnected = true;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceConnected = false;
        me = null;
    }

    public ObjectGraph getObjectGraph() { return objectGraph; }

    private void startStatusActivity() {
        Intent intent = new Intent(this, StatusActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

