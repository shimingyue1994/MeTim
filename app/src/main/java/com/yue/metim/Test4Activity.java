package com.yue.metim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.Toast;


import com.yue.libtim.utils.soft.SoftKeyBoardListener;
import com.yue.metim.databinding.ActivityTest4Binding;

public class Test4Activity extends AppCompatActivity {

    private ActivityTest4Binding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test4);

        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                Toast.makeText(Test4Activity.this, "键盘显示 高度" + height, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void keyBoardHide(int height) {
                Toast.makeText(Test4Activity.this, "键盘隐藏 高度" + height, Toast.LENGTH_SHORT).show();
            }
        });
        mBinding.inputlayout.init(this);

    }
}
