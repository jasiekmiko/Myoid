package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.thalmic.myo.Hub;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MyoidAccessibilityService extends AccessibilityService {
    private final String TAG = "Myoid service";
    private IMyoHubManager myoHubManager;
    private static MyoidAccessibilityService me;
    private WindowManager windowManager;
    private ImageView cursor;
    private WindowManager.LayoutParams cursorParams;
    private Point screenSize;
    private boolean cursorInitialized = false;
    protected boolean serviceConnected = false;

    public static MyoidAccessibilityService getMyoidService() {
        //if (me == null) throw new Error("Myoid service not created.");
        return me;
    }

    public static boolean isServiceConnected() {
        return me != null && me.serviceConnected;
    }

    @Override
    public void onCreate() {
        me = this;
        myoHubManager = MyoHubManager.getInstance();
        myoHubManager.initializeHub(getPackageName());
        Log.i(TAG, "Service created.");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        cursor = new ImageView(this);
        cursor.setImageResource(R.mipmap.ic_launcher);

        cursorParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        cursorParams.gravity = Gravity.TOP | Gravity.LEFT;
        cursorParams.x = 0;
        cursorParams.y = 100;

        Display display = windowManager.getDefaultDisplay();
        screenSize = new Point();
        display.getSize(screenSize);
    }

    @Override
    protected void onServiceConnected() {
        Toast.makeText(this, "service connected", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Service connected.");

        Hub hub = myoHubManager.getHub();
        hub.attachToAdjacentMyo();
        if (hub.getConnectedDevices().isEmpty() || !cursorInitialized) {
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
        if (cursor != null) windowManager.removeView(cursor);
        me = null;
        serviceConnected = false;
    }

    public void initializeCursor() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(cursor, cursorParams);
        cursorInitialized = true;
    }

    public void moveCursor(int x, int y) {
        if (cursorInitialized) {
            cursorParams.x = keepOnScreenX(cursorParams.x + x);
            cursorParams.y = keepOnScreenY(cursorParams.y + y);
            windowManager.updateViewLayout(cursor, cursorParams);
        }
    }

    private int keepOnScreenY(int y) {
        return max(0, min(y, screenSize.y));
    }

    private int keepOnScreenX(int x) {
        return max(0, min(x, screenSize.x));
    }

    private void startStatusActivity() {
        Intent intent = new Intent(this, StatusActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

