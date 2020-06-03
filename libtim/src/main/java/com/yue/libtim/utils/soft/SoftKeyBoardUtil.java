package com.yue.libtim.utils.soft;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.yue.libtim.TUIKit;
import com.yue.libtim.TUIKitConstants;


public class SoftKeyBoardUtil {

    private static int softKeyBoardHeight;
    private static SharedPreferences preferences = TUIKit.getAppContext().getSharedPreferences(TUIKitConstants.UI_PARAMS, Context.MODE_PRIVATE);
    private static InputMethodManager imm = (InputMethodManager) TUIKit.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);

    public static void putHeight(int softHeight) {
        preferences.edit().putInt(TUIKitConstants.SOFT_KEY_BOARD_HEIGHT, softHeight).apply();
    }

    public static int getSaveHeight() {
        int height = preferences.getInt(TUIKitConstants.SOFT_KEY_BOARD_HEIGHT, 0);
        return height;
    }

    public static int getSoftKeyBoardHeight() {
        if (softKeyBoardHeight != 0)
            return softKeyBoardHeight;
        softKeyBoardHeight = getSaveHeight();
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


    public static void hideKeyBoard(EditText editor) {

        imm.hideSoftInputFromWindow(editor.getWindowToken(), 0);
    }

    public static void hideKeyBoard(IBinder token) {

        imm.hideSoftInputFromWindow(token, 0);
    }
}

