package com.stv.msgservice.ui.conversation.message.multimsg;

import android.content.Context;

import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.ui.conversation.message.UiMessage;

import java.util.List;

import androidx.fragment.app.Fragment;

public abstract class MultiMessageAction {
    protected Conversation conversation;
    protected /*ConversationFragment*/ Fragment fragment;

    public MultiMessageAction() {
    }

    public final void onBind(Fragment fragment, Conversation conversation) {
        this.fragment = fragment;
        this.conversation = conversation;

    }

    public abstract void onClick(List<UiMessage> messages);

    public int priority() {
        return 0;
    }

    public boolean confirm() {
        return false;
    }

    public boolean filter(Conversation conversation) {
        return false;
    }

    public abstract int iconResId();

    public abstract String title(Context context);

    public String confirmPrompt() {
        return "";
    }
}
