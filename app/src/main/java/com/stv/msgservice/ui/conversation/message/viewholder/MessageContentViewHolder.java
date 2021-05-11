package com.stv.msgservice.ui.conversation.message.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.stv.msgservice.R2;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;
import com.stv.msgservice.third.utils.TimeUtils;
import com.stv.msgservice.ui.conversation.ConversationFragment;
import com.stv.msgservice.ui.conversation.ConversationMessageAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class MessageContentViewHolder extends RecyclerView.ViewHolder {
    @NonNull
    protected ConversationFragment fragment;
    protected View itemView;
    protected Message message;
    protected int position;
    protected RecyclerView.Adapter adapter;
    protected MessageViewModel messageViewModel;

    @BindView(R2.id.timeTextView)
    TextView timeTextView;


    public MessageContentViewHolder(@NonNull ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(itemView);
        this.fragment = fragment;
        this.itemView = itemView;
        this.adapter = adapter;
//        messageViewModel = ViewModelProviders.of(fragment).get(MessageViewModel.class);
        MessageViewModel.Factory factory = new MessageViewModel.Factory(
                fragment.getActivity().getApplication(), 0);

        messageViewModel = new ViewModelProvider(fragment, factory)
                .get(MessageViewModel.class);
        ButterKnife.bind(this, itemView);
    }

    public void onBind(Message message, int position) {
        setMessageTime(message, position);
    }

    /**
     * @param uiMessage
     * @param tag
     * @return 返回true，将从context menu中排除
     */

    public abstract boolean contextMenuItemFilter(Message uiMessage, String tag);

    public abstract String contextMenuTitle(Context context, String tag);

    public abstract String contextConfirmPrompt(Context context, String tag);

    public void onViewRecycled() {
        // you can do some clean up here
    }

    protected void setMessageTime(Message item, int position) {
        long msgTime = item.getTime();
        if (position > 0) {
            Message preMsg = ((ConversationMessageAdapter) adapter).getItem(position - 1);
            long preMsgTime = preMsg.getTime();
            if (msgTime - preMsgTime > (5 * 60 * 1000)) {
                timeTextView.setVisibility(View.VISIBLE);
                timeTextView.setText(TimeUtils.getMsgFormatTime(msgTime));
            } else {
                timeTextView.setVisibility(View.GONE);
            }
        } else {
            timeTextView.setVisibility(View.VISIBLE);
            timeTextView.setText(TimeUtils.getMsgFormatTime(msgTime));
        }
    }

}
