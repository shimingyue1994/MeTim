package com.yue.libtim.chat.messagevo;

import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMVideoElem;

/**
 * @author shimy
 * @create 2020/5/29 17:02
 * @desc 视频元素
 */
public class VideoElemVO extends BaseMsgElem<String, V2TIMVideoElem> {
    /**
     * @param timMessage 消息
     * @param timElem    这一条目的元素
     */
    public VideoElemVO(V2TIMMessage timMessage, V2TIMVideoElem timElem) {
        super(timMessage, timElem);
    }
}
