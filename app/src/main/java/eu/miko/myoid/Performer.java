package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
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
    private final MouseController mouseController;
    private MediaSessionManager mediaSessionManager;
    private List<MediaController> mediaControllers = null;
    boolean isNotificationListenerStarted = false;
    private MyoidAccessibilityService mas = MyoidAccessibilityService.getMyoidService();
    private ComponentName nlComponentName = new ComponentName(mas, MyoidNotificationListener.class);

    @Inject
    public Performer(OptionsController optionsController, MouseController mouseController) {
        this.optionsController = optionsController;
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
        if (mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS))
            Log.d(TAG, "openNotifications performed.");
        else
            Log.d(TAG, "openNotifications failed.");
    }

    @Override
    public void openRecents() {
        if (mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS))
            Log.d(TAG, "openRecents performed.");
        else
            Log.d(TAG, "openRecents failed.");
    }

    @Override
    public void goBack() {
        if (mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK))
            Log.d(TAG, "Back global action performed");
        else
            Log.d(TAG, "Back global action failed.");
    }

    @Override
    public void goHome() {
        if (mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME))
            Log.d(TAG, "Home global action performed.");
        else
            Log.d(TAG, "Home global action failed. Connection missing.");
    }

    @Override
    public void displayCursor() {
        if (PermissionsController.checkDrawingPermissions(mas))
            mouseController.displayCursor();
        else
            mas.startStatusActivity(true);
    }

    @Override
    public void displayOptions() {
        if (PermissionsController.checkDrawingPermissions(mas))
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
                switch (action) {
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
        OptionsController.Icon hitIcon = optionsController.movePointerByAndChooseIconIfHit(x, y);
        if (hitIcon != null) {
            Event resultingEvent = performOption(hitIcon);
            optionsController.resetScreen();
            return resultingEvent;
        }
        return null;
    }

    @Override
    public boolean optionsGoBack() {
        return optionsController.goBack();
    }

    private Event performOption(OptionsController.Icon target) {
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
        else if (target instanceof OptionsController.NavIcon) {
            switch ((OptionsController.NavIcon) target) {
                case BACK:
                    goBack();
                    break;
                case HOME:
                    goHome();
                    break;
                case RECENT:
                    openRecents();
                    break;
            }
            return Event.OPTION_SELECTED;
        } else if (target instanceof OptionsController.QsIcon)
            switch ((OptionsController.QsIcon) target) {
                case WIFI:
                    toggleWifi();
                    break;
                case TORCH:
                    checkSystemVersionAndToggleTorch();
                    break;
                case RINGER:
                    cycleRingerMode();
                    break;
                case ORIENTATION:
                    toggleOrientation();
                    break;
            }
        return null;
    }

    private void cycleRingerMode() {
        AudioManager audioManager = (AudioManager) mas.getSystemService(Context.AUDIO_SERVICE);
        int currentMode = audioManager.getRingerMode();
        audioManager.setRingerMode((currentMode + 1) % 3);
    }

    private void toggleOrientation() {
        try {
            ContentResolver cr = mas.getContentResolver();
            int currentState = Settings.System.getInt(cr, Settings.System.ACCELEROMETER_ROTATION);
            Settings.System.putInt(cr, Settings.System.ACCELEROMETER_ROTATION, 1 - currentState);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkSystemVersionAndToggleTorch() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            toggleTorch();
        } else {
            String message = "Torch not available for devices with this system version (below M).";
            shortToast(message);
            Log.d(TAG, message);
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void toggleTorch() {
        try {
            CameraManager cameraManager = (CameraManager) mas.getSystemService(Context.CAMERA_SERVICE);
            String[] cameras = cameraManager.getCameraIdList();
            for (String camera : cameras) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(camera);
                //noinspection ConstantConditions
                boolean flashlightPresent = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (flashlightPresent) {
                    cameraManager.setTorchMode(camera, !Options.torchOn);
                    Options.torchOn = !Options.torchOn;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void toggleWifi() {
        WifiManager wifiManager = (WifiManager) mas.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
    }

    private void openVoiceSearch() {
        Intent intent = new Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE);
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
