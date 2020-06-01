package com.yue.libtim.chat.messagevo;

import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMSoundElem;

/**
 * @author shimy
 * @create 2020/6/1 16:09
 * @desc 声音消息
 */
public class SoundElemVO extends BaseMsgElem<String, V2TIMSoundElem> {
    private int sendProgress;//发送进度

    /**
     * @param timMessage 消息
     * @param timElem    这一条目的元素
     */
    public SoundElemVO(V2TIMMessage timMessage, V2TIMSoundElem timElem) {
        super(timMessage, timElem);
    }

    public int getSendProgress() {
        return sendProgress;
    }

    public void setSendProgress(int sendProgress) {
        this.sendProgress = sendProgress;
    }
}
