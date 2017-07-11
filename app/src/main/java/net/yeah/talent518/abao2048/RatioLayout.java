package net.yeah.talent518.abao2048;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class RatioLayout extends LinearLayout {

    private float mPicRatio = 2.4f; //一个固定的宽高比，后面创建属性自定义来设置宽高比

    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
        mPicRatio = typedArray.getFloat(R.styleable.RatioLayout_picRatio, 0);

        typedArray.recycle();

    }

    public RatioLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //父控件是否是固定值或者是match_parent
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            //得到父容器的宽度
            int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
            //得到子控件的宽度
            int childWidth = parentWidth - getPaddingLeft() - getPaddingRight();
            //计算子控件的高度
            int childHeight = (int) (childWidth / mPicRatio + 0.5f);
            //计算父控件的高度
            int parentHeight = childHeight + getPaddingBottom() + getPaddingTop();

            //测量子控件,固定孩子的大小
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);
            //测量
            setMeasuredDimension(parentWidth, parentHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }
}
