package eu.miko.myoid.StateMachine;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

public class Options extends Mode {
    private static Mode instance;
    public static Mode getInstance() {
        if (instance == null) instance = new Options();
        return instance;
    }

    @Override
    public Event resolvePose(Pose pose) {
        Event event = null;
        switch (pose) {
            case FIST:
                event = Event.FIST;
                break;
            case WAVE_IN:
                event = Event.LEFT;
                break;
            case DOUBLE_TAP:
                performer.lockMyo();
        }
        return event;
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
        performer.unlockMyoHold();
    }
}
