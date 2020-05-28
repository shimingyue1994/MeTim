package com.yue.libtim.utils;

public class FileDownloadUtils {

    public static String byteHandle(long byteLength) {
        String text = "0kb";
        double byteLengthCopy = byteLength;
        if (byteLengthCopy < 0)
            return byteLength + "";
        if (byteLengthCopy < 1024 * 1024) {
            text = String.format("%.2fkb", byteLengthCopy / 1024.00);
        } else {
            text = String.format("%.2fmb", byteLengthCopy / (1024.00 * 1024.00));
        }
        return text;
    }

    public static String slashStartRemove(String str) {
        String strCopy = str;
        if (strCopy.startsWith("/"))
            strCopy = strCopy.substring(1, strCopy.length());
        return strCopy;
    }

    public static String slashEndRemove(String str) {
        String strCopy = str;
        if (strCopy.endsWith("/"))
            strCopy = strCopy.substring(0, (strCopy.length() - 1));
        return strCopy;
    }
}
