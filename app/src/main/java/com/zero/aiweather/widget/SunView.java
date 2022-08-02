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
import com.zero.aiweather.utils.DensityUtil;
import com.zero.aiweather.utils.TimeUtil;
import com.zero.base.util.KLog;

import java.util.Date;

public class SunView extends View {
    /** 日出icon*/
    private Drawable iconSun;
    /** 圆弧颜色*/
    private int mCircleColor;
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

    /** icon宽*/
    private int iconWidth;
    /** icon高*/
    private int iconHeight;
    /** 圆弧半径*/
    private float mCircleRadius;
    /** 画布中心坐标-x*/
    private int centerX = 0;
    /** 画布中心坐标-y*/
    private int centerY = 0;
    /** icon坐标*/
    private float[] iconPos;
    /** icon动画*/
    private float mAnimationValue = 0;
    /** 圆弧扫过的圆心角*/
    private float sweepAngle = 0;
    /** 间距*/
    private float innerPadding = 10;

    /** 画布Rect*/
    private Rect mCanvasRect;
    /** 半圆Rect*/
    private RectF mCircleRect;
    /** icon Rect*/
    private Rect mIconRect;
    /** 文字Rect*/
    private Rect mTextRect;

    private Paint mCirclePaint;
    private Paint mTextPaint;

    public SunView(Context context) {
        this(context, null);
    }

    public SunView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SunView);
        mCircleColor = typedArray.getColor(R.styleable.SunView_sun_circle_color, ContextCompat.getColor(context, R.color.white));
        strokeWidth = typedArray.getDimension(R.styleable.SunView_sun_circle_width, 5);
        textColor = typedArray.getColor(R.styleable.SunView_sun_text_color, ContextCompat.getColor(context, R.color.white));
        textSize = typedArray.getDimension(R.styleable.SunView_sun_text_size, DensityUtil.sp2px(14));
        startTime = typedArray.getString(R.styleable.SunView_sun_rise_text);
        endTime = typedArray.getString(R.styleable.SunView_sun_set_text);
        iconSun = typedArray.getDrawable(R.styleable.SunView_sun_icon);

        iconWidth = iconSun.getIntrinsicWidth();
        iconHeight = iconSun.getIntrinsicHeight();
        iconPos = new float[2];

        mCanvasRect = new Rect();
        mCircleRect = new RectF();
        mIconRect = new Rect();
        mTextRect = new Rect();

        initPaint();
    }

    private void initPaint() {
        //圆弧Paint
        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStrokeWidth(strokeWidth);
        //虚线
        mCirclePaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
        //文字Paint
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(textColor);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //画布Rect
        mCanvasRect.set(left + getPaddingLeft(), top + getPaddingTop(),
                right - getPaddingRight(), bottom - getPaddingBottom());
        //中心坐标
        centerX = mCanvasRect.width() / 2;
        centerY = mCanvasRect.height();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initRect();
        drawHalfCircle(canvas, 180);
        drawIcon(canvas);
        drawText(canvas, startTime, centerX - mCircleRadius, centerY);
        drawText(canvas, endTime, centerX + mCircleRadius, centerY);
    }

    private void initRect() {
        //文字Rect
        String text = startTime.length() > endTime.length() ? startTime : endTime;
        mTextPaint.getTextBounds(text, 0, text.length(), mTextRect);
        float textHeight = Math.max(mTextRect.width() / 2, mTextRect.height()) + innerPadding;
        //半径
        mCircleRadius = Math.min(mCanvasRect.width() / 2, mCanvasRect.height())
                - iconHeight / 2 - strokeWidth / 2 - textHeight;
        //圆弧Rect(整体向上平移textHeight距离)
        mCircleRect.set(centerX - mCircleRadius, centerY - mCircleRadius - textHeight,
                centerX + mCircleRadius, centerY + mCircleRadius - textHeight);
    }

    private void drawHalfCircle(Canvas canvas, float sweepAngle) {
        canvas.drawArc(mCircleRect, 180, sweepAngle, false, mCirclePaint);
    }

    private void drawIcon(Canvas canvas) {
        calculateCirclePoint(sweepAngle);
        mIconRect.set((int) (iconPos[0] - iconWidth / 2),
                (int) (iconPos[1] - iconHeight / 2),
                (int) (iconPos[0] + iconWidth / 2),
                (int) iconPos[1] + iconHeight / 2);
        iconSun.setBounds(mIconRect);
        iconSun.draw(canvas);
    }

    private void drawText(Canvas canvas, String text, float x, float y) {
        canvas.drawText(text, x, y, mTextPaint);
    }

    /**
     * 计算坐标
     * */
    private void calculateCirclePoint(float sweepAngle) {
        //1. 添加圆弧路径
        Path rootPath = new Path();
        rootPath.addArc(mCircleRect, 180, sweepAngle);
        //2. 根据圆弧长计算位置坐标
        PathMeasure measure = new PathMeasure(rootPath, false);
        measure.getPosTan(measure.getLength() * mAnimationValue, iconPos, null);
    }

    /**
     * 计算圆弧sweepAngle
     * */
    private void calculateDateDiffer(String startTime, String endTime) {
        Date date1 = TimeUtil.stringToDate("00:00", TimeUtil.format1);
        Date date2 = TimeUtil.stringToDate("24:00", TimeUtil.format1);
        long oneDayDiffer = TimeUtil.getTwoDaysDiffer(date1, date2);
        KLog.i("00:00 ~ 24:00 differ is -------------> " + oneDayDiffer);
        Date sunRiseDate = TimeUtil.stringToDate(startTime, TimeUtil.format2);
        Date sunSetDate = TimeUtil.stringToDate(endTime, TimeUtil.format2);
        long sunDiffer = TimeUtil.getTwoDaysDiffer(sunRiseDate, sunSetDate);
        KLog.i(startTime + " ~ " + endTime + " differ is -------------> " + sunDiffer);
        sweepAngle = Math.round(180 * TimeUtil.div(sunDiffer, oneDayDiffer, 2));
        KLog.i("sweepAngele is -------------> " + sweepAngle);
    }

    /**
     * 开始动画
     * */
    public void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAnimationValue = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
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
        if (getLocalVisibleRect(screenRect)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 添加日出日落时间
     * */
    public void setSunTime(String startTime, String endTime) {
        calculateDateDiffer(startTime, endTime);
        this.startTime = startTime.substring(10);
        this.endTime = endTime.substring(10);
    }

    /**
     * 添加icon
     * */
    public void setIconSun(Drawable iconSun) {
        this.iconSun = iconSun;
    }
}
