package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.widget.Toast;

import com.thalmic.myo.Myo;

public class Performer {
    private static Performer instance;
    private Myo myo;

    private Performer() {}
    public static Performer getInstance() {
        if (instance == null) instance = new Performer();
        return instance;
    }

    private AccessibilityService service = MyoidAccessibilityService.getMyoidService();

    void shortToast(String text) {
        Toast.makeText(service, text, Toast.LENGTH_SHORT).show();
    }

    private void openNotifications() {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
    }

    private void openRecents() {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
    }

    private void goBack() {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    private void goHome() {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    public void setMyo(Myo myo) {
        this.myo = myo;
    }

    public Boolean execute(ActionCode actionCode, Gesture gesture) {
        switch (actionCode) {
            case GO_HOME:
                goHome();
                break;
            case GO_BACK:
                goBack();
                break;
            case OPEN_RECENTS:
                openRecents();
                break;
            case OPEN_NOTIFICATIONS:
                openNotifications();
                break;
        }
        return true;
    }

    public enum ActionCode {
        GO_HOME,
        GO_BACK,
        OPEN_RECENTS,
        OPEN_NOTIFICATIONS,
    }
}
