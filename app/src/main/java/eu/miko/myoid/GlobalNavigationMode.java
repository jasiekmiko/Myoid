package eu.miko.myoid;

import com.thalmic.myo.Pose;

import static eu.miko.myoid.Performer.ActionCode.GO_BACK;
import static eu.miko.myoid.Performer.ActionCode.GO_HOME;
import static eu.miko.myoid.Performer.ActionCode.OPEN_NOTIFICATIONS;
import static eu.miko.myoid.Performer.ActionCode.OPEN_RECENTS;

public class GlobalNavigationMode implements InterfaceMode {

    @Override
    public Gesture.State resolveGestureState(Gesture gesture) {
        Gesture.State state = gesture.getState();
        Pose currentPose = null;
        Pose lastSeenPose = null;
        while (gesture.hasNext()) {
            switch (gesture.nextInputType()) {
                case POSE:
                    resolveSinglePoseGesture(gesture);
                    break;
                case ACCELERATION:
                    break;
                case ROTATION:
                    break;
                case GYRO:
                    break;
            }
        }
        return state;
    }

    private void resolveSinglePoseGesture(Gesture gesture) {
        switch (gesture.nextInputPose()) {
            case FIST:
                gesture.setAction(GO_HOME);
                gesture.setState(Gesture.State.COMPLETE);
                break;
            case WAVE_IN:
                gesture.setAction(GO_BACK);
                break;
            case WAVE_OUT:
                gesture.setAction(OPEN_RECENTS);
                break;
            case FINGERS_SPREAD:
                gesture.setAction(OPEN_NOTIFICATIONS);
            case UNKNOWN:
                gesture.setState(Gesture.State.COMPLETE);
                break;
        }
    }
}