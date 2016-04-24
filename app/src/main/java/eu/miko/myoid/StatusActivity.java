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

import javax.inject.Inject;

public class StatusActivity extends Activity {
    final static int REQUEST_FINE_LOCATION = 101;
    public static final int REQUEST_DRAWING_RIGHTS = 102;
    private final String TAG = "StatusActivity";
    private Boolean injected = false;
    @Inject IPerformer performer;
    @Inject OverlayPermissionsRequester overlayPermissionsRequester;
    @Inject MyoChooserLauncher myoChooserLauncher;
    private Button drawingOverlaysPermissionButton;

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
        boolean updateUi = intent.getBooleanExtra("UPDATE_UI", false);
        if (updateUi) {
            ensureActivityInjected();
            updateDrawingOverlaysButtonText();
        }
    }

    private void initializeUiComponents() {
        final Activity activity = this;
        initializeFAB(activity);
        initializeDrawingPermissionsButton(activity);
        initializeOptionsButton();
        initializeAccessibilityStatus();
    }

    private void initializeAccessibilityStatus() {
        TextView accessibilityStatus = (TextView) findViewById(R.id.accessibilityStatus);
        accessibilityStatus.setText(isServiceConnected() ? R.string.accessibilityStatusConnected : R.string.accessibilityStatusNotConnected);
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

    private void initializeFAB(final Activity activity) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ensureActivityInjected();
                myoChooserLauncher.chooseMyo(activity);
            }
        });
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
            drawingOverlaysPermissionButton.setText("Drawing overlays permission granted!");
        else
            drawingOverlaysPermissionButton.setText("Drawing overlays permission lacking! Click here");
    }

    public void ensureActivityInjected() {
        if(!injected) {
            MyoidAccessibilityService.getMyoidService().getObjectGraph().inject(this);
            injected = true;
        }
    }

    private boolean isServiceConnected() {
        MyoidAccessibilityService mas;
        try {
            mas = MyoidAccessibilityService.getMyoidService();
        } catch (Error e) {
            return false;
        }
        return mas.isServiceConnected();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                if (isPermissionGranted(grantResults)) {
                    MyoChooserLauncher.startMyoChooser(this);
                } else {
                    Log.i(TAG, "Location permission denied.");
                }
                break;
            }
            case REQUEST_DRAWING_RIGHTS: {
                if (isPermissionGranted((grantResults)))
                    drawingOverlaysGranted();
                else
                    drawingOverlaysDenied();
                break;
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DRAWING_RIGHTS: {
                if (Settings.canDrawOverlays(MyoidAccessibilityService.getMyoidService()))
                    drawingOverlaysGranted();
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


    private boolean isPermissionGranted(@NonNull int[] grantResults) {
        return grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
