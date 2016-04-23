package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.Toast;

import com.thalmic.myo.Myo;

import javax.inject.Inject;
import javax.inject.Singleton;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Singleton
public class Performer implements IPerformer {
    private static final String TAG = Performer.class.getName();
    private final OptionsController optionsController;

    @Inject
    public Performer(WindowManager windowManager, OptionsController optionsController) {
        this.windowManager = windowManager;
        this.optionsController = optionsController;

        Display display = windowManager.getDefaultDisplay();
        display.getSize(screenSize);
        initCursor();
    }

    private Myo myo;

    private final Point screenSize = new Point();

    private ImageView cursor;
    private WindowManager.LayoutParams cursorParams;
    private boolean cursorInitialized = false;
    private MyoidAccessibilityService mas = MyoidAccessibilityService.getMyoidService();
    private WindowManager windowManager;

    @Override
    public void shortToast(String text) {
        Toast.makeText(mas, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setMyo(Myo myo) {
        this.myo = myo;
    }

    @Override
    public void lockMyo() {
        myo.lock();
        myo.vibrate(Myo.VibrationType.SHORT);
    }

    @Override
    public void unlockMyoTimed() {
        myo.unlock(Myo.UnlockType.HOLD);
        myo.vibrate(Myo.VibrationType.SHORT);
    }

    @Override
    public void unlockMyoHold() {
        myo.unlock(Myo.UnlockType.HOLD);
        myo.vibrate(Myo.VibrationType.SHORT);
    }

    @Override
    public void openNotifications() {
        mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
    }

    @Override
    public void openRecents() {
        mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
    }

    @Override
    public void goBack() {
        mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        Log.d(TAG, "Back global action performed.");
    }

    @Override
    public void goHome() {
        mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
        Log.d(TAG, "Home global action performed.");
    }

    @Override
    public void initCursor() {
        cursor = new ImageView(mas);
        cursor.setImageResource(R.mipmap.ic_launcher);

        cursorParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        cursorParams.gravity = Gravity.TOP | Gravity.START;
        cursorParams.x = 0;
        cursorParams.y = 100;
    }

    @Override
    public void displayCursor() {
        if(!cursorInitialized) {
            windowManager.addView(cursor, cursorParams);
            cursorInitialized = true;
        }
        cursor.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayOptions() {
        optionsController.displayOptions();
    }

    @Override
    public void dismissOptions() {
        optionsController.dismissOptions();
    }

    @Override
    public void moveCursor(int x, int y) {
        if (cursorInitialized) {
            cursorParams.x = keepOnScreenX(cursorParams.x + x);
            cursorParams.y = keepOnScreenY(cursorParams.y + y);
            windowManager.updateViewLayout(cursor, cursorParams);
        }
    }

    @Override
    public void hideCursor() {
        cursor.setVisibility(View.GONE);
    }

    @Override
    public void mouseScroll(boolean down) {
        int scrollDir = down ? AccessibilityNodeInfo.ACTION_SCROLL_FORWARD : AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD;
        AccessibilityNodeInfo root = mas.getRootInActiveWindow();
        AccessibilityNodeInfo rootUnderCursor = findChildAt(root, cursorParams.x, cursorParams.y);
        if (rootUnderCursor != null) {
            AccessibilityNodeInfo scrollableView = findScrollable(rootUnderCursor);
            if (scrollableView != null)
                scrollableView.performAction(scrollDir);
            else shortToast("nothing to scroll here");
        } else shortToast("nothing here!");
    }

    @Override
    public void mouseTap() {
        AccessibilityNodeInfo root = mas.getRootInActiveWindow();
        AccessibilityNodeInfo rootUnderCursor = findChildAt(root, cursorParams.x, cursorParams.y);
        if (rootUnderCursor != null) {
            AccessibilityNodeInfo clickableNode = findClickable(rootUnderCursor);
            if (clickableNode != null) {
                clickableNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else shortToast("nothing to tap here");
        } else shortToast("nothing here!");

    }

    @Override
    public boolean moveOptionsPointerBy(int x, int y) {
        Icon targetIcon = optionsController.movePointerBy(x, y);
        if (targetIcon != null) {
            optionsController.resetPointerToCenter();
            boolean optionSelected = performOption(targetIcon);
            return optionSelected;
        }
        return false;
    }

    @Override
    public boolean optionsGoBack() {
        return optionsController.goBack();
    }

    private int keepOnScreenY(int y) {
        return max(0, min(y, screenSize.y));
    }

    private int keepOnScreenX(int x) {
        return max(0, min(x, screenSize.x));
    }

    private AccessibilityNodeInfo findChildAt(AccessibilityNodeInfo nodeInfo, int x, int y) {
        if (nodeInfo == null) return null;
        Rect bounds = new Rect();
        nodeInfo.getBoundsInScreen(bounds);
        int childCount = nodeInfo.getChildCount();
        if (!bounds.contains(x, y)) return null;
        else if (childCount == 0) return nodeInfo;

        int childIndex = 0;
        while (childIndex < childCount) {
            AccessibilityNodeInfo result = findChildAt(nodeInfo.getChild(childIndex), x, y);
            if (result != null) return result;
            childIndex += 1;
        }
        return nodeInfo;

    }

    private AccessibilityNodeInfo findClickable(AccessibilityNodeInfo root) {
        if (root.isClickable()) return root;
        int nChildren = root.getChildCount();
        for (int i = 0; i < nChildren; i++){
            AccessibilityNodeInfo child = root.getChild(i);
            AccessibilityNodeInfo maybeClickable = findClickable(child);
            if (maybeClickable != null) return maybeClickable;
        }
        return null;
    }

    private AccessibilityNodeInfo findScrollable(AccessibilityNodeInfo root) {
        if (root.isScrollable()) return root;
        int nChildren = root.getChildCount();
        for (int i = 0; i < nChildren; i++){
            AccessibilityNodeInfo child = root.getChild(i);
            AccessibilityNodeInfo maybeClickable = findScrollable(child);
            if (maybeClickable != null) return maybeClickable;
        }
        return null;
    }

    private boolean performOption(Icon target) {
        if (target instanceof OptionsController.MainIcon)
            switch ((OptionsController.MainIcon) target) {
                case SEARCH:
                    openVoiceSearch();
                    return true;
                case MEDIA_MOUSE:
                    break;
                case NAV:
                    optionsController.showIconSet(OptionsController.IconSet.NAV);
                case QS:
                    break;
            }
        else if (target instanceof OptionsController.NavIcon){
            switch ((OptionsController.NavIcon) target) {
                case BACK:
                    goBack();
                    return true;
                case HOME:
                    goHome();
                    return true;
                case RECENT:
                    break;
            }
        }
        return false;
    }

    private void openVoiceSearch() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mas.startActivity(intent);
    }
}
