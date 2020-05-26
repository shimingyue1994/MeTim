package com.yue.libtim.chat.itemholder;

import android.text.TextUtils;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.TIMUserProfile;
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


    /*消息不同状态时显示的不同的view*/
    public ProgressBar mProgressSending;//消息发送中的progress
    public ImageView mIvMsgStatus;//消息状态

    public MessageContentHolder(@NonNull View itemView) {
        super(itemView);

        mIvLeftAvatar = itemView.findViewById(R.id.iv_left_user_icon);
        mLlRightAvatar = itemView.findViewById(R.id.ll_right_user_icon);

        mLlLeftAvatar = itemView.findViewById(R.id.ll_left_user_icon);
        mIvRightAvatar = itemView.findViewById(R.id.iv_right_user_icon);

        mTvUserName = itemView.findViewById(R.id.tv_user_name);

        mIvMsgStatus = itemView.findViewById(R.id.iv_message_status);
        mProgressSending = itemView.findViewById(R.id.pb_message_sending);


        mLlMsg = itemView.findViewById(R.id.ll_msg);
        mFlMsgContent = itemView.findViewById(R.id.fl_msg_content);
        /*将消息内容布局设置*/
        View.inflate(itemView.getContext(), messageContentView(), mFlMsgContent);
    }


    /**
     * 显示view 头像 名字等view
     */
    public void showAvatars(final BaseMsgElem message, MultiTypeAdapter adapter, final int position, final IMessageItemClick messageItemClick) {
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
