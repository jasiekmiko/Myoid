package eu.miko.myoid;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import static java.lang.Math.abs;

public abstract class Mode {
    protected IPerformer performer;
    abstract public Event resolvePose(Pose pose);
    abstract public Event resolveOrientation(Quaternion rotation);

    public int xMovement(float deg) {
        float degCorrected = deg;
        if (deg < -90) degCorrected = -180 - deg;
        else if (deg > 90) degCorrected = 180 - deg;
        if (abs(degCorrected) < 8) return 0;
        return (int) (degCorrected/3);
    }

    public int yMovement(float deg) {
        if (abs(deg) < 5) return 0;
        return (int)(-deg/3);
    }

    abstract public Event resolveAcceleration(Vector3 acceleration);
    abstract public Event resolveGyro(Vector3 gyro);
    abstract public void resolveUnlock();

    public Mode(Performer performer) {
        this.performer = performer;
    }
}

