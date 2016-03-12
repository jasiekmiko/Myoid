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

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MyoidAccessibilityService extends AccessibilityService {
    private static MyoidAccessibilityService me;
    public static MyoidAccessibilityService getMyoidService() {
        if (me == null) throw new Error ("Myoid service not created.");
        return me;
    }

    private final String TAG = "Myoid service";

    private IMyoHubManager myoHubManager;
    private WindowManager windowManager;
    private Performer performer;

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

        myoHubManager = MyoHubManager.getInstance();
        myoHubManager.initializeHub(getPackageName());
        Log.i(TAG, "Service created.");

        Display display = windowManager.getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        performer = Performer.getInstance();
        performer.initCursor(screenSize);

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
        performer.hideCursor();
        serviceConnected = false;
        me = null;
    }

    private void startStatusActivity() {
        Intent intent = new Intent(this, StatusActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

