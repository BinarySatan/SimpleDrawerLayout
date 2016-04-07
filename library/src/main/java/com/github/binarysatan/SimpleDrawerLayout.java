package com.github.binarysatan;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


/**
 * @author BinarySatan
 * @since 2016/4/5
 * blog http://blog.csdn.net/xuezhe__
 */
public class SimpleDrawerLayout extends ViewGroup {
    private ViewDragHelper mVDH;

    private View mDrawerView;

    private boolean mIsRightToLeft  =true;
    private boolean mIsOpen = true;

    private final int mDefaultInterspace = 100; //dp

    private int mInterspace = mDefaultInterspace;

    public SimpleDrawerLayout(Context context) {
        this(context, null);
    }

    public SimpleDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SimpleDrawerStyle);

        mInterspace = (int) ta.getDimension(R.styleable.SimpleDrawerStyle_interspace, mDefaultInterspace);

        mVDH = ViewDragHelper.create(this, 1.0f, new VDHCallback());
        mVDH.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }


    class VDHCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mDrawerView;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return child.getMeasuredWidth();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            left = left > 0 ? 0 : left;
            return left;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            float proportion = Math.abs(releasedChild.getLeft() / (float) releasedChild.getWidth());
            if (mIsRightToLeft) {
                mIsOpen = proportion <= 0.5;
                mVDH.settleCapturedViewAt(proportion > 0.5 ? -releasedChild.getWidth() : 0, releasedChild.getTop());
            } else {
                mIsOpen = proportion > 0.5;
                mVDH.settleCapturedViewAt(proportion < 0.5 ? 0 : -releasedChild.getWidth(), releasedChild.getTop());
            }
            postInvalidate();
            mIsRightToLeft = false;
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            mVDH.captureChildView(mDrawerView, pointerId);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

            mIsRightToLeft = dx < 0;

            if (!mIsRightToLeft) {
                requestLayout();
                return;
            }
        }
    }


    public void openDrawer() {
        if (!mIsOpen) {
            mVDH.smoothSlideViewTo(mDrawerView, 0, mDrawerView.getTop());
            postInvalidate();
            mIsOpen = true;
        }
    }

    public void closeDrawer() {
        if (mIsOpen) {
            mVDH.smoothSlideViewTo(mDrawerView, -mDrawerView.getMeasuredWidth(), mDrawerView.getTop());
            postInvalidate();
            mIsOpen = false;
        }
    }


    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @Override
    public void computeScroll() {
        if (mVDH.continueSettling(true)) {
            postInvalidate();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() != 2)
            throw new IllegalArgumentException("child count must be 2");

        mDrawerView = getChildAt(1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mVDH.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVDH.processTouchEvent(event);
        return true;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new ViewGroup.MarginLayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }


    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.MarginLayoutParams(getContext(), attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();

            final int contenWidthtSpec;
            final int contentHeightSpec;

            if (i == 1) {
                contenWidthtSpec = View.MeasureSpec.makeMeasureSpec(widthSize - mlp.leftMargin - mlp.rightMargin - dpToPx(getContext(), mInterspace), widthMode);
                contentHeightSpec = View.MeasureSpec.makeMeasureSpec(heightSize - mlp.topMargin - mlp.bottomMargin, heightMode);
            } else {
                contenWidthtSpec = View.MeasureSpec.makeMeasureSpec(widthSize - mlp.leftMargin - mlp.rightMargin, widthMode);
                contentHeightSpec = View.MeasureSpec.makeMeasureSpec(heightSize - mlp.topMargin - mlp.bottomMargin, heightMode);
            }

            child.measure(contenWidthtSpec, contentHeightSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            final int childCount = getChildCount();

            for (int i = 0; i < childCount; i++) {
                ViewGroup.MarginLayoutParams mlp;
                final View child = getChildAt(i);

                if (child.getVisibility() == GONE) {
                    continue;
                }

                if (i == 1) {
                    mlp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                    child.layout(mlp.leftMargin, mlp.topMargin,
                            mlp.leftMargin + child.getMeasuredWidth(),
                            mlp.topMargin + child.getMeasuredHeight());
                    continue;
                }

                mlp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                child.layout(mlp.leftMargin, mlp.topMargin,
                        mlp.leftMargin + child.getMeasuredWidth(),
                        mlp.topMargin + child.getMeasuredHeight());
            }
        }
    }

}
