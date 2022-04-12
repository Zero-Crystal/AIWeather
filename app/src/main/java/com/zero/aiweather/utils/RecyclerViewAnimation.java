package com.zero.aiweather.utils;

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.recyclerview.widget.RecyclerView;

import com.zero.aiweather.R;

/**
 * 动画RecycleView
 */
public class RecyclerViewAnimation {

    //数据变化时显示动画  底部动画
    public static void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                new LayoutAnimationController(AnimationUtils.loadAnimation(context, R.anim.layout_animation_from_bottom));
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    //数据变化时显示动画  右侧动画
    public static void runLayoutAnimationRight(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                new LayoutAnimationController(AnimationUtils.loadAnimation(context, R.anim.layout_animation_slide_right));
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}
