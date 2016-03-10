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

    private StateMachine<State, Event> myoidStateMahine = createMyoidStateMachine();

    private InterfaceMode mode = new GlobalNavigationMode();

    private Arm arm;
    private Gesture gesture = new Gesture();
    private Performer performer = Performer.getInstance();

    public void resolvePose(Pose pose) {
        gesture.append(pose);
        performer.shortToast("Pose: " + pose);
        resolveGesture();
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
        return new StateMachineBuilder<State, Event>(State.Mouse)
                .addTransition(State.Mouse, Event.Fist, State.Tapped)
                .addTransition(State.Tapped, Event.Relax, State.Mouse)
                .addTransition(State.Mouse, Event.Left, State.Mouse)
                .addTransition(State.Mouse, Event.Right, State.Mouse)
                .addTransition(State.Mouse, Event.Up, State.Mouse)
                .addTransition(State.Mouse, Event.Down, State.Mouse)
                .addTransition(State.Mouse, Event.Spread, State.OptionsEntryFromMouse)
                .addTransition(State.OptionsEntryFromMouse, Event.Relax, State.Mouse)
                .onEnter(State.OptionsEntryFromMouse, openOptions())
                .build();
    }

    private Runnable openOptions() {
        performer.shortToast("Options time!");
        return null;
    }

}
