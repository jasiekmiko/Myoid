package eu.miko.myoid;

import android.app.IntentService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class IntentHandlerService extends IntentService {
    public static final String EXTRA_TEXT_BOX_SOURCE = "eu.miko.myoid.IntentHandlerServices.extraTextBoxSource";

    public IntentHandlerService() {
        super(IntentHandlerService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<String> strings = intent.getExtras().getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
        AccessibilityNodeInfo source = intent.getExtras().getParcelable(EXTRA_TEXT_BOX_SOURCE);
        if (strings != null && source != null) {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("voiceTextInput", strings.get(0));
            clipboardManager.setPrimaryClip(clipData);
            SystemClock.sleep(1000);
            source.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }
}
