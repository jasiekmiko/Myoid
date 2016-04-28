package eu.miko.myoid;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.thalmic.myo.scanner.ScanActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyoChooserLauncher {
    private static final String TAG = "MyoChooserLauncher";
    private IMyoHubManager myoHubManager;

    @Inject
    public MyoChooserLauncher(IMyoHubManager myoHubManager) {
        this.myoHubManager = myoHubManager;
    }

    public void checkForLocationPermissionAndLaunchChooser(Activity intentCaller) {
        if (myoHubManager.getIfHubInitialized()) {
            int permissionCheck = ContextCompat.checkSelfPermission(intentCaller, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                startMyoChooser(intentCaller);

            } else {
                ActivityCompat.requestPermissions(intentCaller,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        StatusActivity.REQUEST_FINE_LOCATION);
            }
        } else {
            Log.e(TAG, "Hub not initialized.");
        }
    }

    private static void startMyoChooser(Context intentCaller) {
        Intent intent = new Intent(intentCaller, ScanActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentCaller.startActivity(intent);
    }
}