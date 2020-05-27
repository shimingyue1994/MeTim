package com.yue.metim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.yue.libtim.chat.itembinder.TextElemBinder;
import com.yue.libtim.chat.messagevo.BaseMsgElem;
import com.yue.libtim.chat.messagevo.TextElemVO;
import com.yue.metim.constants.User;
import com.yue.metim.databinding.ActivityTest01Binding;
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
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.suppressLayout(false);
        mBinding.recycler.setItemViewCacheSize(0);
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setFocusableInTouchMode(false);
        mBinding.recycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 20;
            }
        });
        mBinding.recycler.setAdapter(mAdapter);
        sends();
        mBinding.refresh.setEnableLoadMore(false);
        mBinding.refresh.setOnRefreshListener(refreshLayout -> {
            V2TIMManager.getInstance().getMessageManager().getC2CHistoryMessageList(identify.equals(User.userId01) ? User.userId02 : User.userId01,
                    10, lastMessage, new V2TIMSendCallback<List<V2TIMMessage>>() {
                        @Override
                        public void onProgress(int i) {
                            mBinding.progress.setVisibility(View.VISIBLE);
                            mBinding.tvStatus.setText("消息加载中...");
                            mBinding.refresh.finishRefresh();
                        }

                        @Override
                        public void onError(int i, String s) {
                            mBinding.progress.setVisibility(View.GONE);
                            mBinding.tvStatus.setText("加载错误" + i + s);
                            mBinding.refresh.finishRefresh();
                        }

                        @Override
                        public void onSuccess(List<V2TIMMessage> v2TIMMessages) {
                            mBinding.progress.setVisibility(View.GONE);
                            mBinding.tvStatus.setText("加载成功" + v2TIMMessages.size());
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
                            mBinding.tvStatus.setText("发送中" + i);
                            mBinding.progress.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(int i, String s) {
                            mBinding.tvStatus.setText("发送失败" + i + s);
                            mBinding.progress.setVisibility(View.GONE);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onSuccess(V2TIMMessage v2TIMMessage) {
                            mBinding.tvStatus.setText("发送成功");
                            mBinding.progress.setVisibility(View.GONE);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
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

        /*对方的已读回执*/
        @Override
        public void onRecvC2CReadReceipt(List<V2TIMMessageReceipt> receiptList) {
            super.onRecvC2CReadReceipt(receiptList);
        }

        @Override
        public void onRecvMessageRevoked(String msgID) {
            super.onRecvMessageRevoked(msgID);
        }
    };


    /**
     * 是否是加载历史消息
     *
     * @param msg
     * @param isLoadHis
     */
    private void handleMsg(V2TIMMessage msg, boolean isLoadHis) {
        if (msg.getStatus() == V2TIM_MSG_STATUS_HAS_DELETED) {
            /*被删除或失败的消息不展示*/
            return;
        }

        if (msg.getStatus() == V2TIM_MSG_STATUS_LOCAL_REVOKED) {
            /*被撤回的消息处理*/
            V2TIMElem elem = msg.getTextElem();
            if (elem != null) {
                handleElem(msg, elem, isLoadHis);
            }
            if (msg.getElemType() != V2TIM_ELEM_TYPE_NONE && elem != null) {
                /*判断是否还有下一个元素 因为存在一个消息多个元素的情况，但这种情况极少出现，除了特别奇葩的设定*/
                while (elem.getNextElem() != null) {
                    handleElem(msg, elem.getNextElem(), isLoadHis);
                }
            }
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
        if (elem instanceof V2TIMTextElem) {
            V2TIMTextElem textElem = (V2TIMTextElem) elem;
            TextElemVO textElemVO = new TextElemVO(msg, textElem);
            if (isLoadHis) {
                mItems.add(0, textElemVO);
            } else {
                mItems.add(textElemVO);
            }
        } else if (elem instanceof V2TIMImageElem) {

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
                list.add(new MsgPopAction("重发", () -> {
                    Toast.makeText(Test01Activity.this, "重发", Toast.LENGTH_SHORT).show();
                    handleMsgRepeat(position, timMessage);
                }));
                list.add(new MsgPopAction("删除", () -> {
                    mItems.remove(position);
                    mAdapter.notifyDataSetChanged();
                }));
            }
            if (timMessage.getTimMessage().isSelf()) {
                list.add(new MsgPopAction("撤回", () -> {

                }));
            }
            IMPopupView imPopupView = new IMPopupView();
            imPopupView.showPopMsgAction(view, list);
        }
    };


    private void handleMsgRevoke(int position, BaseMsgElem msgElem) {
        V2TIMManager.getMessageManager().revokeMessage(msgElem.getTimMessage(), new V2TIMCallback() {
            @Override
            public void onError(int code, String desc) {
                // 撤回消息失败
                Toast.makeText(Test01Activity.this, "撤回失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                // 撤回消息成功
                for (int i = 0; i < mItems.size(); i++) {
                    BaseMsgElem msgElem1 = (BaseMsgElem) mItems.get(i);
                    if (msgElem1.getTimMessage().getMsgID().equals(msgElem.getTimMessage().getMsgID())) {
                        mItems.add()
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
                        mBinding.tvStatus.setText("发送中" + i);
                        mBinding.progress.setVisibility(View.VISIBLE);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(int i, String s) {
                        mBinding.tvStatus.setText("发送失败" + i + s);
                        mBinding.progress.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onSuccess(V2TIMMessage v2TIMMessage) {
                        mBinding.tvStatus.setText("发送成功");
                        mBinding.progress.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }
                });

    }
}
