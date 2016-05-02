package eu.miko.myoid;

import com.thalmic.myo.Pose;

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
        performer.displayMediaStatus();
        performer.unlockMyoTimed();
        Options.mouseOrMedia = State.MEDIA;
    }

    @Override
    public Event resolvePose(Pose pose) {
        performer.changeMediaStatus(pose);

        Event poseResult = null;
        boolean shouldLockTimerExtend = false;
        switch (pose) {
            case REST:
                break;
            case FIST:
                poseResult = Event.FIST;
                shouldLockTimerExtend = true;
                break;
            case WAVE_IN:
                performer.performMediaAction(Action.NEXT);
                shouldLockTimerExtend = true;
                break;
            case WAVE_OUT:
                performer.performMediaAction(Action.PREV);
                shouldLockTimerExtend = true;
                break;
            case FINGERS_SPREAD:
                poseResult = Event.SPREAD;
                break;
            case DOUBLE_TAP:
                performer.lockMyo();
                break;
            case UNKNOWN:
                break;
        }
        if (shouldLockTimerExtend) performer.unlockMyoTimed();
        return poseResult;
    }

    @Override
    public void onExit() {
        performer.unlockMyoHold();
    }

    @Override
    public void resolveLock() {
        performer.hideMediaStatus();
    }

    @Override
    public void resolveUnlock() {
        performer.displayMediaStatus();
        performer.unlockMyoTimed();
    }

    public enum Action {
        NEXT,
        PREV,
        PLAY_PAUSE
    }
}
