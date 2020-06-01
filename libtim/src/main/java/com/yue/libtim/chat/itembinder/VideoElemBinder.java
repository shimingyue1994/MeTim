package com.yue.libtim.chat.itembinder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.yue.libtim.chat.itemholder.MessageEmptyHolder;
import com.yue.libtim.chat.itemholder.VideoElemHolder;
import com.yue.libtim.chat.messagevo.VideoElemVO;

import me.drakeet.multitype.ItemViewBinder;

/**
 * @author shimy
 * @create 2020/6/1 10:31
 * @desc 视频消息
 */
public class VideoElemBinder extends ItemViewBinder<VideoElemVO, VideoElemHolder> {

    @NonNull
    @Override
    protected VideoElemHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        VideoElemHolder holder = new VideoElemHolder(MessageEmptyHolder.getView(inflater, parent), getAdapter());
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull VideoElemHolder holder, @NonNull VideoElemVO item) {
        holder.showChatTime(item, getAdapter(), holder.getLayoutPosition());
        holder.showAvatars(item, holder.getLayoutPosition(), null);
        holder.showMessage(item);
    }
}
