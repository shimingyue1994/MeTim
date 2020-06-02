package com.yue.libtim.chat.itemholder;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.imsdk.v2.V2TIMDownloadCallback;
import com.tencent.imsdk.v2.V2TIMElem;
import com.tencent.imsdk.v2.V2TIMFileElem;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMSoundElem;
import com.yue.libtim.R;
import com.yue.libtim.TUIKitConstants;
import com.yue.libtim.chat.messagevo.FileElemVO;
import com.yue.libtim.chat.messagevo.SoundElemVO;
import com.yue.libtim.utils.FileDownloadUtils;

import java.io.File;

import cn.hutool.core.io.FileUtil;

/**
 * @author shimy
 * @create 2020/6/1 16:10
 * @desc 声音消息
 */
public class FileElemHolder extends MessageContentHolder {

    public ProgressBar progressBar;
    public TextView tvProgress;
    public ImageView ivFile;
    private ImageView ivDownload;

    public FileElemHolder(@NonNull View itemView, RecyclerView.Adapter adapter) {
        super(itemView, adapter);
        progressBar = itemView.findViewById(R.id.progress);
        tvProgress = itemView.findViewById(R.id.tv_progress);
        ivFile = itemView.findViewById(R.id.iv_file);
        ivDownload = itemView.findViewById(R.id.iv_download);
    }


    public void showMessage(FileElemVO elemVO) {
        progressBar.setVisibility(View.GONE);
        tvProgress.setVisibility(View.GONE);
        ivDownload.setVisibility(View.GONE);
        tvProgress.setText("");
        progressBar.setProgress(0);
        if (elemVO.getTimMessage().isSelf()) {
            ivFile.setImageResource(R.drawable.ic_voice_right);
        } else {
            ivFile.setImageResource(R.drawable.ic_voice_left);
        }

        /*这儿还得处理一下 先这样*/
        if (elemVO.getTimMessage().isSelf()) {
            if (!TextUtils.isEmpty(elemVO.getTimElem().getPath()) && new File(elemVO.getTimElem().getPath()).exists()) {
                if (elemVO.getTimMessage().getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_SENDING) {
                    progressBar.setVisibility(View.VISIBLE);
                    tvProgress.setVisibility(View.VISIBLE);

                    progressBar.setProgress(elemVO.getSendProgress());
                    tvProgress.setText(elemVO.getSendProgress() + "%");
                } else if (elemVO.getTimMessage().getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_SEND_FAIL) {
                    progressBar.setVisibility(View.GONE);
                    tvProgress.setVisibility(View.VISIBLE);

                    tvProgress.setText("发送失败");
                } else if (elemVO.getTimMessage().getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_SEND_SUCC) {
                    progressBar.setVisibility(View.GONE);
                    tvProgress.setVisibility(View.GONE);
                    ivDownload.setVisibility(View.GONE);
                }
            } else {
                ivDownload.setVisibility(View.GONE);
                tvProgress.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                downSound(elemVO);
            }
        } else {
            ivDownload.setVisibility(View.GONE);
            tvProgress.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            downSound(elemVO);
        }

        ivDownload.setOnClickListener(v -> {
            ivDownload.setVisibility(View.GONE);
            tvProgress.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            tvProgress.setText("");
            downSound(elemVO);
        });
    }

    private void downSound(FileElemVO elemVO) {
        File dir = new File(TUIKitConstants.MESSAGE_RECORD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 设置图片下载路径 imagePath，这里可以用 uuid 作为标识，避免重复下载
        final String soundPath = TUIKitConstants.MESSAGE_IMAGE_DIR + "file_" + elemVO.getTimMessage().getSender() + "_" + elemVO.getTimElem().getUUID();
        Log.i("shimyFileUUID",elemVO.getTimElem().getUUID());
        File file = new File(soundPath);
        if (file.exists()) {
            tvProgress.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            ivDownload.setVisibility(View.GONE);
        } else {
            V2TIMFileElem elem = elemVO.getTimElem();
            elem.downloadFile(soundPath, new V2TIMDownloadCallback() {
                @Override
                public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                    int progress = (int) ((progressInfo.getTotalSize() * 100) / progressInfo.getCurrentSize());
                    tvProgress.setText(FileDownloadUtils.byteHandle(progressInfo.getCurrentSize()) + "/" + FileDownloadUtils.byteHandle(progressInfo.getTotalSize()));
                    progressBar.setProgress(progress);
                }

                @Override
                public void onError(int i, String s) {
                    tvProgress.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    tvProgress.setText("加载失败" + i + s);
                    ivDownload.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccess() {
                    tvProgress.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    ivDownload.setVisibility(View.GONE);
                }
            });
        }

    }

    @Override
    public int messageContentView() {
        return R.layout.item_message_file;
    }
}
