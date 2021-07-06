package com.stv.msgservice.ui.conversation.ext;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.stv.msgservice.R;
import com.stv.msgservice.annotation.ExtContextMenuItem;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.third.utils.ImageUtils;
import com.stv.msgservice.ui.WfcBaseActivity;
import com.stv.msgservice.ui.conversation.ConversationActivity;
import com.stv.msgservice.ui.conversation.ext.core.ConversationExt;
import com.stv.msgservice.ui.multimedia.TakePhotoActivity;
import com.stv.msgservice.utils.VideoUtil;

import static android.app.Activity.RESULT_OK;

public class ShootExt extends ConversationExt {

    /**
     * @param containerView 扩展view的container
     * @param conversation
     */
    @ExtContextMenuItem
    public void shoot(View containerView, Conversation conversation) {
        String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!((WfcBaseActivity) activity).checkPermission(permissions)) {
                activity.requestPermissions(permissions, 100);
                return;
            }
        }
        Intent intent = new Intent(activity, TakePhotoActivity.class);
        startActivityForResult(intent, 100);
//        TypingMessageContent content = new TypingMessageContent(TypingMessageContent.TYPING_CAMERA);
//        messageViewModel.sendMessage(conversation, content);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String path = data.getStringExtra("path");
            if (TextUtils.isEmpty(path)) {
                Toast.makeText(activity, "拍照错误, 请向我们反馈", Toast.LENGTH_SHORT).show();
                return;
            }
            if (data.getBooleanExtra("take_photo", true)) {
                //照片
//                ((ConversationActivity)activity).saveMsg(activity, null, /*conversation.getNormalizedDestination()*/conversation.getSenderAddress(), false, path, ImageUtils.genThumbImgFile(path).getPath(), MessageConstants.CONTENT_TYPE_IMAGE);
//                messageViewModel.sendImgMsg(conversation, ImageUtils.genThumbImgFile(path), new File(path));
                if(conversation != null){
                    ((ConversationActivity)activity).saveMsg(activity, null, conversation.getDestinationAddress(), conversation.getSenderAddress(), conversation.getConversationID(), false, path, ImageUtils.genThumbImgFile(path).getPath(), MessageConstants.CONTENT_TYPE_IMAGE, "image/jpg");
                }else if(chatbotId != null){
                    ((ConversationActivity)activity).saveMsg(activity, null, null, chatbotId, null,false, path, ImageUtils.genThumbImgFile(path).getPath(), MessageConstants.CONTENT_TYPE_IMAGE, "image/jpg");
                }
            } else {
                String thumbnail = null;
                try{
                    //小视频
                    Bitmap b = VideoUtil.getVideoThumb(path);
                    if(b != null){
                        thumbnail = VideoUtil.bitmap2File(activity, b, "thumb_"+ SystemClock.currentThreadTimeMillis());
                    }
                }catch (Exception e){
                    Log.i("Junwang", "getVideoThumb "+e.toString());
                }

//                ((ConversationActivity)activity).saveMsg(activity, null, /*conversation.getNormalizedDestination()*/conversation.getSenderAddress(), false, path, thumbnail, MessageConstants.CONTENT_TYPE_VIDEO);
//                messageViewModel.sendVideoMsg(conversation, new File(path));
                if(conversation != null){
                    ((ConversationActivity)activity).saveMsg(activity, null, conversation.getDestinationAddress(), conversation.getSenderAddress(), conversation.getConversationID(), false, path, thumbnail, MessageConstants.CONTENT_TYPE_VIDEO, "video/mp4");
                }else if(chatbotId != null){
                    ((ConversationActivity)activity).saveMsg(activity, null, null, chatbotId, null,false, path, thumbnail, MessageConstants.CONTENT_TYPE_VIDEO, "video/mp4");
                }
            }
            ((ConversationActivity)activity).getConversationFragment().getConversationInputPanel().closeConversationInputPanel();
        }
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public int iconResId() {
        return R.mipmap.ic_func_shot;
    }

    @Override
    public String title(Context context) {
        return "拍摄";
    }

    @Override
    public String contextMenuTitle(Context context, String tag) {
        return title(context);
    }
}
