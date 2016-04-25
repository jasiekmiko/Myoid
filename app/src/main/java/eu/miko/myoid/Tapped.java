package eu.miko.myoid;

import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
class Tapped extends Mouse {
    @Inject
    public Tapped(Performer performer) {
        super(performer);
    }

    @Override
    public Event resolvePose(Pose pose) {
        if (pose == Pose.REST) {
            performer.mouseTap();
            return Event.RELAX;
        }
        return null;
    }

}
