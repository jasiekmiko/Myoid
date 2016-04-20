package eu.miko.myoid;

import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.sin;

public class OptionsController {
    private final WindowManager windowManager;
    private final Point screenSize = new Point();
    private View optionsWindow;
    private ImageView pointer;
    private final Map<MainIcon, View> mainIcons = new HashMap<>();
    private final Map<NavIcon, View> navIcons = new HashMap<>();
    private double circleRadius;
    private Point circleCenter;
    WindowManager.LayoutParams optionsLayoutParams;
    private WindowManager.LayoutParams pointerParams;
    Boolean graphicsInitialized = false;
    private final MyoidAccessibilityService mas;
    private int iconRadius;

    @Inject
    public OptionsController(WindowManager windowManager, MyoidAccessibilityService mas) {
        this.windowManager = windowManager;
        this.mas = mas;
        windowManager.getDefaultDisplay().getSize(screenSize);
        calculateUiSizes();
    }

    private void calculateUiSizes() {
        iconRadius = (int) (min(screenSize.x, screenSize.y) * 1.0/12);
        circleRadius = min(screenSize.x, screenSize.y) * 1.0/3;
        circleCenter = new Point(screenSize.x/2, screenSize.y/2);
    }

    public void displayOptions() {
        if (!graphicsInitialized) {
            initializeGraphics();
            graphicsInitialized = true;
        }
        optionsWindow.setVisibility(View.VISIBLE);
        for (View icon : mainIcons.values()) {
            icon.setVisibility(View.VISIBLE);
        }
    }

    private void initializeGraphics() {
        initOptionsWindow();
        initIcons();
        initPointer();
    }

    public void dismissOptions() {
        optionsWindow.setVisibility(View.GONE);
        for (View icon : mainIcons.values()) {
            icon.setVisibility(View.GONE);
        }
    }

    void initOptionsWindow() {
        optionsWindow = new OptionsWindow(mas);
        optionsWindow.setVisibility(View.GONE);
        optionsWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismissOptions();
                    return true;
                }
                return false;
            }
        });
        optionsLayoutParams = new WindowManager.LayoutParams(
                screenSize.x - 200, // size
                screenSize.y - 200,
                100, // offsets
                100,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        optionsLayoutParams.gravity = Gravity.TOP | Gravity.START;
        windowManager.addView(optionsWindow, optionsLayoutParams);
    }

    private void initPointer() {
        pointer = new ImageView(mas);
        pointer.setImageResource(R.mipmap.ic_launcher);
        pointerParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        resetPointerToCenter();
    }

    private void resetPointerToCenter() {
        pointerParams.x = circleCenter.x;
        pointerParams.y = circleCenter.y;
    }

    public void movePointer(int x, int y) {
        if (graphicsInitialized) {
            pointerParams.x = x;
            pointerParams.y = y;
            windowManager.updateViewLayout(pointer, pointerParams);
        }
    }

    void initIcons() {
        int i = 0;
        for (MainIcon iconName : MainIcon.values()) {
            ImageView icon = initializeIconInCircle(i, MainIcon.values().length);
            mainIcons.put(iconName, icon);
            i++;
        }
        i = 0;
        for (NavIcon iconName: NavIcon.values()) {
            ImageView icon = initializeIconInCircle(i-1, 4);
            navIcons.put(iconName, icon);
            i++;
        }
    }

    @NonNull
    private ImageView initializeIconInCircle(int i, int nIcons) {
        ImageView icon = createIconImageView();
        WindowManager.LayoutParams params = createIconLayoutParams(i, nIcons);
        windowManager.addView(icon, params);
        return icon;
    }

    @NonNull
    private WindowManager.LayoutParams createIconLayoutParams(int i, int nIcons) {
        Rect pos = calculateIconPositions(i, nIcons);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                iconRadius*2, iconRadius*2, //size
                pos.left, pos.top,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.LEFT;
        return params;
    }

    @NonNull
    private ImageView createIconImageView() {
        ImageView icon = new ImageView(mas);
        icon.setImageResource(R.mipmap.ic_launcher);
        icon.setVisibility(View.GONE);
        return icon;
    }

    private Rect calculateIconPositions(int index, int nIcons) {
        double angle = (2.0/nIcons)*index;
        double dx = circleRadius*sin(angle * PI);
        double dy = -circleRadius*cos(angle * PI);
        Point center = new Point(circleCenter);
        center.offset((int) dx, (int) dy);
        Rect iconRect = new Rect(center.x-iconRadius, center.y-iconRadius, center.x+iconRadius, center.y+iconRadius);
        return iconRect;
    }

    private interface Icon {
    }
    private enum MainIcon implements Icon {
        SEARCH,
        MEDIA_MOUSE,
        NAV,
        QS
    }
    private enum NavIcon implements Icon {
        BACK,
        HOME,
        RECENT
    }
}