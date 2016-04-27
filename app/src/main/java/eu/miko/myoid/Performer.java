package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;

import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Performer implements IPerformer {
    private static final String TAG = Performer.class.getName();
    private final OptionsController optionsController;
    private final OverlayPermissionsRequester overlayPermissionsRequester;
    private final MouseController mouseController;
    private MediaSessionManager mediaSessionManager;
    private List<MediaController> mediaControllers = null;
    boolean isNotificationListenerStarted = false;
    private MyoidAccessibilityService mas = MyoidAccessibilityService.getMyoidService();
    private ComponentName nlComponentName = new ComponentName(mas, MyoidNotificationListener.class);

    @Inject
    public Performer(OptionsController optionsController, OverlayPermissionsRequester overlayPermissionsRequester, MouseController mouseController) {
        this.optionsController = optionsController;
        this.overlayPermissionsRequester = overlayPermissionsRequester;
        this.mouseController = mouseController;

        startNotificationListener();
    }

    @Override
    public void initializeMediaControllers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSessionManager = (MediaSessionManager) mas.getSystemService(Context.MEDIA_SESSION_SERVICE);
            addActiveSessionsListener();
            updateMediaControllers();
        } else displayMediaControlsNotImplementedWarning();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void addActiveSessionsListener() {
        Looper.prepare();
        Handler handler = new Handler();
        mediaSessionManager.addOnActiveSessionsChangedListener(new MyoidOnActiveSessionsChangedListener(), nlComponentName, handler);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void updateMediaControllers() {
        mediaControllers = mediaSessionManager.getActiveSessions(nlComponentName);
    }

    private void startNotificationListener() {
        Intent intent = new Intent(mas, MyoidNotificationListener.class);
        mas.startService(intent);
        isNotificationListenerStarted = true;
    }

    @Override
    public void displayMediaControlsNotImplementedWarning() {
        String warningMessage = "Media controls not implemented for this version of Android.";
        Log.w(TAG, warningMessage);
        shortToast(warningMessage);
    }

    private Myo myo;

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
    public void hideOptions() {
        optionsController.dismissOptions();
    }

    @Override
    public void performMediaAction(Media.Action action) {
        if (mediaControllers == null) startNotificationListener();
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            updateMediaControllers();
            for (MediaController mc : mediaControllers) {
                MediaController.TransportControls transportControls = mc.getTransportControls();
                switch(action){
                    case NEXT:
                        transportControls.skipToNext();
                        break;
                    case PREV:
                        transportControls.skipToPrevious();
                        break;
                    case PLAY_PAUSE:
                        PlaybackState playbackState = mc.getPlaybackState();
                        int state = playbackState != null ? playbackState.getState() : PlaybackState.STATE_NONE;
                        switch (state) {
                            case PlaybackState.STATE_PAUSED:
                            case PlaybackState.STATE_STOPPED:
                                transportControls.play();
                                break;
                            case PlaybackState.STATE_PLAYING:
                                transportControls.pause();
                        }
                        break;
                }
            }
        } else
            displayMediaControlsNotImplementedWarning();
    }

    @Override
    public void changePointerImage(Pose pose) {
        optionsController.changePointerImage(getPoseImage(pose));
    }

    @Override
    public void changeCursorImage(Pose pose) {
        mouseController.setCursorResource(getPoseImage(pose));
    }

    public int getPoseImage(Pose pose) {
        int resource = R.drawable.cursor_pan;
        switch (pose) {
            case REST:
                resource = R.drawable.cursor_pan;
                break;
            case FIST:
                resource = R.drawable.cursor_fist;
                break;
            case WAVE_IN:
                resource = R.drawable.cursor_left;
                break;
            case WAVE_OUT:
                resource = R.drawable.cursor_right;
                break;
            case FINGERS_SPREAD:
                resource = R.drawable.cursor_spread;
                break;
            case DOUBLE_TAP:
                resource = R.drawable.cursor_double_tap;
                break;
        }
        return resource;
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class MyoidOnActiveSessionsChangedListener implements MediaSessionManager.OnActiveSessionsChangedListener {
        @Override
        public void onActiveSessionsChanged(List<MediaController> controllers) {
            mediaControllers = controllers;
        }
    }
}
