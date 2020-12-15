package com.github.yangkangli.ui.text_progress_bar;

import android.content.Context;

public class Utils {
    /**
     * dp转px
     *
     * @param dp
     * @return
     */
    public static int dp2px(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp);
    }
}
