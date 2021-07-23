package com.stv.msgservice.ui.conversation.message.viewholder;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.lqr.emoji.MoonUtils;
import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.EnableContextMenu;
import com.stv.msgservice.annotation.MessageContentType;
import com.stv.msgservice.annotation.MessageContextMenuItem;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.ui.WfcWebViewActivity;
import com.stv.msgservice.ui.conversation.ConversationFragment;
import com.stv.msgservice.ui.conversation.message.TextMessageContent;
import com.stv.msgservice.ui.widget.LinkClickListener;
import com.stv.msgservice.ui.widget.LinkTextViewMovementMethod;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

@MessageContentType(value = {
        TextMessageContent.class,
})
@EnableContextMenu
public class TextMessageContentViewHolder extends NormalMessageContentViewHolder {
    @BindView(R2.id.contentTextView)
    TextView contentTextView;
    @BindView(R2.id.refTextView)
    TextView refTextView;

//    private QuoteInfo quoteInfo;

    public TextMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
    }

    @Override
    public void onBind(Message message) {
//        TextMessageContent textMessageContent = (TextMessageContent) message.content;
//        String content = textMessageContent.getContent();
        String content = message.getContent();
        if (content.startsWith("<") && content.endsWith(">")) {
            contentTextView.setText(Html.fromHtml(content));
        } else {
            MoonUtils.identifyFaceExpression(fragment.getContext(), contentTextView, message.getContent(), ImageSpan.ALIGN_BOTTOM);
        }
        contentTextView.setMovementMethod(new LinkTextViewMovementMethod(new LinkClickListener() {
            @Override
            public boolean onLinkClick(String link) {
                WfcWebViewActivity.loadUrl(fragment.getContext(), "", link);
                return true;
            }
        }));

//        quoteInfo = textMessageContent.getQuoteInfo();
//        if (quoteInfo != null && quoteInfo.getMessageUid() > 0) {
//            refTextView.setVisibility(View.VISIBLE);
//            refTextView.setText(quoteInfo.getUserDisplayName() + ": " + quoteInfo.getMessageDigest());
//        } else {
//            refTextView.setVisibility(View.GONE);
//        }
        refTextView.setVisibility(View.GONE);
    }

    @OnClick(R2.id.contentTextView)
    public void onClick(View view) {
//        String content = ((TextMessageContent) message.content).getContent();
//        WfcWebViewActivity.loadHtmlContent(fragment.getActivity(), "消息内容", /*content*/message.getContent());
    }

//    @OnClick(R2.id.refTextView)
//    public void onRefClick(View view) {
//        Message message = ChatManager.Instance().getMessageByUid(quoteInfo.getMessageUid());
//        if (message != null) {
//            // TODO previewMessageActivity
//            MessageContent messageContent = message.content;
//            if (messageContent instanceof TextMessageContent) {
//                WfcWebViewActivity.loadHtmlContent(fragment.getActivity(), "消息内容", ((TextMessageContent) messageContent).getContent());
//            } else {
//                if (messageContent instanceof VideoMessageContent) {
//                    MMPreviewActivity.previewVideo(fragment.getActivity(), (VideoMessageContent) messageContent);
//                } else if (messageContent instanceof ImageMessageContent) {
//                    MMPreviewActivity.previewImage(fragment.getActivity(), (ImageMessageContent) messageContent);
//                }
//            }
//        }
//    }

    @MessageContextMenuItem(tag = MessageContextMenuItemTags.TAG_CLIP, confirm = false, priority = 12)
    public void clip(View itemView, Message message) {
        ClipboardManager clipboardManager = (ClipboardManager) fragment.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager == null) {
            return;
        }
//        TextMessageContent content = (TextMessageContent) message.content;
        ClipData clipData = ClipData.newPlainText("messageContent", message.getContent());
        clipboardManager.setPrimaryClip(clipData);
    }


    @Override
    public String contextMenuTitle(Context context, String tag) {
        if (MessageContextMenuItemTags.TAG_CLIP.equals(tag)) {
            return "复制";
        }
        return super.contextMenuTitle(context, tag);
    }
}

