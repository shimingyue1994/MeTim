package com.yue.metim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.yue.metim.databinding.ActivityTest2Binding;

public class Test2Activity extends AppCompatActivity {

    private ActivityTest2Binding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       mBinding  = DataBindingUtil. setContentView(this,R.layout.activity_test2);
       mBinding.btnTest.setOnClickListener(v -> {
           PictureSelector.create(this)
                   .openGallery(PictureMimeType.ofImage())
                   .maxSelectNum(1)// 最大图片选择数量 int
                   .isCamera(true)// 是否显示拍照按钮 true or false
                   .compress(true)// 是否压缩 true or false
                   .cropCompressQuality(90)
//                    .cutOutQuality(90)
                   .synOrAsy(true)
                   .forResult(PictureConfig.CHOOSE_REQUEST);
       });
    }
}
