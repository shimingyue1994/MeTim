package com.yue.libtim.utils.soft;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.yue.libtim.TUIKit;
import com.yue.libtim.TUIKitConstants;
import com.yue.libtim.utils.ScreenUtil;


public class SoftKeyBoardUtil {

    private static int softKeyBoardHeight;
    private static SharedPreferences preferences = TUIKit.getAppContext().getSharedPreferences(TUIKitConstants.UI_PARAMS, Context.MODE_PRIVATE);
    private static InputMethodManager imm = (InputMethodManager) TUIKit.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);



    public static int getSoftKeyBoardHeight() {
        if (softKeyBoardHeight != 0)
            return softKeyBoardHeight;
        softKeyBoardHeight = preferences.getInt(TUIKitConstants.SOFT_KEY_BOARD_HEIGHT, 0);
        if (softKeyBoardHeight == 0) {
            int height = getScreenSize()[1];
            return height * 2 / 5;
        }
        return softKeyBoardHeight;
    }

    public static int[] getScreenSize() {
        int size[] = new int[2];
        DisplayMetrics dm = TUIKit.getAppContext().getResources().getDisplayMetrics();
        size[0] = dm.widthPixels;
        size[1] = dm.heightPixels;
        return size;
    }
}

