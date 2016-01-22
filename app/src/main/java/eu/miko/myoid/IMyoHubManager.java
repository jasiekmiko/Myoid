package eu.miko.myoid;

import com.thalmic.myo.Hub;

public interface IMyoHubManager {
    void initializeHub(String packageName);
    Hub getHub();
}
