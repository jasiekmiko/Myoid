package eu.miko.myoid;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OptionsDoorway extends Mode {
    @Inject
    public OptionsDoorway(Performer performer) {
        super(performer);
    }

    @Override
    public Event resolvePose(Pose pose) {
        switch (pose) {
            case REST:
                return Event.RELAX;
            case FIST:
                break;
            case WAVE_IN:
                break;
            case WAVE_OUT:
                break;
            case FINGERS_SPREAD:
                break;
            case DOUBLE_TAP:
                break;
            case UNKNOWN:
                break;
        }
        return null;
    }

    @Override
    public Event resolveOrientation(Quaternion rotation) {
        return null;
    }

    @Override
    public Event resolveAcceleration(Vector3 acceleration) {
        if (acceleration.z() > .1) return Event.Z_AXIS;
        return null;
    }

    @Override
    public Event resolveGyro(Vector3 gyro) {
        return null;
    }

    @Override
    public void resolveUnlock() {}

}