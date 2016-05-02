package eu.miko.myoid;

import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MediaStatusController {
    final private MyoidAccessibilityService mas;
    final private WindowManager windowManager;
    private ImageView statusCircle;
    private WindowManager.LayoutParams params;
    private boolean viewsAdded = false;

    @Inject
    public MediaStatusController(MyoidAccessibilityService mas, WindowManager windowManager) {
        this.mas = mas;
        this.windowManager = windowManager;

        initializeViews();
    }

    private void initializeViews() {
        statusCircle = new ImageView(mas);
        statusCircle.setImageResource(R.drawable.blank);
        statusCircle.setVisibility(View.GONE);

        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        params = new WindowManager.LayoutParams(
                138, 138,
                size.x - 120, 100,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
    }

    public void display() {
        if (!viewsAdded) {
            windowManager.addView(statusCircle, params);
            viewsAdded = true;
        }
        statusCircle.setImageResource(R.drawable.blank);
        statusCircle.setVisibility(View.VISIBLE);
    }

    public void changeImage (int resource) {
        statusCircle.setImageResource(resource);
    }

    public void hide() {
        statusCircle.setVisibility(View.GONE);
    }
}
