package com.yue.metim.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author shimy
 * @create 2020/2/24 15:45
 * @desc 文件操作
 */
public class FileUtils {

    public enum Code {
        SUCCESS,
        FAILE

    }

    public interface FileCallBack {
        void onMessage(Code code, String message);
    }

    /**
     * 保存bitmap到本地
     *
     * @param bitmap Bitmap
     */
    public static void saveBitmap(Bitmap bitmap, String dir, String fileName, FileCallBack callBack) {
        File filePic;
        if (callBack == null) {
            callBack = new FileCallBack() {
                @Override
                public void onMessage(Code code, String message) {

                }
            };
        }
        try {
            filePic = new File(dir, fileName);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("tag", "saveBitmap: " + e.getMessage());
            callBack.onMessage(Code.FAILE, e.getMessage());
            return;
        }
        callBack.onMessage(Code.SUCCESS, "成功");
        Log.i("tag", "saveBitmap success: " + filePic.getAbsolutePath());
    }

    /**
     * 删除目录下的所有文件 但不删除此目录
     */
    public static void deleteDirFiles(File dir){
//        判断文件不为null或文件目录存在
        if (dir == null || !dir.exists()){
            return;
        }
        //取得这个目录下的所有子文件对象
        File[] files = dir.listFiles();
        //遍历该目录下的文件对象
        for (File f: files){
            //打印文件名
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()){
                deleteDirFiles(f);
            }else {
                f.delete();
            }
        }
    }


    /**
     * 图片压缩 (AndroidQ以上)
     *
     * @param context context
     * @param uri uri
     * @return 压缩后的图片uri
     */
//    private static Uri compressImageWithAndroidQ (Context context, Uri uri) {
//
//        Uri insertUri = null;
//        ContentResolver resolver = context.getContentResolver();
//        ByteArrayOutputStream bos = null;
//        OutputStream os = null;
//        try {
//            //1.uri转换bitmap类型并旋转图片为正常图片
//            Bitmap tagBitmap = uriToBitmap(context, uri);
//
//            //2.压缩图片并写入byteArrayOutputStream流中
//            bos = new ByteArrayOutputStream();
//            if (tagBitmap != null) {
//                tagBitmap.compress(Bitmap.CompressFormat.JPEG, 85, bos);
//                tagBitmap.recycle();
//            }
//            //3.获取图片需要缓存的uri地址并copy到指定路径,主要通过MediaStore,请参照上篇文章
//            insertUri = getImageFileCache(getCompressImageName(),getCompressImageType());
//            if (insertUri == null) {
//                return null;
//            }
//            os = resolver.openOutputStream(insertUri);
//            if (os != null) {
//                os.write(bos.toByteArray());
//                os.flush();
//            }
//            //4.返回uri类型提供页面展示
//            return insertUri;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (os != null) {
//                    os.close();
//                }
//                if (bos != null) {
//                    bos.close();
//                }
//            } catch (IOException e) {
//            }
//        }
//        return insertUri;
//    }

}
