package com.yue.metim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yue.libtim.utils.soft.SoftKeyBoardListener;
import com.yue.libtim.utils.soft.SoftKeyBoardUtil;
import com.yue.metim.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shimy
 * @create 2020/6/19 14:23
 * @desc 注意 在选择图片更新消息列表是会有视图显示大小bug,导致滑动不到底部，请在选择图片返回时计算好图片大小 然后再mItems.add(ImageMessageVo) 然后再更新recycler，
 * 以防止scrollToEnd 于图片item加载前执行
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private final static int REQ_PERMISSION_CODE = 0x1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        checkPermission();

        /*在登录的时候就应该把高度初始化好*/
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                Toast.makeText(MainActivity.this, "键盘显示 高度" + height, Toast.LENGTH_SHORT).show();
                if (height > 0) {
                    SoftKeyBoardUtil.putHeight(height);
                }
            }

            @Override
            public void keyBoardHide(int height) {
                Toast.makeText(MainActivity.this, "键盘隐藏 高度" + height, Toast.LENGTH_SHORT).show();
                if (height > 0) {
                    SoftKeyBoardUtil.putHeight(height);
                }
            }
        });

        mBinding.btnTest.setOnClickListener((v) -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        mBinding.btnTest2.setOnClickListener((v) -> {
            Intent intent = new Intent(this, Test2Activity.class);
            startActivity(intent);
        });

        mBinding.btnTest3.setOnClickListener(v -> {
            Intent intent = new Intent(this, Test3Activity.class);
            startActivity(intent);
        });

        mBinding.btnTest4.setOnClickListener(v -> {
            Intent intent = new Intent(this, Test4Activity.class);
            startActivity(intent);
        });

        mBinding.btnEnd.setOnClickListener(v -> {
            Intent intent = new Intent(this, Test5Activity.class);
            startActivity(intent);
        });

    }


    //////////////////////////////////    动态权限申请   ////////////////////////////////////////

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        (String[]) permissions.toArray(new String[0]),
                        REQ_PERMISSION_CODE);
                return false;
            }
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION_CODE:
                for (int ret : grantResults) {
                    if (PackageManager.PERMISSION_GRANTED != ret) {
                        Toast.makeText(this, "用户没有允许需要的权限，使用可能会受到限制！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }
}
