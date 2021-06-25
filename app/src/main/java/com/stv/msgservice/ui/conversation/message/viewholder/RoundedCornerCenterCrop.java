package com.stv.msgservice.ui.conversation.message.viewholder;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

import androidx.annotation.NonNull;

public class RoundedCornerCenterCrop extends BitmapTransformation {
    private float radius;

    public RoundedCornerCenterCrop() {
        this(4);
    }

    public RoundedCornerCenterCrop(int radius) {
        this.radius = Resources.getSystem().getDisplayMetrics().density*radius;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Log.i("Junwang", "RoundedCornerCenterCrop transform enter");
//        Bitmap bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
//        return TransformationUtils.roundedCorners(pool, bitmap, radius);
        Bitmap source = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
        if (source == null) {
            return null;
        }
        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, radius, radius, paint);
        //只保留左上角、右上角圆角（注释掉一下两行，则四个角为圆角）
        RectF rectRound = new RectF(0f, 100f, source.getWidth(), source.getHeight());
        canvas.drawRect(rectRound, paint);
        return result;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}
