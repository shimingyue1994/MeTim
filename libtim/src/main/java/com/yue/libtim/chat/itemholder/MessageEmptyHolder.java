package com.yue.libtim.chat.itemholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.yue.libtim.R;
import com.yue.libtim.chat.messagevo.BaseMsgElem;
import com.yue.libtim.utils.DateTimeUtil;

import java.util.Date;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author shimy
 * @create 2020/5/25 10:38
 * @desc 只有一个时间的布局
 */
public abstract class MessageEmptyHolder extends RecyclerView.ViewHolder {
    protected RecyclerView.Adapter  mAdapter;

    public TextView mChatTime;
    public FrameLayout mFlMsgEmpty;

    public MessageEmptyHolder(@NonNull View itemView, RecyclerView.Adapter adapter) {
        super(itemView);
        mAdapter = adapter;
        mChatTime = itemView.findViewById(R.id.tv_chat_time);
        mFlMsgEmpty = itemView.findViewById(R.id.fl_msg_empty);
        /*将消息内容布局设置*/
        View.inflate(itemView.getContext(), messageLayoutLine(), mFlMsgEmpty);
    }

    /**
     * 返回承载消息显示的布局view
     *
     * @return
     */
    public abstract int messageLayoutLine();


    /**
     * 显示时间通用
     *
     * @param msg      当前消息
     * @param adapter  上一条消息 用于计算是否显示时间
     * @param position
     */
    public void showChatTime(final BaseMsgElem msg, MultiTypeAdapter adapter, final int position) {

        //// 时间线设置
        final V2TIMMessage timMsg = msg.getTimMessage();

        if (position >= 1 && adapter != null) {
            BaseMsgElem last = (BaseMsgElem) adapter.getItems().get(position - 1);
            if (last.getTimMessage() != null) {
                /*5分钟设置*/
                if (timMsg.getTimestamp() - last.getTimMessage().getTimestamp() >= 5 * 60) {
                    mChatTime.setVisibility(View.VISIBLE);
                    mChatTime.setText(DateTimeUtil.getTimeFormatText(new Date(timMsg.getTimestamp() * 1000)));
                } else {
                    mChatTime.setVisibility(View.GONE);
                }
            }
        } else {
            mChatTime.setVisibility(View.VISIBLE);
            mChatTime.setText(DateTimeUtil.getTimeFormatText(new Date(timMsg.getTimestamp() * 1000)));
        }
    }

    /**
     * holder 初始化时加载的view
     *
     * @param inflater
     * @param parent
     * @return
     */
    public static View getView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_message_empty, parent, false);
        return view;
    }
}
