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

    @Inject
    public OptionsWindow(Context context) {
        super(context);
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
        paint.setColor(Color.argb(123, 0, 0, 255));
        canvas.drawRect(viewRectangle, paint);
    }
}
