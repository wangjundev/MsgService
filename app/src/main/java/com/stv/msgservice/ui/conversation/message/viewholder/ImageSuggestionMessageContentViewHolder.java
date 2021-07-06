package com.stv.msgservice.ui.conversation.message.viewholder;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cjt2325.cameralibrary.util.LogUtil;
import com.google.gson.Gson;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.MessageContextMenuItem;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.network.chatbot.CardContent;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotFile;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMessageBean;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotStdSingleCard;
import com.stv.msgservice.datamodel.network.chatbot.GeneralPurposeCard;
import com.stv.msgservice.ui.conversation.ConversationFragment;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

import static com.stv.msgservice.ui.conversation.message.viewholder.TextSuggestionMessageContentViewHolder.addSuggestions;

public class ImageSuggestionMessageContentViewHolder extends MediaMessageContentViewHolder {
    @BindView(R2.id.card_image)
    ImageView cardImage;
    @BindView(R2.id.card_layout)
    LinearLayout cardLayout;
    Fragment mFragment;
    String suggestionJson = null;

    public ImageSuggestionMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
        mFragment = fragment;
    }

    @Override
    public void onBind(Message message) {
        Log.i("Junwang", "singlecardstdMessage content view onBind");
        String content = message.getContent();
        String[] messageContent = content.split("--next");
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
        for(ChatbotMessageBean bean : cmbList){
            String cmbText = bean.getContent_text();
            if("application/vnd.gsma.rcs-ft-http+xml".equals(bean.getContent_type())){
                LogUtil.i("Junwang", "start parse image + suggestions RCS. filexmltext="+cmbText);
                try{
                    Serializer se=new Persister();
                    ChatbotFile file=(ChatbotFile )se.read(ChatbotFile.class, cmbText);
                    String fileType = file.getFileInfo().get(0).getContent_type();
                    String fileContentType;
                    String thumbnail = null;
                    String mediaUrl = null;
                    for(int i=0; i<file.getFileInfo().size(); i++){
                        fileType = file.getFileInfo().get(i).getType();
                        fileContentType = file.getFileInfo().get(i).getContent_type();
                        if("thumbnail".equals(fileType)){
                            thumbnail = file.getFileInfo().get(i).getData().getUrl();
                            RequestOptions options = new RequestOptions().error(R.mipmap.default_image).bitmapTransform(new RoundedCornerCenterCrop(8));//图片圆角为8
                            Glide.with(mFragment).load(thumbnail)
                                    .apply(options)
                                    .into(cardImage);
                            cardImage.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
//                                ChatbotVideoNewsDetailsActivity.start(getContext(), cardcontent.getMedia().getMediaUrl(), null, null);
                                }
                            });
                        }
                    }
                }catch (Exception e){
                    Log.i("Junwang", "parse RCS plain file format exception "+e.toString());
                }

            }else if("application/vnd.gsma.botsuggestion.v1.0+json".equals(bean.getContent_type())){
                LogUtil.i("Junwang", "suggestionJson="+cmbText);
                suggestionJson = cmbText;
                addSuggestions(fragment.getContext(), suggestionJson, cardLayout);
            }
        }


        ChatbotStdSingleCard singleCard = new Gson().fromJson(content, ChatbotStdSingleCard.class);
        GeneralPurposeCard gcc = singleCard.getMessage().getGeneralPurposeCard();
//        if(cardTitle.getText() != null){
//            cardLayout.removeAllViews();
//        }
        if(gcc != null){
            CardContent cardcontent = gcc.getContent();
            if(cardcontent != null){
                String thumbnailUrl = cardcontent.getMedia().getThumbnailUrl();
                String thumbnailtype = cardcontent.getMedia().getThumbnailContentType();
                String mediaType = cardcontent.getMedia().getMediaContentType();
                String mediaUrl = cardcontent.getMedia().getMediaUrl();
                Log.i("Junwang", "loadVerticalCard SingleCardStdMessageContentViewHolder thumbnailUrl="+thumbnailUrl+", thumbnailtype="+thumbnailtype+", mediaType="+mediaType+", mediaUrl="+mediaUrl);

                RequestOptions options = new RequestOptions().error(R.mipmap.default_image).transform(new RoundedCornerCenterCrop(8));//图片圆角为30

                if("image/png".equals(cardcontent.getMedia().getThumbnailContentType())
                        || ("image/jpg".equals(cardcontent.getMedia().getThumbnailContentType()))
                        || ("image/jpeg".equals(cardcontent.getMedia().getThumbnailContentType()))){
                    Glide.with(mFragment).load(cardcontent.getMedia().getThumbnailUrl())
                            .apply(options)
                            .into(cardImage);
                    if("image/jpg".equals(cardcontent.getMedia().getMediaContentType())
                            && cardcontent.getMedia().getMediaUrl() != null){
                        cardImage.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
//                                loadMedia(thumbnail,imagePath,imageView);
//                                ChatbotVideoNewsDetailsActivity.start(getContext(), cardcontent.getMedia().getMediaUrl(), null, null);
                            }
                        });
                    }
                }

//                setSuggestionsView(mFragment.getContext(), cardcontent);
            }
        }
    }

//    @OnClick(R2.id.card_layout)
//    public void onClick(View view) {
////        String content = ((TextMessageContent) message.content).getContent();
//        WfcWebViewActivity.loadHtmlContent(fragment.getActivity(), "消息内容", /*content*/message.getContent());
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
    public boolean contextMenuItemFilter(Message uiMessage, String tag) {
        if (MessageContextMenuItemTags.TAG_FORWARD.equals(tag)) {
            return true;
        } else {
            return super.contextMenuItemFilter(uiMessage, tag);
        }
    }
}
