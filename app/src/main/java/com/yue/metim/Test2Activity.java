package com.yue.metim;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.yue.metim.constants.Constants;
import com.yue.metim.databinding.ActivityTest2Binding;
import com.yue.metim.utils.AndroidQTransformUtils;
import com.yue.metim.utils.FileUtils;
import com.yue.metim.utils.GlideEngine;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class Test2Activity extends AppCompatActivity {

    private ActivityTest2Binding mBinding;
    private final static int CHOOSE_REQUEST2 = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test2);

        mBinding.btnTest01.setOnClickListener(v -> {
            PictureFileUtils.deleteCacheDirFile(this, PictureMimeType.ofImage());
            FileUtils.deleteDirFiles(new File(Constants.COMPRESS_IMAGE_CACHE));
        });
        mBinding.btnTest02.setOnClickListener(v -> {
            AndroidQTransformUtils.copyPathToAndroidQ3(this, mSourcePath,"");

        });
        mBinding.btnTest.setOnClickListener(v -> {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .maxSelectNum(1)// 最大图片选择数量 int
                    .isCompress(true)// 是否压缩 true or false
                    .isCamera(true)// 是否显示拍照按钮 true or false
//                    .cutOutQuality(90)
                    .synOrAsy(true)
                    .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        });

        mBinding.btnSelect2.setOnClickListener(v -> {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .maxSelectNum(1)// 最大图片选择数量 int
//                    .isCompress(true)// 是否压缩 true or false
                    .isCamera(true)// 是否显示拍照按钮 true or false
//                    .cutOutQuality(90)
//                    .synOrAsy(true)
                    .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                    .forResult(CHOOSE_REQUEST2);
        });
    }


    private String mSourcePath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    /*图片返回列表*/
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if (selectList.size() == 0 || selectList.get(0) == null) {
                        Toast.makeText(this, "未找到图片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mSourcePath = selectList.get(0).getPath();
                    File fileCompress = new File(selectList.get(0).getCompressPath());
                    File fileSource = new File(selectList.get(0).getPath());
                    if (!fileSource.exists()) {
                        Log.i("shimy", "原图访问失败:" + fileSource.getAbsolutePath());
                    } else {
                        Log.i("shimy", "原图路径：" + fileSource.getAbsolutePath());
                    }

                    if (!fileCompress.exists()) {
                        Log.i("shimy", "压缩图访问失败：" + fileCompress.getAbsolutePath());
                    } else {
                        Log.i("shimy", "压缩图路径：" + fileCompress.getAbsolutePath());
                    }
                    luban(fileSource);
                    luban(fileCompress);

                    if (selectList != null && selectList.size() > 0) {
                        Glide.with(this)
                                .load(selectList.get(0).getPath())
                                .into(mBinding.ivShow);
                    }
                    break;
                case CHOOSE_REQUEST2: {
                    List<LocalMedia> selectList2 = PictureSelector.obtainMultipleResult(data);
                    if (selectList2.size() == 0 || selectList2.get(0) == null) {
                        Toast.makeText(this, "未找到图片", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mSourcePath = selectList2.get(0).getPath();
                    Glide.with(Test2Activity.this)
                            .load(mSourcePath)
                            .into(mBinding.ivShow);
                    new Thread(() -> {

                        String newPath = AndroidQTransformUtils.copyPathToAndroidQ3(Test2Activity.this, mSourcePath,"");
                        if (TextUtils.isEmpty(newPath)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Test2Activity.this, "沙箱转移失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        luban(new File(newPath));
                    }).start();

                }
                break;
            }
        }
    }


    private void luban(File file) {
        Luban.with(this)
                .load(file)
                .ignoreBy(10)
                .setTargetDir(Constants.COMPRESS_IMAGE_CACHE)
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        if (!file.exists()) {
                            Log.i("shimy", "鲁班图访问失败：" + file.getAbsolutePath());
                        } else {
                            Log.i("shimy", "鲁班图路径：" + file.getAbsolutePath());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Glide.with(Test2Activity.this)
//                                            .load(file)
//                                            .into(mBinding.ivShow);
                                }
                            });

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用

                    }
                }).launch();
    }
}
