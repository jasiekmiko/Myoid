package eu.miko.myoid;

import com.thalmic.myo.Pose;

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
}
