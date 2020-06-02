package com.yue.libtim.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author shimy
 * @create 2020/6/2 14:47
 * @desc 输入底部布局
 */
public class InputLayout extends FrameLayout {

    public InputLayout(@NonNull Context context) {
        super(context);
    }

    public InputLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InputLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
