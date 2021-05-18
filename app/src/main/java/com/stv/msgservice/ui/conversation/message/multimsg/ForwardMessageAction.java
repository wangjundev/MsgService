package com.stv.msgservice.ui.conversation.message.multimsg;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.ui.conversation.message.UiMessage;

import java.util.List;

public class ForwardMessageAction extends MultiMessageAction {
    @Override
    public void onClick(List<UiMessage> messages) {
        new MaterialDialog.Builder(fragment.getActivity())
                .items("逐条转发", "合并转发")
                .itemsCallback((dialog, itemView, position, text) -> {
                    switch (position) {
                        case 0:
                            forwardOneByOne(messages);
                            break;
                        case 1:
                            forward(messages);
                            break;
                        default:
                            break;
                    }
                })
                .build()
                .show();
    }

    private void forwardOneByOne(List<UiMessage> messages) {
//        ArrayList<Message> msgs = messages.stream().map(uiMessage -> uiMessage.message).collect(Collectors.toCollection(ArrayList::new));
//        Intent intent = new Intent(fragment.getContext(), ForwardActivity.class);
//        intent.putExtra("messages", msgs);
//        fragment.startActivity(intent);
    }

    private void forward(List<UiMessage> messages) {
//        Toast.makeText(fragment.getActivity(), "合并转发", Toast.LENGTH_SHORT).show();
//        CompositeMessageContent content = new CompositeMessageContent();
//        String title;
//        if (conversation.type == Conversation.ConversationType.Single) {
//            UserInfo userInfo1 = ChatManager.Instance().getUserInfo(ChatManager.Instance().getUserId(), false);
//            UserInfo userInfo2 = ChatManager.Instance().getUserInfo(conversation.target, false);
//            title = userInfo1.displayName + "和" + userInfo2.displayName + "的聊天记录";
//        } else {
//            title = "群的聊天记录";
//        }
//        content.setTitle(title);
//        List<Message> msgs = messages.stream().map(uiMessage -> uiMessage.message).collect(Collectors.toList());
//        content.setMessages(msgs);
//        Message message = new Message();
//        message.content = content;
//
//        Intent intent = new Intent(fragment.getContext(), ForwardActivity.class);
//        intent.putExtra("message", message);
//        fragment.startActivity(intent);
    }

    @Override
    public int iconResId() {
        return R.mipmap.ic_forward;
    }

    @Override
    public String title(Context context) {
        return "转发";
    }

    @Override
    public boolean filter(Conversation conversation) {
        return false;
    }
}
