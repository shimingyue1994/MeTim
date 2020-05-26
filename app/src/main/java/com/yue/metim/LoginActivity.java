package com.yue.metim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMImageElem;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.tencent.imsdk.v2.V2TIMSDKListener;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.yue.metim.constants.Constants;
import com.yue.metim.constants.User;
import com.yue.metim.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mBinding.btnLogin.setOnClickListener(v -> {
            login(User.userId01, User.userSign01);
        });
        mBinding.btnLogin2.setOnClickListener(v -> {
            login(User.userId02, User.userSign02);
        });

        mBinding.btnLogout.setOnClickListener(v -> {
            logout();
        });
    }


    private void login(String userId, String userSign) {
        mBinding.progress.setVisibility(View.VISIBLE);
        mBinding.tvStatus.setText("登录中...");
        V2TIMManager.getInstance().login(userId, userSign, new V2TIMCallback() {
            @Override
            public void onError(int code, String message) {
                mBinding.progress.setVisibility(View.GONE);
                mBinding.tvStatus.setText("登录失败");
            }

            @Override
            public void onSuccess() {
                mBinding.progress.setVisibility(View.GONE);
                mBinding.tvStatus.setText("登录成功");
                Intent intent = new Intent(LoginActivity.this, Test01Activity.class);
                if (User.userId02.equals(userId))
                    intent.putExtra("identify", User.userId01);
                else
                    intent.putExtra("identify", User.userId02);
                startActivity(intent);
            }
        });
    }

    private void logout() {
        mBinding.progress.setVisibility(View.VISIBLE);
        mBinding.tvStatus.setText("退出中...");
        V2TIMManager.getInstance().logout(new V2TIMCallback() {
            @Override
            public void onError(int i, String s) {
                mBinding.progress.setVisibility(View.GONE);
                mBinding.tvStatus.setText("退出失败");
            }

            @Override
            public void onSuccess() {
                mBinding.progress.setVisibility(View.GONE);
                mBinding.tvStatus.setText("退出成功");
            }
        });
    }

}
