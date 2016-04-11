package eu.miko.myoid;

import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.sin;

public class OptionsController {
    private static final int N_ICONS = 4;
    private final WindowManager windowManager;
    private final Point screenSize = new Point();
    private final View optionsWindow;
    private final List<View> optionIcons = new LinkedList<>();
    WindowManager.LayoutParams optionsLayoutParams;
    Boolean optionsWindowInitialized = false;
    private final MyoidAccessibilityService mas;
    private int iconRadius;

    @Inject
    public OptionsController(WindowManager windowManager, MyoidAccessibilityService mas) {
        this.windowManager = windowManager;
        this.mas = mas;
        optionsWindow = new OptionsWindow(mas);
        windowManager.getDefaultDisplay().getSize(screenSize);
    }

    public void displayOptions() {
        if (!optionsWindowInitialized) {
            initOptionsWindow();
            initIcons(N_ICONS);
            optionsWindowInitialized = true;
        }
        optionsWindow.setVisibility(View.VISIBLE);
        for (View icon : optionIcons) {
            icon.setVisibility(View.VISIBLE);
        }
    }

    public void dismissOptions() {
        optionsWindow.setVisibility(View.GONE);
        for (View icon : optionIcons) {
            icon.setVisibility(View.GONE);
        }
    }

    void initOptionsWindow() {
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

    void initIcons(int nIcons) {
        List<Rect> iconPositions = calculateIconPositions(screenSize, nIcons);
        for (Rect pos: iconPositions) {
            ImageView icon = new ImageView(mas);
            icon.setImageResource(R.mipmap.ic_launcher);
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
            windowManager.addView(icon, params);
            optionIcons.add(icon);
        }
    }

    private List<Rect> calculateIconPositions(Point screenSize, int nIcons) {
        List<Rect> result = new LinkedList<>();
        double radius = min(screenSize.x, screenSize.y) * 1.0/3;
        iconRadius = (int)radius/4;
        Point circleCenter = new Point(screenSize.x/2, screenSize.y/2);
        for (int i = 0; i < nIcons; i++) {
            double angle = (2.0/nIcons)*i;
            double dx = radius*sin(angle * PI);
            double dy = radius*cos(angle * PI);
            Point iconCenter = new Point(circleCenter);
            iconCenter.offset((int) dx, (int) dy);
            Rect iconRect = new Rect(iconCenter.x-iconRadius, iconCenter.y-iconRadius,iconCenter.x+iconRadius, iconCenter.y+iconRadius);
            result.add(iconRect);
        }
        return result;
    }
}