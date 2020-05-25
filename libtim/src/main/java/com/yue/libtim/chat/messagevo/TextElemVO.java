package com.yue.libtim.chat.messagevo;

import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMTextElem;

/**
 * @author shimy
 * @create 2020/5/25 15:36
 * @desc 文本消息面
 */
public class TextElemVO extends BaseMsgElem<String> {

    public TextElemVO(V2TIMMessage timMessage, V2TIMTextElem elem) {
        super(timMessage, elem);
    }
}
