package eu.miko.myoid;

import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

public interface IPerformer {
    void initializeMediaControllers();

    void displayMediaControlsNotImplementedWarning();

    void shortToast(String text);

    void setMyo(Myo myo);

    void lockMyo();

    void unlockMyoTimed();

    void unlockMyoHold();

    void openRecents();

    void goBack();

    void goHome();

    void displayCursor();

    void displayOptions();

    void hideOptions();

    void moveCursor(int x, int y);

    void hideCursor();

    void mouseScroll(boolean out);

    void mouseTap();

    Event moveOptionsPointerBy(int x, int y);

    boolean optionsGoBack();

    void changeCursorImage(Pose pose);

    void changePointerImage(Pose pose);

    void performMediaAction(Media.Action action);

    void adjustMediaVolume(float roll);

    void changeMediaStatus(Pose pose);

    void setVolumeAdjustStart();

    void displayMediaStatus();

    void hideMediaStatus();
}
