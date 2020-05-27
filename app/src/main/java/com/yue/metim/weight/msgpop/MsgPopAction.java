package com.yue.metim.weight.msgpop;

import android.view.View;

import com.yue.libtim.chat.messagevo.BaseMsgElem;

/**
 * @author shimy
 * @create 2020/5/26 17:11
 * @desc 消息操作
 */
public class MsgPopAction {


    private String actionName;
    private OnMsgActionListener onMsgActionListener;

    public MsgPopAction(String actionName,  OnMsgActionListener onMsgActionListener) {
        this.actionName = actionName;
        this.onMsgActionListener = onMsgActionListener;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }



    public OnMsgActionListener getOnMsgActionListener() {
        return onMsgActionListener;
    }

    public void setOnMsgActionListener(OnMsgActionListener onMsgActionListener) {
        this.onMsgActionListener = onMsgActionListener;
    }

    public interface OnMsgActionListener {
        void onActionClick();
    }
}
