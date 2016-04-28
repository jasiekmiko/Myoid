package eu.miko.myoid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thalmic.myo.scanner.ScanActivity;

import javax.inject.Inject;

public class StatusActivity extends Activity {
    static final int REQUEST_FINE_LOCATION = 101;
    static final int REQUEST_DRAWING_RIGHTS = 102;
    static final int REQUEST_WIFI_PERMISSIONS = 103;
    static final int REQUEST_FLASHLIGHT_PERMISSIONS = 104;
    static final int REQUEST_SYSTEM_SETTINGS_PERMISSIONS = 105;
    private final String TAG = "StatusActivity";
    private Boolean injected = false;
    @Inject IPerformer performer;
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
        mediaPermissionStatus.setText(PermissionsController.isNotificationListenerConnected(this) ? "Media Permission Granted." : "Media permission not granted.");
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
        accessibilityStatusText.setText(MyoidAccessibilityService.isServiceConnected() ? R.string.accessibilityStatusConnected : R.string.accessibilityStatusNotConnected);
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
        final Activity activity = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAllPermissionsAndLaunchMyoChooser(activity);
            }
        });
    }

    private void checkAllPermissionsAndLaunchMyoChooser(Activity activity) {
        ensureActivityInjected();
        if (PermissionsController.checkAllPermissions(activity))
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

    private void initializeDrawingPermissionsButton(final Activity activity) {
        drawingOverlaysPermissionButton = (Button) findViewById(R.id.drawingOverlayPermission);
        drawingOverlaysPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ensureActivityInjected();
                PermissionsController.checkAndRequestDrawingPermissions(activity);
            }
        });
    }

    private void updateDrawingOverlaysButtonText() {
        if (PermissionsController.checkDrawingPermissions(this))
            drawingOverlaysPermissionButton.setText(R.string.mediaPermissionGranted);
        else
            drawingOverlaysPermissionButton.setText(R.string.mediaPermissionNotGranted);
    }

    public void ensureActivityInjected() {
        if(!injected) {
            MyoidAccessibilityService.getMyoidService().getObjectGraph().inject(this);
            injected = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                if (arePermissionsGranted(grantResults)) {
                    Log.d(TAG, "Location permission granted.");
                    checkAllPermissionsAndLaunchMyoChooser(this);
                } else {
                    Log.i(TAG, "Location permission denied.");
                }
                break;
            }
            case REQUEST_DRAWING_RIGHTS: {
                if (arePermissionsGranted(grantResults)){
                    drawingOverlaysGranted();
                    checkAllPermissionsAndLaunchMyoChooser(this);
                }
                else
                    drawingOverlaysDenied();
                break;
            }
            case REQUEST_WIFI_PERMISSIONS: {
                if (arePermissionsGranted(grantResults)){
                    Log.d(TAG, "Wifi permissions granted.");
                    checkAllPermissionsAndLaunchMyoChooser(this);
                }
                else
                    Log.d(TAG, "Wifi permissions not granted.");
                break;
            }
            case REQUEST_FLASHLIGHT_PERMISSIONS: {
                if (arePermissionsGranted(grantResults)) {
                    Log.d(TAG, "Flashlight permissions granted");
                    checkAllPermissionsAndLaunchMyoChooser(this);
                } else
                    Log.d(TAG, "Flashlight permissions denied.");
                break;
            }
            case REQUEST_SYSTEM_SETTINGS_PERMISSIONS: {
                if (arePermissionsGranted(grantResults)) {
                    Log.d(TAG, "WRITE_SETTINGS permission denied.");
                    checkAllPermissionsAndLaunchMyoChooser(this);
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
                    checkAllPermissionsAndLaunchMyoChooser(this);
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
