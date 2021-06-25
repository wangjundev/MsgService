package com.stv.msgservice.ui.conversation.ext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.stv.msgservice.R;
import com.stv.msgservice.annotation.ExtContextMenuItem;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.third.utils.ImageUtils;
import com.stv.msgservice.ui.conversation.ConversationActivity;
import com.stv.msgservice.ui.conversation.ext.core.ConversationExt;
import com.stv.msgservice.utils.VideoUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ImageExt extends ConversationExt {

    /**
     * @param containerView 扩展view的container
     * @param conversation
     */
    @ExtContextMenuItem
    public void pickImage(View containerView, Conversation conversation) {
        Intent intent = ImagePicker.picker().showCamera(true).enableMultiMode(9).buildPickIntent(activity);
        startActivityForResult(intent, 100);
//        TypingMessageContent content = new TypingMessageContent(TypingMessageContent.TYPING_CAMERA);
//        messageViewModel.sendMessage(conversation, content);
    }
    private boolean isGifFile(String file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int[] flags = new int[5];
            flags[0] = inputStream.read();
            flags[1] = inputStream.read();
            flags[2] = inputStream.read();
            flags[3] = inputStream.read();
            inputStream.skip(inputStream.available() - 1);
            flags[4] = inputStream.read();
            inputStream.close();
            return flags[0] == 71 && flags[1] == 73 && flags[2] == 70 && flags[3] == 56 && flags[4] == 0x3B;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //是否发送原图
                        boolean compress = data.getBooleanExtra(ImagePicker.EXTRA_COMPRESS, true);
                        ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        for (ImageItem imageItem : images) {
                            boolean isGif = isGifFile(imageItem.path);
                            if (isGif) {
//                                UIUtils.postTaskSafely(() -> messageViewModel.sendStickerMsg(conversation, imageItem.path, null));
                                Bitmap b = VideoUtil.getVideoThumb(imageItem.path);
                                String thumbnail = null;
                                if(b != null){
                                    thumbnail = VideoUtil.bitmap2File(activity, b, "thumb_"+ SystemClock.currentThreadTimeMillis());
                                }
                                ((ConversationActivity)activity).saveMsg(activity, null, /*conversation.getNormalizedDestination()*/conversation.getSenderAddress(), false, imageItem.path, thumbnail, MessageConstants.CONTENT_TYPE_IMAGE);
                                continue;
                            }
                            File imageFileThumb;
                            File imageFileSource = null;
                            // FIXME: 2018/11/29 压缩, 不是发原图的时候，大图需要进行压缩
                            if (compress) {
                                imageFileSource = ImageUtils.compressImage(imageItem.path);
                            }
                            imageFileSource = imageFileSource == null ? new File(imageItem.path) : imageFileSource;
//                    if (isOrig) {
//                    imageFileSource = new File(imageItem.path);
                            imageFileThumb = ImageUtils.genThumbImgFile(imageItem.path);
                            if (imageFileThumb == null) {
                                Log.e("ImageExt", "gen image thumb fail");
                                return;
                            }
//                    } else {
//                        //压缩图片
//                        // TODO  压缩的有问题
//                        imageFileSource = ImageUtils.genThumbImgFileEx(imageItem.path);
//                        //imageFileThumb = ImageUtils.genThumbImgFile(imageFileSource.getAbsolutePath());
//                        imageFileThumb = imageFileSource;
//                    }
//                            messageViewModel.sendImgMsg(conversation, imageFileThumb, imageFileSource);
//                            File finalImageFileSource = imageFileSource;
//                            UIUtils.postTaskSafely(() -> messageViewModel.sendImgMsg(conversation, imageFileThumb, finalImageFileSource));
                            ((ConversationActivity)activity).saveMsg(activity, null, /*conversation.getNormalizedDestination()*/conversation.getSenderAddress(), false, imageFileSource.getPath(), imageFileThumb.getPath(), MessageConstants.CONTENT_TYPE_IMAGE);
                        }
                    }
                }).start();
                ((ConversationActivity)activity).getConversationFragment().getConversationInputPanel().closeConversationInputPanel();
//                ChatManager.Instance().getWorkHandler().post(() -> {
//                    //是否发送原图
//                    boolean compress = data.getBooleanExtra(ImagePicker.EXTRA_COMPRESS, true);
//                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
//                    for (ImageItem imageItem : images) {
//                        boolean isGif = isGifFile(imageItem.path);
//                        if (isGif) {
//                            UIUtils.postTaskSafely(() -> messageViewModel.sendStickerMsg(conversation, imageItem.path, null));
//                            continue;
//                        }
//                        File imageFileThumb;
//                        File imageFileSource = null;
//                        // FIXME: 2018/11/29 压缩, 不是发原图的时候，大图需要进行压缩
//                        if (compress) {
//                            imageFileSource = ImageUtils.compressImage(imageItem.path);
//                        }
//                        imageFileSource = imageFileSource == null ? new File(imageItem.path) : imageFileSource;
////                    if (isOrig) {
////                    imageFileSource = new File(imageItem.path);
//                        imageFileThumb = ImageUtils.genThumbImgFile(imageItem.path);
//                        if (imageFileThumb == null) {
//                            Log.e("ImageExt", "gen image thumb fail");
//                            return;
//                        }
////                    } else {
////                        //压缩图片
////                        // TODO  压缩的有问题
////                        imageFileSource = ImageUtils.genThumbImgFileEx(imageItem.path);
////                        //imageFileThumb = ImageUtils.genThumbImgFile(imageFileSource.getAbsolutePath());
////                        imageFileThumb = imageFileSource;
////                    }
////                            messageViewModel.sendImgMsg(conversation, imageFileThumb, imageFileSource);
//                        File finalImageFileSource = imageFileSource;
//                        UIUtils.postTaskSafely(() -> messageViewModel.sendImgMsg(conversation, imageFileThumb, finalImageFileSource));
//
//                    }
//
//                });

            }
        }
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public int iconResId() {
        return R.mipmap.ic_func_pic;
    }

    @Override
    public String title(Context context) {
        return "照片";
    }

    @Override
    public String contextMenuTitle(Context context, String tag) {
        return title(context);
    }
}
