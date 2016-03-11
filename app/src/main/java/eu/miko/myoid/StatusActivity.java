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

public class StatusActivity extends Activity {
    final static int REQUEST_FINE_LOCATION = 101;
    public static final int REQUEST_DRAWING_RIGHTS = 102;
    private final String TAG = "StatusActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        startService(new Intent(this, MyoidAccessibilityService.class));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Activity activity = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyoChooserLauncher.chooseMyo(activity);
            }
        });

        Button cursorButton = (Button) findViewById(R.id.initilizeCursor);
        cursorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PointerInitializer.checkPermissionsAndInitializePointer(activity);
            }
        });

        TextView accessibilityStatus = (TextView) findViewById(R.id.accessibilityStatus);
        accessibilityStatus.setText(MyoidAccessibilityService.getMyoidService().serviceConnected ? R.string.accessibilityStatusConnected : R.string.accessibilityStatusNotConnected);
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
                if (isPermissionGranted((grantResults))) {
                    PointerInitializer.initializePointer();
                } else {
                    Log.i(TAG, "ALERT_WINDOW permission denied.");
                }
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
                    PointerInitializer.initializePointer();
                else Log.i(TAG, "Drawing rights not granted.");
                break;
            }
        }
    }


    private boolean isPermissionGranted(@NonNull int[] grantResults) {
        return grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
