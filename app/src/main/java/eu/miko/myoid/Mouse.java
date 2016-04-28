package eu.miko.myoid;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import javax.inject.Inject;
import javax.inject.Singleton;

import static java.lang.Math.abs;

@Singleton
class Mouse extends Mode {
    @Inject
    public Mouse(Performer performer) {
        super(performer);
    }

    @Override
    public void onEntry() {
        performer.changeCursorImage(Pose.REST);
        performer.displayCursor();
        Options.mouseOrMedia = State.MOUSE;
    }

    @Override
    public Event resolvePose(Pose pose) {
        Event event = null;
        performer.changeCursorImage(pose);
        switch(pose) {
            case REST:
                break;
            case FIST:
                event = Event.FIST;
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
                performer.hideCursor();
                performer.lockMyo();
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
        performer.unlockMyoHold();
        performer.displayCursor();
    }
}
