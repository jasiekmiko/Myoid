package eu.miko.myoid;

import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import eu.miko.myoid.Errors.InvalidStateError;

public class GestureResolver {
    private static GestureResolver instance;
    private GestureResolver() {}
    public static GestureResolver getInstance() {
        if (instance == null) instance = new GestureResolver();
        return instance;
    }

    private InterfaceMode mode = new GlobalNavigationMode();
    private Arm arm;
    private Gesture gesture = new Gesture();

    private Performer performer = Performer.getInstance();

    public void resolvePose(Pose pose) {
        gesture.append(pose);
        performer.shortToast("Pose: " + pose);
        resolveGesture();
    }

    public void resolveOrientation(Quaternion rotation) {
        gesture.append(rotation);
        resolveGesture();
    }


    public void resolveAcceleration(Vector3 acceleration) {
        gesture.appendAcceleration(acceleration);
        resolveGesture();
    }

    public void resolveGyro(Vector3 gyro) {
        gesture.appendGyro(gyro);
        resolveGesture();
    }

    public void setArm(Arm arm) {
        this.arm = arm;
    }

    private void resolveGesture() {
        switch (mode.resolveGestureState(gesture)) {
            case COMPLETE:
                performer.execute(gesture);
                gesture = new Gesture();
                break;
            case PENDING:
                break;
            case SWITCH_MODE:
                mode = new GlobalNavigationMode();
                //TODO figure out whether this should use a FSM implementation
                break;
            default:
                throw new InvalidStateError("Gesture resolution failed.");
        }
    }

}
