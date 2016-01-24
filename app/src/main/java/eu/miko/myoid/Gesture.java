package eu.miko.myoid;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import java.util.LinkedList;
import java.util.List;

public class Gesture {
    private State state;
    private List<GesturePart> inputSequence = new LinkedList<>();

    public void append(Pose pose) {
        inputSequence.add(new GesturePart(pose));
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void append(Quaternion rotation) {
        inputSequence.add(new GesturePart(rotation));
    }

    public void append(Vector3 acceleration, InputType inputType) {
        inputSequence.add(new GesturePart(acceleration, inputType));
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
