package com.zero.aiweather.ui.view;

import android.animation.TypeEvaluator;

import java.io.File;

public class CircleProgressEvaluator implements TypeEvaluator {
    @Override
    public Object evaluate(float v, Object o, Object t1) {
        float startAngle = (Float) o;
        float endAngle = (Float) t1;
        return (startAngle + v * endAngle);
    }
}
