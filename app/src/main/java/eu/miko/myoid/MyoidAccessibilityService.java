package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.app.PendingIntent;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.thalmic.myo.Hub;

import javax.inject.Inject;

import dagger.ObjectGraph;

public class MyoidAccessibilityService extends AccessibilityService {
    static final int REQUEST_VOICE_INPUT = 101;
    private static MyoidAccessibilityService me;
    private ObjectGraph objectGraph;

    public static MyoidAccessibilityService getMyoidService() {
        if (me == null) throw new Error("Myoid service not created.");
        return me;
    }

    private final String TAG = "Myoid service";

    @Inject
    IMyoHubManager myoHubManager;

    protected boolean serviceConnected = false;

    public static boolean isServiceConnected() {
        return me != null && me.serviceConnected;
    }

    @Override
    public void onCreate() {
        me = this;
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
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
            startStatusActivity(false);
        }
        serviceConnected = true;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getClassName().equals(EditText.class.getName()))
            startVoiceInput(event.getSource());
    }

    private void startVoiceInput(AccessibilityNodeInfo source) {
        Intent inputTextIntent = new Intent(this, IntentHandlerService.class);
        inputTextIntent.putExtra(IntentHandlerService.EXTRA_TEXT_BOX_SOURCE, source);
        PendingIntent pendingIntent = PendingIntent.getService(this, REQUEST_VOICE_INPUT, inputTextIntent, PendingIntent.FLAG_ONE_SHOT);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, pendingIntent);
        startActivity(intent);
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

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    void startStatusActivity(boolean updateUi) {
        Intent intent = new Intent(this, StatusActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("UPDATE_UI", updateUi);
        startActivity(intent);
    }

}

