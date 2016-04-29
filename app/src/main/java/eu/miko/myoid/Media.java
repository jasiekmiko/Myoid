package eu.miko.myoid;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Media extends Mode {
    @Inject
    public Media(Performer performer) {
        super(performer);
    }

    @Override
    public void onEntry() {
        performer.unlockMyoTimed();
        Options.mouseOrMedia = State.MEDIA;
    }

    @Override
    public Event resolvePose(Pose pose) {
        Event poseResult = null;
        switch (pose) {
            case REST:
                break;
            case FIST:
                poseResult = Event.FIST;
                break;
            case WAVE_IN:
                performer.performMediaAction(Action.NEXT);
                break;
            case WAVE_OUT:
                performer.performMediaAction(Action.PREV);
                break;
            case FINGERS_SPREAD:
                performer.performMediaAction(Action.PLAY_PAUSE);//TODO: Temporary, in the end should happen only when options are not entered.
                poseResult = Event.SPREAD;
                break;
            case DOUBLE_TAP:
                performer.lockMyo();
                break;
            case UNKNOWN:
                break;
        }
        return poseResult;
    }

    @Override
    public Event resolveOrientation(Quaternion rotation) {
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
    public void onExit() {
        performer.unlockMyoHold();
    }

    @Override
    public void resolveUnlock() {
        performer.unlockMyoTimed();
    }

    public enum Action {
        NEXT,
        PREV,
        PLAY_PAUSE
    }
}
