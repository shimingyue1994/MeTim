package com.yue.libtim.chat.itembinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.yue.libtim.chat.itemholder.MessageEmptyHolder;
import com.yue.libtim.chat.itemholder.RevokeElemHolder;
import com.yue.libtim.chat.messagevo.RevokeElemVO;

import me.drakeet.multitype.ItemViewBinder;

/**
 * @author shimy
 * @create 2020/5/28 9:29
 * @desc 撤回消息
 */
public class RevokeElemBinder extends ItemViewBinder<RevokeElemVO, RevokeElemHolder> {

    private OnRevokeListener onRevokeListener;


    public void setOnRevokeListener(OnRevokeListener onRevokeListener) {
        this.onRevokeListener = onRevokeListener;
    }

    @NonNull
    @Override
    protected RevokeElemHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        RevokeElemHolder holder = new RevokeElemHolder(MessageEmptyHolder.getView(inflater, parent),getAdapter());
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull final RevokeElemHolder holder, @NonNull final RevokeElemVO item) {
        holder.showChatTime(item, getAdapter(), holder.getLayoutPosition());
        holder.showMessage(item);
        if (onRevokeListener != null) {
            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRevokeListener.onDelete(holder.getLayoutPosition(), item);
                }
            });
        }
    }

    public interface OnRevokeListener {
        void onDelete(int position, RevokeElemVO item);
    }
}
