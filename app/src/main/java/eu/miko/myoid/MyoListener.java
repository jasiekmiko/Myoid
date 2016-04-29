package eu.miko.myoid;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import javax.inject.Inject;

public class MyoListener extends AbstractDeviceListener {
    private InputResolver inputResolver;
    private IPerformer performer;

    @Inject
    public MyoListener(IPerformer performer, InputResolver inputResolver) {
        this.performer = performer;
        this.inputResolver = inputResolver;
    }

    @Override
    public void onConnect(Myo myo, long timestamp) {
        performer.setMyo(myo);
        inputResolver.setArm(myo.getArm());
        performer.shortToast("Myo Connected");
    }

    @Override
    public void onLock(Myo myo, long timestamp) {
        inputResolver.resolveLock();
    }

    @Override
    public void onDisconnect(Myo myo, long timestamp) {
        performer.setMyo(null);
        inputResolver.setArm(null);
        performer.shortToast("Myo Disconnected");
    }

    @Override
    public void onPose(Myo myo, long timestamp, Pose pose) {
        inputResolver.resolvePose(pose);
    }

    @Override
    public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
        inputResolver.resolveOrientation(rotation);
    }

    @Override
    public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
        inputResolver.resolveAcceleration(accel);
    }

    @Override
    public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro) {
        inputResolver.resolveGyro(gyro);
    }

    @Override
    public void onUnlock(Myo myo, long timestamp) {
        inputResolver.resolveUnlock();
    }
}
