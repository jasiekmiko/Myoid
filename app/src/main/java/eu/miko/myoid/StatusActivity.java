package eu.miko.myoid;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

public class StatusActivity extends Activity {
    final static int REQUEST_FINE_LOCATION = 100;
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
            }
        }
    }

    private boolean isPermissionGranted(@NonNull int[] grantResults) {
        return grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
