package eu.miko.myoid.StateMachine;

import android.util.Log;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

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
    LOCKED(new Locked());

    private Mode mode;

    State(Mode mode) {
        this.mode = mode;
    }

    State() {
        this.mode = new Mode() {
            final private String TAG = "UnimplementedMode";

            @Override
            public Event poseEffect(Pose pose){
                Log.w(TAG, "poseEffect called on an unimplemented Event");
                return null;
            }

            @Override
            public Event resolveOrientation(Quaternion rotation) {
                Log.w(TAG, "poseEffect called on an unimplemented Event");
                return null;
            }

            @Override
            public Event appendAcceleration(Vector3 acceleration) {
                Log.w(TAG, "poseEffect called on an unimplemented Event");
                return null;
            }

            @Override
            public Event appendGyro(Vector3 gyro) {
                Log.w(TAG, "poseEffect called on an unimplemented Event");
                return null;
            }
        };
    }

    public Mode getMode() { return mode;}
}