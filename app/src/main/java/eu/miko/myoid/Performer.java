package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.widget.Toast;

import com.thalmic.myo.Myo;

public class Performer {
    private static Performer instance;
    private Performer() {}
    public static Performer getInstance() {
        if (instance == null) instance = new Performer();
        return instance;
    }

    private Myo myo;
    private AccessibilityService service = MyoidAccessibilityService.getMyoidService();

    public void shortToast(String text) {
        Toast.makeText(service, text, Toast.LENGTH_SHORT).show();
    }

    public void setMyo(Myo myo) {
        this.myo = myo;
    }

    public void unlockMyo() {
        myo.unlock(Myo.UnlockType.HOLD);
        myo.vibrate(Myo.VibrationType.MEDIUM);
    }

    public void openNotifications() {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
    }

    public void openRecents() {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
    }

    public void goBack() {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    public void goHome() {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }
}
