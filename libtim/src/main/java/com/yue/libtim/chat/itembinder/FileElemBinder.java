package com.yue.libtim.chat.itembinder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.yue.libtim.chat.interfaces.IMessageItemClick;
import com.yue.libtim.chat.itemholder.FileElemHolder;
import com.yue.libtim.chat.itemholder.MessageEmptyHolder;
import com.yue.libtim.chat.itemholder.SoundElemHolder;
import com.yue.libtim.chat.messagevo.FileElemVO;
import com.yue.libtim.chat.messagevo.SoundElemVO;

import me.drakeet.multitype.ItemViewBinder;

/**
 * @author shimy
 * @create 2020/6/1 16:16
 * @desc 声音
 */
public class FileElemBinder extends ItemViewBinder<FileElemVO, FileElemHolder> {
    private IMessageItemClick itemClick;


    public void setItemClick(IMessageItemClick itemClick) {
        this.itemClick = itemClick;
    }


    @NonNull
    @Override
    protected FileElemHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        FileElemHolder holder = new FileElemHolder(MessageEmptyHolder.getView(inflater, parent), getAdapter());
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull FileElemHolder holder, @NonNull FileElemVO item) {
        holder.showChatTime(item, getAdapter(), holder.getLayoutPosition());
        holder.showAvatars(item, holder.getLayoutPosition(), itemClick);
        holder.showMessage(item);
    }
}
