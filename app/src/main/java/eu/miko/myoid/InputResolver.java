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
        Log.d(TAG, "Pose detected: " + pose);
        Event resultingEvent = getCurrentMode().resolvePose(pose);
        myoidStateMachine.apply(resultingEvent);
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
                .onEnter(State.MOUSE, new runnableShowMouse())
                .addTransition(State.MOUSE, Event.FIST, State.TAPPED)
                .addTransition(State.MOUSE, Event.SPREAD, State.OPTIONS_DOORWAY_FROM_MOUSE)
                //OPTIONS_DOORWAY_FROM_MOUSE
                .onEnter(State.OPTIONS_DOORWAY_FROM_MOUSE, runnableEntryNotifier(State.OPTIONS_DOORWAY_FROM_MOUSE.name()))
                .addTransition(State.OPTIONS_DOORWAY_FROM_MOUSE, Event.RELAX, State.MOUSE)
                .addTransition(State.OPTIONS_DOORWAY_FROM_MOUSE, Event.Z_AXIS, State.OPTIONS_FROM_MOUSE)
                //TAPPED
                .addTransition(State.TAPPED, Event.RELAX, State.MOUSE)
                //OPTIONS_FROM_MOUSE
                .onEnter(State.OPTIONS_FROM_MOUSE, new runnableOpenOptions())
                .addTransition(State.OPTIONS_FROM_MOUSE, Event.LEFT, State.MOUSE)
                .addTransition(State.OPTIONS_FROM_MOUSE, Event.OPTION_SELECTED, State.MOUSE)
                .addTransition(State.OPTIONS_FROM_MOUSE, Event.SWITCH_MODE, State.MEDIA)
                .onExit(State.OPTIONS_FROM_MOUSE, new runnableCloseOptions())
                //MEDIA
                .onEnter(State.MEDIA, runnableEntryNotifier(State.MEDIA.name()))
                .addTransition(State.MEDIA, Event.SPREAD, State.OPTIONS_DOORWAY_FROM_MEDIA)
                //OPTIONS_DOORWAY_FROM_MEDIA
                .onEnter(State.OPTIONS_DOORWAY_FROM_MEDIA, runnableEntryNotifier(State.OPTIONS_DOORWAY_FROM_MEDIA.name()))
                .addTransition(State.OPTIONS_DOORWAY_FROM_MEDIA, Event.Z_AXIS, State.OPTIONS_FROM_MEDIA)
                .addTransition(State.OPTIONS_DOORWAY_FROM_MEDIA, Event.RELAX, State.MEDIA)
                //OPTIONS_FROM_MEDIA
                .onEnter(State.OPTIONS_FROM_MEDIA, new runnableOpenOptions())
                .addTransition(State.OPTIONS_FROM_MEDIA, Event.LEFT, State.MEDIA)
                .addTransition(State.OPTIONS_FROM_MEDIA, Event.OPTION_SELECTED, State.MEDIA)
                .addTransition(State.OPTIONS_FROM_MEDIA, Event.SWITCH_MODE, State.MOUSE)
                .onExit(State.OPTIONS_FROM_MEDIA, new runnableCloseOptions())
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

    private class runnableShowMouse implements Runnable {
        @Override
        public void run() {
            performer.displayCursor();
        }
    }
    private class runnableOpenOptions implements Runnable {

        @Override
        public void run() {
            performer.hideCursor();
            performer.displayOptions();
        }
    }
    private class runnableCloseOptions implements Runnable {

        @Override
        public void run() {
            performer.dismissOptions();
        }

    }
}
