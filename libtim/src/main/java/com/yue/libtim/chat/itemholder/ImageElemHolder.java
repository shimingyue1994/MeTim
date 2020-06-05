package com.yue.libtim.chat.itemholder;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tencent.imsdk.TIMImageType;
import com.tencent.imsdk.v2.V2TIMDownloadCallback;
import com.tencent.imsdk.v2.V2TIMElem;
import com.tencent.imsdk.v2.V2TIMImageElem;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.yue.libtim.R;
import com.yue.libtim.TUIKitConstants;
import com.yue.libtim.chat.messagevo.ImageElemVO;
import com.yue.libtim.utils.DensityUtils;
import com.yue.libtim.utils.FileDownloadUtils;

import java.io.File;
import java.util.List;

/**
 * @author shimy
 * @create 2020/5/28 14:08
 * @desc 图片消息
 */
public class ImageElemHolder extends MessageContentHolder {

    public ImageView ivImage;
    public ProgressBar progressBar;
    public TextView tvProgress;
    public LinearLayout llMask;
    public Button btnReDown;

    public ImageElemHolder(@NonNull View itemView, RecyclerView.Adapter adapter) {
        super(itemView, adapter);
        ivImage = itemView.findViewById(R.id.iv_image);

        progressBar = itemView.findViewById(R.id.progress);
        tvProgress = itemView.findViewById(R.id.tv_progress);
        llMask = itemView.findViewById(R.id.ll_mask);
        btnReDown = itemView.findViewById(R.id.btn_redown);
    }


    public void showMessage(final ImageElemVO elemVO, final RecyclerView recyclerView) {
        mFlMsgContent.setBackgroundColor(ivImage.getResources().getColor(android.R.color.transparent));
        /*重置view*/
//        ivImage.setImageResource(R.drawable.ic_default_image);
        llMask.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvProgress.setVisibility(View.GONE);
        btnReDown.setVisibility(View.GONE);


        // 一个图片消息会包含三种格式大小的图片，分别为原图、大图、微缩图(SDK内部自动生成大图和微缩图)， 这三种图片通过getImageList获取
        // 大图：是将原图等比压缩，压缩后宽、高中较小的一个等于720像素。
        // 缩略图：是将原图等比压缩，压缩后宽、高中较小的一个等于198像素。
        V2TIMImageElem v2TIMImageElem = elemVO.getTimElem();
        final List<V2TIMImageElem.V2TIMImage> imageList = v2TIMImageElem.getImageList();

        /*这儿还得处理一下 先这样*/
        if (elemVO.getTimMessage().isSelf()) {
            if (!TextUtils.isEmpty(elemVO.getTimElem().getPath()) && new File(elemVO.getTimElem().getPath()).exists()) {
                if (elemVO.getTimMessage().getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_SENDING) {
                    llMask.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    tvProgress.setVisibility(View.VISIBLE);
                    btnReDown.setVisibility(View.GONE);

                    progressBar.setProgress(elemVO.getSendProgress());
                    tvProgress.setText(elemVO.getSendProgress() + "%");
                } else if (elemVO.getTimMessage().getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_SEND_FAIL) {
                    llMask.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    tvProgress.setVisibility(View.VISIBLE);
                    btnReDown.setVisibility(View.GONE);

                    tvProgress.setText("发送失败");
                } else if (elemVO.getTimMessage().getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_SEND_SUCC) {
                    llMask.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    tvProgress.setVisibility(View.GONE);
                    btnReDown.setVisibility(View.GONE);
                }
                showImage(elemVO.getTimElem().getPath(), recyclerView);
            } else {
                downImage(imageList, elemVO.getTimMessage().getSender(), recyclerView);
            }
        } else {
            downImage(imageList, elemVO.getTimMessage().getSender(), recyclerView);
        }

        /*重新下载*/
        btnReDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!elemVO.getTimMessage().isSelf()) {
                    downImage(imageList, elemVO.getTimMessage().getSender(), recyclerView);
                } else {

                }
            }
        });
    }

    /**
     * 图片下载
     *
     * @param imageList
     * @param userid    沙雕腾讯 不把url开放出来了
     */
    private void downImage(List<V2TIMImageElem.V2TIMImage> imageList, String userid, final RecyclerView recyclerView) {
        for (V2TIMImageElem.V2TIMImage v2TIMImage : imageList) {
            String uuid = v2TIMImage.getUUID(); // 图片 ID
            Log.i("shimyFileUUID", uuid);
            int imageType = v2TIMImage.getType(); // 图片类型
            if (imageType != TIMImageType.Thumb.value()) {
                /*不是缩略图不下载*/
                continue;
            }
            int size = v2TIMImage.getSize(); // 图片大小（字节）
            /*根据返回的宽度改变视图，*/
            int width = v2TIMImage.getWidth(); // 图片宽度
            int height = v2TIMImage.getHeight(); // 图片高度
            int maxWidth = (int) DensityUtils.dp(llMask.getContext(), 100);
            int maxHeight = (int) DensityUtils.dp(llMask.getContext(), 260);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ivImage.getLayoutParams();
            FrameLayout.LayoutParams paramsMask = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


            //计算缩放比例
            int scaleX = width / maxWidth;
            int scaleY = height / maxHeight;
            int scale = 1;
            if (scaleX >= scaleY && scaleX > 1) {
                scale = scaleX;
            }
            if (scaleY >= scaleX && scaleY > 1) {
                scale = scaleY;
            }

            if (width > maxWidth || height > maxHeight) {
                paramsMask.width = width / scale;
                params.width = width / scale;
                paramsMask.height = height / scale;
                params.height = height / scale;
            } else {
                paramsMask.width = width;
                params.width = width;
                paramsMask.height = height;
                params.height = height;
            }
//                        if (width > maxWidth) {
//                            paramsMask.width = maxWidth;
//                            params.width = maxWidth;
//                        } else {
//                            paramsMask.width = width;
//                            params.width = width;
//                        }
//                        if (height > maxHeight) {
//                            paramsMask.height = maxHeight;
//                            params.height = maxHeight;
//                        } else {
//                            paramsMask.height = height;
//                            params.height = height;
//                        }
            ivImage.setLayoutParams(params);
            llMask.setLayoutParams(paramsMask);

            File dir = new File(TUIKitConstants.MESSAGE_IMAGE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 设置图片下载路径 imagePath，这里可以用 uuid 作为标识，避免重复下载
            final String imagePath = TUIKitConstants.MESSAGE_IMAGE_DIR + "thumb_" + uuid;
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                llMask.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                tvProgress.setVisibility(View.VISIBLE);
                btnReDown.setVisibility(View.GONE);
                v2TIMImage.downloadImage(imagePath, new V2TIMDownloadCallback() {
                    @Override
                    public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                        // 图片下载进度：已下载大小 v2ProgressInfo.getCurrentSize()；总文件大小 v2ProgressInfo.getTotalSize()
                        int progress = (int) ((progressInfo.getTotalSize() * 100) / progressInfo.getCurrentSize());
                        tvProgress.setText(FileDownloadUtils.byteHandle(progressInfo.getCurrentSize()) + "/" + FileDownloadUtils.byteHandle(progressInfo.getTotalSize()));
                        progressBar.setProgress(progress);

                    }

                    @Override
                    public void onError(int code, String desc) {
                        // 图片下载失败
                        llMask.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        tvProgress.setVisibility(View.VISIBLE);
                        btnReDown.setVisibility(View.GONE);

                        tvProgress.setText("下载失败");
                    }

                    @Override
                    public void onSuccess() {
                        // 图片下载完成
                        llMask.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        tvProgress.setVisibility(View.GONE);
                        btnReDown.setVisibility(View.GONE);
                        showImage(imagePath, recyclerView);

                    }
                });
            } else {
                llMask.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                tvProgress.setVisibility(View.GONE);
                btnReDown.setVisibility(View.GONE);
                // 图片已存在
                showImage(imagePath, recyclerView);
            }
        }
    }


    private void showImage(String path, final RecyclerView recyclerView) {
//        int maxWidth = (int) DensityUtils.dp(llMask.getContext(), 100);
//        int maxHeight = (int) DensityUtils.dp(llMask.getContext(), 260);
//        Glide.with(ivImage.getContext())
//                .asBitmap()
//                .load(path)
//                .override(maxWidth, maxHeight)
//                .fitCenter()
//                .into(ivImage);
        Glide.with(ivImage.getContext())
                .asBitmap()
                .load(path)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        int width = resource.getWidth();
                        int height = resource.getHeight();
                        int maxWidth = (int) DensityUtils.dp(llMask.getContext(), 100);
                        int maxHeight = (int) DensityUtils.dp(llMask.getContext(), 260);

                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ivImage.getLayoutParams();
                        FrameLayout.LayoutParams paramsMask = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        //计算缩放比例
                        int scaleX = width / maxWidth;
                        int scaleY = height / maxHeight;
                        int scale = 1;
                        if (scaleX >= scaleY && scaleX > 1) {
                            scale = scaleX;
                        }
                        if (scaleY >= scaleX && scaleY > 1) {
                            scale = scaleY;
                        }

                        if (width > maxWidth || height > maxHeight) {
                            paramsMask.width = width / scale;
                            params.width = width / scale;
                            paramsMask.height = height / scale;
                            params.height = height / scale;
                        } else {
                            paramsMask.width = width;
                            params.width = width;
                            paramsMask.height = height;
                            params.height = height;
                        }
//                        if (width > maxWidth) {
//                            paramsMask.width = maxWidth;
//                            params.width = maxWidth;
//                        } else {
//                            paramsMask.width = width;
//                            params.width = width;
//                        }
//                        if (height > maxHeight) {
//                            paramsMask.height = maxHeight;
//                            params.height = maxHeight;
//                        } else {
//                            paramsMask.height = height;
//                            params.height = height;
//                        }
                        llMask.setLayoutParams(paramsMask);
                        ivImage.setLayoutParams(params);

                        ivImage.setImageBitmap(resource);
                        Log.i("shimyHolder", "holder又tm执行了");
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

    }

    @Override
    public int messageContentView() {
        return R.layout.item_message_image;
    }
}
