package com.yue.metim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.v2.V2TIMAdvancedMsgListener;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMMessageManager;
import com.tencent.imsdk.v2.V2TIMMessageReceipt;
import com.tencent.imsdk.v2.V2TIMSendCallback;
import com.tencent.imsdk.v2.V2TIMSimpleMsgListener;
import com.tencent.imsdk.v2.V2TIMUserInfo;
import com.yue.metim.constants.User;
import com.yue.metim.databinding.ActivityTest01Binding;

import java.util.List;

import static com.tencent.imsdk.v2.V2TIMMessage.*;

public class Test01Activity extends AppCompatActivity {

    private ActivityTest01Binding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test01);
        V2TIMManager.getMessageManager().addAdvancedMsgListener(msgListener);
        V2TIMManager.getInstance().addSimpleMsgListener(simpleMsgListener);


        mBinding.btnSend.setOnClickListener(v -> {
           V2TIMManager.getInstance().sendC2CTextMessage(mBinding.etInput.getText().toString(), User.userId02, new V2TIMSendCallback<V2TIMMessage>() {
               @Override
               public void onProgress(int i) {

               }

               @Override
               public void onError(int i, String s) {

               }

               @Override
               public void onSuccess(V2TIMMessage v2TIMMessage) {

               }
           });
        });
    }


    private V2TIMSimpleMsgListener simpleMsgListener = new V2TIMSimpleMsgListener() {
        @Override
        public void onRecvC2CTextMessage(String msgID, V2TIMUserInfo sender, String text) {
            super.onRecvC2CTextMessage(msgID, sender, text);
            Log.i("shimy", text);
        }

        @Override
        public void onRecvC2CCustomMessage(String msgID, V2TIMUserInfo sender, byte[] customData) {
            super.onRecvC2CCustomMessage(msgID, sender, customData);
        }
    };

    private V2TIMAdvancedMsgListener msgListener = new V2TIMAdvancedMsgListener() {
        @Override
        public void onRecvNewMessage(V2TIMMessage msg) {
            super.onRecvNewMessage(msg);
            switch (msg.getElemType()) {
                case V2TIM_ELEM_TYPE_NONE:
                    /*空消息*/

                    break;
                case V2TIM_ELEM_TYPE_TEXT:
                    while (msg.getTextElem().getNextElem() != null) {
                        Log.i("shimy", msg.getTextElem().getText());
                    }
                    break;
                case V2TIM_ELEM_TYPE_CUSTOM:

                    break;
                case V2TIM_ELEM_TYPE_IMAGE:

                    break;
                case V2TIM_ELEM_TYPE_SOUND:

                    break;
                case V2TIM_ELEM_TYPE_VIDEO:

                    break;
                case V2TIM_ELEM_TYPE_FILE:

                    break;
                case V2TIM_ELEM_TYPE_LOCATION:

                    break;
                case V2TIM_ELEM_TYPE_GROUP_TIPS:

                    break;
                case V2TIM_ELEM_TYPE_FACE:

                    break;
            }

        }

        /*对方的已读回执*/
        @Override
        public void onRecvC2CReadReceipt(List<V2TIMMessageReceipt> receiptList) {
            super.onRecvC2CReadReceipt(receiptList);
        }

        @Override
        public void onRecvMessageRevoked(String msgID) {
            super.onRecvMessageRevoked(msgID);
        }
    };


}
