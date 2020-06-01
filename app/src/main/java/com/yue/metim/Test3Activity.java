package com.yue.metim;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.yue.metim.databinding.ActivityTest3Binding;
import com.yue.metim.utils.GlideEngine;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Test3Activity extends AppCompatActivity {


    private ActivityTest3Binding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test3);
        mBinding.btnSnapshot.setOnClickListener(v -> {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofVideo())
                    .maxSelectNum(1)// 最大图片选择数量 int
                    .isCamera(true)// 是否显示拍照按钮 true or false
//                    .cutOutQuality(90)
                    .synOrAsy(true)
                    .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        });
    }


    private void showImage(LocalMedia localMedia) {
        String miniType = localMedia.getMimeType();
        if (miniType.contains("/") && miniType.split("/").length >= 2) {
            miniType = miniType.split("/")[1];
        } else {

        }
        Log.i("shimy", miniType);
        File file = new File(localMedia.getPath());
        if (!file.exists() ) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
        } else if (file.length() <= 0){
            Toast.makeText(this, "文件大小为0", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "文件存在", Toast.LENGTH_SHORT).show();
        }
        Glide.with(this)
                .load(localMedia.getPath())
                .into(mBinding.ivShow);
        Glide.with(this)
                .asBitmap()
                .load(localMedia.getPath())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mBinding.ivShow2.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST: {
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    showImage(selectList.get(0));
                }
                break;
            }
        }
    }
}
