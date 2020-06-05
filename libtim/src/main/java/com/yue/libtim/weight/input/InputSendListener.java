package com.yue.libtim.weight.input;

/**
 * @author shimy
 * @create 2020/6/5 10:44
 * @desc 消息发送监听
 */
public interface InputSendListener {
    /*文本消息*/
    void sendText(String text);

    /*语音消息*/
    void sendVoice(String path, int duration);
}
