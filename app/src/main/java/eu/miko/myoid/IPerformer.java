package eu.miko.myoid;

import com.thalmic.myo.Myo;

public interface IPerformer {
    void shortToast(String text);

    void setMyo(Myo myo);

    void lockMyo();

    void unlockMyoTimed();

    void unlockMyoHold();

    void openNotifications();

    void openRecents();

    void goBack();

    void goHome();

    void initCursor();

    void displayCursor();

    void displayOptions();

    void dismissOptions();

    void moveCursor(int x, int y);

    void hideCursor();

    void mouseScroll(boolean down);

    void mouseTap();

    void moveOptionsPointer(int x, int y);
}
