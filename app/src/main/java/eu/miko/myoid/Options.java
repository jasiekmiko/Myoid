package eu.miko.myoid;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Options extends Mode {
    static public State mouseOrMedia = State.MOUSE;

    @Inject
    public Options(Performer performer) {
        super(performer);
    }

    @Override
    public void onEntry() {
        performer.hideCursor();
        performer.displayOptions();
    }

    @Override
    public Event resolvePose(Pose pose) {
        Event event = null;
        performer.changePointerImage(pose);
        switch (pose) {
            case FIST:
                event = Event.FIST;
                break;
            case WAVE_IN:
                if(goBackAndCheckIfOptionsClose()) event = Event.LEFT;
                break;
            case DOUBLE_TAP:
                performer.hideOptions();
                performer.lockMyo();
        }
        return event;
    }

    private boolean goBackAndCheckIfOptionsClose() {
        return performer.optionsGoBack();
    }

    @Override
    public Event resolveOrientation(Quaternion rotation) {
        //float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
        float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
        float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));

        return performer.moveOptionsPointerBy(xMovement(yaw), yMovement(pitch));
    }

    @Override
    public Event resolveAcceleration(Vector3 acceleration) {
        return null;
    }

    @Override
    public Event resolveGyro(Vector3 gyro) {
        return null;
    }

    @Override
    public void resolveUnlock() {
        performer.unlockMyoHold();
        performer.displayOptions();
    }

    @Override
    public void onExit() {
        performer.hideOptions();
    }
}
