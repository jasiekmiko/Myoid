package eu.miko.myoid;

import eu.miko.myoid.Gesture.State;

public interface InterfaceMode {
    State resolveGestureState(Gesture gesture);
}
