package com.yue.libtim.chat.itembinder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.yue.libtim.chat.interfaces.IMessageItemClick;
import com.yue.libtim.chat.itemholder.ImageElemHolder;
import com.yue.libtim.chat.itemholder.MessageEmptyHolder;
import com.yue.libtim.chat.itemholder.SoundElemHolder;
import com.yue.libtim.chat.messagevo.SoundElemVO;

import me.drakeet.multitype.ItemViewBinder;

/**
 * @author shimy
 * @create 2020/6/1 16:16
 * @desc 声音
 */
public class SoundElemBinder extends ItemViewBinder<SoundElemVO, SoundElemHolder> {
    private IMessageItemClick itemClick;


    public void setItemClick(IMessageItemClick itemClick) {
        this.itemClick = itemClick;
    }


    @NonNull
    @Override
    protected SoundElemHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        SoundElemHolder holder = new SoundElemHolder(MessageEmptyHolder.getView(inflater, parent), getAdapter());
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull SoundElemHolder holder, @NonNull SoundElemVO item) {
        holder.showChatTime(item, getAdapter(), holder.getLayoutPosition());
        holder.showAvatars(item, holder.getLayoutPosition(), itemClick);
        holder.showMessage(item);
    }
}
