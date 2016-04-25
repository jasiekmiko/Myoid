package eu.miko.myoid;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OverlayPermissionsRequester {
    private static final String TAG = OverlayPermissionsRequester.class.getName();

    @Inject
    public OverlayPermissionsRequester() {
    }

    public boolean checkDrawingPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else {
            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW);
            return permissionCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

    public void requestDrawingPermissions(Activity activity) {
        Log.d(TAG, "Launching request for permission.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, StatusActivity.REQUEST_DRAWING_RIGHTS);
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                    StatusActivity.REQUEST_DRAWING_RIGHTS);
        }
    }

    public void checkAndRequestDrawingPermissions(Activity activity) {
        if (!checkDrawingPermissions(activity)) requestDrawingPermissions(activity);
    }
}
