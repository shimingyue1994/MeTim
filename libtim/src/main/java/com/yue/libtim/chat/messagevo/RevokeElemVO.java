package com.yue.libtim.chat.messagevo;

import com.tencent.imsdk.v2.V2TIMElem;
import com.tencent.imsdk.v2.V2TIMMessage;

/**
 * @author shimy
 * @create 2020/5/28 9:27
 * @desc 撤回消息
 */
public class RevokeElemVO extends BaseMsgElem {

    /**
     * @param timMessage 消息
     * @param timElem    这一条目的元素
     */
    public RevokeElemVO(V2TIMMessage timMessage, V2TIMElem timElem) {
        super(timMessage, timElem);
    }
}
