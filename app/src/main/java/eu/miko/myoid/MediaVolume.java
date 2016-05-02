package eu.miko.myoid;

import com.thalmic.myo.Pose;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MediaVolume extends Mode {
    private boolean startingRotationNeeded;
    private float startingRotation;

    @Inject
    public MediaVolume(Performer performer) {
        super(performer);
    }

    @Override
    public void onEntry() {
        startingRotationNeeded = true;
    }

    @Override
    public Event resolvePose(Pose pose) {
        if (pose == Pose.REST) return Event.RELAX;
        return null;
    }

    @Override
    public Event resolveOrientation(float roll, float pitch, float yaw) {
        if (startingRotationNeeded) {
            startingRotation = roll;
            startingRotationNeeded = false;
            performer.setVolumeAdjustStart();
        }
        else
            performer.adjustMediaVolume(startingRotation - roll);
        return null;
    }
}
