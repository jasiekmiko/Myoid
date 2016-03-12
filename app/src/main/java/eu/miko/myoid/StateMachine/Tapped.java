package eu.miko.myoid.StateMachine;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

class Tapped extends  Mode {

    @Override
    public Event poseEffect(Pose pose) {
        if (pose == Pose.DOUBLE_TAP) {
            performer.unlockMyo();
        }
        return null;
    }

    @Override
    public Event resolveOrientation(Quaternion rotation) {
        return null;
    }

    @Override
    public Event appendAcceleration(Vector3 acceleration) {
        return null;
    }

    @Override
    public Event appendGyro(Vector3 gyro) {
        return null;
    }
}
