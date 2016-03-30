package eu.miko.myoid;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
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
    @Inject
    public Performer(WindowManager windowManager) {
        this.windowManager = windowManager;

        Display display = windowManager.getDefaultDisplay();
        display.getSize(screenSize);
        initCursor();
    }

    private View optionsWindow;
    private WindowManager.LayoutParams optionsLayoutParams;
    private Boolean optionsWindowInitialized = false;

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
    }

    @Override
    public void goHome() {
        mas.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
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
        windowManager.addView(cursor, cursorParams);
        cursorInitialized = true;
    }

    @Override
    public void displayOptions() {
        if (!optionsWindowInitialized) {
            initOptionsWindow();
            windowManager.addView(optionsWindow, optionsLayoutParams);
            optionsWindowInitialized = true;
        }
        optionsWindow.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissOptions() {
        optionsWindow.setVisibility(View.GONE);
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
        if (cursor != null) windowManager.removeView(cursor);
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

    private void initOptionsWindow() {
        optionsWindow = new View(mas) {
            private Rect rect = new Rect();
            private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
                rect.set(0, 0, w, h);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.argb(123,0,0,255));
                canvas.drawRect(rect, paint);
            }
        };
        optionsWindow.setVisibility(View.GONE);
        optionsWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE){
                    dismissOptions();
                    return true;
                }
                return false;
            }
        });
        optionsLayoutParams = new WindowManager.LayoutParams(
                screenSize.x - 200, // size
                screenSize.y - 200,
                0, // position
                0,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
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
}
