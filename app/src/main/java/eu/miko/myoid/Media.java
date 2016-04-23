package eu.miko.myoid;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Media extends Mode {
    @Inject
    public Media(Performer performer) {
        super(performer);
    }

    @Override
    public Event resolvePose(Pose pose) {
        switch (pose) {
            case REST:
                break;
            case FIST:
                break;
            case WAVE_IN:
                break;
            case WAVE_OUT:
                break;
            case FINGERS_SPREAD:
                return Event.SPREAD;
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
        return null;
    }

    @Override
    public Event resolveGyro(Vector3 gyro) {
        return null;
    }

    @Override
    public void resolveUnlock() {

    }
}
