package eu.miko.myoid;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import static java.lang.Math.abs;

public abstract class Mode {
    protected IPerformer performer;
    public Mode(Performer performer) {
        this.performer = performer;
    }

    public void onEntry() {    }

    public Event resolvePose(Pose pose){
        return null;
    }

    public Event resolveOrientation(float roll, float pitch, float yaw) {
        return null;
    }

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

    public Event resolveAcceleration(Vector3 acceleration) {
        return null;
    }

    public Event resolveGyro(Vector3 gyro) {
        return null;
    }

    public void resolveUnlock() {
    }

    public void resolveLock() {
    }

    public void onExit() {    }
}

