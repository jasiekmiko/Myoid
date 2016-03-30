package eu.miko.myoid;

import android.support.annotation.NonNull;
import android.util.Log;

import com.github.zevada.stateful.StateMachine;
import com.github.zevada.stateful.StateMachineBuilder;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import javax.inject.Inject;

public class InputResolver {
    private static final String TAG = InputResolver.class.getName();
    private ModeFromStateMap modeFromState;

    @Inject
    public InputResolver(ModeFromStateMap modeFromState, IPerformer performer) {
        this.modeFromState = modeFromState;
        this.performer = performer;
    }

    private StateMachine<State, Event> myoidStateMachine = createMyoidStateMachine();
    private Arm arm;
    private IPerformer performer;

    public void resolvePose(Pose pose) {
        Event resultingEvent = getCurrentMode().resolvePose(pose);
        myoidStateMachine.apply(resultingEvent);
        Log.d(TAG, "Pose detected: " + pose);
    }

    public void resolveOrientation(Quaternion rotation) {
        Event resultingEvent = getCurrentMode().resolveOrientation(rotation);
        if(resultingEvent != null) myoidStateMachine.apply(resultingEvent);
    }

    public void resolveAcceleration(Vector3 acceleration) {
        Event resultingEvent = getCurrentMode().resolveAcceleration(acceleration);
        if(resultingEvent != null) myoidStateMachine.apply(resultingEvent);
    }

    public void resolveGyro(Vector3 gyro) {
        Event resultingEvent = getCurrentMode().resolveGyro(gyro);
        if(resultingEvent != null) myoidStateMachine.apply(resultingEvent);
    }

    public void resolveUnlock() {
        getCurrentMode().resolveUnlock();
    }

    public void setArm(Arm arm) {
        this.arm = arm;
    }

    private Mode getCurrentMode() {
        return modeFromState.get(myoidStateMachine.getState());
    }

    @NonNull
    private StateMachine<State, Event> createMyoidStateMachine() {
        return new StateMachineBuilder<State, Event>(State.MOUSE)
                //MOUSE
                .onEnter(State.MOUSE, runnableEntryNotifier("Mouse"))
                .addTransition(State.MOUSE, Event.FIST, State.TAPPED)
                .addTransition(State.MOUSE, Event.SPREAD, State.OPTIONS_DOORWAY_FROM_MOUSE)
                //OPTIONS_DOORWAY_FROM_MOUSE
                .onEnter(State.OPTIONS_DOORWAY_FROM_MOUSE, new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, String.format("%s state entered.", "optionsEntry"));
                        performer.shortToast("Options time!");
                    }
                })
                .addTransition(State.OPTIONS_DOORWAY_FROM_MOUSE, Event.RELAX, State.MOUSE)
                .addTransition(State.OPTIONS_DOORWAY_FROM_MOUSE, Event.Z_AXIS, State.OPTIONS_FROM_MOUSE)
                //TAPPED
                .addTransition(State.TAPPED, Event.RELAX, State.MOUSE)
                //OPTIONS_FROM_MOUSE
                .onEnter(State.OPTIONS_FROM_MOUSE, new runnableOpenOptions())
                .addTransition(State.OPTIONS_FROM_MOUSE, Event.LEFT, State.MOUSE)
                .build();
    }

    private Runnable runnableEntryNotifier(final String newState) {
        return new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, String.format("%s state entered.", newState));
            }
        };
    }

    private class runnableOpenOptions implements Runnable {
        @Override
        public void run() {
            performer.displayOptions();
        }
    }
}