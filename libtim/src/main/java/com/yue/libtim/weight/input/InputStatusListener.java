package com.yue.libtim.weight.input;

/**
 * @author shimy
 * @create 2020/6/5 10:45
 * @desc 输入状态发生改变  语音 控件
 */
public interface InputStatusListener {
    int RECORD_START = 1;
    int RECORD_STOP = 2;
    int RECORD_CANCELING = 3;//取消中 手指移动并未松开
    int RECORD_CANCEL = 4;
    int RECORD_TOO_SHORT = 5;
    int RECORD_FAILED = 6;
    int RECORD_COMPLETE = 7;

    /**
     * 输入控件发生改变 主要是输入法 表情 更多布局的弹出
     *
     * @param open 是否是展开的
     */
    void onInputAreaChanged(boolean open);

    /*语音状态发生改变*/
    void onVoiceStatusChanged(int status);
}
