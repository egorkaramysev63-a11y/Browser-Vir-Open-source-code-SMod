package com.vir.brower;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.graphics.Color;

// Кастомный прозрачный холст для рисования поверх WebView
 public class DrawingView extends View {
    private Paint paint;
    private Path path;

    public DrawingView(Context context) {
        super(context);
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED); // Цвет кисти по умолчанию
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(12f); // Толщина кисти
        setBackgroundColor(Color.TRANSPARENT); // Холст полностью прозрачный
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    public void clear() {
        path.reset();
        invalidate();
    }
}

