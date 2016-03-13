package eu.miko.myoid.StateMachine;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import static java.lang.Math.abs;

class Mouse extends Mode {

    @Override
    public Event resolvePose(Pose pose) {
        Event event = null;
        switch(pose) {
            case REST:
                break;
            case FIST:
                performer.mouseTap();
                break;
            case WAVE_IN:
                //TODO: distinguish between down, top, left and right
                performer.mouseScroll(false);
                break;
            case WAVE_OUT:
                performer.mouseScroll(true);
                break;
            case FINGERS_SPREAD:
                event = Event.SPREAD;
                break;
            case DOUBLE_TAP:
                event = Event.DOUBLT_TAP;
                performer.unlockMyoHold();
                break;
            case UNKNOWN:
                break;
        }
        return event;
    }

    @Override
    public Event resolveOrientation(Quaternion rotation) {
        //float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
        float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
        float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));

        performer.moveCursor(xMovement(yaw), yMovement(pitch));
        return null;
    }

    public int yMovement(float deg) {
        if (abs(deg) < 5) return 0;
        return (int)(deg/3);
    }

    public int xMovement(float deg) {
        if (abs(deg) < 5) return 0;
        return (int) (deg/10);
    }

    @Override
    public Event resolveAcceleration(Vector3 acceleration) {
        return null;
    }

    @Override
    public Event resolveGyro(Vector3 gyro) {
        return null;
    }
}
