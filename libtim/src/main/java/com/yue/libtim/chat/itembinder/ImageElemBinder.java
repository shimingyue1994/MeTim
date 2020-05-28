package com.yue.libtim.chat.itembinder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.yue.libtim.R;
import com.yue.libtim.chat.itemholder.ImageElemHolder;
import com.yue.libtim.chat.itemholder.MessageEmptyHolder;
import com.yue.libtim.chat.messagevo.ImageElemVO;

import me.drakeet.multitype.ItemViewBinder;

/**
 * @author shimy
 * @create 2020/5/28 14:09
 * @desc 图片消息
 */
public class ImageElemBinder extends ItemViewBinder<ImageElemVO, ImageElemHolder> {
    @NonNull
    @Override
    protected ImageElemHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ImageElemHolder holder = new ImageElemHolder(MessageEmptyHolder.getView(inflater, parent));
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ImageElemHolder holder, @NonNull ImageElemVO item) {
        holder.showChatTime(item, getAdapter(), holder.getLayoutPosition());
        holder.showAvatars(item, getAdapter(), holder.getLayoutPosition(), null);
        holder.showMessage(item);
    }
}
