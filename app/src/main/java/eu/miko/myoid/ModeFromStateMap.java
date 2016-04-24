package eu.miko.myoid;

import android.util.Log;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ModeFromStateMap {
    final Mouse mouse;
    final Tapped tapped;
    final OptionsDoorway optionsDoorway;
    final Options options;
    final Media media;
    final MediaVolume mediaVolume;

    @Inject
    public ModeFromStateMap(Mouse mouse, Tapped tapped, OptionsDoorway optionsDoorway, Options options, Media media, MediaVolume mediaVolume) {
        this.mouse = mouse;
        this.tapped = tapped;
        this.optionsDoorway = optionsDoorway;
        this.options = options;
        this.media = media;
        this.mediaVolume = mediaVolume;
    }

    public Mode get(State state) {
        switch (state) {
            case MOUSE:
                return mouse;
            case TAPPED:
                return tapped;
            case OPTIONS_DOORWAY_FROM_MOUSE:
                return optionsDoorway;
            case OPTIONS_FROM_MOUSE:
                return options;
            case OPTIONS_DOORWAY_FROM_MEDIA:
                return optionsDoorway;
            case MEDIA:
                return media;
            case OPTIONS_FROM_MEDIA:
                return options;
            case MEIDA_VOLUME:
                return mediaVolume;
            default:
                return new Mode(null) {
                    final private String TAG = "UnimplementedMode";
                    @Override
                    public Event resolvePose(Pose pose) {
                        Log.w(TAG, "resolvePose called on an unimplemented Event");
                        return null;
                    }

                    @Override
                    public Event resolveOrientation(Quaternion rotation) {
                        Log.w(TAG, "resolveOrientation called on an unimplemented Event");
                        return null;
                    }

                    @Override
                    public Event resolveAcceleration(Vector3 acceleration) {
                        Log.w(TAG, "resolveAcceleration called on an unimplemented Event");
                        return null;
                    }

                    @Override
                    public Event resolveGyro(Vector3 gyro) {
                        Log.w(TAG, "resolveGyro called on an unimplemented Event");
                        return null;
                    }

                    @Override
                    public void resolveUnlock() {
                        Log.w(TAG, "resolveGyro called on an unimplemented Event");
                    }
                };
        }
    }
}
