package com.yue.libtim.chat.itembinder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.v2.V2TIMTextElem;
import com.yue.libtim.chat.itemholder.MessageContentHolder;
import com.yue.libtim.chat.itemholder.TextElemHolder;
import com.yue.libtim.chat.messagevo.TextElemVO;

import me.drakeet.multitype.ItemViewBinder;

/**
 * @author shimy
 * @create 2020/5/25 15:43
 * @desc 文本消息元素展示
 */
public class TextElemBinder extends ItemViewBinder<TextElemVO, TextElemHolder> {
    @NonNull
    @Override
    protected TextElemHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        TextElemHolder holder = new TextElemHolder(MessageContentHolder.getView(inflater,parent));
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull TextElemHolder holder, @NonNull TextElemVO item) {
        holder.showChatTime(item, getAdapter(), holder.getLayoutPosition());
        holder.showAvatars(item, getAdapter(), holder.getLayoutPosition(), null);
        V2TIMTextElem elem = (V2TIMTextElem) item.getTimElem();
        holder.showMessage(elem);
    }
}
