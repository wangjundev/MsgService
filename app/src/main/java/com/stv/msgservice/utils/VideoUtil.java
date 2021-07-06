package com.stv.msgservice.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class VideoUtil {
    /**
     * 获取视频文件截图
     *
     * @param path 视频文件的路径
     * @return Bitmap 返回获取的Bitmap
     */

    public static Bitmap getVideoThumb(String path) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

//        retriever.setDataSource(path, new HashMap());
        try {
            //根据url获取缩略图
            retriever.setDataSource(path, new HashMap());
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }

        return bitmap;

    }

    /**
     * Bitmap保存成File
     *
     * @param bitmap input bitmap
     * @param name output file's name
     * @return String output file's path
     */

    public static String bitmap2File(Context context, Bitmap bitmap, String name) {
        String path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/"+ name + ".jpg";
        Log.i("Junwang", "bitmap2File path="+path);
        File f = new File(path);

        if (f.exists()) f.delete();

        FileOutputStream fOut = null;

        try {

            fOut = new FileOutputStream(f);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

            fOut.flush();

            fOut.close();

        } catch (IOException e) {

            return null;

        }

        return f.getAbsolutePath();

    }




}
