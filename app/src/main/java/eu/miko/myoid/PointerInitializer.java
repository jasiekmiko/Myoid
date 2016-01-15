package eu.miko.myoid;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PointerInitializer {
    private static final String TAG = PointerInitializer.class.getName();;

    public static void checkPermissionsAndInitializePointer(Activity activity) {

        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.SYSTEM_ALERT_WINDOW);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            initializePointer();

        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                    StatusActivity.REQUEST_DRAWING_RIGHTS);
        }
    }

    public static void initializePointer() {
        SharedServiceResources.mas.initializeCursor();
    }
}