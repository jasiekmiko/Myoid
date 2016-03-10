package eu.miko.myoid;

import com.thalmic.myo.Pose;

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

    public static Event fromPose(Pose pose) {
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
