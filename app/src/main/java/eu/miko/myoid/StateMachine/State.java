package eu.miko.myoid.StateMachine;

import com.thalmic.myo.Quaternion;

import eu.miko.myoid.Performer;

/**
 * Created by jasie on 10/03/2016.
 */
public enum State {
    MOUSE(new Mouse()),
    TAPPED(new Tapped()),
    OPTIONS_ENTRY_FROM_MOUSE,
    OPTIONS,
    OPTIONS_ENTRY_FROM_MEDIA,
    SYSTEM_VOLUME,
    QUICK_SETTINGS,
    MEDIA,
    MEIDA_VOLUME,
    PREVIOUS,
    NEXT,
    PHONE_CALLING,
    IN_CALL,
    CALL_VOLUME,
    CAMERA,
    CAMERA_ZOOM,
    LOCKED;

    private Mode mode;

    State(Mode mode) {
        this.mode = mode;
    }

    State() {
        this.mode = new Mode();
    }

    public Mode getMode() { return mode;}
}