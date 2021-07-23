package com.stv.msgservice.ui.conversation.message.viewholder;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cjt2325.cameralibrary.util.LogUtil;
import com.google.gson.Gson;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.EnableContextMenu;
import com.stv.msgservice.annotation.MessageContentType;
import com.stv.msgservice.annotation.MessageContextMenuItem;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.network.chatbot.CardContent;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMessageBean;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotStdSingleCard;
import com.stv.msgservice.datamodel.network.chatbot.GeneralPurposeCard;
import com.stv.msgservice.datamodel.network.chatbot.SuggestionAction;
import com.stv.msgservice.datamodel.network.chatbot.SuggestionActionWrapper;
import com.stv.msgservice.datamodel.network.chatbot.Suggestions;
import com.stv.msgservice.ui.WebViewNewsActivity;
import com.stv.msgservice.ui.conversation.ConversationFragment;
import com.stv.msgservice.ui.conversation.message.SingleCardWithSuggestionsMessageContent;
import com.stv.msgservice.utils.NativeFunctionUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

@MessageContentType(value = {
        SingleCardWithSuggestionsMessageContent.class,
})
@EnableContextMenu
public class SingleCardWithSuggestionsMessageContentViewHolder extends MediaMessageContentViewHolder {
    //    @BindView(R2.id.contentTextView)
//    TextView contentTextView;
//    @BindView(R2.id.refTextView)
//    TextView refTextView;
    @BindView(R2.id.card_image)
    ImageView cardImage;
    @BindView(R2.id.card_title)
    TextView cardTitle;
    @BindView(R2.id.card_description)
    TextView cardDescription;
    @BindView(R2.id.card_layout)
    LinearLayout cardLayout;
    //    private QuoteInfo quoteInfo;
    Fragment mFragment;
    String cardJson = null;
    String suggestionJson = null;

    public SingleCardWithSuggestionsMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
        mFragment = fragment;
    }

    private void addSuggestions(Context context){
        Log.i("Junwang", "suggestionJson="+suggestionJson);
        Suggestions suggestions = new Gson().fromJson(suggestionJson, Suggestions.class);
        SuggestionActionWrapper[] saw = suggestions.getSuggestions();
        if(saw != null && saw.length>0){
            if(cardLayout.getChildCount() > 6){
                return;
            }
            int i = 0;
            TextView tv1;
            Log.i("Junwang", "setSuggestionsView length="+saw.length);
            for(; i<saw.length; i++){
                if(saw[i].action != null) {
                    tv1 = new TextView(context);
                    tv1.setText(saw[i].action.displayText);
                    tv1.setBackgroundResource(R.drawable.border_textview);
                    tv1.setGravity(Gravity.CENTER);
                    tv1.setPadding(0, 15, 0, 15);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(100,12,100,12);
//                    cardLayout.addView(tv1, j, lp);
                    Log.i("Junwang", "add view i="+i);
                    cardLayout.addView(tv1, lp);
                    SuggestionAction sa = saw[i].action;
                    if ((sa != null) && (sa.urlAction != null)) {
                        tv1.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
//                                WfcWebViewActivity.loadUrl(context, "", sa.urlAction.openUrl.url);
                                WebViewNewsActivity.start(context, sa.urlAction.openUrl.url);
                            }
                        });
                    }else if((sa != null) && (sa.dialerAction != null)){
                        tv1.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                NativeFunctionUtil.callNativeFunction(MessageConstants.NativeActionType.PHONE_CALL, context,
                                        null, null, sa.dialerAction.dialPhoneNumber.phoneNumber);
                            }
                        });
                    }else if((sa != null) && (sa.mapAction != null)){
                        tv1.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                NativeFunctionUtil.openLocation(sa.mapAction.showLocation.location.label, sa.mapAction.showLocation.location.latitude,
                                        sa.mapAction.showLocation.location.longitude, context);
                            }
                        });
                    }
                }
                if(saw[i].reply != null){
                    tv1 = new TextView(context);
                    tv1.setText(saw[i].reply.displayText);
                    tv1.setBackgroundResource(R.drawable.border_textview);
                    tv1.setGravity(Gravity.CENTER);
                    tv1.setPadding(0, 15, 0, 15);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(100,12,100,12);
//                    cardLayout.addView(tv1, j, lp);
                    Log.i("Junwang", "add view1 i="+i);
                    cardLayout.addView(tv1, lp);
                }
            }
        }
    }

    private void setSuggestionsView(Context context, CardContent cardcontent){
        SuggestionActionWrapper[] saw = cardcontent.getSuggestionActionWrapper();
        if(saw != null && saw.length>0){
            if(cardLayout.getChildCount() > 3){
                return;
            }
            int i = 0;
            TextView tv1;
            Log.i("Junwang", "setSuggestionsView length="+saw.length);
            for(; i<saw.length; i++){
                if(saw[i].action != null) {
                    tv1 = new TextView(context);
                    tv1.setText(saw[i].action.displayText);
                    tv1.setBackgroundResource(R.drawable.border_textview);
                    tv1.setGravity(Gravity.CENTER);
                    tv1.setPadding(0, 15, 0, 15);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(100,12,100,12);
//                    cardLayout.addView(tv1, j, lp);
                    Log.i("Junwang", "add view i="+i);
                    cardLayout.addView(tv1, lp);
                    SuggestionAction sa = saw[i].action;
                    if ((sa != null) && (sa.urlAction != null)) {
                        tv1.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
//                                WfcWebViewActivity.loadUrl(context, "", sa.urlAction.openUrl.url);
                                WebViewNewsActivity.start(context, sa.urlAction.openUrl.url);
                            }
                        });
                    }else if((sa != null) && (sa.dialerAction != null)){
                        tv1.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                NativeFunctionUtil.callNativeFunction(MessageConstants.NativeActionType.PHONE_CALL, context,
                                        null, null, sa.dialerAction.dialPhoneNumber.phoneNumber);
                            }
                        });
                    }else if((sa != null) && (sa.mapAction != null)){
                        tv1.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                NativeFunctionUtil.openLocation(sa.mapAction.showLocation.location.label, sa.mapAction.showLocation.location.latitude,
                                        sa.mapAction.showLocation.location.longitude, context);
                            }
                        });
                    }
                }
                if(saw[i].reply != null){
                    tv1 = new TextView(context);
                    tv1.setText(saw[i].reply.displayText);
                    tv1.setBackgroundResource(R.drawable.border_textview);
                    tv1.setGravity(Gravity.CENTER);
                    tv1.setPadding(0, 15, 0, 15);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(100,12,100,12);
//                    cardLayout.addView(tv1, j, lp);
                    Log.i("Junwang", "add view1 i="+i);
                    cardLayout.addView(tv1, lp);
                }
            }
        }
    }

    private void parseSingleCardWithSuggestion(String text){
        if(/*text != null && text.startsWith("--next")*/true){
            String[] messageContent = text.split("--next");
            ArrayList<ChatbotMessageBean> cmbList = new ArrayList<>();
            for(int i=0; i<messageContent.length; i++){
                BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(messageContent[i].getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
                String line;
                StringBuffer strbuf=new StringBuffer();
                ChatbotMessageBean cmb = new ChatbotMessageBean(null, null, null);
                try{
                    while ( (line = br.readLine()) != null ) {
                        if(!line.trim().equals("")){
                            if(line.startsWith("Content-Type: ")){
                                cmb.setContent_type(line.substring(14));
                            }else if(line.startsWith("Content-Length: ")){
                                cmb.setContent_length(line.substring(16));
                            }else if(line.startsWith("Content-Disposition:")){

                            }else{
                                strbuf.append(line+"\r\n");
                            }
                        }
                    }
                    if(strbuf != null) {
                        cmb.setContent_text(strbuf.toString());
                    }
                    cmbList.add(cmb);
                }catch (Exception e){
                    LogUtil.e("Junwang", "parse ChatbotMessageBean exception "+e.toString());
                }
            }
            String cmbText = null;
            for(ChatbotMessageBean bean : cmbList){
                LogUtil.i("Junwang", "bean type="+bean.getContent_type());
                cmbText = bean.getContent_text();
                if("application/vnd.gsma.botmessage.v1.0+json".equals(bean.getContent_type())){
                    LogUtil.i("Junwang", "start parse suggestions.");
                    if(cmbText.startsWith("{")){
                        if(cmbText.indexOf("generalPurposeCardCarousel") != -1) {
                            cardJson = cmbText;
                            LogUtil.i("Junwang", "cardJson="+cardJson);
                        }else if(cmbText.indexOf("generalPurposeCard") != -1){
                            cardJson = cmbText;
                            LogUtil.i("Junwang", "cardJson="+cardJson);
                        }else{
                            LogUtil.i("Junwang", "不能被识别的带suggestions的单卡片消息");
                        }
                    }
                }else if("application/vnd.gsma.botsuggestion.v1.0+json".equals(bean.getContent_type())){
                    LogUtil.i("Junwang", "suggestionJson="+cmbText);
                    suggestionJson = cmbText;
                }
            }
        }
    }

    @Override
    public void onBind(Message message) {
        Log.i("Junwang", "singlecard with suggestions Message content view onBind content="+message.getContent());
        String content = message.getContent();
        parseSingleCardWithSuggestion(content);

        ChatbotStdSingleCard singleCard = new Gson().fromJson(cardJson, ChatbotStdSingleCard.class);
        GeneralPurposeCard gcc = singleCard.getMessage().getGeneralPurposeCard();
        if(gcc != null){
            CardContent cardcontent = gcc.getContent();
            if(cardcontent != null){
                String thumbnailUrl = cardcontent.getMedia().getThumbnailUrl();
                String thumbnailtype = cardcontent.getMedia().getThumbnailContentType();
                String mediaType = cardcontent.getMedia().getMediaContentType();
                String mediaUrl = cardcontent.getMedia().getMediaUrl();
                Log.i("Junwang", "loadVerticalCard thumbnailUrl="+thumbnailUrl+", thumbnailtype="+thumbnailtype+", mediaType="+mediaType+", mediaUrl="+mediaUrl);
                cardTitle.setText(cardcontent.getTitle());
                cardDescription.setText(cardcontent.getDescription());

                RequestOptions options = new RequestOptions().error(R.mipmap.default_image).bitmapTransform(new RoundedCornerCenterCrop(8));//图片圆角为8
                if("image/png".equals(cardcontent.getMedia().getThumbnailContentType())
                        || ("image/jpg".equals(cardcontent.getMedia().getThumbnailContentType()))
                        || ("image/jpeg".equals(cardcontent.getMedia().getThumbnailContentType()))){
                    Glide.with(mFragment).load(cardcontent.getMedia().getThumbnailUrl())
                            .apply(options)
                            .into(cardImage);
                    if(("video/mp4".equals(cardcontent.getMedia().getMediaContentType())
                            || "image/jpg".equals(cardcontent.getMedia().getMediaContentType()))
                            && cardcontent.getMedia().getMediaUrl() != null){
                        cardImage.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
//                                ChatbotVideoNewsDetailsActivity.start(getContext(), cardcontent.getMedia().getMediaUrl(), null, null);
                            }
                        });
                    }
                }
                setSuggestionsView(mFragment.getContext(), cardcontent);
                addSuggestions(mFragment.getContext());
            }
        }
    }

    @OnClick(R2.id.card_layout)
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


//    @Override
//    public String contextMenuTitle(Context context, String tag) {
//        if (MessageContextMenuItemTags.TAG_CLIP.equals(tag)) {
//            return "复制";
//        }
//        return super.contextMenuTitle(context, tag);
//    }

    @Override
    public boolean contextMenuItemFilter(Message uiMessage, String tag) {
        if (MessageContextMenuItemTags.TAG_FORWARD.equals(tag)) {
            return true;
        } else {
            return super.contextMenuItemFilter(uiMessage, tag);
        }
    }
}