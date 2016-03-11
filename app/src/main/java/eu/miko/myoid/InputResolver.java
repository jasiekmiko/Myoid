package eu.miko.myoid;

import android.support.annotation.NonNull;

import com.github.zevada.stateful.StateMachine;
import com.github.zevada.stateful.StateMachineBuilder;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import eu.miko.myoid.StateMachine.Event;
import eu.miko.myoid.StateMachine.Mode;
import eu.miko.myoid.StateMachine.State;

public class InputResolver {
    private static InputResolver instance;
    private InputResolver() {}
    public static InputResolver getInstance() {
        if (instance == null) instance = new InputResolver();
        return instance;
    }

    private StateMachine<State, Event> myoidStateMachine = createMyoidStateMachine();
    private Arm arm;
    private Quaternion currentRotation = null;
    private Vector3 currentAcceleration = null;
    private Vector3 currentGyro = null;
    private Performer performer = Performer.getInstance();

    public void resolvePose(Pose pose) {
        getCurrentMode().poseEffect(pose);
        myoidStateMachine.apply(Event.fromPose(pose, arm, currentRotation));
        performer.shortToast("Pose: " + pose);
    }

    public void resolveOrientation(Quaternion rotation) {
        Event resultingEvent = getCurrentMode().resolveOrientation(rotation);
        if(resultingEvent != null) myoidStateMachine.apply(resultingEvent);
    }

    public void resolveAcceleration(Vector3 acceleration) {
        Event resultingEvent = getCurrentMode().appendAcceleration(acceleration);
        if(resultingEvent != null) myoidStateMachine.apply(resultingEvent);
    }

    public void resolveGyro(Vector3 gyro) {
        Event resultingEvent = getCurrentMode().appendGyro(gyro);
        if(resultingEvent != null) myoidStateMachine.apply(resultingEvent);
    }

    public void setArm(Arm arm) {
        this.arm = arm;
    }

    private Mode getCurrentMode() {
        return myoidStateMachine.getState().getMode();
    }

    @NonNull
    private StateMachine<State, Event> createMyoidStateMachine() {
        return new StateMachineBuilder<State, Event>(State.LOCKED)
                //LOCKED
                .addTransition(State.LOCKED, Event.DOUBLT_TAP, State.MOUSE)
                //MOUSE
                .addTransition(State.MOUSE, Event.DOUBLT_TAP, State.MOUSE)
                .addTransition(State.MOUSE, Event.FIST, State.TAPPED)
                .addTransition(State.MOUSE, Event.LEFT, State.MOUSE)
                .addTransition(State.MOUSE, Event.RIGHT, State.MOUSE)
                .addTransition(State.MOUSE, Event.UP, State.MOUSE)
                .addTransition(State.MOUSE, Event.DOWN, State.MOUSE)
                .addTransition(State.MOUSE, Event.SPREAD, State.OPTIONS_ENTRY_FROM_MOUSE)
                //OPTIONS_ENTRY_FROM_MOUSE
                .onEnter(State.OPTIONS_ENTRY_FROM_MOUSE, openOptions())
                .addTransition(State.OPTIONS_ENTRY_FROM_MOUSE, Event.RELAX, State.MOUSE)
                //TAPPED
                .addTransition(State.TAPPED, Event.RELAX, State.MOUSE)
                .build();
    }

    private Runnable openOptions() {
        performer.shortToast("Options time!");
        return null;
    }

}
