package eu.miko.myoid.StateMachine;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import eu.miko.myoid.Performer;

public abstract class Mode {
    protected Performer performer = Performer.getInstance();

    abstract public Event resolvePose(Pose pose);
    abstract public  Event resolveOrientation(Quaternion rotation);
    abstract public Event resolveAcceleration(Vector3 acceleration);
    abstract public Event resolveGyro(Vector3 gyro);
}

