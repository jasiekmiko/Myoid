package eu.miko.myoid;

import android.support.annotation.NonNull;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Options extends Mode {
    @Inject
    public Options(Performer performer) {
        super(performer);
    }

    @Override
    public Event resolvePose(Pose pose) {
        Event event = null;
        switch (pose) {
            case FIST:
                event = Event.FIST;
                break;
            case WAVE_IN:
                if(goBackAndCheckIfOptionsClose()) event = Event.LEFT;
                break;
            case DOUBLE_TAP:
                performer.lockMyo();
        }
        return event;
    }

    @NonNull
    private boolean goBackAndCheckIfOptionsClose() {
        return performer.optionsGoBack();
    }

    @Override
    public Event resolveOrientation(Quaternion rotation) {
        //float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
        float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
        float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));

        boolean optionSelected = performer.moveOptionsPointerBy(xMovement(yaw), yMovement(pitch));
        if (optionSelected)
            return Event.OPTION_SELECTED;
        else return null;
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
    }
}
