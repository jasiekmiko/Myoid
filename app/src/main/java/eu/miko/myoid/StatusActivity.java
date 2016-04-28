package eu.miko.myoid;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thalmic.myo.scanner.ScanActivity;

import javax.inject.Inject;

public class StatusActivity extends Activity {
    final static int REQUEST_FINE_LOCATION = 101;
    public static final int REQUEST_DRAWING_RIGHTS = 102;
    private static final int REQUEST_WIFI_PERMISSIONS = 103;
    private static final int REQUEST_FLASHLIGHT_PERMISSIONS = 104;
    private static final int REQUEST_SYSTEM_SETTINGS_PERMISSIONS = 105;
    private final String TAG = "StatusActivity";
    private Boolean injected = false;
    @Inject IPerformer performer;
    @Inject OverlayPermissionsRequester overlayPermissionsRequester;
    @Inject IMyoHubManager myoHubManager;
    private Button drawingOverlaysPermissionButton;
    private TextView accessibilityStatusText;
    private TextView mediaPermissionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, MyoidAccessibilityService.class));
        setContentView(R.layout.activity_status);
        initializeUiComponents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        boolean shouldUpdateUi = intent.getBooleanExtra("UPDATE_UI", false);
        if (shouldUpdateUi) updateUi();
    }

    private void updateUi() {
        ensureActivityInjected();
        updateDrawingOverlaysButtonText();
    }

    private void initializeUiComponents() {
        final Activity activity = this;
        initializeFAB();
        initializeDrawingPermissionsButton(activity);
        initializeOptionsButton();
        initializeAccessibilityStatusText();
        initializeMediaPermissionsStatus();
    }

    private void initializeMediaPermissionsStatus() {
        mediaPermissionStatus = (TextView) findViewById(R.id.mediaPermissionStatus);
        updateMediaPermissionStatusText();
        mediaPermissionStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMediaPermissionStatusText();
            }
        });
    }

    private void updateMediaPermissionStatusText() {
        mediaPermissionStatus.setText(isNotificationListenerConnected() ? "Media Permission Granted." : "Media permission not granted.");
    }

    private void initializeAccessibilityStatusText() {
        accessibilityStatusText = (TextView) findViewById(R.id.accessibilityStatus);
        updateAccessibilityStatusText();
        accessibilityStatusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAccessibilityStatusText();
            }
        });
    }

    private void updateAccessibilityStatusText() {
        accessibilityStatusText.setText(isServiceConnected() ? R.string.accessibilityStatusConnected : R.string.accessibilityStatusNotConnected);
    }

    private void initializeOptionsButton() {
        Button optionsViewTestButton = (Button) findViewById(R.id.myoidScreenTestButton);
        optionsViewTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ensureActivityInjected();
                performer.displayOptions();
            }
        });
    }

    private void initializeFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAllPermissionsAndLaunchMyoChooser();
            }
        });
    }

    private void checkAllPermissionsAndLaunchMyoChooser() {
        ensureActivityInjected();
        if (!isAccessibilityOn())
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        else if (!isNotificationListenerConnected())
            startNotificationsAccessSettingsScreen();
        else if (!overlayPermissionsRequester.checkDrawingPermissions(this))
            overlayPermissionsRequester.requestDrawingPermissions(this);
        else if (!WifiPermissionsGranted())
            requestPermission(new String[] {permission.ACCESS_WIFI_STATE, permission.CHANGE_WIFI_STATE}, REQUEST_WIFI_PERMISSIONS);
        else if (!isPermissionGranted(permission.FLASHLIGHT))
            requestPermission(new String[] {permission.FLASHLIGHT}, REQUEST_FLASHLIGHT_PERMISSIONS);
        else if (!isWriteSettingsPermissionGranted())
            requestWriteSettingsPermission();
        else if(!isPermissionGranted(permission.ACCESS_FINE_LOCATION))
            requestPermission(new String[]{permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        else
            checkHubStartedAndLaunchMyoChooser();
    }

    private void checkHubStartedAndLaunchMyoChooser() {
        if (myoHubManager.getIfHubInitialized()) {
            Intent intent = new Intent(this, ScanActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Log.e(TAG, "Hub not initialized.");
        }
    }

    private boolean isWriteSettingsPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return Settings.System.canWrite(this);
        else
            return isPermissionGranted(permission.WRITE_SETTINGS);
    }

    private void requestWriteSettingsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
        }
        else
            requestPermission(new String[] {permission.WRITE_SETTINGS}, REQUEST_SYSTEM_SETTINGS_PERMISSIONS);
    }


    private void requestPermission(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(this,
                permissions,
                requestCode);
    }

    private boolean isPermissionGranted(String permission) {
        int permissionGrant = ContextCompat.checkSelfPermission(this, permission);
        return permissionGrant == PackageManager.PERMISSION_GRANTED;
    }

    private boolean WifiPermissionsGranted() {
        int changeStatePermission = ContextCompat.checkSelfPermission(this, permission.CHANGE_WIFI_STATE);
        int accessStatePermission = ContextCompat.checkSelfPermission(this, permission.ACCESS_WIFI_STATE);
        if (changeStatePermission == PackageManager.PERMISSION_GRANTED
                && accessStatePermission == PackageManager.PERMISSION_GRANTED) {
            performer.setAreWifiPermissionsGranted(true);
            return true;
        }
        return false;
    }

    private boolean isNotificationListenerConnected() {
        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
    }

    private void startNotificationsAccessSettingsScreen() {
        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }

    private void initializeDrawingPermissionsButton(final Activity activity) {
        drawingOverlaysPermissionButton = (Button) findViewById(R.id.drawingOverlayPermission);
        drawingOverlaysPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ensureActivityInjected();
                overlayPermissionsRequester.checkAndRequestDrawingPermissions(activity);
            }
        });
    }

    private void updateDrawingOverlaysButtonText() {
        if (overlayPermissionsRequester.checkDrawingPermissions(this))
            drawingOverlaysPermissionButton.setText(R.string.mediaPermissionGranted);
        else
            drawingOverlaysPermissionButton.setText(R.string.mediaPermissionNotGranted);
    }

    private boolean isAccessibilityOn() {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + MyoidAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;

    }

    public void ensureActivityInjected() {
        if(!injected) {
            MyoidAccessibilityService.getMyoidService().getObjectGraph().inject(this);
            injected = true;
        }
    }

    private boolean isServiceConnected() {
        return MyoidAccessibilityService.isServiceConnected();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                if (arePermissionsGranted(grantResults)) {
                    Log.d(TAG, "Location permission granted.");
                    checkAllPermissionsAndLaunchMyoChooser();
                } else {
                    Log.i(TAG, "Location permission denied.");
                }
                break;
            }
            case REQUEST_DRAWING_RIGHTS: {
                if (arePermissionsGranted(grantResults)){
                    drawingOverlaysGranted();
                    checkAllPermissionsAndLaunchMyoChooser();
                }
                else
                    drawingOverlaysDenied();
                break;
            }
            case REQUEST_WIFI_PERMISSIONS: {
                if (arePermissionsGranted(grantResults)){
                    Log.d(TAG, "Wifi permissions granted.");
                    checkAllPermissionsAndLaunchMyoChooser();
                }
                else
                    Log.d(TAG, "Wifi permissions not granted.");
                break;
            }
            case REQUEST_FLASHLIGHT_PERMISSIONS: {
                if (arePermissionsGranted(grantResults)) {
                    Log.d(TAG, "Flashlight permissions granted");
                    checkAllPermissionsAndLaunchMyoChooser();
                } else
                    Log.d(TAG, "Flashlight permissions denied.");
                break;
            }
            case REQUEST_SYSTEM_SETTINGS_PERMISSIONS: {
                if (arePermissionsGranted(grantResults)) {
                    Log.d(TAG, "WRITE_SETTINGS permission denied.");
                    checkAllPermissionsAndLaunchMyoChooser();
                }
                else
                    Log.d (TAG, "WRITE_SETTINGS permission denied.");
                break;
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DRAWING_RIGHTS: {
                if (Settings.canDrawOverlays(MyoidAccessibilityService.getMyoidService())){
                    drawingOverlaysGranted();
                    checkAllPermissionsAndLaunchMyoChooser();
                }
                else
                    drawingOverlaysDenied();
                break;
            }
        }
    }

    private void drawingOverlaysDenied() {
        Log.i(TAG, "Drawing rights not granted.");
        updateDrawingOverlaysButtonText();
    }

    private void drawingOverlaysGranted() {
        Log.d(TAG, "drawing permissions granted");
        updateDrawingOverlaysButtonText();
    }


    private boolean arePermissionsGranted(@NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            Log.w(TAG, "Empty grantResults array");
            return false;
        }
        for (int grantResult : grantResults)
            if (grantResult == PackageManager.PERMISSION_DENIED) return false;
        return true;
    }
}
