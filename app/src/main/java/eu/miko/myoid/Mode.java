package eu.miko.myoid;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

public abstract class Mode {
    protected IPerformer performer;
    abstract public Event resolvePose(Pose pose);
    abstract public Event resolveOrientation(Quaternion rotation);
    abstract public Event resolveAcceleration(Vector3 acceleration);
    abstract public Event resolveGyro(Vector3 gyro);
    abstract public void resolveUnlock();

    public Mode(Performer performer) {
        this.performer = performer;
    }
}

