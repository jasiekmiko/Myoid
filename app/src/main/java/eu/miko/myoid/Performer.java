package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.Toast;

import com.thalmic.myo.Myo;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Performer {
    private static Performer instance;

    public static Performer getInstance() {
        if (instance == null) instance = new Performer();
        return instance;
    }

    private Myo myo;
    private Point screenSize;
    private ImageView cursor;
    private WindowManager.LayoutParams cursorParams;
    private boolean cursorInitialized = false;
    private MyoidAccessibilityService mas = MyoidAccessibilityService.getMyoidService();

    public void shortToast(String text) {
        Toast.makeText(mas, text, Toast.LENGTH_SHORT).show();
    }

    public void setMyo(Myo myo) {
        this.myo = myo;
    }

    public void lockMyo() {
        myo.lock();
        myo.vibrate(Myo.VibrationType.SHORT);
    }

    public void unlockMyoTimed() {
        myo.unlock(Myo.UnlockType.HOLD);
        myo.vibrate(Myo.VibrationType.SHORT);
    }

    public void unlockMyoHold() {
        myo.unlock(Myo.UnlockType.HOLD);
        myo.vibrate(Myo.VibrationType.SHORT);
    }

    public void openNotifications() {
        mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
    }

    public void openRecents() {
        mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
    }

    public void goBack() {
        mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    public void goHome() {
        mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    public void initCursor(Point screenSize) {
        this.screenSize = screenSize;
        cursor = new ImageView(mas);
        cursor.setImageResource(R.mipmap.ic_launcher);

        cursorParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        cursorParams.gravity = Gravity.TOP | Gravity.LEFT;
        cursorParams.x = 0;
        cursorParams.y = 100;
    }

    public void displayCursor() {
        WindowManager windowManager = mas.getWindowManager();
        windowManager.addView(cursor, cursorParams);
        cursorInitialized = true;
    }

    public void moveCursor(int x, int y) {
        if (cursorInitialized) {
            cursorParams.x = keepOnScreenX(cursorParams.x + x);
            cursorParams.y = keepOnScreenY(cursorParams.y + y);
            mas.getWindowManager().updateViewLayout(cursor, cursorParams);
        }
    }

    private int keepOnScreenY(int y) {
        return max(0, min(y, screenSize.y));
    }

    private int keepOnScreenX(int x) {
        return max(0, min(x, screenSize.x));
    }

    public void hideCursor() {
        if (cursor != null) mas.getWindowManager().removeView(cursor);
    }

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
}
