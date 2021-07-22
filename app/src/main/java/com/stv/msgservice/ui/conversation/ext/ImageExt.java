package com.stv.msgservice.ui.conversation.ext;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.MediaUtils;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;
import com.stv.msgservice.R;
import com.stv.msgservice.annotation.ExtContextMenuItem;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.third.utils.ImageUtils;
import com.stv.msgservice.ui.conversation.ConversationActivity;
import com.stv.msgservice.ui.conversation.GlideEngine;
import com.stv.msgservice.ui.conversation.ext.core.ConversationExt;
import com.stv.msgservice.utils.VideoUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageExt extends ConversationExt {
    public static final String TAG = "Junwang";
    private final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };
    /**
     * @param containerView 扩展view的container
     * @param conversation
     */
    @ExtContextMenuItem
    public void pickImage(View containerView, Conversation conversation) {
//        Intent intent = ImagePicker.picker().showCamera(true).enableMultiMode(9).buildPickIntent(activity);
//        startActivityForResult(intent, 100);
        PermissionX.init(activity)
                .permissions(BASIC_PERMISSIONS)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(ExplainScope scope, List<String> deniedList) {
                        scope.showRequestReasonDialog(deniedList, "需要您同意以下权限才能正常使用", "确定", "取消");
                    }
                })
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                        scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "我已明白", "取消");
                    }
                })
                .setDialogTintColor(Color.parseColor("#008577"), Color.parseColor("#83e8dd"))
                .request((allGranted, grantedList, deniedList) -> {
                    if(allGranted){
                        Log.i("Junwang", "all permission granted.");
                        PictureSelector.create(activity)
                                .openGallery(/*PictureMimeType.ofImage()*/PictureMimeType.ofAll())
                                .loadImageEngine(GlideEngine.createGlideEngine())
                                .forResult(new MyResultCallback());
                    }else{
                        Log.i("Junwang", "These permissions denied "+ deniedList.toString());
                    }
                });
//                .forResult(PictureConfig.CHOOSE_REQUEST);
//        TypingMessageContent content = new TypingMessageContent(TypingMessageContent.TYPING_CAMERA);
//        messageViewModel.sendMessage(conversation, content);
    }

    private /*static*/ class MyResultCallback implements OnResultCallbackListener<LocalMedia> {
        @Override
        public void onResult(List<LocalMedia> result) {
            Log.i("Junwang", "MyResultCallback onResult");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //是否发送原图
                    boolean compress = true;//data.getBooleanExtra(ImagePicker.EXTRA_COMPRESS, true);
                    for (LocalMedia media : result) {
                        if (media.getWidth() == 0 || media.getHeight() == 0) {
                            if (PictureMimeType.isHasImage(media.getMimeType())) {
                                MediaExtraInfo imageExtraInfo = MediaUtils.getImageSize(media.getPath());
                                media.setWidth(imageExtraInfo.getWidth());
                                media.setHeight(imageExtraInfo.getHeight());
                            } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                                MediaExtraInfo videoExtraInfo = MediaUtils.getVideoSize(PictureAppMaster.getInstance().getAppContext(), media.getPath());
                                media.setWidth(videoExtraInfo.getWidth());
                                media.setHeight(videoExtraInfo.getHeight());
                            }
                        }
                        Log.i(TAG, "文件名: " + media.getFileName());
                        Log.i(TAG, "是否压缩:" + media.isCompressed());
                        Log.i(TAG, "压缩:" + media.getCompressPath());
                        Log.i(TAG, "原图:" + media.getPath());
                        Log.i(TAG, "绝对路径:" + media.getRealPath());
                        Log.i(TAG, "是否裁剪:" + media.isCut());
                        Log.i(TAG, "裁剪:" + media.getCutPath());
                        Log.i(TAG, "是否开启原图:" + media.isOriginal());
                        Log.i(TAG, "原图路径:" + media.getOriginalPath());
                        Log.i(TAG, "Android Q 特有Path:" + media.getAndroidQToPath());
                        Log.i(TAG, "宽高: " + media.getWidth() + "x" + media.getHeight());
                        Log.i(TAG, "Size: " + media.getSize());

                        Log.i(TAG, "onResult: " + media.toString());

                        // TODO 可以通过PictureSelectorExternalUtils.getExifInterface();方法获取一些额外的资源信息，如旋转角度、经纬度等信息

                        boolean isGif = isGifFile(media.getPath());
                        if (isGif) {
                            Bitmap b = VideoUtil.getVideoThumb(media.getRealPath());
                            String thumbnail = null;
                            if(b != null){
                                thumbnail = VideoUtil.bitmap2File(activity, b, "thumb_"+ SystemClock.currentThreadTimeMillis());
                            }
                            if(conversation != null){
                                ((ConversationActivity)activity).saveMsg(activity, null, conversation.getDestinationAddress(), conversation.getSenderAddress(), conversation.getConversationID(), false, media.getRealPath(), thumbnail, MessageConstants.CONTENT_TYPE_IMAGE, "image/jpg");
                            }else if(chatbotId != null){
                                ((ConversationActivity)activity).saveMsg(activity, null, null, chatbotId, null,false, media.getRealPath(), thumbnail, MessageConstants.CONTENT_TYPE_IMAGE, "image/jpg");
                            }
                            continue;
                        }
                        File imageFileThumb = null;
                        File imageFileSource = null;
                        // FIXME: 2018/11/29 压缩, 不是发原图的时候，大图需要进行压缩
                        if (/*compress*/false) {
                            imageFileSource = ImageUtils.compressImage(media.getRealPath());
                        }
                        imageFileSource = imageFileSource == null ? new File(media.getRealPath()) : imageFileSource;
                        if (PictureMimeType.isHasImage(media.getMimeType())){
                            imageFileThumb = ImageUtils.genThumbImgFile(/*activity,*/ media.getRealPath());
                        }else if(PictureMimeType.isHasVideo(media.getMimeType())){
                            Bitmap bitmap = VideoUtil.getVideoThumb(media.getRealPath());
                            if(bitmap != null){
                                String filePath = VideoUtil.bitmap2File(activity, bitmap, "thumb_"+ SystemClock.currentThreadTimeMillis());
                                imageFileThumb = new File(filePath);
                            }
                        }
                        if (imageFileThumb == null) {
                            Log.e("ImageExt", "gen image thumb fail");
                            return;
                        }
                        if(conversation != null){
                            if(PictureMimeType.isHasImage(media.getMimeType())){
                                ((ConversationActivity)activity).saveMsg(activity, null, conversation.getDestinationAddress(), conversation.getSenderAddress(), conversation.getConversationID(), false, imageFileSource.getPath(), imageFileThumb.getPath(), MessageConstants.CONTENT_TYPE_IMAGE, "image/jpg");
                            }else if(PictureMimeType.isHasVideo(media.getMimeType())){
                                ((ConversationActivity)activity).saveMsg(activity, null, conversation.getDestinationAddress(), conversation.getSenderAddress(), conversation.getConversationID(), false, imageFileSource.getPath(), imageFileThumb.getPath(), MessageConstants.CONTENT_TYPE_VIDEO, "video/mp4");
                            }
                        }else if(chatbotId != null){
                            if(PictureMimeType.isHasImage(media.getMimeType())){
                                ((ConversationActivity)activity).saveMsg(activity, null, null, chatbotId, null,false, imageFileSource.getPath(), imageFileThumb.getPath(), MessageConstants.CONTENT_TYPE_IMAGE, "image/jpg");
                            }else if(PictureMimeType.isHasVideo(media.getMimeType())){
                                ((ConversationActivity)activity).saveMsg(activity, null, null, chatbotId, null,false, imageFileSource.getPath(), imageFileThumb.getPath(), MessageConstants.CONTENT_TYPE_VIDEO, "video/mp4");
                            }
                        }
                    }
                }
            }).start();
            ((ConversationActivity)activity).getConversationFragment().getConversationInputPanel().closeConversationInputPanel();
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "PictureSelector Cancel");
        }
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
        Log.i("Junwang", "ImageExt onActivityResult");
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == PictureConfig.CHOOSE_REQUEST){
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //是否发送原图
                        boolean compress = false;//data.getBooleanExtra(ImagePicker.EXTRA_COMPRESS, true);
                        for (LocalMedia imageItem : selectList) {
                            boolean isGif = isGifFile(imageItem.getPath());
                            if (isGif) {
                                Bitmap b = VideoUtil.getVideoThumb(imageItem.getPath());
                                String thumbnail = null;
                                if(b != null){
                                    thumbnail = VideoUtil.bitmap2File(activity, b, "thumb_"+ SystemClock.currentThreadTimeMillis());
                                }
                                if(conversation != null){
                                    ((ConversationActivity)activity).saveMsg(activity, null, conversation.getDestinationAddress(), conversation.getSenderAddress(), conversation.getConversationID(), false, imageItem.getPath(), thumbnail, MessageConstants.CONTENT_TYPE_IMAGE, "image/jpg");
                                }else if(chatbotId != null){
                                    ((ConversationActivity)activity).saveMsg(activity, null, null, chatbotId, null,false, imageItem.getPath(), thumbnail, MessageConstants.CONTENT_TYPE_IMAGE, "image/jpg");
                                }
                                continue;
                            }
                            File imageFileThumb = null;
                            File imageFileSource = null;
                            // FIXME: 2018/11/29 压缩, 不是发原图的时候，大图需要进行压缩
                            if (compress) {
                                imageFileSource = ImageUtils.compressImage(imageItem.getPath());
                            }
                            imageFileSource = imageFileSource == null ? new File(imageItem.getPath()) : imageFileSource;
                            if (PictureMimeType.isHasImage(imageItem.getMimeType())){
                                imageFileThumb = ImageUtils.genThumbImgFile(imageItem.getPath());
                            }else if(PictureMimeType.isHasVideo(imageItem.getMimeType())){
                                Bitmap bitmap = VideoUtil.getVideoThumb(imageItem.getPath());
                                if(bitmap != null){
                                    String filePath = VideoUtil.bitmap2File(activity, bitmap, "thumb_"+ SystemClock.currentThreadTimeMillis());
                                    imageFileThumb = new File(filePath);
                                }
                            }
                            if (imageFileThumb == null) {
                                Log.e("ImageExt", "gen image thumb fail");
                                return;
                            }
                            if(conversation != null){
                                if(PictureMimeType.isHasImage(imageItem.getMimeType())){
                                    ((ConversationActivity)activity).saveMsg(activity, null, conversation.getDestinationAddress(), conversation.getSenderAddress(), conversation.getConversationID(), false, imageFileSource.getPath(), imageFileThumb.getPath(), MessageConstants.CONTENT_TYPE_IMAGE, "image/jpg");
                                }else if(PictureMimeType.isHasVideo(imageItem.getMimeType())){
                                    ((ConversationActivity)activity).saveMsg(activity, null, conversation.getDestinationAddress(), conversation.getSenderAddress(), conversation.getConversationID(), false, imageFileSource.getPath(), imageFileThumb.getPath(), MessageConstants.CONTENT_TYPE_VIDEO, "video/mp4");
                                }
                            }else if(chatbotId != null){
                                if(PictureMimeType.isHasImage(imageItem.getMimeType())){
                                    ((ConversationActivity)activity).saveMsg(activity, null, null, chatbotId, null,false, imageFileSource.getPath(), imageFileThumb.getPath(), MessageConstants.CONTENT_TYPE_IMAGE, "image/jpg");
                                }else if(PictureMimeType.isHasVideo(imageItem.getMimeType())){
                                    ((ConversationActivity)activity).saveMsg(activity, null, null, chatbotId, null,false, imageFileSource.getPath(), imageFileThumb.getPath(), MessageConstants.CONTENT_TYPE_VIDEO, "video/mp4");
                                }
                            }
                        }
                    }
                }).start();
                ((ConversationActivity)activity).getConversationFragment().getConversationInputPanel().closeConversationInputPanel();
            }
            else
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
//                                ((ConversationActivity)activity).saveMsg(activity, null, /*conversation.getNormalizedDestination()*/conversation.getSenderAddress(), false, imageItem.path, thumbnail, MessageConstants.CONTENT_TYPE_IMAGE);
                                if(conversation != null){
                                    ((ConversationActivity)activity).saveMsg(activity, null, conversation.getDestinationAddress(), conversation.getSenderAddress(), conversation.getConversationID(), false, imageItem.path, thumbnail, MessageConstants.CONTENT_TYPE_IMAGE, "image/jpg");
                                }else if(chatbotId != null){
                                    ((ConversationActivity)activity).saveMsg(activity, null, null, chatbotId, null,false, imageItem.path, thumbnail, MessageConstants.CONTENT_TYPE_IMAGE, "image/jpg");
                                }
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
//                            ((ConversationActivity)activity).saveMsg(activity, null, /*conversation.getNormalizedDestination()*/conversation.getSenderAddress(), false, imageFileSource.getPath(), imageFileThumb.getPath(), MessageConstants.CONTENT_TYPE_IMAGE);
                            if(conversation != null){
                                ((ConversationActivity)activity).saveMsg(activity, null, conversation.getDestinationAddress(), conversation.getSenderAddress(), conversation.getConversationID(), false, imageFileSource.getPath(), imageFileThumb.getPath(), MessageConstants.CONTENT_TYPE_IMAGE, "image/jpg");
                            }else if(chatbotId != null){
                                ((ConversationActivity)activity).saveMsg(activity, null, null, chatbotId, null,false, imageFileSource.getPath(), imageFileThumb.getPath(), MessageConstants.CONTENT_TYPE_IMAGE, "image/jpg");
                            }
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
