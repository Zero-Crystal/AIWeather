package com.zero.aiweather.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.zero.aiweather.R;
import com.zero.base.util.DensityUtil;
import com.zero.base.util.LogUtils;

import java.math.BigDecimal;

/**
 * 圆环进度条
 * */
public class CircleProgressView extends View {
    private final String TAG = "CircleProgressView";

    //进度条颜色
    private int progressColor;
    //进度条宽度
    private float progressWidth;
    //进度最大值
    private int maxNum;
    //进度结束角度
    private float sweepAngle;
    //动画更新角度
    private float updateAngle;
    //圆环范围
    private RectF circleRect;
    //设置文字颜色
    private int textColor;
    //设置文字间隔
    private float textVerticalInterval;
    //Aqi
    private String aqi;
    //Level
    private String level;

    private Paint paint;
    private int defaultColor = getResources().getColor(R.color.white_70);
    private boolean isFirst = true;

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        progressColor = array.getColor(R.styleable.CircleProgressView_progressColor, ContextCompat.getColor(context, R.color.purple_200));
        progressWidth = array.getDimension(R.styleable.CircleProgressView_progressWidth, 10);
        aqi = array.getString(R.styleable.CircleProgressView_aqiText);
        level = array.getString(R.styleable.CircleProgressView_levelText);
        textColor = array.getColor(R.styleable.CircleProgressView_aqiTextColor, ContextCompat.getColor(context, R.color.white));
        textVerticalInterval = array.getDimension(R.styleable.CircleProgressView_textVerticalInterval, DensityUtil.dip2px(10));
        updateAngle = 0f;

        circleRect = new RectF();

        paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        circleRect.set(getLeft(), getTop(), getRight() - progressWidth * 2,getBottom() - progressWidth * 2);
        //绘制level
        drawText(canvas, level, -(textVerticalInterval / 2));
        //绘制Aqi
        Rect aqiRect = new Rect();
        paint.getTextBounds(aqi, 0, aqi.length(), aqiRect);
        Log.i(TAG, "onDraw: ---------> aqiHeight=" + aqiRect.height());
        drawText(canvas, aqi, aqiRect.height() + Math.round(textVerticalInterval / 2));

        //绘制背景圆弧
        drawProgress(canvas, defaultColor, 240f);
        //绘制进度条
        drawProgress(canvas, progressColor, updateAngle);
        //开始动画
        startAnimator();
    }

    private void drawText(Canvas canvas, String text, float offset) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);
        paint.setColor(textColor);
        paint.setTextSize(DensityUtil.sp2px(17));
        paint.setTextAlign(Paint.Align.CENTER);
        float textX = getWidth() / 2;
        float textY = getHeight() / 2 + offset;
        canvas.drawText(text, textX, textY, paint);
    }

    private void drawProgress(Canvas canvas, int color, float angle) {
        paint.setStrokeWidth(progressWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        canvas.drawArc(circleRect, -210f, angle, false, paint);
    }

    private void startAnimator() {
        ValueAnimator animator = ValueAnimator.ofObject(new CircleProgressEvaluator(), 1f, sweepAngle);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float updateValue = (float) valueAnimator.getAnimatedValue();
                updateAngle = Math.round(updateValue);
                invalidate();
            }
        });
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(2 * 1000);
        if (isFirst) {
            animator.start();
            isFirst = false;
        }
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public void setSweepAngle(float endNum) {
        if (endNum > 300) {
            sweepAngle = 240;
        } else {
            sweepAngle = Math.round(240 * div(endNum,  maxNum, 2));
        }
        LogUtils.d(TAG, TAG + ": 计算角度=" + sweepAngle);
        isFirst = true;
        invalidate();
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
