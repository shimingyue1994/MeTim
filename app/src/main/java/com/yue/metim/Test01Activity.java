package com.yue.metim;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
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
import com.tencent.imsdk.v2.V2TIMMessageManager;
import com.tencent.imsdk.v2.V2TIMMessageReceipt;
import com.tencent.imsdk.v2.V2TIMSendCallback;
import com.tencent.imsdk.v2.V2TIMSimpleMsgListener;
import com.tencent.imsdk.v2.V2TIMSoundElem;
import com.tencent.imsdk.v2.V2TIMTextElem;
import com.tencent.imsdk.v2.V2TIMUserInfo;
import com.tencent.imsdk.v2.V2TIMVideoElem;
import com.yue.libtim.chat.interfaces.IMessageItemClick;
import com.yue.libtim.chat.itembinder.ImageElemBinder;
import com.yue.libtim.chat.itembinder.RevokeElemBinder;
import com.yue.libtim.chat.itembinder.TextElemBinder;
import com.yue.libtim.chat.messagevo.BaseMsgElem;
import com.yue.libtim.chat.messagevo.ImageElemVO;
import com.yue.libtim.chat.messagevo.RevokeElemVO;
import com.yue.libtim.chat.messagevo.TextElemVO;
import com.yue.metim.constants.User;
import com.yue.metim.databinding.ActivityTest01Binding;
import com.yue.metim.utils.GlideEngine;
import com.yue.metim.weight.msgpop.IMPopupView;
import com.yue.metim.weight.msgpop.MsgPopAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

import static com.tencent.imsdk.v2.V2TIMMessage.*;

public class Test01Activity extends AppCompatActivity {

    private ActivityTest01Binding mBinding;
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

        TextElemBinder textElemBinder = new TextElemBinder();
        textElemBinder.setItemClick(messageItemClick);
        mAdapter.register(TextElemVO.class, textElemBinder);
        RevokeElemBinder revokeElemBinder = new RevokeElemBinder();
        revokeElemBinder.setOnRevokeListener((position, item) -> {
            handleDelete(item);
        });
        mAdapter.register(RevokeElemVO.class, revokeElemBinder);
        mAdapter.register(ImageElemVO.class, new ImageElemBinder());

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
    }


    private void sendImage(String path) {
        /*发送图片消息*/
        V2TIMMessage timMessage = V2TIMManager.getMessageManager().createImageMessage(path);
        V2TIMImageElem imageElem = timMessage.getImageElem();
        ImageElemVO imageElemVO = new ImageElemVO(timMessage, imageElem);
        mItems.add(imageElemVO);
        mAdapter.notifyDataSetChanged();
        int position = mItems.size() - 1;
        mBinding.recycler.scrollToPosition(mItems.size() - 1);
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
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onSuccess(V2TIMMessage v2TIMMessage) {
                        mAdapter.notifyDataSetChanged();
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
            for (int i = 0; i < mItems.size(); i++) {
                BaseMsgElem elem = (BaseMsgElem) mItems.get(i);
                if (elem.getTimMessage().getTimestamp() <= maxTimestamp) {
                    elem.setLocalRead(true);
                }
            }
            mAdapter.notifyDataSetChanged();
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

        } else if (elem instanceof V2TIMVideoElem) {

        } else if (elem instanceof V2TIMFileElem) {

        } else if (elem instanceof V2TIMLocationElem) {

        } else if (elem instanceof V2TIMFaceElem) {

        } else if (elem instanceof V2TIMCustomElem) {

        } else if (elem instanceof V2TIMGroupTipsElem) {

        }

    }

    private IMessageItemClick messageItemClick = new IMessageItemClick() {
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
