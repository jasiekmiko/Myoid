package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;

import com.thalmic.myo.Pose;

public class GestureResolver {
    private static GestureResolver instance;
    private GestureResolver() {}
    public static GestureResolver getInstance() {
        if (instance == null) instance = new GestureResolver();
        return instance;
    }

    private Performer performer = Performer.getInstance();
    private AccessibilityService service = MyoidAccessibilityService.getMyoidService();

    public void resolvePose(Pose pose) {
        switch (pose) {
            case FIST:
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                break;
            case WAVE_IN:
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case WAVE_OUT:
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                break;
            case FINGERS_SPREAD:
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
        }
        performer.shortToast("Pose: " + pose);
    }

}
