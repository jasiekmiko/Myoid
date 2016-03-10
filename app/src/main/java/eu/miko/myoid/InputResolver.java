package eu.miko.myoid;

import android.support.annotation.NonNull;

import com.github.zevada.stateful.StateMachine;
import com.github.zevada.stateful.StateMachineBuilder;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import eu.miko.myoid.Errors.InvalidStateError;

public class InputResolver {
    private static InputResolver instance;
    private InputResolver() {}
    public static InputResolver getInstance() {
        if (instance == null) instance = new InputResolver();
        return instance;
    }

    private StateMachine<State, Event> myoidStateMachine = createMyoidStateMachine();

    private InterfaceMode mode = new GlobalNavigationMode();

    private Arm arm;
    private Gesture gesture = new Gesture();
    private Performer performer = Performer.getInstance();

    public void resolvePose(Pose pose) {
        myoidStateMachine.apply(Event.fromPose(pose));
        performer.shortToast("Pose: " + pose);
    }

    public void resolveOrientation(Quaternion rotation) {
        gesture.append(rotation);
        resolveGesture();
    }

    public void resolveAcceleration(Vector3 acceleration) {
        gesture.appendAcceleration(acceleration);
        resolveGesture();
    }


    public void resolveGyro(Vector3 gyro) {
        gesture.appendGyro(gyro);
        resolveGesture();
    }

    public void setArm(Arm arm) {
        this.arm = arm;
    }

    private void resolveGesture() {
        switch (mode.resolveGestureState(gesture)) {
            case COMPLETE:
                performer.execute(gesture);
                gesture = new Gesture();
                break;
            case PENDING:
                break;
            case SWITCH_MODE:
                mode = new GlobalNavigationMode();
                //TODO figure out whether this should use a FSM implementation
                break;
            default:
                throw new InvalidStateError("Gesture resolution failed.");
        }
    }

    @NonNull
    private StateMachine<State, Event> createMyoidStateMachine() {
        return new StateMachineBuilder<State, Event>(State.LOCKED)
                .addTransition(State.LOCKED, Event.DOUBLT_TAP, State.MOUSE)
                .addTransition(State.MOUSE, Event.DOUBLT_TAP, State.MOUSE)
                .addTransition(State.MOUSE, Event.FIST, State.TAPPED)
                .addTransition(State.TAPPED, Event.RELAX, State.MOUSE)
                .addTransition(State.MOUSE, Event.LEFT, State.MOUSE)
                .addTransition(State.MOUSE, Event.RIGHT, State.MOUSE)
                .addTransition(State.MOUSE, Event.UP, State.MOUSE)
                .addTransition(State.MOUSE, Event.DOWN, State.MOUSE)
                .addTransition(State.MOUSE, Event.SPREAD, State.OPTIONS_ENTRY_FROM_MOUSE)
                .addTransition(State.OPTIONS_ENTRY_FROM_MOUSE, Event.RELAX, State.MOUSE)
                .onEnter(State.OPTIONS_ENTRY_FROM_MOUSE, openOptions())
                .build();
    }

    private Runnable openOptions() {
        performer.shortToast("Options time!");
        return null;
    }

}
