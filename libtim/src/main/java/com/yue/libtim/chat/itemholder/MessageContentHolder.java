package com.yue.libtim.chat.itemholder;

import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.yue.libtim.R;
import com.yue.libtim.chat.interfaces.IMessageItemClick;
import com.yue.libtim.chat.messagevo.BaseMsgElem;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author shimy
 * @create 2020/5/25 13:19
 * @desc 有头像的 holder
 */
public abstract class MessageContentHolder extends MessageEmptyHolder {

    public ImageView mIvLeftAvatar;
    public LinearLayout mLlRightAvatar;

    public ImageView mIvRightAvatar;
    public LinearLayout mLlLeftAvatar;
    public TextView mTvUserName;//消息发送者的名字

    public LinearLayout mLlMsg;//消息布局
    public FrameLayout mFlMsgContent;//消息内容布局

    private TextView mTvReadStatusSelf;//自己的消息的已读状态


    /*消息不同状态时显示的不同的view*/
    public ProgressBar mProgressSending;//消息发送中的progress
    public ImageView mIvMsgStatus;//消息状态

    public MessageContentHolder(@NonNull View itemView, RecyclerView.Adapter adapter) {
        super(itemView, adapter);

        mIvLeftAvatar = itemView.findViewById(R.id.iv_left_user_icon);
        mLlRightAvatar = itemView.findViewById(R.id.ll_right_user_icon);

        mLlLeftAvatar = itemView.findViewById(R.id.ll_left_user_icon);
        mIvRightAvatar = itemView.findViewById(R.id.iv_right_user_icon);

        mTvUserName = itemView.findViewById(R.id.tv_user_name);

        mIvMsgStatus = itemView.findViewById(R.id.iv_message_status);
        mProgressSending = itemView.findViewById(R.id.pb_message_sending);


        mLlMsg = itemView.findViewById(R.id.ll_msg);
        mFlMsgContent = itemView.findViewById(R.id.fl_msg_content);

        mTvReadStatusSelf = itemView.findViewById(R.id.tv_read_status_self);
        /*将消息内容布局设置*/
        View.inflate(itemView.getContext(), messageContentView(), mFlMsgContent);
    }


    /**
     * 显示view 头像 名字等view
     */
    public void showAvatars(final BaseMsgElem message, final int position, final IMessageItemClick messageItemClick) {
        V2TIMMessage msg = message.getTimMessage();

        //// 头像设置
        if (msg.isSelf()) {
            mLlLeftAvatar.setVisibility(View.GONE);
            mLlRightAvatar.setVisibility(View.VISIBLE);
        } else {
            mLlLeftAvatar.setVisibility(View.VISIBLE);
            mLlRightAvatar.setVisibility(View.GONE);
        }
        RequestOptions imgOptions = new RequestOptions();
        /*设置图片为圆形图片*/
        imgOptions.circleCrop();
        if (!msg.isSelf()) {//不是自己的消息
            if (!TextUtils.isEmpty(msg.getGroupID())) {//群组消息 显示username
                String groupCard = "";
                if (!TextUtils.isEmpty(msg.getNameCard())) {
                    groupCard = msg.getNameCard();
                }
                if (TextUtils.isEmpty(groupCard)) {
                    mTvUserName.setText(!TextUtils.isEmpty(msg.getNickName()) ? msg.getNickName() : msg.getSender());
                } else {
                    mTvUserName.setText(groupCard);
                }
                mTvUserName.setVisibility(View.VISIBLE);
            } else {//不是群组消息 不显示username
                mTvUserName.setVisibility(View.GONE);
            }
        } else {//是自己
            mTvUserName.setVisibility(View.GONE);
        }
        /*头像设置*/
        Glide.with(itemView.getContext())
                .load(msg.getFaceUrl())
                .apply(imgOptions)
                .placeholder(R.drawable.avatar_male_44dp)
                .into(mIvLeftAvatar);

        //// 聊天气泡设置
        if (msg.isSelf()) {
            mFlMsgContent.setBackgroundResource(R.drawable.ic_bubble_right);
        } else {
            mFlMsgContent.setBackgroundResource(R.drawable.ic_bubble_left);
        }
        //// 左右边的消息需要调整一下内容的位置
        if (msg.isSelf()) {
            mLlMsg.removeView(mFlMsgContent);
            mLlMsg.addView(mFlMsgContent);
        } else {
            mLlMsg.removeView(mFlMsgContent);
            mLlMsg.addView(mFlMsgContent, 0);
        }

        /*消息状态设置*/
        if (msg.getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_SENDING) {
            mProgressSending.setVisibility(View.VISIBLE);
        } else {
            mProgressSending.setVisibility(View.GONE);
        }

        //// 发送状态的设置
        if (msg.getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_SEND_FAIL) {
            mIvMsgStatus.setVisibility(View.VISIBLE);
        } else {
            mIvMsgStatus.setVisibility(View.GONE);
        }


        if (msg.isSelf()) {
            if (msg.isRead() || message.isLocalRead()) {
                mTvReadStatusSelf.setText("已读");
                mTvReadStatusSelf.setTextColor(mTvReadStatusSelf.getContext().getResources().getColor(android.R.color.darker_gray));
            } else {
                mTvReadStatusSelf.setText("未读");
                mTvReadStatusSelf.setTextColor(0xff2e87e4);
            }
        } else {
            if (message.isLocalRead() || msg.isRead()) {

            } else {
                //将来自 haven 的消息均标记为已读
                V2TIMManager.getMessageManager().markC2CMessageAsRead(msg.getUserID(), new V2TIMCallback() {
                    @Override
                    public void onError(int code, String desc) {
                        // 设置消息已读失败
                        Log.i("shimyHz", "已读标记失败：" + code + desc);
                    }

                    @Override
                    public void onSuccess() {
                        // 设置消息已读成功
                        Log.i("shimyHz", "已读标记成功");
                        message.setLocalRead(true);
                    }
                });
            }
        }
        mIvMsgStatus.setOnClickListener(v -> {
            if (messageItemClick != null) {
                messageItemClick.onClickFailStatus(v, position, message);
            }
        });
        mFlMsgContent.setOnClickListener(new View.OnClickListener() {
            int clickCount = 0;

            @Override
            public void onClick(final View v) {
                if (messageItemClick != null) {
                    clickCount++;
                    if (clickCount == 1)
                        mFlMsgContent.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (clickCount >= 2)
                                    messageItemClick.onDoubleClickBubble(v, position, message);
                                else
                                    messageItemClick.onClickBubble(v, position, message);
                                clickCount = 0;
                            }
                        }, 210);
                }
            }
        });

        mFlMsgContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (messageItemClick != null)
                    messageItemClick.onLongClickBubble(v, position, message);
                return true;
            }
        });

        mIvLeftAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageItemClick != null)
                    messageItemClick.onClickAvatar(v, position, message);
            }
        });
        mIvRightAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageItemClick != null)
                    messageItemClick.onClickAvatar(v, position, message);
            }
        });

        mIvLeftAvatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (messageItemClick != null)
                    messageItemClick.onLongClickAvatar(v, position, message);
                return true;
            }
        });

        mIvRightAvatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (messageItemClick != null)
                    messageItemClick.onLongClickAvatar(v, position, message);
                return true;
            }
        });


    }


    /**
     * 消息的整体布局
     *
     * @return
     */
    @Override
    public int messageLayoutLine() {
        return R.layout.item_message_content;
    }


    /**
     * 返回承载消息的内容的布局
     *
     * @return
     */
    public abstract int messageContentView();

}
