package eu.miko.myoid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import javax.inject.Inject;

class OptionsWindow extends View {
    private Rect viewRectangle = new Rect();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private double circleRadius;

    @Inject
    public OptionsWindow(Context context, double circleRadius) {
        super(context);
        this.circleRadius = circleRadius*1.1f;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);
        viewRectangle.set(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
    }

    private void drawBackground(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(150, 0, 188, 222));
        canvas.drawCircle(viewRectangle.centerX(),viewRectangle.centerY(), (float) circleRadius, paint);
    }
}
