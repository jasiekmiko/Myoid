package eu.miko.myoid;

import com.thalmic.myo.Pose;

import javax.inject.Inject;
import javax.inject.Singleton;

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
    public Event resolveOrientation(float roll, float pitch, float yaw) {
        performer.moveCursor(xMovement(yaw), yMovement(pitch));
        return null;
    }

    @Override
    public void resolveUnlock() {
        performer.unlockMyoHold();
        performer.displayCursor();
    }
}
