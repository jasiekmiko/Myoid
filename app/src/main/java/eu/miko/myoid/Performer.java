package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Performer implements IPerformer {
    private static final String TAG = Performer.class.getName();
    private final OptionsController optionsController;
    private final OverlayPermissionsRequester overlayPermissionsRequester;
    private final MouseController mouseController;

    @Inject
    public Performer(WindowManager windowManager, OptionsController optionsController, OverlayPermissionsRequester overlayPermissionsRequester, MouseController mouseController, MouseController mouseController1) {
        this.optionsController = optionsController;
        this.overlayPermissionsRequester = overlayPermissionsRequester;
        this.mouseController = mouseController1;
    }

    private Myo myo;

    private MyoidAccessibilityService mas = MyoidAccessibilityService.getMyoidService();

    @Override
    public void shortToast(String text) {
        Toast.makeText(mas, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setMyo(Myo myo) {
        this.myo = myo;
    }

    @Override
    public void lockMyo() {
        myo.lock();
        myo.vibrate(Myo.VibrationType.SHORT);
    }

    @Override
    public void unlockMyoTimed() {
        myo.unlock(Myo.UnlockType.HOLD);
        myo.vibrate(Myo.VibrationType.SHORT);
    }

    @Override
    public void unlockMyoHold() {
        myo.unlock(Myo.UnlockType.HOLD);
        myo.vibrate(Myo.VibrationType.SHORT);
    }

    @Override
    public void openNotifications() {
        mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
    }

    @Override
    public void openRecents() {
        mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
    }

    @Override
    public void goBack() {
        mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        Log.d(TAG, "Back global action performed.");
    }

    @Override
    public void goHome() {
        if (mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME))
            Log.d(TAG, "Home global action performed.");
        else
            Log.d(TAG, "Home global action attempted but failed. Connection missing.");
    }

    @Override
    public void initCursorAndCursorParams() {

        mouseController.initCursorAndCursorParams();
    }

    @Override
    public void displayCursor() {
        if (overlayPermissionsRequester.checkDrawingPermissions(mas))
            mouseController.displayCursor();
        else
            mas.startStatusActivity(true);
    }

    @Override
    public void displayOptions() {
        if (overlayPermissionsRequester.checkDrawingPermissions(mas))
            optionsController.displayOptions();
        else
            mas.startStatusActivity(true);
    }

    @Override
    public void dismissOptions() {
        optionsController.dismissOptions();
    }

    @Override
    public void changeCursorImage(Pose pose) {
        mouseController.changeCursorImage(pose);
    }

    @Override
    public void moveCursor(int x, int y) {
        mouseController.moveCursor(x, y);
    }

    @Override
    public void hideCursor() {
        mouseController.hideCursor();
    }

    @Override
    public void mouseScroll(boolean down) {
        String result = mouseController.mouseScroll(down);
        if (result != null) shortToast(result);
    }

    @Override
    public void mouseTap() {
        String result = mouseController.mouseTap();
        if (result != null) shortToast(result);
    }

    @Override
    public Event moveOptionsPointerBy(int x, int y) {
        Icon targetIcon = optionsController.movePointerBy(x, y);
        if (targetIcon != null) {
            optionsController.resetPointerToCenter();
            return performOption(targetIcon);
        }
        return null;
    }

    @Override
    public boolean optionsGoBack() {
        return optionsController.goBack();
    }

    private Event performOption(Icon target) {
        if (target instanceof OptionsController.MainIcon)
            switch ((OptionsController.MainIcon) target) {
                case SEARCH:
                    openVoiceSearch();
                    return Event.OPTION_SELECTED;
                case MEDIA_MOUSE:
                    return Event.SWITCH_MODE;
                case NAV:
                    optionsController.showIconSet(OptionsController.IconSet.NAV);
                    break;
                case QS:
                    optionsController.showIconSet(OptionsController.IconSet.QS);
                    break;
            }
        else if (target instanceof OptionsController.NavIcon)
            switch ((OptionsController.NavIcon) target) {
                case BACK:
                    goBack();
                    return Event.OPTION_SELECTED;
                case HOME:
                    goHome();
                    return Event.OPTION_SELECTED;
                case RECENT:
                    break;
            }
        else if (target instanceof OptionsController.QsIcon)
            switch ((OptionsController.QsIcon) target) {
                case WIFI:
                    break;
                case TORCH:
                    break;
                case MUTE:
                    break;
                case GPS:
                    break;
            }
        return null;
    }

    private void openVoiceSearch() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mas.startActivity(intent);
    }
}
