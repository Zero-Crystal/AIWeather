package com.zero.aiweather.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
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
import com.zero.base.util.DateUtil;

import java.util.Date;

public class AstronomyView extends View {
    /** 日出icon*/
    private Drawable iconSun;
    /** 圆弧颜色*/
    private int bgCircleColor;
    /** 进度圆弧颜色*/
    private int progressColor;
    /** 描边宽度*/
    private float strokeWidth;
    /** 文字颜色*/
    private int textColor;
    /** 文字大小*/
    private float textSize;
    /** 日出时间*/
    private String startTime = "00:00";
    /** 日落时间*/
    private String endTime = "24:00";

    /** 圆弧半径*/
    private float mCircleRadius;
    /** 画布中心坐标-x*/
    private float centerX;
    /** 画布中心坐标-y*/
    private float centerY;
    /** icon坐标*/
    private final float[] iconPos = new float[2];
    /** icon动画*/
    private float animationValue;
    /** 圆弧扫过的圆心角*/
    private float sweepAngle;

    /** 文字Rect*/
    private final Rect textBound = new Rect();
    /** 半圆Rect*/
    private final RectF circleBound = new RectF();
    /** icon Rect*/
    private final Rect iconRect = new Rect();

    private Paint paint;

    public AstronomyView(Context context) {
        this(context, null);
    }

    public AstronomyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AstronomyView);
        try {
            bgCircleColor = typedArray.getColor(R.styleable.AstronomyView_astronomy_circle_color, ContextCompat.getColor(context, R.color.white));
            progressColor = typedArray.getColor(R.styleable.AstronomyView_astronomy_progress_circle_color, ContextCompat.getColor(context, R.color.gray));
            mCircleRadius = typedArray.getDimension(R.styleable.AstronomyView_astronomy_circle_radius, 0);
            strokeWidth = typedArray.getDimension(R.styleable.AstronomyView_astronomy_circle_width, 5);
            textColor = typedArray.getColor(R.styleable.AstronomyView_astronomy_text_color, ContextCompat.getColor(context, R.color.white));
            textSize = typedArray.getDimension(R.styleable.AstronomyView_astronomy_text_size, DensityUtil.sp2px(14));
            iconSun = typedArray.getDrawable(R.styleable.AstronomyView_astronomy_icon);
        } finally {
            typedArray.recycle();
        }

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float width = right - left;
        float height = bottom - top;
        //文字
        updateTextBound();
        //中心坐标
        centerX = width / 2;
        centerY = height - textBound.height() * 2 - getPaddingBottom();
        //半径
        if (mCircleRadius == 0) {
            mCircleRadius = Math.min(centerX, centerY) - (iconSun.getIntrinsicHeight() >> 1) - strokeWidth - getPaddingTop();
        }
        //icon初始坐标
        iconPos[0] = centerX - mCircleRadius;
        iconPos[1] = centerY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawBgCircle(canvas);
        drawIcon(canvas);
        drawProgressCircle(canvas);
    }

    private void drawText(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(textColor);
        updateTextBound();
        canvas.drawText(startTime, centerX - mCircleRadius, centerY + textBound.height() * 2, paint);
        canvas.drawText(endTime, centerX + mCircleRadius, centerY + textBound.height() * 2, paint);
    }

    private void updateTextBound() {
        String text = startTime.length() > endTime.length() ? startTime : endTime;
        paint.getTextBounds(text, 0, text.length(), textBound);
    }

    private void drawBgCircle(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(bgCircleColor);
        paint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
        updateCircleBound();
        canvas.drawArc(circleBound, 180, 180, false, paint);
    }

    private void updateCircleBound() {
        circleBound.set(centerX - mCircleRadius, centerY - mCircleRadius,
                centerX + mCircleRadius, centerY + mCircleRadius);
    }

    private void drawIcon(Canvas canvas) {
        calculateCirclePoint(sweepAngle);
        iconRect.set((int) (iconPos[0] - iconSun.getIntrinsicWidth() / 2),
                (int) (iconPos[1] - iconSun.getIntrinsicHeight() / 2),
                (int) (iconPos[0] + iconSun.getIntrinsicWidth() / 2),
                (int) iconPos[1] + iconSun.getIntrinsicHeight() / 2);
        iconSun.setBounds(iconRect);
        iconSun.draw(canvas);
    }

    private void drawProgressCircle(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(progressColor);
        paint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
        canvas.drawArc(circleBound, 180, sweepAngle * animationValue, false, paint);
    }

    /**
     * 计算坐标
     * */
    private void calculateCirclePoint(float sweepAngle) {
        //1. 添加圆弧路径
        Path rootPath = new Path();
        rootPath.addArc(circleBound, 180, sweepAngle);
        //2. 根据圆弧长计算位置坐标
        PathMeasure measure = new PathMeasure(rootPath, false);
        measure.getPosTan(measure.getLength() * animationValue, iconPos, null);
    }

    /**
     * 计算圆弧sweepAngle
     * */
    private void calculateSweepAngle(String startTime, String endTime) {
        Date riseDate = DateUtil.stringToDate(startTime, DateUtil.format2);
        Date setDate = DateUtil.stringToDate(endTime, DateUtil.format2);
        long riseSetDiffer = DateUtil.getTwoDaysDiffer(riseDate, setDate);
        Date nowDate = new Date(System.currentTimeMillis());
        long nowDiffer = DateUtil.getTwoDaysDiffer(riseDate, nowDate);

        sweepAngle = Math.round(180 * MathCalculateUtil.div(nowDiffer, riseSetDiffer));
    }

    /**
     * 开始动画
     * */
    public void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(valueAnimator -> {
            animationValue = (float) valueAnimator.getAnimatedValue();
            invalidate();
        });
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(5 * 1000);
        animator.start();
    }

    /**
     * 是否在屏幕上可见
     * */
    public boolean isVisibleInScreen() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Rect screenRect = new Rect();
        display.getRectSize(screenRect);
        return getLocalVisibleRect(screenRect);
    }

    /**
     * 添加日出日落时间
     * */
    public void setSunTime(String startTime, String endTime) {
        calculateSweepAngle(startTime, endTime);
        this.startTime = startTime.substring(10);
        this.endTime = endTime.substring(10);
    }

    /**
     * 设置圆弧半径
     * */
    public void setCircleRadius(float mCircleRadius) {
        this.mCircleRadius = mCircleRadius;
    }

    /**
     * 获取文字大小
     * */
    public Rect getTextBound() {
        return textBound;
    }

    /**
     * 获取半径大小
     * */
    public float getCircleRadius() {
        return mCircleRadius;
    }
}
