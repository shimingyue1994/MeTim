package com.yue.libtim;

import android.content.Context;

public class TUIKit {

    private static Context sAppContext;

    public static void init(Context context) {
        sAppContext = context;
    }

    public static Context getAppContext() {
        return sAppContext;
    }
}
