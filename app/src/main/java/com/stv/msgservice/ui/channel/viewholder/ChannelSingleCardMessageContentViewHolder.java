package com.stv.msgservice.ui.channel.viewholder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ArrayUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cjt2325.cameralibrary.util.LogUtil;
import com.google.gson.Gson;
import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.database.entity.MessageUserInfoEntity;
import com.stv.msgservice.datamodel.network.chatbot.CardContent;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMessageBean;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotStdSingleCard;
import com.stv.msgservice.datamodel.network.chatbot.GeneralPurposeCard;
import com.stv.msgservice.datamodel.network.chatbot.SuggestionActionWrapper;
import com.stv.msgservice.datamodel.network.chatbot.Suggestions;
import com.stv.msgservice.ui.channel.ChannelCardSuggestionListAdapter;
import com.stv.msgservice.ui.channel.HorizontalItemDecoration;
import com.stv.msgservice.ui.conversation.message.viewholder.RoundedCornerCenterCrop;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChannelSingleCardMessageContentViewHolder extends ChannelMsgItemViewHolder {
    TextView channelSingleCardDescription;
    ImageView channelSingleCardImage;
    ImageView channelSingleCardPlayImageView;
    RecyclerView channelSingleCardSuggestion;
    Fragment mFragment;
    String cardJson = null;
    String suggestionJson = null;
    ChannelCardSuggestionListAdapter adpter;
    MessageUserInfoEntity messageUserInfoEntity;

    public ChannelSingleCardMessageContentViewHolder(Fragment fragment, RecyclerView.Adapter adapter, View itemView, View viewStubInflator, MessageUserInfoEntity messageUserInfoEntity) {
        super(fragment, adapter, itemView);
        mFragment = fragment;
        channelSingleCardDescription = viewStubInflator.findViewById(R.id.channel_singlecard_description);
        channelSingleCardImage = viewStubInflator.findViewById(R.id.channel_singlecard_image);
        channelSingleCardPlayImageView = viewStubInflator.findViewById(R.id.channel_singlecard_playImageView);
        channelSingleCardSuggestion = viewStubInflator.findViewById(R.id.channel_singlecard_suggestion);
        this.messageUserInfoEntity = messageUserInfoEntity;
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

    private void setSuggestionsView(Context context, CardContent cardcontent){
        SuggestionActionWrapper[] saw = cardcontent.getSuggestionActionWrapper();
        if(suggestionJson != null){
            Suggestions suggestions = new Gson().fromJson(suggestionJson, Suggestions.class);
            if(suggestions != null){
                SuggestionActionWrapper[] saw1 = suggestions.getSuggestions();
                saw = ArrayUtils.add(saw, saw1);
            }
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(mFragment.getContext(), LinearLayoutManager.HORIZONTAL, false);
        channelSingleCardSuggestion.setLayoutManager(layoutManager);
        channelSingleCardSuggestion.addItemDecoration(new HorizontalItemDecoration(12,mFragment.getContext()));//10表示10dp
        adpter = new ChannelCardSuggestionListAdapter();
        adpter.setSuggestions(mFragment, saw, messageUserInfoEntity.getPortrait(), messageUserInfoEntity.getName(),
                messageUserInfoEntity.getSenderAddress(), messageUserInfoEntity.isAttentioned());
        channelSingleCardSuggestion.setAdapter(adpter);
    }

    @Override
    public void onBind(MessageUserInfoEntity message) {
        Log.i("Junwang", "ChannelSingleCardMessageContentViewHolder onBind content="+message.getContent());
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
                channelSingleCardDescription.setText(cardcontent.getDescription());

                RequestOptions options = new RequestOptions().error(R.mipmap.default_image).bitmapTransform(new RoundedCornerCenterCrop(8));//图片圆角为8
                if("image/png".equals(cardcontent.getMedia().getThumbnailContentType())
                        || ("image/jpg".equals(cardcontent.getMedia().getThumbnailContentType()))
                        || ("image/jpeg".equals(cardcontent.getMedia().getThumbnailContentType()))){
                    Glide.with(mFragment).load(cardcontent.getMedia().getThumbnailUrl())
                            .apply(options)
                            .into(channelSingleCardImage);
                    if(("video/mp4".equals(cardcontent.getMedia().getMediaContentType())
                            || "image/jpg".equals(cardcontent.getMedia().getMediaContentType()))
                            && cardcontent.getMedia().getMediaUrl() != null){
                        channelSingleCardImage.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
//                                ChatbotVideoNewsDetailsActivity.start(getContext(), cardcontent.getMedia().getMediaUrl(), null, null);
                            }
                        });
                        if(("video/mp4".equals(cardcontent.getMedia().getMediaContentType()))){
                            channelSingleCardPlayImageView.setVisibility(View.VISIBLE);
                        }
                    }
                }
                setSuggestionsView(mFragment.getContext(), cardcontent);
//                addSuggestions(mFragment.getContext());
            }
        }
    }
}
