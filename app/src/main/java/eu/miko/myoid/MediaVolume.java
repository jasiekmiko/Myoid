package eu.miko.myoid;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MediaVolume extends Mode {
    @Inject
    public MediaVolume(Performer performer) {
        super(performer);
    }

    @Override
    public Event resolvePose(Pose pose) {
        if (pose == Pose.REST) return Event.RELAX;
        return null;
    }

    @Override
    public Event resolveOrientation(float roll, float pitch, float yaw) {

        return null;
    }

    @Override
    public Event resolveAcceleration(Vector3 acceleration) {
        return null;
    }

    @Override
    public Event resolveGyro(Vector3 gyro) {
        return null;
    }
}
