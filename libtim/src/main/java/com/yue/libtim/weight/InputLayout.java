package com.yue.libtim.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yue.libtim.R;

/**
 * @author shimy
 * @create 2020/6/2 14:47
 * @desc 输入底部布局
 */
public class InputLayout extends FrameLayout {

    public InputLayout(@NonNull Context context) {
        this(context, null);
    }

    public InputLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.layout_input, this);
    }
}