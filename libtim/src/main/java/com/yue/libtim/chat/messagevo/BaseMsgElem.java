package com.yue.libtim.chat.messagevo;

import androidx.annotation.NonNull;

import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.v2.V2TIMElem;
import com.tencent.imsdk.v2.V2TIMMessage;

/**
 * @author shimy
 * @create 2019/7/2 16:11
 * @desc 消息元素基类 因为存在一个消息中多个元素的问题（很少出现这种情况），所以一个元素一个列
 */
public class BaseMsgElem<T, E extends V2TIMElem> {

    /**
     * 消息内容下载中状态
     */
    public static final int MSG_STATUS_DOWNLOADING = 4;
    /**
     * 消息内容未下载状态
     */
    public static final int MSG_STATUS_UN_DOWNLOAD = 5;
    /**
     * 消息内容已下载状态
     */
    public static final int MSG_STATUS_DOWNLOADED = 6;

    @NonNull
    private V2TIMMessage timMessage;//原始消息

    private E timElem;//消息中的某一个元素，存在一个消息中多个元素的问题,但是正经人几乎不会这么干


    /*本地对消息的已读判断*/
    private boolean isLocalRead = false;
    /**
     * 额外数据
     * idea:可以在 {@link #parseTIMMessage}方法里解析消息后放置可以快速获取的数据，
     * 如解析出来的提示字符串可以放在这个字段，需要显示提示时直接获取即可
     */
    private T extra;

    /**
     * @param timMessage 消息
     * @param timElem    这一条目的元素
     */
    public BaseMsgElem(V2TIMMessage timMessage, E timElem) {
        this.timMessage = timMessage;
        this.timElem = timElem;
        parseTIMMessage(timMessage);
    }

    public V2TIMMessage getTimMessage() {
        return timMessage;
    }

    public void setTimMessage(V2TIMMessage timMessage) {
        this.timMessage = timMessage;
    }


    public T getExtra() {
        return extra;
    }

    public void setExtra(T extra) {
        this.extra = extra;
    }

    public E getTimElem() {
        return timElem;
    }

    public void setTimElem(E timElem) {
        this.timElem = timElem;
    }

    /**
     * 解析消息的方法 可以不实现方法体，空着
     */
    public void parseTIMMessage(V2TIMMessage timMessage) {
    }

    public boolean isLocalRead() {
        return isLocalRead;
    }

    public void setLocalRead(boolean localRead) {
        isLocalRead = localRead;
    }
}
