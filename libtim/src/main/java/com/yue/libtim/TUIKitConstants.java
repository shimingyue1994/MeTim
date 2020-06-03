package com.yue.libtim;

import android.os.Environment;

public class TUIKitConstants {
    public static String SD_CARD_PATH = TUIKit.getAppContext().getExternalFilesDir("imchat").getAbsolutePath();
    public static String APP_DIR = SD_CARD_PATH;


    /*录音下载*/
    public static String MESSAGE_RECORD_DIR = APP_DIR + "/record/";
    /*视频消息下载存放路径*/
    public static String MESSAGE_VIDEO_DIR = APP_DIR + "message/video/";
    /*消息 图片存放路径 图片消息和视频快照*/
    public static String MESSAGE_IMAGE_DIR = APP_DIR + "/message/image/";
    public static String MESSAGE_VIDEO_SNAPSHOT = APP_DIR + "/message/snapshot/";
    /*多媒体*/
    public static String MESSAGE_MEDIA_DIR = APP_DIR + "/media";
    /*文件下载路径*/
    public static String MESSAGE_FILE_DIR = APP_DIR + "/file/";
    /*crash 日志存放路径*/
    public static String CRASH_LOG_DIR = APP_DIR + "/crash/";


    public static String UI_PARAMS = "ilive_ui_params";
    public static String SOFT_KEY_BOARD_HEIGHT = "soft_key_board_height";

}
