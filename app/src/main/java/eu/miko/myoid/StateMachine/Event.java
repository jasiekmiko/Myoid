package eu.miko.myoid.StateMachine;

import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;

/**
 * Created by jasie on 10/03/2016.
 */
public enum Event {
    FIST,
    LEFT,
    RIGHT,
    UP,
    DOWN,
    SPREAD,
    RELAX,
    DOUBLT_TAP,
    Z_AXIS,
    OPTION_SELECTED;

    public static Event fromPose(Pose pose, Arm arm, Quaternion currentRotation) {
        switch (pose) {
            case REST:
                return RELAX;
            case FIST:
                return FIST;
            //TODO: arm-rotation based move
            case WAVE_IN:
                return DOWN;
            case WAVE_OUT:
                return UP;
            case FINGERS_SPREAD:
                return SPREAD;
            case DOUBLE_TAP:
                return DOUBLT_TAP;
            case UNKNOWN:
                return RELAX;
        }
        return null;
    }
}
