package com.yue.libtim.chat.messagevo;

import com.tencent.imsdk.v2.V2TIMImageElem;
import com.tencent.imsdk.v2.V2TIMMessage;

/**
 * @author shimy
 * @create 2020/5/28 10:54
 * @desc 图片消息
 */
public class ImageElemVO extends BaseMsgElem<String, V2TIMImageElem> {

    private int sendProgress = 0;//发送的进度

    /**
     * @param timMessage 消息
     * @param timElem    这一条目的元素
     */
    public ImageElemVO(V2TIMMessage timMessage, V2TIMImageElem timElem) {
        super(timMessage, timElem);
    }

    public int getSendProgress() {
        return sendProgress;
    }

    public void setSendProgress(int sendProgress) {
        this.sendProgress = sendProgress;
    }
}
