package com.yue.libtim.chat.itembinder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    private RecyclerView recyclerView;

    public ImageElemBinder(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    protected ImageElemHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ImageElemHolder holder = new ImageElemHolder(MessageEmptyHolder.getView(inflater, parent), getAdapter());
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ImageElemHolder holder, @NonNull ImageElemVO item) {
        holder.showChatTime(item, getAdapter(), holder.getLayoutPosition());
        holder.showAvatars(item, holder.getLayoutPosition(), null);
        Log.i("shimy", "ItemViewBinder又tm执行了");
        holder.showMessage(item,recyclerView);
    }
}
