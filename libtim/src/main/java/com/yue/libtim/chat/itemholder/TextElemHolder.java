package com.yue.libtim.chat.itemholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.v2.V2TIMTextElem;
import com.yue.libtim.R;

/**
 * @author shimy
 * @create 2020/5/25 14:54
 * @desc 文本消息
 */
public class TextElemHolder extends MessageContentHolder {

    public TextView mTvMessage;

    public TextElemHolder(@NonNull View itemView) {
        super(itemView);
        mTvMessage = itemView.findViewById(R.id.tv_msg_text);
    }

    public void showMessage(V2TIMTextElem timTextElem) {
        mTvMessage.setText(timTextElem.getText());
    }

    @Override
    public int messageContentView() {
        return R.layout.item_message_text;
    }
}
