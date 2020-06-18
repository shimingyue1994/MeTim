package com.yue.metim.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.yue.metim.constants.Constants;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;

import okio.BufferedSource;
import okio.Okio;

/**
 * @author：luck
 * @date：2019-11-08 19:25
 * @describe：Android Q(10)相关处理类
 */
public class AndroidQTransformUtils {


    /**
     * 解析Android Q版本下图片
     * #耗时操作需要放在子线程中操作
     *
     * @param ctx
     * @param url 图片路径
     * @return
     */
    public static String copyPathToAndroidQ(Context ctx, String url) {
        // 走普通的文件复制流程，拷贝至应用沙盒内来
        BufferedSource inBuffer = null;
        try {
            Uri uri = Uri.parse(url);
            String newPath = Constants.SANDBOX_COPY + File.separator + ("MM_" + UUID.randomUUID()).replace("-", "");
            File outFile = new File(newPath);
            if (outFile.exists()) {
                return newPath;
            }
            long startTime = System.currentTimeMillis();
            InputStream inputStream = ctx.getContentResolver().openInputStream(uri);
            long endTime = System.currentTimeMillis();
            long useTime = endTime - startTime;
            Log.i("shimyTime", "读取耗时" + useTime);

            long writeStartTime = System.currentTimeMillis();
            inBuffer = Okio.buffer(Okio.source(Objects.requireNonNull(inputStream)));
            boolean copyFileSuccess = PictureFileUtils.bufferCopy(inBuffer, outFile);
            if (copyFileSuccess) {
                return newPath;
            }
            long writeEndTime = System.currentTimeMillis();
            long writeUseTime = writeEndTime - writeStartTime;
            Log.i("shimyTime", "写入耗时" + writeUseTime);
            long allTime = useTime + writeUseTime;
            Log.i("shimyTime", "总耗时" + allTime);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inBuffer != null && inBuffer.isOpen()) {
                PictureFileUtils.close(inBuffer);
            }
        }
        return null;
    }

    public static String copyPathToAndroidQ2(Context ctx, String url) {


        // 走普通的文件复制流程，拷贝至应用沙盒内来
        BufferedSource inBuffer = null;
        try {
            Uri uri = Uri.parse(url);
            String newPath = Constants.SANDBOX_COPY + File.separator + ("MM_" + UUID.randomUUID()).replaceAll("-", "");
            File outFile = new File(newPath);
            if (outFile.exists()) {
                return newPath;
            }
            long startTime = System.currentTimeMillis();
            InputStream inputStream = ctx.getContentResolver().openInputStream(uri);
            long endTime = System.currentTimeMillis();
            long useTime = endTime - startTime;
            Log.i("shimyTime", "读取耗时" + useTime);

            long writeStartTime = System.currentTimeMillis();
            FileOutputStream fos = new FileOutputStream(outFile);
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = (inputStream.read(buf))) != -1) {
                fos.write(buf);
            }
            long writeEndTime = System.currentTimeMillis();
            long writeUseTime = writeEndTime - writeStartTime;
            Log.i("shimyTime", "写入耗时" + writeUseTime);
            long allTime = useTime + writeUseTime;
            Log.i("shimyTime", "总耗时" + allTime);
            inputStream.close();
            fos.close();
            return newPath;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    /**
     * 这是错误的方式 没使用getContentResolver获取不到文件
     * @param ctx
     * @param url
     * @param miniType
     * @return
     */
    public static String copyPathToAndroidQ3(Context ctx, String url, String miniType) {

        ContentResolver resolver = ctx.getContentResolver();
        Uri uri = Uri.parse(url);
        try {
            long startTime = System.currentTimeMillis();
            ParcelFileDescriptor descriptor = resolver.openFileDescriptor(uri, "r");
            long endTime = System.currentTimeMillis();
            long useTime = endTime - startTime;
            Log.i("shimyTime", "读取耗时：" + useTime);
            FileDescriptor fileDescriptor = descriptor.getFileDescriptor();
            FileInputStream inputStream = new FileInputStream(fileDescriptor);
            String newPath = "";
            if (!TextUtils.isEmpty(miniType))
                newPath = Constants.SANDBOX_COPY + File.separator + ("MM_" + UUID.randomUUID()).replaceAll("-", "") + "." + miniType;
            else
                newPath = Constants.SANDBOX_COPY + File.separator + ("MM_" + UUID.randomUUID()).replaceAll("-", "");
            File outFile = new File(newPath);

            long writeStartTime = System.currentTimeMillis();
            FileOutputStream fos = new FileOutputStream(outFile);
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = (inputStream.read(buf))) != -1) {
                fos.write(buf);
            }
            long writeEndTime = System.currentTimeMillis();
            long writeUseTime = writeEndTime - writeStartTime;
            Log.i("shimyTime", "写入耗时" + writeUseTime);
            long allTime = useTime + writeUseTime;
            Log.i("shimyTime", "总耗时" + allTime);
            inputStream.close();
            fos.close();
            return newPath;
        } catch (
                FileNotFoundException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 复制文件至AndroidQ手机相册目录
     *
     * @param context
     * @param inFile
     * @param outUri
     */
    public static boolean copyPathToDCIM(Context context, File inFile, Uri outUri) {
        try {
            OutputStream fileOutputStream = context.getContentResolver().openOutputStream(outUri);
            return PictureFileUtils.bufferCopy(inFile, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
