package com.yue.libtim.chat.itemholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yue.libtim.R;
import com.yue.libtim.chat.messagevo.BaseMsgElem;

/**
 * @author shimy
 * @create 2020/5/28 9:28
 * @desc 撤回消息
 */
public class RevokeElemHolder extends MessageEmptyHolder {

    public TextView tvTip;
    public TextView tvDelete;

    public RevokeElemHolder(@NonNull View itemView, RecyclerView.Adapter adapter) {
        super(itemView,adapter);
        tvDelete = itemView.findViewById(R.id.tv_revoke_delete);
        tvTip = itemView.findViewById(R.id.tv_revoke_tip);
    }

    public void showMessage(BaseMsgElem elem) {
        if (elem.getTimMessage().isSelf()) {
            tvTip.setText("自己撤回了一条消息");
        } else {
            tvTip.setText(elem.getTimMessage().getNickName() + "撤回了一条消息");
        }
    }

    @Override
    public int messageLayoutLine() {
        return R.layout.item_message_revoke;
    }
}
