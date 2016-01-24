package eu.miko.myoid;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import java.util.LinkedList;
import java.util.List;

public class Gesture {
    private State state;
    private Performer.ActionCode action;
    private List<GesturePart> inputSequence = new LinkedList<>();
    private int next = 0;

    public void append(Pose pose) {
        next = 0;
        inputSequence.add(new GesturePart(pose));
    }

    public void append(Quaternion rotation) {
        next = 0;
        inputSequence.add(new GesturePart(rotation));
    }

    public void appendAcceleration(Vector3 acceleration) {
        next = 0;
        inputSequence.add(new GesturePart(acceleration, InputType.ACCELERATION));
    }

    public void appendGyro (Vector3 gyro) {
        next = 0;
        inputSequence.add(new GesturePart(gyro, InputType.GYRO));
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public InputType nextInputType() {
        return inputSequence.get(next).type;
    }

    public boolean hasNext() {
        return next < inputSequence.size();
    }

    public Pose nextInputPose() {
        return inputSequence.get(next).pose;
    }

    public Performer.ActionCode getAction() {
        return action;
    }

    public void setAction(Performer.ActionCode action) {
        this.action = action;
    }

    enum State {
        COMPLETE,
        PENDING,
        SWITCH_MODE
    }

    public enum InputType {
        POSE,
        ACCELERATION,
        ROTATION,
        GYRO
    }

    private class GesturePart {
        public Vector3 gyro;
        public Vector3 acceleration;
        public Quaternion rotation;
        public InputType type;
        public Pose pose;

        public GesturePart(Pose pose) {
            type = InputType.POSE;
            this.pose = pose;
        }

        public GesturePart(Quaternion rotation) {
            type = InputType.ROTATION;
            this.rotation = rotation;
        }

        public GesturePart(Vector3 vectorData, InputType inputType) {
            type = inputType;
            if (inputType == InputType.ACCELERATION) this.acceleration = vectorData;
            else this.gyro = vectorData;
        }
    }
}
