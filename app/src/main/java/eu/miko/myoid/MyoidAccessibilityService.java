package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.thalmic.myo.Hub;

import static eu.miko.myoid.SharedServiceResources.getHub;
import static eu.miko.myoid.SharedServiceResources.initializeHub;
import static eu.miko.myoid.SharedServiceResources.registerMyoidAccessibilityService;

public class MyoidAccessibilityService extends AccessibilityService {
    private final String TAG = "Myoid service";
    private WindowManager windowManager;
    private ImageView cursor;
    private WindowManager.LayoutParams params;
    private boolean cursorInitialized = false;


    @Override
    public void onCreate() {
        registerMyoidAccessibilityService(this);
        initializeHub(getPackageName());
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        cursor = new ImageView(this);
        cursor.setImageResource(R.mipmap.ic_launcher);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        Log.i(TAG, "Service created.");
    }

    @Override
    protected void onServiceConnected() {
        Toast.makeText(this, "service connected", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Service connected.");

        Hub hub = getHub();
        hub.attachToAdjacentMyo();
        if (hub.getConnectedDevices().isEmpty() || !cursorInitialized) {
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
        super.onDestroy();
        if (cursor != null) windowManager.removeView(cursor);
    }

    public void initializeCursor() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(cursor, params);
        cursorInitialized = true;
    }

    public void moveCursor(int x, int y) {
        if (cursorInitialized) {
            params.x = params.x + x;
            params.y = params.y + y;
            windowManager.updateViewLayout(cursor, params);
        }
    }

}
