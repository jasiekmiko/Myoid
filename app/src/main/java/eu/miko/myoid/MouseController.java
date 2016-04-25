package eu.miko.myoid;

import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.test.RenamingDelegatingContext;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;

import com.thalmic.myo.Pose;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseController {
    ImageView cursor;
    WindowManager.LayoutParams cursorParams;
    boolean cursorViewAdded = false;
    private final MyoidAccessibilityService mas;
    private final WindowManager windowManager;
    private final Point screenSize = new Point();

    @Inject
    public MouseController(MyoidAccessibilityService mas, WindowManager windowManager) {
        this.mas = mas;
        this.windowManager = windowManager;

        Display display = windowManager.getDefaultDisplay();
        display.getSize(screenSize);
        initCursorAndCursorParams();
    }

    public void initCursorAndCursorParams() {
        cursor = new ImageView(mas);
        cursor.setImageResource(R.drawable.cursor_pan);

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

    public void displayCursor() {
        if (!cursorViewAdded) {
            windowManager.addView(cursor, cursorParams);
            cursorViewAdded = true;
        }
        cursor.setVisibility(View.VISIBLE);
    }

    public void moveCursor(int x, int y) {
        if (cursorViewAdded) {
            cursorParams.x = keepOnScreenX(cursorParams.x + x);
            cursorParams.y = keepOnScreenY(cursorParams.y + y);
            windowManager.updateViewLayout(cursor, cursorParams);
        }
    }

    public void hideCursor() {
        cursor.setVisibility(View.GONE);
    }

    public String mouseScroll(boolean down) {
        int scrollDir = down ? AccessibilityNodeInfo.ACTION_SCROLL_FORWARD : AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD;
        AccessibilityNodeInfo root = mas.getRootInActiveWindow();
        AccessibilityNodeInfo rootUnderCursor = findChildAt(root, cursorParams.x, cursorParams.y);
        if (rootUnderCursor != null) {
            AccessibilityNodeInfo scrollableView = findScrollable(rootUnderCursor);
            if (scrollableView != null)
                scrollableView.performAction(scrollDir);
            else return "nothing to scroll here";
        } else return "nothing here!";
        return null;
    }

    public String mouseTap() {
        AccessibilityNodeInfo root = mas.getRootInActiveWindow();
        AccessibilityNodeInfo rootUnderCursor = findChildAt(root, cursorParams.x, cursorParams.y);
        if (rootUnderCursor != null) {
            AccessibilityNodeInfo clickableNode = findClickable(rootUnderCursor);
            if (clickableNode != null) {
                clickableNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else return "nothing to tap here";
        } else return "nothing here!";
        return null;
    }

    private int keepOnScreenY(int y) {
        return Math.max(0, Math.min(y, screenSize.y));
    }

    private int keepOnScreenX(int x) {
        return Math.max(0, Math.min(x, screenSize.x));
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
        for (int i = 0; i < nChildren; i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            AccessibilityNodeInfo maybeClickable = findClickable(child);
            if (maybeClickable != null) return maybeClickable;
        }
        return null;
    }

    private AccessibilityNodeInfo findScrollable(AccessibilityNodeInfo root) {
        if (root.isScrollable()) return root;
        int nChildren = root.getChildCount();
        for (int i = 0; i < nChildren; i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            AccessibilityNodeInfo maybeClickable = findScrollable(child);
            if (maybeClickable != null) return maybeClickable;
        }
        return null;
    }

    public void changeCursorImage(Pose pose) {
        int resource = R.drawable.cursor_pan;
        switch (pose) {
            case REST:
                resource = R.drawable.cursor_pan;
                break;
            case FIST:
                resource = R.drawable.cursor_fist;
                break;
            case WAVE_IN:
                resource = R.drawable.cursor_left;
                break;
            case WAVE_OUT:
                resource = R.drawable.cursor_right;
                break;
            case FINGERS_SPREAD:
                resource = R.drawable.cursor_spread;
                break;
            case DOUBLE_TAP:
                resource = R.drawable.cursor_double_tap;
                break;
        }
        cursor.setImageResource(resource);
    }
}