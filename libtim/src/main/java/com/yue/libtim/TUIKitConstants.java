package com.yue.libtim;

import android.os.Environment;

public class TUIKitConstants {
    public static String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String APP_DIR = SD_CARD_PATH + "/" + TUIKit.getAppContext().getPackageName();


    /*录音*/
    public static String RECORD_DIR = APP_DIR + "/record/";
    /*录音下载*/
    public static String RECORD_DOWNLOAD_DIR = APP_DIR + "/record/download/";
    /*视频下载存放路径*/
    public static String VIDEO_DOWNLOAD_DIR = APP_DIR + "/video/download/";
    /*图片路径*/
    public static String IMAGE_BASE_DIR = APP_DIR + "/image/";
    /*图片下载存放路径*/
    public static String IMAGE_DOWNLOAD_DIR = IMAGE_BASE_DIR + "download/";
    /*多媒体*/
    public static String MEDIA_DIR = APP_DIR + "/media";
    /*文件下载路径*/
    public static String FILE_DOWNLOAD_DIR = APP_DIR + "/file/download/";
    /*crash 日志存放路径*/
    public static String CRASH_LOG_DIR = APP_DIR + "/crash/";

}
