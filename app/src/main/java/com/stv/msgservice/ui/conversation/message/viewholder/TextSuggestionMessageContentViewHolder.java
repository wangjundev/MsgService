package com.stv.msgservice.ui.conversation.message.viewholder;

import android.content.Context;
import android.text.Html;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjt2325.cameralibrary.util.LogUtil;
import com.google.gson.Gson;
import com.lqr.emoji.MoonUtils;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.EnableContextMenu;
import com.stv.msgservice.annotation.MessageContentType;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMessageBean;
import com.stv.msgservice.datamodel.network.chatbot.SuggestionAction;
import com.stv.msgservice.datamodel.network.chatbot.SuggestionActionWrapper;
import com.stv.msgservice.datamodel.network.chatbot.Suggestions;
import com.stv.msgservice.ui.WebViewNewsActivity;
import com.stv.msgservice.ui.WfcWebViewActivity;
import com.stv.msgservice.ui.conversation.ConversationFragment;
import com.stv.msgservice.ui.conversation.message.TextSuggestionMessageContent;
import com.stv.msgservice.ui.widget.LinkClickListener;
import com.stv.msgservice.ui.widget.LinkTextViewMovementMethod;
import com.stv.msgservice.utils.NativeFunctionUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

@MessageContentType(value = {
        TextSuggestionMessageContent.class,
})
@EnableContextMenu
public class TextSuggestionMessageContentViewHolder extends NormalMessageContentViewHolder{
    @BindView(R2.id.text_content)
    TextView contentTextView;
    @BindView(R2.id.cardView)
    CardView cardView;
    String suggestionJson = null;

    public TextSuggestionMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
    }

    public static void addSuggestions(Context context, String suggestionJson, ViewGroup cardView){
        Log.i("Junwang", "suggestionJson="+suggestionJson);
        Suggestions suggestions = new Gson().fromJson(suggestionJson, Suggestions.class);
        SuggestionActionWrapper[] saw = suggestions.getSuggestions();
        if(saw != null && saw.length>0){
            if(cardView.getChildCount() > 4){
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
                    cardView.addView(tv1, lp);
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
                    cardView.addView(tv1, lp);
                }
            }
        }
    }

    @Override
    public void onBind(Message message) {
//        TextMessageContent textMessageContent = (TextMessageContent) message.content;
//        String content = textMessageContent.getContent();
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
            if("text/plain".equals(bean.getContent_type())){
                LogUtil.i("Junwang", "start parse plain text + suggestions RCS. text="+cmbText);
                if(cmbText.startsWith("<") && cmbText.endsWith(">")) {
                    contentTextView.setText(Html.fromHtml(cmbText));
                }else{
                    MoonUtils.identifyFaceExpression(fragment.getContext(), contentTextView, cmbText, ImageSpan.ALIGN_BOTTOM);
                }
            }else if("application/vnd.gsma.botsuggestion.v1.0+json".equals(bean.getContent_type())){
                LogUtil.i("Junwang", "suggestionJson="+cmbText);
                suggestionJson = cmbText;
                addSuggestions(fragment.getContext(), suggestionJson, cardView);
            }
        }
        contentTextView.setMovementMethod(new LinkTextViewMovementMethod(new LinkClickListener() {
            @Override
            public boolean onLinkClick(String link) {
                WfcWebViewActivity.loadUrl(fragment.getContext(), "", link);
                return true;
            }
        }));
    }
}
