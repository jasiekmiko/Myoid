package eu.miko.myoid;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyoidNotificationListener extends NotificationListenerService{
    private static final String TAG = MyoidNotificationListener.class.getName();
    @Inject IPerformer performer;
    private boolean injected = false;

    @Override
    public void onCreate() {
        String message = "NotificationListener created.";
        if(ensureListenerInjected())
            performer.shortToast(message);
        Log.d(TAG, message);
    }

    public boolean ensureListenerInjected() {
        if (!injected) {
            try {
                MyoidAccessibilityService.getMyoidService().getObjectGraph().inject(this);
                injected = true;
            } catch (Error e) {
                Log.d(TAG, "Notification listener cannot inject - MAS not started yet.");
            }
        }
        return injected;
    }

    @Override
    public void onListenerConnected() {
        Log.d(TAG, "Notification listener connected.");
        if (ensureListenerInjected()) {
            performer.initializeMediaControllers();
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}
