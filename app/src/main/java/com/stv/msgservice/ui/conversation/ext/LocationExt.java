package com.stv.msgservice.ui.conversation.ext;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.stv.msgservice.R;
import com.stv.msgservice.annotation.ExtContextMenuItem;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.third.activity.LocationData;
import com.stv.msgservice.third.activity.MyLocationActivity;
import com.stv.msgservice.ui.WfcBaseActivity;
import com.stv.msgservice.ui.conversation.ConversationActivity;
import com.stv.msgservice.ui.conversation.ext.core.ConversationExt;

import static android.app.Activity.RESULT_OK;

public class LocationExt extends ConversationExt {

    /**
     * @param containerView 扩展view的container
     * @param conversation
     */
    @ExtContextMenuItem
    public void pickLocation(View containerView, Conversation conversation) {
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!((WfcBaseActivity) activity).checkPermission(permissions)) {
                activity.requestPermissions(permissions, 100);
                return;
            }
        }

        Intent intent = new Intent(activity, MyLocationActivity.class);
        startActivityForResult(intent, 100);
//        TypingMessageContent content = new TypingMessageContent(TypingMessageContent.TYPING_LOCATION);
//        messageViewModel.sendMessage(conversation, content);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            LocationData locationData = (LocationData) data.getSerializableExtra("location");
            ((ConversationActivity)activity).saveLocationMsg(activity, null, conversation.getNormalizedDestination(), false, MessageConstants.CONTENT_TYPE_LOCATION, locationData);
            ((ConversationActivity)activity).getConversationFragment().getConversationInputPanel().closeConversationInputPanel();
        }
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public int iconResId() {
        return R.mipmap.ic_func_location;
    }

    @Override
    public String title(Context context) {
        return "位置";
    }

    @Override
    public String contextMenuTitle(Context context, String tag) {
        return title(context);
    }
}
