package com.yue.libtim.chat.interfaces;

import android.view.View;

import com.yue.libtim.chat.messagevo.BaseMsgElem;


/**
 * @author shimy
 * @create 2019/8/23 17:34
 * @desc 消息item点击事件
 */
public interface IMessageItemClick {

    /**
     * 头像点击
     *
     * @param view
     * @param position
     * @param timMessage
     */
    void onClickAvatar(View view, int position, BaseMsgElem timMessage);

    /**
     * 头像长按
     *
     * @param view
     * @param position
     * @param timMessage
     */
    void onLongClickAvatar(View view, int position, BaseMsgElem timMessage);


    /**
     * 气泡点击
     *
     * @param view
     * @param position
     * @param timMessage
     */
    void onClickBubble(View view, int position, BaseMsgElem timMessage);

    /**
     * 气泡长按
     *
     * @param view
     * @param position
     * @param timMessage
     */
    void onLongClickBubble(View view, int position, BaseMsgElem timMessage);
}
