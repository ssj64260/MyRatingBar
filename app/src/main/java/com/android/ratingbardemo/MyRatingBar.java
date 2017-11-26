package com.android.ratingbardemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义打分控件
 */

public class MyRatingBar extends ViewGroup {

    private static final int ITEM_PADDING_DP = 20;
    private static final int ITEM_WIDTH_DP = 30;
    private static final int ITEM_HEIGHT_DP = 30;
    private static final int LINE_WIDTH_DP = 2;

    private OnPointChangeListener mPointChangeListener;

    private int mItemPaddingPX;
    private int mItemWidthPX;
    private int mItemHeightPX;
    private int mLineWidthPX;
    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;
    private int mLastItemLeft = 0;
    private int mItemBackgroundSelector;
    private int mLineColorNormal;
    private int mLineColorSelected;

    private boolean mOnlyShow = false;
    private boolean mCanSetZeroPoint = true;//是否能给零分

    private Paint mPaint;//连线样式
    private Path mPath;//路径

    private List<View> mViewList;

    public MyRatingBar(Context context) {
        this(context, null, 0);
    }

    public MyRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyRatingBar, defStyleAttr, 0);
        mOnlyShow = ta.getBoolean(R.styleable.MyRatingBar_only_show, false);
        mCanSetZeroPoint = ta.getBoolean(R.styleable.MyRatingBar_can_set_zero_point, true);
        mItemWidthPX = ta.getDimensionPixelSize(R.styleable.MyRatingBar_item_width, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ITEM_WIDTH_DP, DisplayUtil.getMetrics()));
        mItemHeightPX = ta.getDimensionPixelSize(R.styleable.MyRatingBar_item_height, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ITEM_HEIGHT_DP, DisplayUtil.getMetrics()));
        mItemPaddingPX = ta.getDimensionPixelSize(R.styleable.MyRatingBar_item_padding, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ITEM_PADDING_DP, DisplayUtil.getMetrics()));
        mLineWidthPX = ta.getDimensionPixelSize(R.styleable.MyRatingBar_line_width, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, LINE_WIDTH_DP, DisplayUtil.getMetrics()));
        mItemBackgroundSelector = ta.getResourceId(R.styleable.MyRatingBar_item_background_selector, R.drawable.selector_ratingbar);
        mLineColorNormal = ta.getColor(R.styleable.MyRatingBar_line_color_normal, Color.GRAY);
        mLineColorSelected = ta.getColor(R.styleable.MyRatingBar_line_color_selected, Color.YELLOW);
        ta.recycle();

        mViewList = new ArrayList<>();
        mWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mItemWidthPX, MeasureSpec.EXACTLY);
        mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mItemHeightPX, MeasureSpec.EXACTLY);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.reset();
        mPaint.setStrokeWidth(mLineWidthPX);
        mPaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();
        mPath.reset();
    }

    private void initView(int maxPoint, int defaultPoint) {
        mViewList.clear();
        removeAllViews();
        final int itemTop = 0;
        mLastItemLeft = 0;
        for (int i = 0; i < maxPoint; i++) {
            final View view = new View(getContext());
            view.setTop(itemTop);
            view.setLeft(mLastItemLeft);
            view.setBackgroundResource(mItemBackgroundSelector);
            view.setSelected(i < defaultPoint);
            if (i < maxPoint - 1) {
                mLastItemLeft += mItemWidthPX + mItemPaddingPX;
            }
            this.addView(view);
            mViewList.add(view);
        }
    }

    public void setItem(int maxPoint, int defaultPoint) {
        if (maxPoint >= 1 && maxPoint <= 5) {
            initView(maxPoint, defaultPoint);
            invalidate();
        } else {
            throw new IllegalAccessError("选项最少要1个，最多只能5个");
        }
    }

    public int getPointNumber() {
        int point = 0;
        for (View view : mViewList) {
            if (view.isSelected()) {
                point++;
            }
        }
        return point;
    }

    public void setOnlyShow(boolean onlyShow) {
        mOnlyShow = onlyShow;
    }

    public void setPointChangeListener(OnPointChangeListener pointChangeListener) {
        this.mPointChangeListener = pointChangeListener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            final int viewLeft = childView.getLeft();
            final int viewTop = childView.getTop();
            childView.layout(viewLeft, viewTop, viewLeft + mItemWidthPX, viewTop + mItemHeightPX);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            childView.measure(mWidthMeasureSpec, mHeightMeasureSpec);
        }

        setMeasuredDimension(mLastItemLeft + mItemWidthPX, mItemHeightPX);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        if (mItemPaddingPX > 0) {
            for (int i = 1; i < mViewList.size(); i++) {
                final View leftView = mViewList.get(i - 1);
                final View view = mViewList.get(i);
                final int lineColor = view.isSelected() ? mLineColorSelected : mLineColorNormal;

                final float lineY = view.getTop() + mItemHeightPX / 2f;
                final float lineStartX = view.getLeft();
                final float lineEndX = leftView.getLeft() + mItemWidthPX;

                mPaint.setColor(lineColor);
                mPath.reset();
                mPath.moveTo(lineStartX, lineY);
                mPath.lineTo(lineEndX, lineY);
                canvas.drawPath(mPath, mPaint);
            }
        }
    }

    private void setStatus(int touchX) {
        int i = mCanSetZeroPoint ? 0 : 1;
        int point = i;
        for (; i < mViewList.size(); i++) {
            final View view = mViewList.get(i);
            final int viewLeft = view.getLeft();
            view.setSelected(touchX > viewLeft);
            if (view.isSelected()) {
                point++;
            }
        }
        if (mPointChangeListener != null) {
            mPointChangeListener.onPointChangeListener(point);
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mOnlyShow) {
            final int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    final int currentTouchX = (int) event.getX();
                    setStatus(currentTouchX);
                    break;
                case MotionEvent.ACTION_UP:

                    break;
            }
        }
        return true;
    }

    public interface OnPointChangeListener {
        void onPointChangeListener(int point);
    }
}
