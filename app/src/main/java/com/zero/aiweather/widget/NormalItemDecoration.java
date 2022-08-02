package com.zero.aiweather.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.DimenRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class NormalItemDecoration extends RecyclerView.ItemDecoration {
    private int dividerHeight = 1;//分割线高度
    private Paint paint;
    private RecyclerView.LayoutManager layoutManager;
    /* 是否显示最后位置的下划线 */
    private boolean mShowLastDivider = true;
    /* 距离左右的距离 */
    private int leftMargin = 0;
    private int rightMargin = 0;
    int rowCounts = 0;
    int spanCount = 0;

    /*默认初始化*/
    public NormalItemDecoration() {
        initPaint();
        paint.setColor(Color.GRAY);
    }

    private void initPaint() {
        if (paint == null) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    public NormalItemDecoration setDividerHeight(int dividerHeight) {
        this.dividerHeight = dividerHeight;
        return this;

    }

    public NormalItemDecoration setDividerColor(int color) {
        initPaint();
        paint.setColor(color);
        return this;
    }

    public void setShowLastDivider(boolean mShowLastDivider) {
        this.mShowLastDivider = mShowLastDivider;
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    public void setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
    }

    /**
     * 设置分割线线的宽度和高度
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (layoutManager == null) {
            layoutManager = parent.getLayoutManager();
        }
        // 适用 LinearLayoutManager 和 GridLayoutManager
        if (layoutManager instanceof LinearLayoutManager) {
            int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                // 水平分割线将绘制在item底部
                outRect.bottom = dividerHeight;
            } else if (orientation == LinearLayoutManager.HORIZONTAL) {
                // 垂直分割线将绘制在item右侧
                outRect.right = dividerHeight;
            }
            if (layoutManager instanceof GridLayoutManager) {
                getRowCount(parent);
                GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                // 如果是 GridLayoutManager 则需要绘制另一个方向上的分割线
                if (orientation == LinearLayoutManager.VERTICAL && lp != null && lp.getSpanIndex() > 0) {
                    // 如果列表是垂直方向,则最左边的一列略过
                    outRect.left = dividerHeight;
                } else if (orientation == LinearLayoutManager.HORIZONTAL && lp != null && lp.getSpanIndex() > 0) {
                    // 如果列表是水平方向,则最上边的一列略过
                    outRect.top = dividerHeight;
                }
            }
        }
    }

    private void getRowCount(RecyclerView parent) {
        spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        rowCounts = parent.getChildCount() / spanCount;
        if (parent.getChildCount() % spanCount > 0) {
            rowCounts++;
        }
    }

    /**
     * itemView 绘制之前调用
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        // 这个值是为了补偿横竖方向上分割线交叉处间隙
        int offSet = (int) Math.ceil(dividerHeight * 1f / 2);
        int childCount = parent.getChildCount();
        if(!mShowLastDivider){
            childCount--;
        }
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left1 = child.getRight() + params.rightMargin;
            int right1 = left1 + dividerHeight;
            int top1 = child.getTop() - offSet - params.topMargin;
            int bottom1 = child.getBottom() + offSet + params.bottomMargin;
            //绘制分割线(矩形)-right
            if (checkRightBound(i)) {
                c.drawRect(left1, top1, right1, bottom1, paint);
            }
            int left2 = child.getLeft() - offSet - params.leftMargin+leftMargin;
            int right2 = child.getRight() + offSet + params.rightMargin-rightMargin;
            int top2 = child.getBottom() + params.bottomMargin;
            int bottom2 = top2 + dividerHeight;
            //绘制分割线(矩形)-bottom
            if (checkBottomBound(i)) {
                c.drawRect(left2, top2, right2, bottom2, paint);
            }
        }
    }

    private boolean checkRightBound(int position) {
        if (spanCount == 0) return true;
        if (position % spanCount == spanCount - 1) return false;
        return true;
    }

    private boolean checkBottomBound(int position) {
        if (spanCount == 0) return true;
        if (position / spanCount == rowCounts - 1) return false;
        return true;
    }

    /**
     * itemView 绘制之后调用
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    public static class Builder {
        private Context mContext;
        protected Resources mResources;
        private NormalItemDecoration itemDecoration;

        public Builder(Context context) {
            mContext = context;
            mResources = context.getResources();
            itemDecoration = new NormalItemDecoration();
        }

        public Builder color(int color){
            itemDecoration.setDividerColor(color);
            return this;
        }

        public Builder colorResId(int color){
            itemDecoration.setDividerColor(ContextCompat.getColor(mContext,color));
            return this;
        }

        public Builder height(final int height) {
            itemDecoration.setDividerHeight(height);
            return this;
        }

        public Builder heightResId(@DimenRes int id) {
            itemDecoration.setDividerHeight(mResources.getDimensionPixelSize(id));
            return this;
        }

        public NormalItemDecoration create(){
            return itemDecoration;
        }

        public Builder showLastDivider(boolean isShowLastDivider) {
            itemDecoration.setShowLastDivider(isShowLastDivider);
            return this;
        }

        public Builder leftMargin(int leftMargin){

            itemDecoration.setLeftMargin(leftMargin);
            return this;
        }

        public Builder rightMargin(int rightMargin){
            itemDecoration.setRightMargin(rightMargin);
            return this;

        }

        public Builder leftMarginRes(int id){
            itemDecoration.setRightMargin(mResources.getDimensionPixelSize(id));
            return this;
        }

        public Builder rightMarginRes(int id){
            itemDecoration.setRightMargin(mResources.getDimensionPixelSize(id));
            return this;
        }
    }
}
