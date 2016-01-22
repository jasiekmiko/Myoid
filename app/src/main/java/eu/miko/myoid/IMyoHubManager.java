package eu.miko.myoid;

import com.thalmic.myo.Hub;

public interface IMyoHubManager {
    void initializeHub(MyoidAccessibilityService mas, String packageName);
    Hub getHub();
}
