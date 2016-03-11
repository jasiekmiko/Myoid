package eu.miko.myoid.StateMachine;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

public class Mode {
    public Event poseEffect(Pose pose) {
        return null;
    }

    public  Event resolveOrientation(Quaternion rotation) {
        return null;
    }

    public Event appendAcceleration(Vector3 acceleration) {
        return null;
    }

    public Event appendGyro(Vector3 gyro) {
        return null;
    }
}

class Mouse extends Mode {

}

class Tapped extends  Mode {

}