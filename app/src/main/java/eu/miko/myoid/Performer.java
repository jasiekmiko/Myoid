package eu.miko.myoid;

import android.app.Service;
import android.widget.Toast;

public class Performer {
    private static Performer instance;
    private Performer() {}
    public static Performer getInstance() {
        if (instance == null) instance = new Performer();
        return instance;
    }

    private Service mService = MyoidAccessibilityService.getMyoidService();

    void shortToast(String text) {
        Toast.makeText(mService, text, Toast.LENGTH_SHORT).show();
    }
}
