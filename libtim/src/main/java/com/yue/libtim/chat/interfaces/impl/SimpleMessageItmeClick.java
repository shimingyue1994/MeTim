package com.yue.libtim.chat.interfaces.impl;

import android.view.View;

import com.tencent.imsdk.v2.V2TIMElem;
import com.yue.libtim.chat.interfaces.IMessageItemClick;
import com.yue.libtim.chat.messagevo.BaseMsgElem;


/**
 * @author shimy
 * @create 2019/8/23 17:40
 * @desc 消息item点击事件
 */
public abstract class SimpleMessageItmeClick implements IMessageItemClick {

    @Override
    public void onClickAvatar(View view, int position, BaseMsgElem timMessage) {

    }

    @Override
    public void onLongClickAvatar(View view, int position, BaseMsgElem timMessage) {

    }

    @Override
    public void onClickBubble(View view, int position, BaseMsgElem timMessage) {

    }

    @Override
    public void onLongClickBubble(View view, int position, BaseMsgElem timMessage) {

    }
}
