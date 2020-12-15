package com.github.yangkangli.ui.text_progress_bar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

public class TextProgressBar extends RelativeLayout {

    private View rootLayout;

    private TextView tvProgressText;

    private View vBackground;

    private View vProgress;

    private int progressTextDrawableLeft;

    private int progressTextDrawableRight;

    private int progressMaxValue;

    private int currentProgress;

    private int wholeWidth;

    private OnProgressChangedListener onProgressChangedListener;

    public TextProgressBar(Context context) {
        this(context, null);
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootLayout = inflater.inflate(R.layout.root_layout, this, true);
        tvProgressText = rootLayout.findViewById(R.id.tv_progress_text);
        vBackground = rootLayout.findViewById(R.id.v_background);
        vProgress = rootLayout.findViewById(R.id.v_progress);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextProgressBar);
        setBackgroundDrawable(a.getResourceId(R.styleable.TextProgressBar_background_drawable, R.drawable.xml_background));
        setProgressDrawable(a.getResourceId(R.styleable.TextProgressBar_progress_drawable, R.drawable.xml_progress));
        setProgressBarHeight(a.getDimension(R.styleable.TextProgressBar_progress_bar_height, Utils.dp2px(context, 5)));


        progressTextDrawableLeft = a.getResourceId(R.styleable.TextProgressBar_progress_text_drawable_left, R.drawable.bg_text_angle_at_left);
        progressTextDrawableRight = a.getResourceId(R.styleable.TextProgressBar_progress_text_drawable_right, R.drawable.bg_text_angle_at_right);


        // 进度最大值
        progressMaxValue = a.getInteger(R.styleable.TextProgressBar_progress_max_value, 100);
        if (progressMaxValue < 0) {
            progressMaxValue = 100;
        }
        // 进度当前值
        currentProgress = a.getInteger(R.styleable.TextProgressBar_progress, 0);
        if (currentProgress < 0) {
            currentProgress = 0;
        } else if (currentProgress > progressMaxValue) {
            currentProgress = progressMaxValue;
        }
        a.recycle();

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                wholeWidth = getWidth();
                // 得到全局宽度后，再更新一次
                updateProgressBar();
                updateProgressText();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setProgress(currentProgress);
    }

    /**
     * 设置进度条的进度背景Drawable
     *
     * @param resId
     */
    public void setBackgroundDrawable(@DrawableRes int resId) {
        if (resId != 0) {
            vBackground.setBackgroundResource(resId);
        } else {
            vBackground.setBackgroundResource(R.drawable.xml_background);
        }
    }

    /**
     * 设置进度条的进度Drawable
     *
     * @param resId
     */
    public void setProgressDrawable(@DrawableRes int resId) {
        if (resId != 0) {
            vProgress.setBackgroundResource(resId);
        } else {
            vProgress.setBackgroundResource(R.drawable.xml_progress);
        }
    }

    /**
     * 设置高度
     *
     * @param height
     */
    public void setProgressBarHeight(float height) {
        // 背景的高度
        LayoutParams layoutParams = (LayoutParams) vBackground.getLayoutParams();
        layoutParams.height = (int) height;
        vBackground.setLayoutParams(layoutParams);

        // 设置进度的高度
        layoutParams = (LayoutParams) vProgress.getLayoutParams();
        layoutParams.height = (int) height;
        vProgress.setLayoutParams(layoutParams);
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        if (progress >= 0 && progress <= progressMaxValue) {
            // 赋值
            int oldProgress = currentProgress;
            currentProgress = progress;
            // 更新控件
            updateProgressBar();
            // 回调
            if (onProgressChangedListener != null) {
                onProgressChangedListener.onProgressChange(oldProgress, progress);
            }
        }

    }

    /**
     * 计算当前进度下，右侧的空间是否可以容纳下进度文本控件
     *
     * @return
     */
    private boolean isSpaceEnough() {
        // 1、得到进度文本控件的宽度
        int textWidth = calculateProgressTextWidth();
        // 2、计算出新的进度下，进度条的宽度
        int newProgressBarWidth = calculateNewProgressBarWidth();
        // 3、判断空间是否足够
        if (wholeWidth - newProgressBarWidth >= textWidth) {
            return true;
        }
        return false;
    }

    /**
     * 计算进度文本框的宽度
     *
     * @return
     */
    private int calculateProgressTextWidth() {
        String text = tvProgressText.getText().toString();
        int textWidth = (int) tvProgressText.getPaint().measureText(text);
        LayoutParams layoutParams = (LayoutParams) tvProgressText.getLayoutParams();
        textWidth += layoutParams.leftMargin + layoutParams.rightMargin
                + tvProgressText.getPaddingStart() + tvProgressText.getPaddingEnd();
        return textWidth;
    }

    /**
     * 计算新进度下进度条的宽度
     *
     * @return
     */
    private int calculateNewProgressBarWidth() {
        // 1、计算百分比
        float percent = (float) currentProgress / progressMaxValue;
        // 2、计算新的进度条的宽度 && 返回
        return (int) (wholeWidth * percent);
    }

    /**
     * 更新进度条
     */
    private void updateProgressBar() {
        ViewGroup.LayoutParams layoutParams = vProgress.getLayoutParams();
        layoutParams.width = calculateNewProgressBarWidth();
        vProgress.setLayoutParams(layoutParams);
    }

    /**
     * 更新进度文本
     */
    private void updateProgressText() {
        if (isSpaceEnough()) {
            LayoutParams params = (LayoutParams) tvProgressText.getLayoutParams();
            params.addRule(RelativeLayout.END_OF, vProgress.getId());
            params.removeRule(RelativeLayout.ALIGN_END);
            tvProgressText.setLayoutParams(params);
            tvProgressText.setBackgroundResource(progressTextDrawableLeft);
        } else {
            LayoutParams params = (LayoutParams) tvProgressText.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_END, vProgress.getId());
            params.removeRule(RelativeLayout.END_OF);
            tvProgressText.setLayoutParams(params);
            tvProgressText.setBackgroundResource(progressTextDrawableRight);
        }
    }

    /**
     * 获得当前进度
     *
     * @return
     */
    public int getCurrentProgress() {
        return currentProgress;
    }

    /**
     * 设置进度文本
     *
     * @param text
     */
    public void setProgressText(String text) {
        tvProgressText.setText(text);
        updateProgressText();
    }

    /**
     * 设置进度变化监听器
     *
     * @param onProgressChangedListener
     */
    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        this.onProgressChangedListener = onProgressChangedListener;
    }

    /**
     * 进度变化监听器
     */
    public interface OnProgressChangedListener {
        /**
         * 进度变化回调方法
         *
         * @param oldProgress
         * @param newProgress
         */
        void onProgressChange(int oldProgress, int newProgress);
    }
}
