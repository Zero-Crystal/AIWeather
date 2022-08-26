package com.zero.aiweather.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.zero.aiweather.R;
import com.zero.aiweather.utils.MathCalculateUtil;
import com.zero.aiweather.utils.DensityUtil;
import com.zero.base.util.KLog;

/**
 * 圆环进度条
 * */
public class CircleProgressView extends View {

    //背景圆环颜色
    private int bgCircleColor;
    //进度条颜色
    private int progressColor;
    //进度条宽度
    private float progressWidth;
    //进度最大值
    private int maxProgress;
    //设置level颜色
    private int levelTextColor;
    //设置level大小
    private float levelTextSize;
    //Level
    private String level;
    //设置aqi颜色
    private int aqiTextColor;
    //设置aqi大小
    private float aqiTextSize;
    //Aqi
    private String aqi;
    //设置文字间隔
    private float textVerticalPadding;

    //进度结束角度
    private float sweepAngle;
    //文字X坐标
    private float textBaseX;
    //文字Y坐标
    private float textBaseY;
    //动画变化值
    private float animationValue;
    //圆环
    private final RectF circleBound = new RectF();

    private Paint paint;

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        try {
            bgCircleColor = array.getColor(R.styleable.CircleProgressView_bgCircleColor, ContextCompat.getColor(context, R.color.white_70));
            progressColor = array.getColor(R.styleable.CircleProgressView_progressColor, ContextCompat.getColor(context, R.color.green));
            progressWidth = array.getDimension(R.styleable.CircleProgressView_progressWidth, DensityUtil.dp2px(10));
            maxProgress = array.getInteger(R.styleable.CircleProgressView_maxProgress, 500);
            levelTextColor = array.getColor(R.styleable.CircleProgressView_levelTextColor, ContextCompat.getColor(context, R.color.white));
            levelTextSize = array.getDimension(R.styleable.CircleProgressView_levelTextSize, DensityUtil.sp2px(14));
            level = array.getString(R.styleable.CircleProgressView_levelText);
            aqiTextColor = array.getColor(R.styleable.CircleProgressView_aqiTextColor, ContextCompat.getColor(context, R.color.white));
            aqiTextSize = array.getDimension(R.styleable.CircleProgressView_aqiTextSize, DensityUtil.sp2px(18));
            aqi = array.getString(R.styleable.CircleProgressView_aqiText);
            textVerticalPadding = array.getDimension(R.styleable.CircleProgressView_textVerticalPadding, DensityUtil.dp2px(40));
        } finally {
            array.recycle();
        }

        paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float width = right - left - getPaddingLeft() - getPaddingRight();
        float height = bottom - top - getPaddingTop() - getPaddingBottom();
        float centerX = width / 2;
        float centerY = height / 2;
        float circleRadius = centerX - progressWidth;
        textBaseX = centerX;
        textBaseY = height / 3 * 2;
        circleBound.set(centerX - circleRadius, centerY - circleRadius,
                centerX + circleRadius, centerY + circleRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制level
        drawLevel(canvas);
        //绘制Aqi
        drawAqi(canvas);
        //绘制背景圆弧
        drawBgCircle(canvas);
        //绘制进度条
        drawProgress(canvas);
    }

    private void drawLevel(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(levelTextColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(levelTextSize);
        canvas.drawText(level, textBaseX, textBaseY - textVerticalPadding, paint);
    }

    private void drawAqi(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(aqiTextColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(aqiTextSize);
        canvas.drawText(aqi, textBaseX, textBaseY, paint);
    }

    private void drawBgCircle(Canvas canvas) {
        paint.setStrokeWidth(progressWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(bgCircleColor);
        canvas.drawArc(circleBound, 150, 240, false, paint);
    }

    private void drawProgress(Canvas canvas) {
        paint.setStrokeWidth(progressWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(progressColor);
        canvas.drawArc(circleBound, 150, sweepAngle * animationValue, false, paint);
    }

    public void startAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(valueAnimator -> {
            animationValue = (float) valueAnimator.getAnimatedValue();
            invalidate();
        });
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(2 * 1000);
        animator.start();
    }

    public boolean isVisibleInScreen() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Rect screenRect = new Rect();
        display.getRectSize(screenRect);
        return getLocalVisibleRect(screenRect);
    }

    private void setSweepAngle(float endNum) {
        if (endNum > 300) {
            sweepAngle = 240;
        } else {
            sweepAngle = Math.round(240 * MathCalculateUtil.div(endNum, maxProgress));
        }
        KLog.d("计算角度= " + sweepAngle);
        invalidate();
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
        setSweepAngle(Float.parseFloat(aqi));
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }
}
