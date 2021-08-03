package com.stv.msgservice.ui.conversation.message.viewholder;

import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.EnableContextMenu;
import com.stv.msgservice.annotation.MessageContentType;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.network.chatbot.CardContent;
import com.stv.msgservice.datamodel.network.chatbot.GeneralPurposeCardCarousel;
import com.stv.msgservice.datamodel.network.chatbot.MultiCardChatbotMsg;
import com.stv.msgservice.ui.conversation.ConversationFragment;
import com.stv.msgservice.ui.conversation.message.MultiCardMessageContent;

import java.util.Arrays;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

@MessageContentType(value = {
        MultiCardMessageContent.class,
})
@EnableContextMenu
public class MultiCardMessageContentViewHolder extends MediaMessageContentViewHolder {
    //    @BindView(R2.id.contentTextView)
//    TextView contentTextView;
//    @BindView(R2.id.refTextView)
//    TextView refTextView;
//    @BindView(R2.id.card_image)
//    ImageView cardImage;
//    @BindView(R2.id.card_title)
//    TextView cardTitle;
//    @BindView(R2.id.card_description)
//    TextView cardDescription;
//    @BindView(R2.id.card_layout)
//    LinearLayout cardLayout;
    //    private QuoteInfo quoteInfo;
    @BindView(R2.id.card_rv)
    RecyclerView cardRv;
    Fragment mFragment;

    public MultiCardMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
        mFragment = fragment;
    }

    @Override
    public void onBind(Message message) {
        Log.i("Junwang", "multicard content view onBind");
        String content = message.getContent();
        MultiCardChatbotMsg multiCard = new Gson().fromJson(content, MultiCardChatbotMsg.class);
        GeneralPurposeCardCarousel gpcc = multiCard.getGeneralPurposeCardCarousel();
        if(gpcc != null){
            CardContent[] cardcontents = gpcc.getContent();
            if((cardcontents != null) && (cardcontents.length>0)){
//                loadChatbotMultiCard(cardcontents);
                cardRv.setLayoutManager(new LinearLayoutManager(mFragment.getContext(), LinearLayoutManager.HORIZONTAL, false));
                MultiCardItemViewAdapter listAdapter = new MultiCardItemViewAdapter(Arrays.asList(cardcontents), mFragment.getContext(), R.layout.multi_card_itemview);
                cardRv.setAdapter(listAdapter);
            }
        }
    }

//    @OnClick(R2.id.card_layout)
//    public void onClick(View view) {
////        String content = ((TextMessageContent) message.content).getContent();
//        WfcWebViewActivity.loadHtmlContent(fragment.getActivity(), "消息内容", /*content*/message.getContent());
//    }

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

//    @MessageContextMenuItem(tag = MessageContextMenuItemTags.TAG_CLIP, confirm = false, priority = 12)
//    public void clip(View itemView, Message message) {
//        ClipboardManager clipboardManager = (ClipboardManager) fragment.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//        if (clipboardManager == null) {
//            return;
//        }
////        TextMessageContent content = (TextMessageContent) message.content;
//        ClipData clipData = ClipData.newPlainText("messageContent", message.getContent());
//        clipboardManager.setPrimaryClip(clipData);
//    }


//    @Override
//    public String contextMenuTitle(Context context, String tag) {
//        if (MessageContextMenuItemTags.TAG_CLIP.equals(tag)) {
//            return "复制";
//        }
//        return super.contextMenuTitle(context, tag);
//    }
}
