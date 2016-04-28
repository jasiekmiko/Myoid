package eu.miko.myoid;

import android.Manifest.permission;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

public class PermissionsController {
    private static final String TAG = PermissionsController.class.getName();

    private static boolean isWriteSettingsPermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return Settings.System.canWrite(activity);
        else
            return isPermissionGranted(activity, permission.WRITE_SETTINGS);
    }

    private static void requestWriteSettingsPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } else
            requestPermission(activity, new String[]{permission.WRITE_SETTINGS}, StatusActivity.REQUEST_SYSTEM_SETTINGS_PERMISSIONS);
    }

    private static void requestPermission(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity,
                permissions,
                requestCode);
    }

    private static boolean isPermissionGranted(Activity activity, String permission) {
        int permissionGrant = ContextCompat.checkSelfPermission(activity, permission);
        return permissionGrant == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean WifiPermissionsGranted(Activity activity) {
        int changeStatePermission = ContextCompat.checkSelfPermission(activity, permission.CHANGE_WIFI_STATE);
        int accessStatePermission = ContextCompat.checkSelfPermission(activity, permission.ACCESS_WIFI_STATE);
        return changeStatePermission == PackageManager.PERMISSION_GRANTED
                && accessStatePermission == PackageManager.PERMISSION_GRANTED;
    }

    static boolean isNotificationListenerConnected(Activity activity) {
        ContentResolver contentResolver = activity.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = activity.getPackageName();
        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
    }

    private static void startNotificationsAccessSettingsScreen(Activity activity) {
        activity.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }

    static boolean checkAllPermissions(Activity activity) {
        if (!isAccessibilityOn(activity))
            activity.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        else if (!isNotificationListenerConnected(activity))
            startNotificationsAccessSettingsScreen(activity);
        else if (!checkDrawingPermissions(activity))
            requestDrawingPermissions(activity);
        else if (!WifiPermissionsGranted(activity))
            requestPermission(activity, new String[]{permission.ACCESS_WIFI_STATE, permission.CHANGE_WIFI_STATE}, StatusActivity.REQUEST_WIFI_PERMISSIONS);
        else if (!isPermissionGranted(activity, permission.FLASHLIGHT))
            requestPermission(activity, new String[]{permission.FLASHLIGHT}, StatusActivity.REQUEST_FLASHLIGHT_PERMISSIONS);
        else if (!isWriteSettingsPermissionGranted(activity))
            requestWriteSettingsPermission(activity);
        else if(!isPermissionGranted(activity, permission.ACCESS_FINE_LOCATION))
            requestPermission(activity, new String[]{permission.ACCESS_FINE_LOCATION}, StatusActivity.REQUEST_FINE_LOCATION);
        else return true;
        return false;
    }

    static private void requestDrawingPermissions(Activity activity) {
        Log.d(TAG, "Launching request for permission.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, StatusActivity.REQUEST_DRAWING_RIGHTS);
        } else {
            requestPermission(activity, new String[]{permission.SYSTEM_ALERT_WINDOW}, StatusActivity.REQUEST_DRAWING_RIGHTS);
        }
    }

    static boolean isAccessibilityOn(Activity activity) {
        int accessibilityEnabled = 0;
        final String service = activity.getPackageName() + "/" + MyoidAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    activity.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    activity.getApplicationContext().getContentResolver(),
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

    static void checkAndRequestDrawingPermissions(Activity activity) {
        if (!checkDrawingPermissions(activity)) requestDrawingPermissions(activity);
    }

    public static boolean checkDrawingPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else {
            int permissionCheck = ContextCompat.checkSelfPermission(context, permission.SYSTEM_ALERT_WINDOW);
            return permissionCheck == PackageManager.PERMISSION_GRANTED;
        }
    }
}