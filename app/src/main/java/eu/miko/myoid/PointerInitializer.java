package eu.miko.myoid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PointerInitializer {
    private static final String TAG = PointerInitializer.class.getName();
    private Performer performer;

    @Inject
    public PointerInitializer(Performer performer) {
        this.performer = performer;
    }

    public void checkPermissionsAndInitializePointer(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, StatusActivity.REQUEST_DRAWING_RIGHTS);
            } else initializePointer();
        } else {
            int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.SYSTEM_ALERT_WINDOW);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                initializePointer();

            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                        StatusActivity.REQUEST_DRAWING_RIGHTS);
            }
        }
    }

    public void initializePointer() {
        performer.displayCursor();
    }
}
