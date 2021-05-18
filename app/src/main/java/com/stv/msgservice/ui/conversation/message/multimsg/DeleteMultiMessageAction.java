package com.stv.msgservice.ui.conversation.message.multimsg;

import android.content.Context;

import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.ui.conversation.ConversationActivity;
import com.stv.msgservice.ui.conversation.message.UiMessage;

import java.util.ArrayList;
import java.util.List;

public class DeleteMultiMessageAction extends MultiMessageAction {

    @Override
    public void onClick(List<UiMessage> messages) {
//        MessageViewModel messageViewModel = new ViewModelProvider(fragment).get(MessageViewModel.class);
//        new MaterialDialog.Builder(fragment.getContext())
//                .items("删除本地消息", "删除远程消息")
//                .items("删除消息")
//                .itemsCallback(new MaterialDialog.ListCallback() {
//                    @Override
//                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
//                        List<MessageEntity> list = new ArrayList<>();
//                        for (UiMessage message : messages) {
////                            messageViewModel.deleteMessage(message.message);
//                            list.add(message.message);
//                        }
//                        ((ConversationActivity)fragment.getActivity()).deleteMsgs(list);
//                    }
//                })
//                .show();
        List<MessageEntity> list = new ArrayList<>();
        for (UiMessage message : messages) {
//                            messageViewModel.deleteMessage(message.message);
            list.add(message.message);
        }
        ((ConversationActivity)fragment.getActivity()).deleteMsgs(list);
    }

    @Override
    public int iconResId() {
        return R.mipmap.ic_delete;
    }

    @Override
    public String title(Context context) {
        return "删除";
    }

    @Override
    public boolean confirm() {
        return true;
    }

    @Override
    public String confirmPrompt() {
        return "确认删除?";
    }
}
