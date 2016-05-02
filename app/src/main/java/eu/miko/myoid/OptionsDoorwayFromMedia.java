package eu.miko.myoid;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Vector3;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OptionsDoorwayFromMedia extends OptionsDoorway{
    @Inject
    public OptionsDoorwayFromMedia(Performer performer) {
        super(performer);
    }

    @Override
    public Event resolvePose(Pose pose) {
        if (pose == Pose.REST) {
            performer.performMediaAction(Media.Action.PLAY_PAUSE);
            return Event.RELAX;
        }
        return null;
    }

    @Override
    public Event resolveAcceleration(Vector3 acceleration, boolean xDirectionTowardsElbow) {
        Event result = super.resolveAcceleration(acceleration, xDirectionTowardsElbow);
        if (result == Event.X_AXIS_PULL) performer.hideMediaStatus();
        return result;
    }
}
