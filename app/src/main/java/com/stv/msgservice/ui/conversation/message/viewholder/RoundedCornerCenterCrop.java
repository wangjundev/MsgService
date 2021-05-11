package com.stv.msgservice.ui.conversation.message.viewholder;

import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

import androidx.annotation.NonNull;

public class RoundedCornerCenterCrop extends BitmapTransformation {
    private int radius;

    public RoundedCornerCenterCrop(int radius) {
        this.radius = radius;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Log.i("Junwang", "RoundedCornerCenterCrop transform enter");
        Bitmap bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
        return TransformationUtils.roundedCorners(pool, bitmap, radius);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}
