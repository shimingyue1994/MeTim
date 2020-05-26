package com.yue.metim;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.tencent.imsdk.v2.V2TIMSDKListener;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.yue.metim.constants.Constants;

public class AppApplication extends Application {
    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }


    private void init() {
// 1. 从 IM 控制台获取应用 SDKAppID，详情请参考 SDKAppID。
// 2. 初始化 config 对象
        V2TIMSDKConfig config = new V2TIMSDKConfig();
// 3. 指定 log 输出级别，详情请参考 SDKConfig。
        config.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_INFO);
// 4. 初始化 SDK 并设置 V2TIMSDKListener 的监听对象。
// initSDK 后 SDK 会自动连接网络，网络连接状态可以在 V2TIMSDKListener 回调里面监听。
        V2TIMManager.getInstance().initSDK(this, Constants.appid, config, new V2TIMSDKListener() {
            // 5. 监听 V2TIMSDKListener 回调
            @Override
            public void onConnecting() {
                // 正在连接到腾讯云服务器
                Toast.makeText(AppApplication.this, "正在连接到腾讯云服务器...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectSuccess() {
                // 已经成功连接到腾讯云服务器
                Toast.makeText(AppApplication.this, "成功连接到腾讯云服务器", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectFailed(int code, String error) {
                // 连接腾讯云服务器失败
                Toast.makeText(AppApplication.this, "连接到腾讯云服务器失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onKickedOffline() {
                super.onKickedOffline();
                /*当前用户被踢下线*/
                Toast.makeText(AppApplication.this, "被踢下线了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserSigExpired() {
                super.onUserSigExpired();
                /*登录票据已经过期*/
                Toast.makeText(AppApplication.this, "签名过期", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSelfInfoUpdated(V2TIMUserFullInfo info) {
                super.onSelfInfoUpdated(info);
                /* 当前用户的资料发生了更新*/
                Toast.makeText(AppApplication.this, "用户资料发生变更", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
