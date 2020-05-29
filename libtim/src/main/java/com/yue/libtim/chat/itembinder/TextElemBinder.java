package com.yue.libtim.chat.itembinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.v2.V2TIMTextElem;
import com.yue.libtim.chat.interfaces.IMessageItemClick;
import com.yue.libtim.chat.itemholder.MessageContentHolder;
import com.yue.libtim.chat.itemholder.TextElemHolder;
import com.yue.libtim.chat.messagevo.BaseMsgElem;
import com.yue.libtim.chat.messagevo.TextElemVO;

import me.drakeet.multitype.ItemViewBinder;

/**
 * @author shimy
 * @create 2020/5/25 15:43
 * @desc 文本消息元素展示
 */
public class TextElemBinder extends ItemViewBinder<TextElemVO, TextElemHolder> {
    private IMessageItemClick itemClick;


    public void setItemClick(IMessageItemClick itemClick) {
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    protected TextElemHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        TextElemHolder holder = new TextElemHolder(MessageContentHolder.getView(inflater, parent), getAdapter());
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull final TextElemHolder holder, @NonNull final TextElemVO item) {
        holder.showChatTime(item, getAdapter(), holder.getLayoutPosition());
        holder.showAvatars(item, holder.getLayoutPosition(), itemClick);
        V2TIMTextElem elem = (V2TIMTextElem) item.getTimElem();
        holder.showMessage(elem);
    }
}
