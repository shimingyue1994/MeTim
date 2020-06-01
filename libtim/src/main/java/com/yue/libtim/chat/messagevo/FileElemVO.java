package com.yue.libtim.chat.messagevo;

import com.tencent.imsdk.v2.V2TIMFileElem;
import com.tencent.imsdk.v2.V2TIMMessage;

/**
 * @author shimy
 * @create 2020/6/1 17:20
 * @desc 文件消息
 */
public class FileElemVO extends BaseMsgElem<String, V2TIMFileElem> {

    private int sendProgress;//发送进度

    /**
     * @param timMessage 消息
     * @param timElem    这一条目的元素
     */
    public FileElemVO(V2TIMMessage timMessage, V2TIMFileElem timElem) {
        super(timMessage, timElem);
    }

    public int getSendProgress() {
        return sendProgress;
    }

    public void setSendProgress(int sendProgress) {
        this.sendProgress = sendProgress;
    }
}
