package com.zero.aiweather.widget.circleProgress;

import android.animation.TypeEvaluator;

public class CircleProgressEvaluator implements TypeEvaluator {
    @Override
    public Object evaluate(float v, Object o, Object t1) {
        float startAngle = (Float) o;
        float endAngle = (Float) t1;
        return (startAngle + v * endAngle);
    }
}
