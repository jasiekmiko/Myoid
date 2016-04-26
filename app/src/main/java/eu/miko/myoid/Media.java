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
    public void onEntry() {
        performer.unlockMyoTimed();
    }

    @Override
    public Event resolvePose(Pose pose) {
        Event poseResult = null;
        switch (pose) {
            case REST:
                break;
            case FIST:
                poseResult = Event.FIST;
                break;
            case WAVE_IN:
                performer.MediaNext();
                break;
            case WAVE_OUT:
                performer.MediaPrev();
                break;
            case FINGERS_SPREAD:
                poseResult = Event.SPREAD;
            case DOUBLE_TAP:
                performer.lockMyo();
                break;
            case UNKNOWN:
                break;
        }
        return poseResult;
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
    public void onExit() {
        performer.unlockMyoHold();
    }

    @Override
    public void resolveUnlock() {
        performer.unlockMyoTimed();
    }
}
