package com.yue.metim.weight.msgpop;

/**
 * @author shimy
 * @create 2020/5/26 17:11
 * @desc 消息操作
 */
public class MsgPopAction {


    private String actionName;

    public MsgPopAction(String actionName) {
        this.actionName = actionName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}
