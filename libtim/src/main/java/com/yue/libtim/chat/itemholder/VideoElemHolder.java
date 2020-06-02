package com.yue.libtim.chat.itemholder;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
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
import com.tencent.imsdk.v2.V2TIMDownloadCallback;
import com.tencent.imsdk.v2.V2TIMElem;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMVideoElem;
import com.yue.libtim.R;
import com.yue.libtim.TUIKitConstants;
import com.yue.libtim.chat.messagevo.VideoElemVO;
import com.yue.libtim.utils.DensityUtils;
import com.yue.libtim.utils.FileDownloadUtils;

import java.io.File;

/**
 * @author shimy
 * @create 2020/6/1 10:30
 * @desc 视频消息
 */
public class VideoElemHolder extends MessageContentHolder {

    public ImageView ivSnapshot;
    public ProgressBar progressBar;
    public TextView tvProgress;
    public LinearLayout llMask;
    public Button btnDown;


    public VideoElemHolder(@NonNull View itemView, RecyclerView.Adapter adapter) {
        super(itemView, adapter);
        ivSnapshot = itemView.findViewById(R.id.iv_snapshot);

        progressBar = itemView.findViewById(R.id.progress);
        tvProgress = itemView.findViewById(R.id.tv_progress);
        llMask = itemView.findViewById(R.id.ll_mask);
        btnDown = itemView.findViewById(R.id.btn_down);
    }

    public void showMessage(VideoElemVO elemVO) {
        mFlMsgContent.setBackgroundColor(ivSnapshot.getResources().getColor(android.R.color.transparent));
        llMask.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvProgress.setVisibility(View.GONE);
        btnDown.setVisibility(View.GONE);

        ivSnapshot.setImageResource(R.drawable.ic_vrect);
        btnDown.setText("下载");
        progressBar.setProgress(0);
        tvProgress.setText("");

        V2TIMVideoElem videoElem = elemVO.getTimElem();

        /*这儿还得处理一下 先这样*/
        if (elemVO.getTimMessage().isSelf()) {
            if (!TextUtils.isEmpty(videoElem.getVideoPath()) && new File(videoElem.getVideoPath()).exists()) {
                if (elemVO.getTimMessage().getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_SENDING) {
                    llMask.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    tvProgress.setVisibility(View.VISIBLE);
                    btnDown.setVisibility(View.GONE);

                    progressBar.setProgress(elemVO.getSendProgress());
                    tvProgress.setText(elemVO.getSendProgress() + "%");
                } else if (elemVO.getTimMessage().getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_SEND_FAIL) {
                    llMask.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    tvProgress.setVisibility(View.VISIBLE);
                    btnDown.setVisibility(View.GONE);

                    tvProgress.setText("发送失败");
                } else if (elemVO.getTimMessage().getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_SEND_SUCC) {
                    llMask.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    tvProgress.setVisibility(View.GONE);
                    btnDown.setVisibility(View.GONE);
                } else {
                    llMask.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    tvProgress.setVisibility(View.GONE);
                    btnDown.setVisibility(View.GONE);
                }
                showImage(videoElem.getVideoPath());
            } else {
                downLoadVideo(elemVO);
            }
        } else {
            downLoadVideo(elemVO);
        }

        /*重新下载 or 下载*/
        btnDown.setOnClickListener((v) -> {
            llMask.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            tvProgress.setVisibility(View.VISIBLE);
            btnDown.setVisibility(View.GONE);
            downLoadVideo(elemVO);
        });
    }

    private void downLoadVideo(VideoElemVO elemVO) {
        V2TIMVideoElem videoElem = elemVO.getTimElem();
        File dir = new File(TUIKitConstants.MESSAGE_VIDEO_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 设置图片下载路径 imagePath，这里可以用 uuid 作为标识，避免重复下载
        String videoPath = TUIKitConstants.MESSAGE_VIDEO_DIR + "video_" + videoElem.getVideoUUID();
        Log.i("shimyFileUUID", elemVO.getTimElem().getVideoUUID());
        File videoFile = new File(videoPath);
        /*如果视频文件存在*/
        if (videoFile.exists()) {
            llMask.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            tvProgress.setVisibility(View.GONE);
            btnDown.setVisibility(View.GONE);
            showImage(videoPath);
        } else {
            ivSnapshot.setImageResource(R.drawable.ic_vrect);
            /*小于90秒的视频直接下载*/
            if (elemVO.getTimElem().getDuration() < 90) {
                llMask.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                tvProgress.setVisibility(View.VISIBLE);
                btnDown.setVisibility(View.GONE);
                downLoadVideo(elemVO);
            } else {
                llMask.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                tvProgress.setVisibility(View.GONE);
                btnDown.setVisibility(View.VISIBLE);
                btnDown.setText("下载");
            }
        }
        elemVO.getTimElem().downloadVideo(videoPath, new V2TIMDownloadCallback() {
            @Override
            public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                // 图片下载进度：已下载大小 v2ProgressInfo.getCurrentSize()；总文件大小 v2ProgressInfo.getTotalSize()
                int progress = (int) ((progressInfo.getTotalSize() * 100) / progressInfo.getCurrentSize());
                tvProgress.setText(FileDownloadUtils.byteHandle(progressInfo.getCurrentSize()) + "/" + FileDownloadUtils.byteHandle(progressInfo.getTotalSize()));
                progressBar.setProgress(progress);
            }

            @Override
            public void onError(int i, String s) {
                llMask.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                tvProgress.setVisibility(View.VISIBLE);
                btnDown.setVisibility(View.VISIBLE);

                tvProgress.setText("下载错误");
                btnDown.setText("重新下载");
            }

            @Override
            public void onSuccess() {
                llMask.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                tvProgress.setVisibility(View.GONE);
                btnDown.setVisibility(View.GONE);

                showImage(videoPath);
            }
        });

    }


    private void showImage(String path) {
        Glide.with(ivSnapshot.getContext())
                .asBitmap()
                .load(path)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        int width = resource.getWidth();
                        int height = resource.getHeight();
                        int maxWidth = (int) DensityUtils.dp(llMask.getContext(), 100);
                        int maxHeight = (int) DensityUtils.dp(llMask.getContext(), 260);

                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ivSnapshot.getLayoutParams();
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
                        ivSnapshot.setLayoutParams(params);

                        ivSnapshot.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

    }

    @Override
    public int messageContentView() {
        return R.layout.item_message_video;
    }
}
