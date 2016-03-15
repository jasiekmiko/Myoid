package eu.miko.myoid.StateMachine;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

class Tapped extends Mode {

    @Override
    public Event resolvePose(Pose pose) {
        if (pose == Pose.REST) {
            performer.mouseTap();
            return Event.RELAX;
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
