package eu.miko.myoid.StateMachine;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;

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

        performer.moveCursor((int) -yaw, (int)pitch);
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
}
