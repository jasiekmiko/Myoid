package eu.miko.myoid;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import javax.inject.Inject;

public class OptionsController {
    private final WindowManager windowManager;
    private final Point screenSize = new Point();
    View optionsWindow;
    WindowManager.LayoutParams optionsLayoutParams;
    Boolean optionsWindowInitialized = false;
    private final MyoidAccessibilityService mas;

    @Inject
    public OptionsController(WindowManager windowManager, MyoidAccessibilityService mas) {
        this.windowManager = windowManager;
        this.mas = mas;

        Display display = windowManager.getDefaultDisplay();
        display.getSize(screenSize);
    }

    public void displayOptions() {
        if (!optionsWindowInitialized) {
            initOptionsWindow();
            windowManager.addView(optionsWindow, optionsLayoutParams);
            optionsWindowInitialized = true;
        }
        optionsWindow.setVisibility(View.VISIBLE);
    }

    public void dismissOptions() {
        optionsWindow.setVisibility(View.GONE);
    }

    void initOptionsWindow() {
        optionsWindow = new View(mas) {
            private Rect rect = new Rect();
            private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            @Override
            protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
                super.onSizeChanged(w, h, oldWidth, oldHeight);
                rect.set(0, 0, w, h);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.argb(123, 0, 0, 255));
                canvas.drawRect(rect, paint);
            }
        };
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
                0, // position
                0,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
    }
}