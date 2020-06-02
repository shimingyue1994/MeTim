package com.yue.metim;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.imsdk.v2.V2TIMAdvancedMsgListener;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMCustomElem;
import com.tencent.imsdk.v2.V2TIMElem;
import com.tencent.imsdk.v2.V2TIMFaceElem;
import com.tencent.imsdk.v2.V2TIMFileElem;
import com.tencent.imsdk.v2.V2TIMGroupTipsElem;
import com.tencent.imsdk.v2.V2TIMImageElem;
import com.tencent.imsdk.v2.V2TIMLocationElem;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMMessageReceipt;
import com.tencent.imsdk.v2.V2TIMSendCallback;
import com.tencent.imsdk.v2.V2TIMSoundElem;
import com.tencent.imsdk.v2.V2TIMTextElem;
import com.tencent.imsdk.v2.V2TIMVideoElem;
import com.yalantis.ucrop.util.FileUtils;
import com.yue.libtim.TUIKitConstants;
import com.yue.libtim.chat.interfaces.IMessageItemClick;
import com.yue.libtim.chat.interfaces.impl.SimpleMessageItmeClick;
import com.yue.libtim.chat.itembinder.FileElemBinder;
import com.yue.libtim.chat.itembinder.ImageElemBinder;
import com.yue.libtim.chat.itembinder.RevokeElemBinder;
import com.yue.libtim.chat.itembinder.SoundElemBinder;
import com.yue.libtim.chat.itembinder.TextElemBinder;
import com.yue.libtim.chat.itembinder.VideoElemBinder;
import com.yue.libtim.chat.messagevo.BaseMsgElem;
import com.yue.libtim.chat.messagevo.FileElemVO;
import com.yue.libtim.chat.messagevo.ImageElemVO;
import com.yue.libtim.chat.messagevo.RevokeElemVO;
import com.yue.libtim.chat.messagevo.SoundElemVO;
import com.yue.libtim.chat.messagevo.TextElemVO;
import com.yue.libtim.chat.messagevo.VideoElemVO;
import com.yue.libtim.utils.AudioPlayer;
import com.yue.libtim.utils.FileUtil;
import com.yue.metim.constants.User;
import com.yue.metim.databinding.ActivityTest01Binding;
import com.yue.metim.utils.GlideEngine;
import com.yue.metim.weight.msgpop.IMPopupView;
import com.yue.metim.weight.msgpop.MsgPopAction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

import static com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM;
import static com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_FACE;
import static com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_FILE;
import static com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_GROUP_TIPS;
import static com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE;
import static com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_LOCATION;
import static com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_NONE;
import static com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_SOUND;
import static com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_TEXT;
import static com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_VIDEO;
import static com.tencent.imsdk.v2.V2TIMMessage.V2TIM_MSG_STATUS_HAS_DELETED;
import static com.tencent.imsdk.v2.V2TIMMessage.V2TIM_MSG_STATUS_LOCAL_REVOKED;
import static com.tencent.imsdk.v2.V2TIMMessage.V2TIM_MSG_STATUS_SEND_FAIL;
import static com.tencent.imsdk.v2.V2TIMMessage.V2TIM_PRIORITY_DEFAULT;

public class Test01Activity extends AppCompatActivity {

    private ActivityTest01Binding mBinding;
    private final int REQUESTCODE_FILE = 0x1003;


    private Items mItems = new Items();
    private MultiTypeAdapter mAdapter = new MultiTypeAdapter(mItems);
    private String identify;

    /*最后一条消息，用于加载消息使用*/
    private V2TIMMessage lastMessage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test01);
        identify = getIntent().getStringExtra("identify");

        V2TIMManager.getMessageManager().addAdvancedMsgListener(msgListener);
        registerBinder();

        mBinding.recycler.suppressLayout(false);
        mBinding.recycler.setItemViewCacheSize(0);
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setFocusableInTouchMode(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mBinding.recycler.setLayoutManager(linearLayoutManager);
        ((DefaultItemAnimator) mBinding.recycler.getItemAnimator()).setSupportsChangeAnimations(false);


        mBinding.recycler.setAdapter(mAdapter);
        sends();
        mBinding.refresh.setEnableLoadMore(false);


        mBinding.refresh.setOnRefreshListener(refreshLayout -> {
            V2TIMManager.getInstance().getMessageManager().getC2CHistoryMessageList(identify.equals(User.userId01) ? User.userId02 : User.userId01,
                    10, lastMessage, new V2TIMSendCallback<List<V2TIMMessage>>() {
                        @Override
                        public void onProgress(int i) {
//                            mBinding.progress.setVisibility(View.VISIBLE);
//                            mBinding.tvStatus.setText("消息加载中...");
                            mBinding.refresh.finishRefresh();
                        }

                        @Override
                        public void onError(int i, String s) {
                            mBinding.refresh.finishRefresh();
                        }

                        @Override
                        public void onSuccess(List<V2TIMMessage> v2TIMMessages) {
                            Collections.reverse(v2TIMMessages);
                            if (v2TIMMessages.size() > 0)
                                lastMessage = v2TIMMessages.get(0);
                            for (V2TIMMessage timMessage : v2TIMMessages) {
                                handleMsg(timMessage, true);
                            }

                            mAdapter.notifyDataSetChanged();
                            mBinding.recycler.scrollToPosition(mItems.size() - 1);
                            mBinding.refresh.finishRefresh();
                        }
                    });
        });


    }

    private void registerBinder() {
        TextElemBinder textElemBinder = new TextElemBinder();
        textElemBinder.setItemClick(messageItemClick);
        mAdapter.register(TextElemVO.class, textElemBinder);
        RevokeElemBinder revokeElemBinder = new RevokeElemBinder();
        revokeElemBinder.setOnRevokeListener((position, item) -> {
            handleDelete(item);
        });
        mAdapter.register(RevokeElemVO.class, revokeElemBinder);
        mAdapter.register(ImageElemVO.class, new ImageElemBinder(mBinding.recycler));
        VideoElemBinder videoElemBinder = new VideoElemBinder();
        videoElemBinder.setItemClick(messageItemClick);
        mAdapter.register(VideoElemVO.class, videoElemBinder);
        SoundElemBinder soundElemBinder = new SoundElemBinder();
        soundElemBinder.setItemClick(new SimpleMessageItmeClick() {
            @Override
            public void onClickBubble(View view, int position, BaseMsgElem timMessage) {
                super.onClickBubble(view, position, timMessage);
                V2TIMSoundElem elem = (V2TIMSoundElem) timMessage.getTimElem();
                /*气泡点击  播放音频*/
                Log.i("shimySound", elem.getPath());
            }
        });
        mAdapter.register(SoundElemVO.class, soundElemBinder);

        FileElemBinder fileElemBinder = new FileElemBinder();
        mAdapter.register(FileElemVO.class, fileElemBinder);
    }

    private void sends() {
        mBinding.btnSend.setOnClickListener(v -> {
            V2TIMMessage timMessage = V2TIMManager.getMessageManager().createTextMessage(mBinding.etInput.getText().toString());
            V2TIMTextElem textElem = timMessage.getTextElem();
            TextElemVO textElemVO = new TextElemVO(timMessage, textElem);
            mItems.add(textElemVO);
            mAdapter.notifyDataSetChanged();
            mBinding.recycler.scrollToPosition(mItems.size() - 1);
            mBinding.etInput.setText("");
            V2TIMManager.getMessageManager().sendMessage(timMessage, identify, "",
                    V2TIM_PRIORITY_DEFAULT, true, null,
                    new V2TIMSendCallback<V2TIMMessage>() {
                        @Override
                        public void onProgress(int i) {
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(int i, String s) {
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onSuccess(V2TIMMessage v2TIMMessage) {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        });
        mBinding.btnSendImage.setOnClickListener(v -> {
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
        mBinding.btnSendVideo.setOnClickListener(v -> {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofVideo())
                    .maxSelectNum(1)// 最大图片选择数量 int
                    .isCamera(true)// 是否显示拍照按钮 true or false
//                    .cutOutQuality(90)
                    .synOrAsy(true)
                    .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                    .forResult(PictureConfig.CHOOSE_REQUEST + 1);
        });
        mBinding.btnStartSound.setOnClickListener(v -> {
            File dir = new File(TUIKitConstants.MESSAGE_RECORD_DIR);
            if (!dir.exists())
                dir.mkdirs();
            AudioPlayer.getInstance().startRecord(new AudioPlayer.Callback() {
                @Override
                public void onCompletion(Boolean success) {
                    String audioPath = AudioPlayer.getInstance().getPath();
                    Toast.makeText(Test01Activity.this, "" + audioPath, Toast.LENGTH_SHORT).show();
                    sendAudio(audioPath);
                }
            });
        });
        mBinding.btnStopSound.setOnClickListener(v -> {
            AudioPlayer.getInstance().stopRecord();
        });

        mBinding.btnFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, REQUESTCODE_FILE);
        });
    }


    private void sendImage(String path) {
        /*发送图片消息*/
        V2TIMMessage timMessage = V2TIMManager.getMessageManager().createImageMessage(path);
        V2TIMImageElem imageElem = timMessage.getImageElem();
        ImageElemVO imageElemVO = new ImageElemVO(timMessage, imageElem);
        mItems.add(imageElemVO);
        mAdapter.notifyDataSetChanged();
        int position = mItems.size() - 1;
        mBinding.recycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemChanged(position, mItems.size() - 1);
                mBinding.recycler.scrollToPosition(mItems.size() - 1);
            }
        }, 500);


        V2TIMManager.getMessageManager().sendMessage(timMessage, identify, "",
                V2TIM_PRIORITY_DEFAULT, true, null,
                new V2TIMSendCallback<V2TIMMessage>() {
                    @Override
                    public void onProgress(int i) {
                        imageElemVO.setSendProgress(i);
                        mAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onError(int i, String s) {
                        mAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onSuccess(V2TIMMessage v2TIMMessage) {
                        mAdapter.notifyItemChanged(position);
                    }
                });
    }

    /**
     * 发送视频消息
     */
    public void sendVideo(LocalMedia localMedia) {
        String miniType = localMedia.getMimeType();
        if (miniType.contains("/") && miniType.split("/").length >= 2) {
            miniType = miniType.split("/")[1];
        }
        String snapshotPath = "";
//        Glide.with(this)
//                .asBitmap()
//                .load(localMedia.getPath())
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        File dir = new File(TUIKitConstants.MESSAGE_VIDEO_SNAPSHOT);
//                        if (!dir.exists()){
//                            dir.mkdirs();
//                        }
//                        String snapshotPath = TUIKitConstants.MESSAGE_VIDEO_SNAPSHOT + "snapshot_"
//                                + V2TIMManager.getInstance().getLoginUser()
//                                + "_"
//                                + UUID.randomUUID().toString().replaceAll("-","")
//                                + ".jpg";
//                        File file = new File(snapshotPath);
//                        FileOutputStream os = null;
//                        try {
//                            os = new FileOutputStream(file);
//                            resource.compress(Bitmap.CompressFormat.JPEG, 100, os);
////                            resource.recycle();
//                            os.flush();
//                            os.close();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                    }
//                });
        V2TIMMessage timMessage = V2TIMManager.getMessageManager().createVideoMessage(localMedia.getPath(), miniType, (int) localMedia.getDuration(), snapshotPath);
        V2TIMVideoElem videoElem = timMessage.getVideoElem();
        VideoElemVO videoElemVO = new VideoElemVO(timMessage, videoElem);
        mItems.add(videoElemVO);
        mAdapter.notifyDataSetChanged();
        int position = mItems.size() - 1;
        mBinding.recycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemChanged(position, mItems.size() - 1);
                mBinding.recycler.scrollToPosition(mItems.size() - 1);
            }
        }, 500);


        V2TIMManager.getMessageManager().sendMessage(timMessage, identify, "",
                V2TIM_PRIORITY_DEFAULT, true, null,
                new V2TIMSendCallback<V2TIMMessage>() {
                    @Override
                    public void onProgress(int i) {
                        videoElemVO.setSendProgress(i);
                        mAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onError(int i, String s) {
                        showTip("发送失败：code->" + i + "  " + s);
                        mAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onSuccess(V2TIMMessage v2TIMMessage) {
                        mAdapter.notifyItemChanged(position);
                    }
                });
    }

    private void sendAudio(String path) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            int duration = mediaPlayer.getDuration();
            mediaPlayer.release();
            mediaPlayer = null;
            V2TIMMessage timMessage = V2TIMManager.getMessageManager().createSoundMessage(path, duration);
            V2TIMSoundElem elem = timMessage.getSoundElem();
            SoundElemVO soundElemVO = new SoundElemVO(timMessage, elem);
            mItems.add(soundElemVO);
            mAdapter.notifyDataSetChanged();
            int position = mItems.size() - 1;
            mBinding.recycler.scrollToPosition(mItems.size() - 1);
            V2TIMManager.getMessageManager().sendMessage(timMessage, identify, "",
                    V2TIM_PRIORITY_DEFAULT, true, null,
                    new V2TIMSendCallback<V2TIMMessage>() {
                        @Override
                        public void onProgress(int i) {
                            soundElemVO.setSendProgress(i);
                            mAdapter.notifyItemChanged(position);
                        }

                        @Override
                        public void onError(int i, String s) {
//                            showTip("发送失败：code->" + i + "  " + s);
                            mAdapter.notifyItemChanged(position);
                        }

                        @Override
                        public void onSuccess(V2TIMMessage v2TIMMessage) {
                            mAdapter.notifyItemChanged(position);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void sendFile(String path) {
        File file = new File(path);
        V2TIMMessage timMessage = V2TIMManager.getMessageManager().createFileMessage(path, file.getName());
        V2TIMFileElem elem = timMessage.getFileElem();
        FileElemVO fileElemVO = new FileElemVO(timMessage, elem);
        mItems.add(fileElemVO);
        mAdapter.notifyDataSetChanged();
        int position = mItems.size() - 1;
        V2TIMManager.getMessageManager().sendMessage(timMessage, identify, "",
                V2TIM_PRIORITY_DEFAULT, true, null,
                new V2TIMSendCallback<V2TIMMessage>() {
                    @Override
                    public void onProgress(int i) {
                        fileElemVO.setSendProgress(i);
                        mAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onError(int i, String s) {
                        showTip("发送失败：code->" + i + "  " + s);
                        mAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onSuccess(V2TIMMessage v2TIMMessage) {
                        mAdapter.notifyItemChanged(position);
                    }
                });
    }

    /**
     * 接收别人的消息
     */
    private V2TIMAdvancedMsgListener msgListener = new V2TIMAdvancedMsgListener() {
        @Override
        public void onRecvNewMessage(V2TIMMessage msg) {
            super.onRecvNewMessage(msg);
            String userId = msg.getUserID();
            if (!TextUtils.equals(userId, identify)) {
                return;
            }
            handleMsg(msg, false);
            mAdapter.notifyDataSetChanged();
            mBinding.recycler.scrollToPosition(mItems.size() - 1);
        }

        /*对方的已读回执 目前仅C2C支持*/
        @Override
        public void onRecvC2CReadReceipt(List<V2TIMMessageReceipt> receiptList) {
            super.onRecvC2CReadReceipt(receiptList);
            Log.i("shimyHz", "已读回止》");

            long maxTimestamp = 0;//最大时间戳
            // 由于接收方一次性可能会收到多个已读回执，所以这里采用了数组的回调形式
            for (V2TIMMessageReceipt v2TIMMessageReceipt : receiptList) {
                // 消息接收者 receiver
                String userID = v2TIMMessageReceipt.getUserID();
                // 已读回执时间，聊天窗口中时间戳小于或等于 timestamp 的消息都可以被认为已读
                long timestamp = v2TIMMessageReceipt.getTimestamp();
                if (maxTimestamp < timestamp)
                    maxTimestamp = timestamp;
            }

            maxTimestamp = maxTimestamp + 3;
            /*是否需要更新adapter  当自己的消息都已经是已读时，不需要更新*/
            boolean isNotify = false;
            for (int i = 0; i < mItems.size(); i++) {
                BaseMsgElem elem = (BaseMsgElem) mItems.get(i);
                long timestamp = elem.getTimMessage().getTimestamp();
                if (!elem.getTimMessage().isSelf()) {
                    continue;
                }
                if (elem.isLocalRead()) {
                    continue;
                }
                if (timestamp <= maxTimestamp) {
                    isNotify = true;
                    elem.setLocalRead(true);
                    Log.i("shimyHz", "已读回执设置2");
                }
            }
            Log.i("shimyHz", "已读回执");
            boolean finalIsNotify = isNotify;
            if (finalIsNotify) {
                mAdapter.notifyDataSetChanged();
                showTip("已读回执了");
            } else {
                showTip("没有回执了");
            }


        }

        @Override
        public void onRecvMessageRevoked(String msgID) {
            super.onRecvMessageRevoked(msgID);
            /*感知别人的消息被撤回的回调*/
            // msgList 为当前聊天界面的消息列表
//            for (V2TIMMessage msg : msgList) {
//                if (msg.getMsgID().equals(msgID)) {
//                    // msg 即为被撤回的消息，您需要修改 UI 上相应的消息气泡的状态
//                }
//            }
        }
    };


    /**
     * 是否是加载历史消息
     *
     * @param msg
     * @param isLoadHis
     */
    private void handleMsg(V2TIMMessage msg, boolean isLoadHis) {
        if (msg.getStatus() == V2TIM_MSG_STATUS_HAS_DELETED || msg.getStatus() == V2TIM_ELEM_TYPE_NONE) {
            /*none或被删除的消息不展示*/
            return;
        }


        V2TIMElem elem = null;
        switch (msg.getElemType()) {
            case V2TIM_ELEM_TYPE_NONE:
                /*空消息*/

                break;
            case V2TIM_ELEM_TYPE_TEXT:
                elem = msg.getTextElem();
                break;
            case V2TIM_ELEM_TYPE_IMAGE:
                elem = msg.getImageElem();
                break;
            case V2TIM_ELEM_TYPE_SOUND:
                elem = msg.getSoundElem();
                break;
            case V2TIM_ELEM_TYPE_VIDEO:
                elem = msg.getVideoElem();
                break;
            case V2TIM_ELEM_TYPE_FILE:
                elem = msg.getFileElem();
                break;
            case V2TIM_ELEM_TYPE_LOCATION:
                elem = msg.getLocationElem();
                break;
            case V2TIM_ELEM_TYPE_FACE:
                elem = msg.getFaceElem();
                break;
            case V2TIM_ELEM_TYPE_CUSTOM:
                elem = msg.getCustomElem();
                break;
            case V2TIM_ELEM_TYPE_GROUP_TIPS:
                elem = msg.getGroupTipsElem();
                break;

        }
        if (elem != null) {
            handleElem(msg, elem, isLoadHis);
        }
        if (msg.getElemType() != V2TIM_ELEM_TYPE_NONE && elem != null) {
            /*判断是否还有下一个元素 因为存在一个消息多个元素的情况，但这种情况极少出现，除了特别奇葩的设定*/
            while (elem.getNextElem() != null) {
                handleElem(msg, elem.getNextElem(), isLoadHis);
            }
        }
    }


    /**
     * 处理消息中的元素并显示
     *
     * @param msg       实际消息对象
     * @param elem      消息中的某一个元素
     * @param isLoadHis 是否是加载消息
     */
    private void handleElem(V2TIMMessage msg, V2TIMElem elem, boolean isLoadHis) {
        if (msg.getStatus() == V2TIM_MSG_STATUS_LOCAL_REVOKED) {
            /*撤回的消息需要单独处理*/
            RevokeElemVO revokeElemVO = new RevokeElemVO(msg, elem);
            mItems.add(revokeElemVO);
            return;
        }

        if (elem instanceof V2TIMTextElem) {
            V2TIMTextElem textElem = (V2TIMTextElem) elem;
            TextElemVO textElemVO = new TextElemVO(msg, textElem);
            if (isLoadHis) {
                mItems.add(0, textElemVO);
            } else {
                mItems.add(textElemVO);
            }
        } else if (elem instanceof V2TIMImageElem) {
            V2TIMImageElem imageElem = (V2TIMImageElem) elem;
            ImageElemVO imageElemVO = new ImageElemVO(msg, imageElem);
            if (isLoadHis) {
                mItems.add(0, imageElemVO);
            } else {
                mItems.add(imageElemVO);
            }
        } else if (elem instanceof V2TIMSoundElem) {
            V2TIMSoundElem soundElem = (V2TIMSoundElem) elem;
            SoundElemVO soundElemVO = new SoundElemVO(msg, soundElem);
            if (isLoadHis) {
                mItems.add(0, soundElemVO);
            } else {
                mItems.add(soundElemVO);
            }
        } else if (elem instanceof V2TIMVideoElem) {
            V2TIMVideoElem videoElem = (V2TIMVideoElem) elem;
            VideoElemVO videoElemVO = new VideoElemVO(msg, videoElem);
            if (isLoadHis) {
                mItems.add(0, videoElemVO);
            } else {
                mItems.add(videoElemVO);
            }
        } else if (elem instanceof V2TIMFileElem) {
            V2TIMFileElem fileElem = (V2TIMFileElem) elem;
            FileElemVO fileElemVO = new FileElemVO(msg, fileElem);
            if (isLoadHis) {
                mItems.add(0, fileElemVO);
            } else {
                mItems.add(fileElemVO);
            }
        } else if (elem instanceof V2TIMLocationElem) {

        } else if (elem instanceof V2TIMFaceElem) {

        } else if (elem instanceof V2TIMCustomElem) {

        } else if (elem instanceof V2TIMGroupTipsElem) {

        }

    }

    private IMessageItemClick messageItemClick = new IMessageItemClick() {

        @Override
        public void onClickFailStatus(View view, int position, BaseMsgElem timMessage) {
            AlertDialog dialog = new AlertDialog.Builder(Test01Activity.this)
                    .setMessage("是否重发")
                    .setPositiveButton("重发", (dialog1, which) -> {
                        dialog1.dismiss();
                        handleMsgRepeat(position, timMessage);
                    })
                    .setNegativeButton("取消", (dialog12, which) -> {
                        dialog12.dismiss();
                    })
                    .create();
            dialog.show();
        }

        @Override
        public void onClickAvatar(View view, int position, BaseMsgElem timMessage) {

        }

        @Override
        public void onLongClickAvatar(View view, int position, BaseMsgElem timMessage) {

        }

        @Override
        public void onClickBubble(View view, int position, BaseMsgElem timMessage) {

        }

        @Override
        public void onDoubleClickBubble(View view, int position, BaseMsgElem timMessage) {

        }

        @Override
        public void onLongClickBubble(View view, int position, BaseMsgElem timMessage) {
            List<MsgPopAction> list = new ArrayList<>();
            if (timMessage.getTimMessage().getStatus() == V2TIM_MSG_STATUS_SEND_FAIL) {
                list.add(new MsgPopAction("重发", (popupWindow) -> {
                    popupWindow.dismiss();
                    handleMsgRepeat(position, timMessage);
                }));
            }

            if (timMessage.getTimMessage().isSelf()) {
                list.add(new MsgPopAction("删除", (popupWindow) -> {
                    popupWindow.dismiss();
                    handleDelete(timMessage);
                }));
                list.add(new MsgPopAction("撤回", (popupWindow) -> {
                    popupWindow.dismiss();
                    handleMsgRevoke(position, timMessage);
                }));
            }
            IMPopupView imPopupView = new IMPopupView();
            imPopupView.showPopMsgAction(view, list);
        }
    };


    private void handleMsgRevoke(int position, BaseMsgElem msgElem) {
        showProgress();
        V2TIMManager.getMessageManager().revokeMessage(msgElem.getTimMessage(), new V2TIMCallback() {
            @Override
            public void onError(int code, String desc) {
                hideProgress();
                if (code == 20016) {
                    showTip("超过两分钟的消息不可撤回");
                } else if (code == 20022) {
                    showTip("撤回的消息不存在");
                } else if (code == 20023) {
                    showTip("消息已被撤回");
                } else {
                    // 撤回消息失败
                    showTip("撤回失败：code:" + code + " " + desc);
                }
            }

            @Override
            public void onSuccess() {
                hideProgress();
                // 撤回消息成功
                for (int i = 0; i < mItems.size(); i++) {
                    BaseMsgElem msgElem1 = (BaseMsgElem) mItems.get(i);
                    if (msgElem1.getTimMessage().getMsgID().equals(msgElem.getTimMessage().getMsgID())) {
                        RevokeElemVO revokeElemVO = new RevokeElemVO(msgElem1.getTimMessage(), msgElem1.getTimElem());
                        mItems.add(i, revokeElemVO);
                        mItems.remove(i);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 消息重发
     */
    private void handleMsgRepeat(int position, BaseMsgElem timMessage) {
        mItems.remove(position);
        mItems.add(timMessage);
        mAdapter.notifyDataSetChanged();
        mBinding.recycler.scrollToPosition(mItems.size() - 1);
        V2TIMManager.getMessageManager().sendMessage(timMessage.getTimMessage(), identify, "",
                V2TIM_PRIORITY_DEFAULT, true, null,
                new V2TIMSendCallback<V2TIMMessage>() {
                    @Override
                    public void onProgress(int i) {
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(int i, String s) {
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onSuccess(V2TIMMessage v2TIMMessage) {
                        mAdapter.notifyDataSetChanged();
                    }
                });

    }


    /**
     * 删除消息 目前只支持本地删除，不支持云端删除
     *
     * @param elem
     */
    private void handleDelete(BaseMsgElem elem) {
        V2TIMManager.getMessageManager().deleteMessageFromLocalStorage(elem.getTimMessage(), new V2TIMCallback() {
            @Override
            public void onError(int code, String desc) {
                // 删除消息失败
                showTip("删除消息失败 code " + code + desc);
            }

            @Override
            public void onSuccess() {
                // 删除消息成功
                for (int i = 0; i < mItems.size(); i++) {
                    BaseMsgElem msgElem1 = (BaseMsgElem) mItems.get(i);
                    if (msgElem1.getTimMessage().getMsgID().equals(elem.getTimMessage().getMsgID())) {
                        mItems.remove(i);
                    }
                }
                mAdapter.notifyDataSetChanged();
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
                    sendImage(selectList.get(0).getCompressPath());
                }
                break;
                case PictureConfig.CHOOSE_REQUEST + 1: {//视频
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    sendVideo(selectList.get(0));
                }
                break;
                case REQUESTCODE_FILE: {
                    if (data == null) {
                        // 用户未选择任何文件，直接返回
                        return;
                    }
                    Uri uri = data.getData(); // 获取用户选择文件的URI
                    String path = FileUtil.getPathFromUri(this, uri);
                    Log.i("shimyFile", path);
                    sendFile(path);
                }
                break;
            }
        }
    }

    private AlertDialog mDialogTip;


    private void showTip(String text) {
        if (mDialogTip == null && !this.isFinishing()) {
            mDialogTip = new AlertDialog.Builder(this).create();
        }
        mDialogTip.setMessage(text);
        mDialogTip.show();
    }

    private AlertDialog mDialogProgress;

    private void showProgress() {
        if (mDialogProgress == null && !this.isFinishing()) {
            mDialogProgress = new AlertDialog.Builder(this)
                    .setView(R.layout.dialog_progress)
                    .create();

        }
        if (!mDialogProgress.isShowing())
            mDialogProgress.show();
    }


    private void hideProgress() {
        if (mDialogProgress != null && !this.isFinishing() && mDialogProgress.isShowing()) {
            mDialogProgress.dismiss();
        }
    }
}
