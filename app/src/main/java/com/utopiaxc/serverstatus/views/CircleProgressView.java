package com.utopiaxc.serverstatus.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 圆形进度组件
 *
 * @author UtopiaXC
 * @since 2022-05-24 12:33
 */
public class CircleProgressView extends View {
    private static final int COMPLETE = 360;
    private final int okSpeed = 3;

    private Paint paint;
    private int circleOutsideColor;
    private int circleProgressColor;
    private float progressW;
    private float circleProgressR;

    private RectF progressRect;
    private float progress;
    private String progressText;
    private Path okPath;

    public CircleProgressView(Context context) {
        super(context);
        init();
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circleOutsideColor = 0xffF2F4F5;
        circleProgressColor = 0xff6066DD;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
    }

    public void setProgress(float progress) {
        this.progress = progress * COMPLETE;
        if (this.progress >= COMPLETE) {
            this.progress = COMPLETE;
        }
        progressText = (int) (progress * 100) + "%";
        invalidate();
    }

    public void reset() {
        setProgress(0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (circleProgressR == 0 || okPath == null) {
            int w = getWidth();
            progressW = w * 0.061f;
            circleProgressR = (w - progressW) / 2f;

            progressRect = new RectF(0 + progressW / 2, 0 + progressW / 2, w - progressW / 2, w - progressW / 2);

            int okW = (int) ((getWidth() - progressW) * 0.45);
            int okH = (int) ((getHeight() - progressW) * 0.32);

            int sX;
            int cX = sX = (getWidth() - okW) / 2;
            int sY;
            int cY = sY = getHeight() / 2;
            int mX = (int) (sX + 0.39 * okW);
            int mY = (int) (sY + 0.35 * okW);
            int eX = getWidth() - (getWidth() - okW) / 2;
            int eY = (getHeight() - okH) / 2;

            okPath = new Path();
            okPath.moveTo(sX, sY);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(circleOutsideColor);
        paint.setStrokeWidth(progressW);
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, circleProgressR, paint);

        paint.setColor(circleProgressColor);
        canvas.drawArc(progressRect, -90, progress, false, paint);

        if (!TextUtils.isEmpty(progressText)) {
            int dx = getWidth() / 2;
            int dy = getHeight() / 2;
            canvas.translate(dx, dy);
            paint.setTextSize(sp2px(getContext(),14));
            float textWidth = paint.measureText(progressText);
            float baseLineY = Math.abs(paint.ascent() + paint.descent()) / 2;
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(progressText, -textWidth / 2, baseLineY, paint);
        }
    }

    public float getProgress() {
        return progress / COMPLETE;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
    }

    public interface OnProgressListener {
        void complete();
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
