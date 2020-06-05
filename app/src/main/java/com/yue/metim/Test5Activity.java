package com.yue.metim;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

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
import com.yue.libtim.fragment.InputFaceFragment;
import com.yue.libtim.weight.input.InputSendListener;
import com.yue.libtim.weight.input.InputStatusListener;
import com.yue.metim.constants.User;
import com.yue.metim.databinding.ActivityTest5Binding;
import com.yue.metim.fragment.MyInputMoreFragment;
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

public class Test5Activity extends AppCompatActivity {
    private ActivityTest5Binding mBinding;
    private Items mItems = new Items();
    private MultiTypeAdapter mAdapter = new MultiTypeAdapter(mItems);
    private String identify;
    /*最后一条消息，用于加载消息使用*/
    private V2TIMMessage lastMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test5);
        identify = getIntent().getStringExtra("identify");

        mBinding.inputlayout.init(this);
        mBinding.inputlayout.enableVoice(true);
        mBinding.inputlayout.enableMore(true);
        mBinding.inputlayout.enableFace(true);
        mBinding.inputlayout.setInputFaceFragment(InputFaceFragment.newInstance());
        MyInputMoreFragment myInputMoreFragment = MyInputMoreFragment.newInstance();
        mBinding.inputlayout.setInputMoreFragment(myInputMoreFragment);
        myInputMoreFragment.setOnFragmentListener(onFragmentListener);
        mBinding.inputlayout.setInputStatusListener(inputStatusListener);
        mBinding.inputlayout.setInputSendListener(inputSendListener);

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
        mBinding.refresh.setEnableLoadMore(false);

        /*拉去消息*/
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

    private InputSendListener inputSendListener = new InputSendListener() {
        @Override
        public void sendText(String text) {
            V2TIMMessage timMessage = V2TIMManager.getMessageManager().createTextMessage(text);
            V2TIMTextElem textElem = timMessage.getTextElem();
            TextElemVO textElemVO = new TextElemVO(timMessage, textElem);
            mItems.add(textElemVO);
            mAdapter.notifyDataSetChanged();
            mBinding.recycler.scrollToPosition(mItems.size() - 1);
            V2TIMManager.getMessageManager().sendMessage(timMessage, identify, "",
                    V2TIM_PRIORITY_DEFAULT, true, null,
                    new V2TIMSendCallback<V2TIMMessage>() {
                        @Override
                        public void onProgress(int i) {
//                            mAdapter.notifyDataSetChanged();
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

        @Override
        public void sendVoice(String path, int duration) {
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

        }
    };


    private MyInputMoreFragment.OnFragmentListener onFragmentListener = new MyInputMoreFragment.OnFragmentListener() {
        @Override
        public void sendImage(String path) {
            /*发送图片消息*/
            V2TIMMessage timMessage = V2TIMManager.getMessageManager().createImageMessage(path);
            V2TIMImageElem imageElem = timMessage.getImageElem();
            ImageElemVO imageElemVO = new ImageElemVO(timMessage, imageElem);
            mItems.add(imageElemVO);
            int position = mItems.size() - 1;
            mAdapter.notifyItemChanged(position, mItems.size() - 1);
            mBinding.recycler.scrollToPosition(mItems.size() - 1);
            mBinding.recycler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    mAdapter.notifyItemChanged(position, mItems.size() - 1);
                    mBinding.recycler.scrollToPosition(mItems.size() - 1);
                }
            }, 200);


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

        @Override
        public void sendVideo(String path, String miniType, int duration) {
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
            V2TIMMessage timMessage = V2TIMManager.getMessageManager().createVideoMessage(path, miniType, duration, snapshotPath);
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

        @Override
        public void sendFile(String path) {
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
    };


    private InputStatusListener inputStatusListener = new InputStatusListener() {
        @Override
        public void onInputAreaChanged(boolean open) {

        }

        @Override
        public void onVoiceStatusChanged(int status) {

        }
    };
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
                Log.i("shimyHz", "已读回执了");
            } else {
                Log.i("shimyHz", "没有回执了");
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


            for (int i = 0; i < mItems.size(); i++) {
                BaseMsgElem msgElem = (BaseMsgElem) mItems.get(i);
                if (!msgElem.getTimMessage().isSelf() && msgElem.getTimMessage().getMsgID().equals(msgID)) {
                    /*这个消息被对方撤回了*/
                    RevokeElemVO revokeElemVO = new RevokeElemVO(msgElem.getTimMessage(), msgElem.getTimElem());
                    mItems.add(i, revokeElemVO);
                    mItems.remove(i);
                }
            }
        }
    };

    private IMessageItemClick messageItemClick = new IMessageItemClick() {

        @Override
        public void onClickFailStatus(View view, int position, BaseMsgElem timMessage) {
            AlertDialog dialog = new AlertDialog.Builder(Test5Activity.this)
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

    /**
     * @param position 位置
     * @param msgElem  要撤回的某条消息
     */
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


    @Override
    public void onBackPressed() {
        if (mBinding.inputlayout.hideMore()) {

        }else {
            super.onBackPressed();
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
