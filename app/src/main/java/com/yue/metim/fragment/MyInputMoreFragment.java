package com.yue.metim.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.yue.libtim.utils.FileUtil;
import com.yue.libtim.utils.soft.SoftKeyBoardUtil;
import com.yue.metim.R;
import com.yue.metim.databinding.FragmentMyInputMoreBinding;
import com.yue.metim.utils.GlideEngine;

import java.util.List;

/**
 * @author shimy
 * @create 2020/6/5 14:00
 * @desc 输入更多
 */
public class MyInputMoreFragment extends Fragment {

    private FragmentMyInputMoreBinding mBinding;

    public MyInputMoreFragment() {
        // Required empty public constructor
    }


    public static MyInputMoreFragment newInstance() {
        MyInputMoreFragment fragment = new MyInputMoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_input_more, container, false);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mBinding.llContent.getLayoutParams();
        params.height = SoftKeyBoardUtil.getSoftKeyBoardHeight();
        mBinding.llContent.setLayoutParams(params);
        return mBinding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBinding.btnImage.setOnClickListener(v -> {
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

        mBinding.btnVideo.setOnClickListener(v -> {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofVideo())
                    .maxSelectNum(1)// 最大图片选择数量 int
                    .isCamera(true)// 是否显示拍照按钮 true or false
//                    .cutOutQuality(90)
                    .synOrAsy(true)
                    .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                    .forResult(PictureConfig.CHOOSE_REQUEST + 1);
        });
        mBinding.btnFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, PictureConfig.CHOOSE_REQUEST + 2);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST: {
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if (onFragmentListener != null)
                        onFragmentListener.sendImage(selectList.get(0).getCompressPath());
                }
                break;
                case PictureConfig.CHOOSE_REQUEST + 1: {//视频
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    LocalMedia localMedia = selectList.get(0);
                    if (onFragmentListener != null)
                        onFragmentListener.sendVideo(localMedia.getPath(), localMedia.getMimeType(), (int) localMedia.getDuration());
//                    sendVideo(selectList.get(0));
                }
                break;
                case PictureConfig.CHOOSE_REQUEST + 2: {
                    if (data == null) {
                        // 用户未选择任何文件，直接返回
                        return;
                    }
                    Uri uri = data.getData(); // 获取用户选择文件的URI
                    String path = FileUtil.getPathFromUri(getActivity(), uri);
                    Log.i("shimyFile", path);
                    if (onFragmentListener != null)
                        onFragmentListener.sendFile(path);
                }
                break;
            }
        }
    }

    private OnFragmentListener onFragmentListener;

    public void setOnFragmentListener(OnFragmentListener onFragmentListener) {
        this.onFragmentListener = onFragmentListener;
    }

    public interface OnFragmentListener {
        void sendImage(String path);

        void sendVideo(String path, String miniType, int duration);

        void sendFile(String path);

    }
}
